package models;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final int floorNumber;
    private final List<ParkingSpot> parkingSpots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
    }

    public void addParkingSpot(List<ParkingSpot> parkingSpots) {
        parkingSpots.forEach(this::addParkingSpot);
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        parkingSpot.setSlotNumber(parkingSpots.size() + 1);
        this.parkingSpots.add(parkingSpot);
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public int getFloorNumber() {
        return floorNumber;
    }
}
