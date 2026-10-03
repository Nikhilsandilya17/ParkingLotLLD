package service;

import models.ParkingSpot;
import models.ParkingTicket;
import models.Vehicle;
import repository.TicketRepository;

public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public ParkingTicket issueTicket(String ticketId, Vehicle vehicle, ParkingSpot parkingSpot) {
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, parkingSpot);
        ticketRepository.save(ticket);
        return ticket;
    }

    @Override
    public boolean hasActiveTicket(Vehicle vehicle) {
        return ticketRepository.findAll().stream()
                .anyMatch(ticket -> ticket.getVehicle() == vehicle);
    }

    @Override
    public ParkingTicket closeTicket(String ticketId) {
        ParkingTicket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Invalid Ticket");
        }
        ticket.closeTicket();
        return ticketRepository.remove(ticketId);
    }
}
