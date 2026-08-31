package strategy.fee;

import models.Vehicle;

public class RegularFee implements FeeStrategy {
    private static final double BASE_RATE = 10.0;

    @Override
    public double calculateCharge(Vehicle vehicle, long durationInHours) {
        return BASE_RATE * durationInHours;
    }
}
