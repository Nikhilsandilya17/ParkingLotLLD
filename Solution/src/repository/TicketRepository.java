package repository;

import models.ParkingTicket;

import java.util.Collection;

public interface TicketRepository {
    void save(ParkingTicket ticket);

    ParkingTicket findById(String ticketId);

    ParkingTicket remove(String ticketId);

    Collection<ParkingTicket> findAll();
}
