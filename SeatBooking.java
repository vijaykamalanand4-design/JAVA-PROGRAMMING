import java.util.concurrent.atomic.AtomicBoolean;

public class SeatBooking {
    private final boolean[] booked;
    private int bookingsMade = 0;

    SeatBooking(int seats) { booked = new boolean[seats]; }

    // Synchronized method solution.
    public synchronized void book(int seat) {
        if (!booked[seat]) {
            booked[seat] = true;
            bookingsMade++;
        }
    }

    // Alternative synchronized-block solution.
    public void bookWithBlock(int seat) {
        synchronized (this) {
            if (!booked[seat]) {
                booked[seat] = true;
                bookingsMade++;
            }
        }
    }

    public int getBookingsMade() {
        synchronized (this) { return bookingsMade; }
    }

    public static void main(String[] args) throws InterruptedException {
        SeatBooking screen = new SeatBooking(10);
        Thread[] users = new Thread[100];

        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10;
            users[i] = new Thread(() -> screen.book(seat));
            users[i].start();
        }
        for (Thread t : users) t.join();
        System.out.println("Seats sold: " + screen.getBookingsMade());

        // AtomicBoolean compareAndSet alternative.
        AtomicBoolean[] atomicSeats = new AtomicBoolean[10];
        for (int i = 0; i < atomicSeats.length; i++)
            atomicSeats[i] = new AtomicBoolean(false);
        int successful = 0;
        for (AtomicBoolean seat : atomicSeats)
            if (seat.compareAndSet(false, true)) successful++;
        System.out.println("Atomic seats claimed: " + successful);
    }
}
