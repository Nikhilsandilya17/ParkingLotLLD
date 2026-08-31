import enums.SpotType;
import enums.VehicleType;
import models.*;
import service.ParkingLotService;
import service.ParkingLotServiceImpl;

import java.util.List;

public class ParkingLotDemo {
    static void main() {
        ParkingLotService parkingLotService = ParkingLotServiceImpl.getInstance();

        System.out.println("---Parking Lot Demo---");
        //1. Create a vehicle
        Vehicle car = parkingLotService.createVehicle(VehicleType.CAR);
        Vehicle truck = parkingLotService.createVehicle(VehicleType.TRUCK);
        Vehicle bike = parkingLotService.createVehicle(VehicleType.BIKE);

        //2. Create a parking lot with floors and spots
        ParkingSpot carParkingSpot = parkingLotService.createParkingSpot(SpotType.CAR);
        ParkingSpot bikeParkingSpot = parkingLotService.createParkingSpot(SpotType.BIKE);
        ParkingSpot truckParkingSpot = parkingLotService.createParkingSpot(SpotType.TRUCK);

        Floor floor1 = new Floor("F1");
        Floor floor2 = new Floor("F2");

        floor1.addParkingSpot(List.of(carParkingSpot, bikeParkingSpot, truckParkingSpot, carParkingSpot, bikeParkingSpot, bikeParkingSpot));
        floor2.addParkingSpot(List.of(carParkingSpot, truckParkingSpot, bikeParkingSpot));

        ParkingLot parkingLot = new ParkingLot.ParkingLotBuilder(2)
                .parkingFloors(List.of(floor1, floor2))
                .build();

        //3. Simulate vehicle entries
        parkingLot.displayAvailability();

//        ParkingTicket parkingTicket = parkingLotService.parkVehicle(car);








    }
}
