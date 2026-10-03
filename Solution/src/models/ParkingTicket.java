package models;

import java.util.UUID;

public class ParkingTicket {
    private final String id;
    private final Vehicle vehicle;
    private final long entryTime;
    private long exitTime;
    private final ParkingSpot parkingSpot;

    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot) {
        this.id = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.entryTime = System.currentTimeMillis();
        this.parkingSpot = parkingSpot;
    }

    public String getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public long getExitTime() {
        return exitTime;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void closeTicket() {
        this.exitTime = System.currentTimeMillis();
    }
}
