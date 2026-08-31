package service;

import enums.SpotType;
import enums.VehicleType;
import factory.ParkingSpotFactory;
import factory.VehicleFactory;
import models.*;

import java.util.List;

public class ParkingLotServiceImpl implements ParkingLotService{
    public static volatile ParkingLotService instance;

    public static ParkingLotService getInstance() {
        if(instance == null){
            synchronized(ParkingLotServiceImpl.class){
                if(instance == null){
                    instance = new ParkingLotServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Vehicle createVehicle(VehicleType type) {
        return VehicleFactory.createVehicle(type);
    }

    @Override
    public ParkingSpot createParkingSpot(SpotType spotType) {
        return ParkingSpotFactory.createParkingSpot(spotType);
    }

    @Override
    public boolean isAvailable(ParkingLot parkingLot, Vehicle vehicle) {
        return parkingLot.getFloors().stream()
                .flatMap(floor -> floor.getParkingSpots().stream())
                .anyMatch(parkingSpot -> parkingSpot.getSpotType().name().equals(vehicle.getVehicleType().name()));
    }

    @Override
    public ParkingTicket parkVehicle(Vehicle car) {
        return null;
    }


}
