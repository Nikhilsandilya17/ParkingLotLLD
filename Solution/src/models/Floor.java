package models;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final String floorNumber;
    private final List<ParkingSpot> parkingSpots;

    public Floor(String floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
    }

    public void addParkingSpot(List<ParkingSpot> parkingSpots) {
        this.parkingSpots.addAll(parkingSpots);
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public String getFloorNumber() {
        return floorNumber;
    }
}
