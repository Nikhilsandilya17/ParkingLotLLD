package service;

import enums.SpotType;
import enums.VehicleType;
import factory.ParkingSpotFactory;
import factory.VehicleFactory;
import models.*;

import repository.TicketRepositoryImpl;

import java.util.Map;

public class ParkingLotServiceImpl implements ParkingLotService{
    public static volatile ParkingLotService instance;

    private final TicketService ticketService;

    private ParkingLotServiceImpl() {
        this.ticketService = new TicketServiceImpl(TicketRepositoryImpl.getInstance());
    }

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
        if (ticketService.hasActiveTicket(vehicle)) {
            throw new IllegalStateException("Vehicle already has an active ticket");
        }
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getParkingSpots()) {
                if (spot.getSpotType().name().equals(vehicle.getVehicleType().name())
                        && spot.tryAssignVehicle(vehicle)) {
                    String ticketId = parkingLot.getId() + "_" + floor.getFloorNumber() + "_" + spot.getSlotNumber();
                    return ticketService.issueTicket(ticketId, vehicle, spot);
                }
            }
        }
        throw new ParkingLotFullException("Parking Lot Full for vehicle type: " + vehicle.getVehicleType());
    }

    @Override
    public void getAvailabilityCountByType(ParkingLot parkingLot) {
        System.out.println("Available Parking Lot");
        Map<VehicleType, Long> availableSpots = parkingLot.getFreeSpotCountByType();
        availableSpots.forEach((type, count) ->
                System.out.println(type + ": " + count + " spots available"));
    }

}
