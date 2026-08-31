package factory;

import enums.SpotType;
import models.ParkingSpot;

public class ParkingSpotFactory {
    public static ParkingSpot createParkingSpot(SpotType spotType) {
        return switch (spotType) {
            case CAR -> new ParkingSpot(SpotType.CAR);
            case BIKE -> new ParkingSpot(SpotType.BIKE);
            case TRUCK -> new ParkingSpot(SpotType.TRUCK);
            default -> null;
        };
    }
}
