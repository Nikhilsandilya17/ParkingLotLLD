package service;

import models.ParkingSpot;
import models.ParkingTicket;
import models.Vehicle;

public interface TicketService {
    ParkingTicket issueTicket(String ticketId, Vehicle vehicle, ParkingSpot parkingSpot);

    boolean hasActiveTicket(Vehicle vehicle);

    ParkingTicket closeTicket(String ticketId);
}
