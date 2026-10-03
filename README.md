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
- If no free spot of the vehicle's type exists, the system must return/reject with a "Parking Lot Full" error (not silently assign a wrong-type spot).
- A vehicle must never be assigned a spot of a different type (wrong vehicle type must be rejected by the system).
- On exit, an unknown/expired ticket id must be rejected with "Invalid Ticket".
- A vehicle with an already-active ticket must not be issued another ticket.

### 3. Availability
- The system should track spot availability and provide real-time information (e.g., available spot counts per vehicle type).

### 4. Concurrency
- The system should handle multiple entry and exit points and support concurrent access.
- **No double assignment:** two threads entering at the same time must never be assigned the same spot (atomic spot reservation, e.g., `AtomicBoolean.compareAndSet` or equivalent).
- A vehicle must not be issued two active tickets simultaneously (thread-safe ticket registry, e.g., `ConcurrentHashMap`).
- Exit (spot release) must be thread-safe as well.

## Current Status

### Done
- [x] Enums: `VehicleType`, `SpotType`
- [x] Models: `Vehicle`, `ParkingSpot`, `ParkingTicket`, `Floor`, `ParkingLot` (with builder)
- [x] Factories: `VehicleFactory`, `ParkingSpotFactory`
- [x] Fee strategies: `FeeStrategy`, `RegularFee`, `VehicleBasedFee`
- [x] `ParkingLotService` interface with `createVehicle`, `createParkingSpot`, `isAvailable`, `parkVehicle`
- [x] Thread-safe singleton (`ParkingLotServiceImpl.getInstance()`)

### Bugs to Fix
- [x] `Car` doesn't compile — `super(vehicleType)` references undefined variable; should be `super(VehicleType.CAR)`.
- [x] `Bike`/`Truck` constructors take `VehicleType`, but `VehicleFactory` calls them with no args — align signatures.
- [x] `ParkingLotDemo.main` is not a valid entry point — must be `public static void main(String[] args)`.
- [x] `ParkingLot` builder drops the floors passed via `parkingFloors(...)` — constructor creates an empty list and never copies them.
- [x] `ParkingLot.addFloor` off-by-one — allows `numberOfFloors + 1` floors; condition should be `>=`.
- [x] `isAvailable` occupancy check is inverted — uses `parkingSpot.isOccupied()` instead of `!parkingSpot.isOccupied()`; returns true when a matching spot is occupied.
- [x] Demo reuses the same `ParkingSpot` instance across floors and multiple times within a floor — each spot must be a distinct instance.

### To Implement
- [ ] `parkVehicle`: find a free, type-compatible spot using deterministic allocation (lowest floor, lowest slot number), atomically reserve it, mark occupied, issue a `ParkingTicket`. Throw/return a "Parking Lot Full" error if no matching spot is available.
- [ ] Structured ticket ID: generate ticket ids as `<parking_lot_id>_<floor_no>_<slot_no>` instead of a random UUID (requires floor numbers and slot numbers on `Floor`/`ParkingSpot`).
- [ ] `unparkVehicle`: look up the active ticket by ticket id, set exit time, mark the spot free, and compute the fee via `FeeStrategy`. Reject unknown/inactive ticket ids with "Invalid Ticket".
- [ ] Active ticket registry (`ConcurrentHashMap<ticketId, ParkingTicket>` or vehicle → ticket) supporting lookup at exit and preventing duplicate active tickets for the same vehicle.
- [ ] Concurrent spot allocation: prevent double assignment of the same spot (e.g., `AtomicBoolean.compareAndSet` per spot).
- [ ] Entry/exit gate abstraction (multiple entry and exit points) sharing the same service.
- [ ] Wire `FeeStrategy` into the exit flow (currently unused).

## Suggested Extensions (optional)
- Per-floor availability display instead of whole-lot aggregate
- Different fee strategies per time-of-day / weekend
- Parking attendant / admin panel for adding floors and spots
- Ticket payment status
