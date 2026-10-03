package service;

import models.ParkingTicket;
import models.Vehicle;
import repository.TicketRepository;

public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public ParkingTicket issueTicket(String ticketId, Vehicle vehicle, models.ParkingSpot parkingSpot) {
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, parkingSpot);
        ticketRepository.save(ticket);
        return ticket;
    }

    public boolean hasActiveTicket(Vehicle vehicle) {
        return ticketRepository.findAll().stream()
                .anyMatch(ticket -> ticket.getVehicle() == vehicle);
    }

    public ParkingTicket closeTicket(String ticketId) {
        ParkingTicket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Invalid Ticket");
        }
        ticket.closeTicket();
        return ticketRepository.remove(ticketId);
    }
}
