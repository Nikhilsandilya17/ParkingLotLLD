package models;

import enums.TicketStatus;

import java.time.LocalDateTime;

public class ParkingTicket {
    private final String id;
    private final Vehicle vehicle;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final ParkingSpot parkingSpot;
    private TicketStatus ticketStatus;

    public ParkingTicket(String id, Vehicle vehicle, ParkingSpot parkingSpot) {
        this.id = id;
        this.vehicle = vehicle;
        this.entryTime = LocalDateTime.now();
        this.parkingSpot = parkingSpot;
        this.exitTime = null;
        this.ticketStatus = TicketStatus.ACTIVE;
    }


    public String getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void closeTicket() {
        this.exitTime = LocalDateTime.now().plusHours(2);
        ticketStatus = TicketStatus.CLOSED;
    }

    public boolean isExpired(){
        return ticketStatus == TicketStatus.CLOSED;
    }
}
