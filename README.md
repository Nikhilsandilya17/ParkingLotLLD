# Parking Lot LLD (Java)

Low-level design for a multi-level parking lot system.

## Requirements

### 1. Structure
- The parking lot should have multiple levels (floors), each level with a certain number of parking spots.
- The parking lot should support different types of vehicles: cars, motorcycles (bikes), and trucks.
- Each parking spot should accommodate a specific type of vehicle (`SpotType` matches `VehicleType`).

### 2. Core Flow
- **Entry:** The system should assign a free, type-compatible parking spot to a vehicle upon entry and issue a parking ticket.
- **Deterministic allocation:** the assigned spot must be the first available spot of the matching type on the lowest floor, and within a floor, the spot with the lowest slot number (slots numbered 1..n per floor).
- **Structured ticket ID:** ticket id must follow the format `<parking_lot_id>_<floor_no>_<slot_no>` (e.g., `PR1234_2_5` — 5th slot of 2nd floor of lot PR1234).
- **Exit:** The system should release the spot (mark free) and calculate the fee when the vehicle exits, using a `FeeStrategy`.

### 3. Validation & Error Cases
- If no free spot of the vehicle's type exists, the system must throw a `ParkingLotFullException` (not silently assign a wrong-type spot).
- A vehicle must never be assigned a spot of a different type (wrong vehicle type must be rejected by the system).
- On exit, an unknown/expired ticket id must be rejected with "Invalid Ticket".
- A vehicle with an already-active ticket must not be issued another ticket.

### 4. Availability
- The system should track spot availability and provide real-time information (available spot counts per vehicle type).

### 5. Concurrency
- The system should handle multiple entry and exit points and support concurrent access.
- **No double assignment:** two threads entering at the same time must never be assigned the same spot (atomic spot reservation via `AtomicBoolean.compareAndSet` in `tryAssignVehicle`).
- **No double exit:** two threads unparking with the same ticket id — exactly one wins. The atomic `remove(ticketId)` in the ticket registry returns the ticket to exactly one caller; the other gets "Invalid Ticket".
- A vehicle must not be issued two active tickets simultaneously.

## Status: COMPLETE

All requirements implemented and verified. Run:

```
javac -d out $(find Solution/src -name "*.java") && java -cp out ParkingLotDemo
```

## Architecture

```
ParkingLotDemo
     │
ParkingLotService (interface) ──► ParkingLotServiceImpl      [orchestration]
     │                                    │
     │                              TicketService (interface) ──► TicketServiceImpl
     │                                    │                        │
ParkingLot ──► Floor ──► ParkingSpot       │                        │
 (builder)             (CAS + vehicle)     │                        ▼
                                          │              TicketRepository (interface)
     │                                    │                ──► TicketRepositoryImpl
Vehicle ──► Car/Bike/Truck  (factory)     │                    (ConcurrentHashMap)
     │                                    │
     └──► ParkingTicket  (TicketStatus)   └──► FeeStrategy ──► RegularFee / VehicleBasedFee
```

## Design Decisions

### 1. Layering: service orchestrates, models own domain rules
- Domain logic lives with the data: `ParkingLot.isAvailableFor(type)`, spot finding/allocation rules on the lot, fee rules in `FeeStrategy`.
- Services only sequence use-case steps (find spot → reserve → issue ticket; look up ticket → close → release → charge).
- Rule of thumb used: if a method would move with the data to a different app, it is domain logic → model; if it only sequences one use case, it is orchestration → service.

### 2. Ticket: pass ID at the boundary, not the object
- `unparkVehicle(String ticketId)` — callers only ever supply an identifier; the authoritative ticket record is fetched from the registry.
- Prevents forged/stale tickets: the caller can only say *which* record to fetch, never *what's in it*. A whole-object parameter would let the ID be valid while other fields (spot, times) are attacker-controlled.

### 3. Ticket lifecycle: registry as source of truth
- `TicketRepository` (interface) + `TicketRepositoryImpl` (in-memory `ConcurrentHashMap`).
- `closeTicket(ticketId)` uses a single atomic `remove(ticketId)` — remove-and-fetch in one call. The return value decides the winner: first caller gets the ticket, everyone else gets "Invalid Ticket". This makes double-exit impossible without any explicit lock.
- `TicketStatus` enum (`ACTIVE`/`EXPIRED`) guards against closing an already-closed ticket that is somehow still registered (defense in depth).

### 4. Spot allocation: atomic check-and-claim
- `tryAssignVehicle` — the "try" signals it may lose. `AtomicBoolean.compareAndSet(false, true)` makes check-and-set one indivisible step; losers return false and the caller moves to the next candidate (no pre-filter + separate set race window).
- The spot stores its parked vehicle (spot ↔ vehicle mapping lives in the spot, not a repo): occupancy and vehicle cannot disagree, and no second source of truth needs syncing.
- Deterministic order: floors iterated in floor-number order, spots in slot-number order — first candidate that wins the CAS is by construction the lowest floor / lowest slot.

### 5. Concurrency: each guard at the smallest scope
- Spot flag: `AtomicBoolean` CAS (single variable, check-and-set).
- Singleton `getInstance`: double-checked locking with `volatile` — class lock on `ParkingLotServiceImpl.class` (lock the class whose static state you're protecting, not the interface).
- Registry: `ConcurrentHashMap` — many tickets, atomic per-key operations.
- **No synchronized service methods**: a service-level lock would serialize all parking operations behind one lock; safety is instead provided by the layers below, each at its own scope. (Known limitation: the `hasActiveTicket` → `issueTicket` sequence in `parkVehicle` is check-then-act across two calls; would need `putIfAbsent` semantics to be fully race-free.)

### 6. Builder + defensive copying
- `ParkingLotBuilder` for readable construction; `parkingFloors(...)` copies the incoming list so external mutation of the caller's list cannot alter the lot.
- Null-safe: building without floors yields an empty lot, not an NPE.

### 7. Errors as exceptions, not prints
- `ParkingLotFullException` for no compatible free spot; `IllegalArgumentException("Invalid Ticket")` for unknown/closed tickets; `IllegalStateException` for duplicate active ticket. Callers decide how to present them.

## Known Simplifications (interview talking points)
- `closeTicket()` adds +2h to exit time for demo purposes (so a park+unpark shows a nonzero fee) — remove before "real" use.
- Lot ID is a random UUID; the classic problem statement uses `PR1234`-style ids (builder could accept the id).
- Vehicles get random UUID license numbers; the classic statement uses registration number + color, which would also serve as the duplicate-vehicle identity key.
- `FeeStrategy.pay()` mixes payment into a pricing strategy — pricing and payment are separate concerns; extraction is a natural follow-up.
- Duration is billed per whole hour via `Duration.toHours()` (a 5-minute stay bills 0h; real systems bill per started hour).

## Design Patterns Used

| Pattern | Where | What it buys |
|---|---|---|
| **Singleton** (double-checked locking + `volatile`) | `TicketRepositoryImpl.getInstance()` | One shared ticket registry across all gates. Lock taken on the impl class, not the interface. |
| **Builder** | `ParkingLot.ParkingLotBuilder` | Readable step-by-step construction of the lot; defensive copy of floors keeps external mutation out. |
| **Factory Method** | `VehicleFactory`, `ParkingSpotFactory` | Creation of vehicles/spots by enum, isolated from callers; adding a vehicle type touches one switch. |
| **Strategy** | `FeeStrategy` ← `RegularFee`, `VehicleBasedFee` | Fee model is swappable at runtime without touching the exit flow — pricing rules vary independently of parking logic. |
| **Template-ish inheritance** | `Vehicle` base + `Car`/`Bike`/`Truck` | Subclasses fix their own `VehicleType`; shared state (license, type) in one place. (Currently shallow — subclasses add no behavior; honest talking point.) |
| **Repository** | `TicketRepository` (interface) + `TicketRepositoryImpl` | Storage abstracted behind an interface — in-memory today, DB or test fake tomorrow, no caller changes. |
| **Service layer / DTO-ish separation** | `ParkingLotService`, `TicketService` | Use-case orchestration separated from domain rules and storage. |
| **Enum-based state** | `TicketStatus` (ACTIVE/EXPIRED) | Ticket lifecycle as a typed state, not booleans scattered around. |

Not used, and why (interviewers ask): **Observer** (display boards) — display is a single pull-based query (`getFreeSpotCountByType`) and no push requirement yet; **Command** — commands are two simple methods, not queueable/undoable operations; **Decorator/Composite** — no object wrapping or part-whole hierarchy in the domain.

## Package Layout
```
Solution/src/
├── ParkingLotDemo.java          # entry point / manual test
├── enums/                       # VehicleType, SpotType, TicketStatus
├── models/                      # Vehicle, Car/Bike/Truck, ParkingSpot, ParkingTicket, Floor, ParkingLot
├── factory/                     # VehicleFactory, ParkingSpotFactory
├── repository/                  # TicketRepository (interface), TicketRepositoryImpl
├── service/                     # ParkingLotService(+Impl), TicketService(+Impl), ParkingLotFullException
└── strategy/fee/                # FeeStrategy, RegularFee, VehicleBasedFee
```
