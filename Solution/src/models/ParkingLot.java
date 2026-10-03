package models;

import enums.VehicleType;

import java.util.*;
import java.util.stream.Collectors;

public class ParkingLot {
    private final String id;
    private final int numberOfFloors;
    private List<Floor> floors;

    private ParkingLot(ParkingLotBuilder parkingLotBuilder) {
        this.id = parkingLotBuilder.id;
        this.numberOfFloors = parkingLotBuilder.numberOfFloors;
        this.floors = parkingLotBuilder.floors;
    }

    public String getId() {
        return id;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public List<Floor> getFloors() {
        return floors;
    }


    public void addFloor(Floor floor) {
        if(floors.size() >= numberOfFloors){
            throw new IllegalStateException("Cannot add more floors");
        }
        floors.add(floor);
    }

    public Map<VehicleType, Long> displayAvailability() {
        return floors.stream()
                .flatMap(floor -> floor.getParkingSpots().stream())
                .filter(spot -> !spot.isOccupied())
                .collect(Collectors.groupingBy(
                        spot -> VehicleType.valueOf(spot.getSpotType().name()),
                        Collectors.counting()
                ));
    }


    public static class ParkingLotBuilder{
        private final String id;
        private final int numberOfFloors;
        private List<Floor> floors;

        public ParkingLotBuilder(int numberOfFloors){
            this.id = UUID.randomUUID().toString();
            this.numberOfFloors = numberOfFloors;
        }

        public ParkingLotBuilder parkingFloors(List<Floor> floors){
            this.floors = floors;
            return this;
        }

        public ParkingLot build(){
            return new ParkingLot(this);
        }
    }
}
