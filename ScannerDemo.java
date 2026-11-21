class ScannerStation extends Thread {
    private final int scripts;
    ScannerStation(String name, int scripts) {
        super(name);
        this.scripts = scripts;
    }
    public void run() {
        int count = 0;
        for (int i = 0; i < scripts; i++) {
            try { Thread.sleep(100); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            count++;
        }
        System.out.println(getName() + " scanned " + count + " scripts.");
    }
}

class ScannerTask implements Runnable {
    private final String station;
    private final int scripts;
    ScannerTask(String station, int scripts) {
        this.station = station;
        this.scripts = scripts;
    }
    public void run() {
        int count = 0;
        for (int i = 0; i < scripts; i++) {
            try { Thread.sleep(100); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            count++;
        }
        System.out.println(station + " scanned " + count + " scripts.");
    }
}

public class ScannerDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Runnable implementation:");
        Thread[] stations = new Thread[4];
        for (int i = 0; i < 4; i++) {
            stations[i] = new Thread(new ScannerTask("Station-" + (i + 1), 5));
            stations[i].start();
        }
        for (Thread t : stations) {
            System.out.println(t.getName() + " alive before join: " + t.isAlive());
            t.join();
        }
        System.out.println("All Runnable stations completed.");

        System.out.println("\\nThread subclass implementation:");
        ScannerStation[] workers = new ScannerStation[4];
        for (int i = 0; i < 4; i++) {
            workers[i] = new ScannerStation("Thread-Station-" + (i + 1), 5);
            workers[i].start();
        }
        for (ScannerStation worker : workers) worker.join();
        System.out.println("All Thread stations completed.");
    }
}
