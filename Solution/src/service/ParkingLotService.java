package service;

import enums.SpotType;
import enums.VehicleType;
import models.ParkingLot;
import models.ParkingSpot;
import models.ParkingTicket;
import models.Vehicle;

public interface ParkingLotService {
    Vehicle createVehicle(VehicleType type);

    ParkingSpot createParkingSpot(SpotType spotType);

    boolean isAvailable(ParkingLot parkingLot, Vehicle vehicle);

    ParkingTicket parkVehicle(Vehicle vehicle, ParkingLot parkingLot);

    void getAvailabilityCountByType(ParkingLot parkingLot);

    void unparkVehicle(String ticketId);
}
