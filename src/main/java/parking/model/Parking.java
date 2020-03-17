package parking.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Parking {
    //кол-во мест
    private int countPlace;
    //список машин
    private Hashtable<Ticket, Car> cars;
    //список билетов
    private List<Ticket> tickets;

    public Parking(int countPlace) {
        this.countPlace = countPlace;
        cars = new Hashtable<>();
        tickets = new ArrayList<>();
        for (int i = 0; i < countPlace; i++) {
            int numbTicket = Integer.parseInt(RandomStringUtils.randomNumeric(3));
            tickets.add(new Ticket(numbTicket));
        }
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
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

    //кол-во оставшихся мест на парковке
    public int countRemainingPlace() {
        return countPlace - cars.size();
    }

    /**
     * Въезд на парковку
     */
    public synchronized void parkingEntrance(Car car) {
        if (countRemainingPlace() == 0) {
            try {
//                System.out.println("Извините,парковка заполнена.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Ticket t = tickets.get(tickets.size() - 1);
        cars.put(t, car);
        tickets.remove(t);
//        System.out.println("Машина " + car.getNumberCar() + " въехала на парковку  с билетом " + t.getNumberTicket());
//        notify();
    }

    /**
     * Выезд с парковки
     */
    public synchronized void parkingExit(int numberTicket) {
        Set<Ticket> ticketsTmp = cars.keySet();
        Ticket ticketFind = null;
        for (Ticket t : ticketsTmp) {
            if (t.getNumberTicket() == numberTicket) {
                ticketFind = t;
            }
        }
        if (ticketFind != null) {
            tickets.add(ticketFind);
            Car car = cars.get(ticketFind);
            cars.remove(ticketFind);
//            System.out.println("Машина " + car.getNumberCar() + " выехала с парковки! Был билет: " + ticketFind.getNumberTicket());
            notify();
        } else {
            System.out.println("\nНет машин с номером билета: " + numberTicket);
        }
    }
}
