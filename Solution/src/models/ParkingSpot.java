package models;

import enums.SpotType;

import java.util.UUID;

public class ParkingSpot {
    private final String id;
    private final SpotType spotType;
    private boolean isOccupied;

    public ParkingSpot(SpotType spotType) {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.spotType = spotType;
        this.isOccupied = false;
    }

    public String getId() {
        return id;
    }

    public SpotType getSpotType() {
        return spotType;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public boolean assignVehicle() {
        if (isOccupied) {
            return false;
        }
        this.isOccupied = true;
        return true;
    }

    public void removeVehicle() {
        this.isOccupied = false;
    }
}
