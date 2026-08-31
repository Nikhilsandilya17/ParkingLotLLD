package strategy.fee;

import enums.SpotType;
import enums.VehicleType;
import models.Vehicle;

import java.util.Map;

public class VehicleBasedFee implements FeeStrategy {
    private static final double BASE_RATE = 5.0;
    private static final Map<VehicleType, Double> rates = Map.of(
            VehicleType.CAR, 10.0,
            VehicleType.BIKE, 5.0,
            VehicleType.TRUCK, 15.0
    );


    @Override
    public double calculateCharge(Vehicle vehicle, long durationInHours) {
        return rates.getOrDefault(vehicle.getVehicleType(), 0.0) * durationInHours + BASE_RATE;
    }

}
