package strategy.fee;

import models.Vehicle;

public interface FeeStrategy {
    double calculateCharge(Vehicle vehicle, long durationInHours);

    void pay(double parkingCharge);
}
