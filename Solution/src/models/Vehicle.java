package models;

import enums.VehicleType;

import java.util.UUID;

public class Vehicle {
    private final String licenseNumber;
    private final VehicleType vehicleType;

    public Vehicle(VehicleType vehicleType) {
        this.licenseNumber = UUID.randomUUID().toString();
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}
