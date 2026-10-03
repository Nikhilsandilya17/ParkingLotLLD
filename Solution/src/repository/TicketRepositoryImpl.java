package repository;

import models.ParkingTicket;
import service.ParkingLotService;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TicketRepositoryImpl implements TicketRepository {
    private final ConcurrentHashMap<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();

    public static volatile TicketRepository ticketRepository;

    public static TicketRepository getInstance(){
        if(ticketRepository == null){
            synchronized (TicketRepositoryImpl.class){
                if(ticketRepository == null){
                    ticketRepository = new TicketRepositoryImpl();
                }
            }
        }
        return ticketRepository;
    }

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
