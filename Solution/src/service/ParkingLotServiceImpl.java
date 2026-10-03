package service;

import enums.SpotType;
import enums.VehicleType;
import exception.ParkingLotFullException;
import factory.ParkingSpotFactory;
import factory.VehicleFactory;
import models.*;

import repository.TicketRepository;
import repository.TicketRepositoryImpl;
import strategy.fee.FeeStrategy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class ParkingLotServiceImpl implements ParkingLotService{
    public static volatile ParkingLotService instance;

    private final TicketService ticketService;
    private final FeeStrategy feeStrategy;

    public ParkingLotServiceImpl(FeeStrategy feeStrategy, TicketRepository ticketRepository) {
        this.ticketService = new TicketServiceImpl(ticketRepository);
        this.feeStrategy = feeStrategy;
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

    @Override
    public void unparkVehicle(String ticketId) {
        ParkingTicket parkingTicket = ticketService.closeTicket(ticketId);
        parkingTicket.getParkingSpot().removeVehicle();
        long durationInHours = Duration.between(parkingTicket.getEntryTime(), parkingTicket.getExitTime()).toHours();
        double parkingCharge = feeStrategy.calculateCharge(parkingTicket.getVehicle(), durationInHours);
        feeStrategy.pay(parkingCharge);

    }

}
