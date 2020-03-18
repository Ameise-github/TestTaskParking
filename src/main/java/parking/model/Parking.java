package parking.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class Parking {
    //кол-во мест
    private int countPlace;
    //список машин
    private Hashtable<Ticket, Car> cars;
    //список билетов
    private Vector<Ticket> tickets;
    //Время въезда машины на парковку
    private long countSec;

    public Parking(int countPlace, long countSec) {
        this.countPlace = countPlace;
        this.countSec = countSec;
        cars = new Hashtable<>();
        tickets = new Vector<>();
        for (int i = 0; i < countPlace; i++) {
            int numbTicket = Integer.parseInt(RandomStringUtils.randomNumeric(3));
            tickets.add(new Ticket(numbTicket));
        }
    }

    public void setTickets(Vector<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Vector<Ticket> getTickets() {
        return tickets;
    }

    public void getCarsPrint() {
        System.out.printf("%5s %8s %8s \n", "№ п/п", "Номер машины", "Номер билета");

        int i = 1;
        for (Entry<Ticket, Car> m : cars.entrySet()) {
            System.out.printf("%-5s %10s %10s\n", i, m.getValue().getNumberCar(), m.getKey().getNumberTicket());
            i++;
        }
    }

    public Hashtable<Ticket, Car> getCars() {
        return cars;
    }

    /**
     * кол-во оставшихся мест на парковке
     */
    public int countRemainingPlace() {
        return countPlace - cars.size();
    }

    /**
     * Въезд на парковку
     */
    public synchronized boolean parkingEntrance(Car car) {
        if (countRemainingPlace() != 0) {
            try {
                Ticket t = tickets.get(tickets.size() - 1);
                tickets.remove(t);
                cars.put(t, car);
//                System.out.println("Car " + car.getNumberCar() + " enter, get ticket " + t.getNumberTicket());
                TimeUnit.SECONDS.sleep(countSec);
            } catch (InterruptedException e) {
                System.err.println("Задача прервана. error= ");
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Выезд с парковки
     */
    public void parkingExit(Ticket ticket) {
        tickets.add(ticket);
        cars.remove(ticket);
//        System.out.println("Car exit, the ticket number was " + ticket.getNumberTicket());
    }

    /**
     * проверка номера билета
     */
    public Ticket checkedTicket(int numberTicket) {
        Set<Ticket> ticketsTmp = cars.keySet();
        for (Ticket t : ticketsTmp) {
            if (t.getNumberTicket() == numberTicket) {
                return t;
            }
        }
        return null;
    }
}
