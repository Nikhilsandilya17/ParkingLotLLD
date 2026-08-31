package factory;

import enums.VehicleType;
import models.Bike;
import models.Car;
import models.Truck;
import models.Vehicle;

public class VehicleFactory {

    public static Vehicle createVehicle(VehicleType type) {
        if(type == VehicleType.CAR) {
            return new Car();
        }
        else if(type == VehicleType.TRUCK) {
            return new Truck();
        }
        else if(type == VehicleType.BIKE) {
            return new Bike();
        }
        return null;
    }
}
