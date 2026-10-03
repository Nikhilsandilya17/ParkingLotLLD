package models;

import enums.SpotType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParkingSpot {
    private final String id;
    private final SpotType spotType;
    private final AtomicBoolean isOccupied = new AtomicBoolean(false);
    private volatile Vehicle parkedVehicle;
    private int slotNumber;

    public ParkingSpot(SpotType spotType) {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.spotType = spotType;
    }

    public String getId() {
        return id;
    }

    public SpotType getSpotType() {
        return spotType;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public boolean isOccupied() {
        return isOccupied.get();
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public boolean tryAssignVehicle(Vehicle vehicle) {
        if (!isOccupied.compareAndSet(false, true)) {
            return false;
        }
        this.parkedVehicle = vehicle;
        return true;
    }

    public Vehicle removeVehicle() {
        Vehicle vehicle = parkedVehicle;
        parkedVehicle = null;
        isOccupied.set(false);
        return vehicle;
    }
}
