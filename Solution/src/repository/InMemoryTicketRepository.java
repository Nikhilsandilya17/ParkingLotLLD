package repository;

import models.ParkingTicket;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTicketRepository implements TicketRepository {
    private final ConcurrentHashMap<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();

    @Override
    public void save(ParkingTicket ticket) {
        activeTickets.put(ticket.getId(), ticket);
    }

    @Override
    public ParkingTicket findById(String ticketId) {
        return activeTickets.get(ticketId);
    }

    @Override
    public ParkingTicket remove(String ticketId) {
        return activeTickets.remove(ticketId);
    }

    @Override
    public Collection<ParkingTicket> findAll() {
        return activeTickets.values();
    }
}
