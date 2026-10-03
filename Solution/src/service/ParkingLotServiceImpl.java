package service;

import enums.SpotType;
import enums.VehicleType;
import factory.ParkingSpotFactory;
import factory.VehicleFactory;
import models.*;

import java.util.List;
import java.util.Map;

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
        return parkingLot.isAvailableFor(vehicle.getVehicleType());
    }

    @Override
    public ParkingTicket parkVehicle(Vehicle vehicle, ParkingLot parkingLot) {
        //iterate over all the parking spots floor by floor and whichever is the first empty spot assign that to the vehicle
        //and issue a parking ticket
        ParkingSpot parkingSpot = parkingLot.getFloors().stream()
                .flatMap(floor -> floor.getParkingSpots().stream())
                .filter(spot -> !spot.isOccupied() && spot.getSpotType().equals(SpotType.CAR))
                .findFirst().orElse(null);
        if(parkingSpot == null) {
            System.out.println("No Parking Spot found");
            return null;
        }
        parkingSpot.assignVehicle(vehicle);
        String parkingTicketId = parkingLot.getId() + parkingSpot.getId();
        ParkingTicket parkingTicket = new ParkingTicket(parkingTicketId, vehicle, parkingSpot);

    }

    @Override
    public void getAvailabilityCountByType(ParkingLot parkingLot) {
        System.out.println("Available Parking Lot");
        Map<VehicleType, Long> availableSpots = parkingLot.getFreeSpotCountByType();
        availableSpots.forEach((type, count) ->
                System.out.println(type + ": " + count + " spots available"));
    }


}
