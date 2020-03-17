package parking.model;

public class Ticket {
    private int numberTicket;

    public Ticket(int numberTicket) {
        this.numberTicket = numberTicket;
    }

    public int getNumberTicket() {
        return numberTicket;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "numberTicket=" + numberTicket +
                '}';
    }
}
