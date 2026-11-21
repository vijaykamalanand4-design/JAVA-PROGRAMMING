import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TokenCounter {
    private final int[] buffer = new int[5];
    private int count = 0, in = 0, out = 0;

    public synchronized void put(int token) throws InterruptedException {
        while (count == buffer.length) wait();
        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;
        notifyAll();
    }

    public synchronized int take() throws InterruptedException {
        while (count == 0) wait();
        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;
        notifyAll();
        return token;
    }

    static class Wallet {
        final int id;
        int balance;
        final ReentrantLock lock = new ReentrantLock();
        Wallet(int id, int balance) { this.id = id; this.balance = balance; }
    }

    // Lock wallets in ascending ID order to prevent circular wait.
    static void transfer(Wallet a, Wallet b, int amount) {
        Wallet first = a.id < b.id ? a : b;
        Wallet second = a.id < b.id ? b : a;
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (a.balance >= amount) {
                    a.balance -= amount;
                    b.balance += amount;
                }
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenCounter counter = new TokenCounter();
        int[] collected = new int[2];

        Thread cook = new Thread(() -> {
            try {
                for (int token = 1; token <= 20; token++) counter.put(token);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread[] students = new Thread[2];
        for (int i = 0; i < students.length; i++) {
            final int id = i;
            students[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) collected[id] += counter.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        cook.start();
        for (Thread student : students) student.start();
        cook.join();
        for (Thread student : students) student.join();

        System.out.println("Sum of tokens collected: " +
                (collected[0] + collected[1]) + " (expected 210)");

        // Standard-library producer-consumer alternative.
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        queue.put(1);
        System.out.println("ArrayBlockingQueue took: " + queue.take());
    }
}
