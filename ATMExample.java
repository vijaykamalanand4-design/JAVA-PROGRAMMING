import java.io.*;
import java.util.*;

class InsufficientFundsException extends Exception {
    InsufficientFundsException(String message) { super(message); }
}

public class ATMExample {
    private static double balance = 5000.0;

    private static void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient balance");
        balance -= amount;
        System.out.println("Cash dispensed: Rs." + amount);
        System.out.println("Remaining balance: Rs." + balance);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Simulate the card-reader configuration file.
            try (FileInputStream card = new FileInputStream("card-reader.txt")) {
                System.out.println("Card reader ready.");
            } catch (FileNotFoundException e) {
                System.out.println("Card-reader file missing: " + e.getMessage());
                return;
            }

            System.out.print("Enter account number: ");
            String account = scanner.nextLine().trim();
            if (account.isEmpty()) throw new IllegalArgumentException("Account number is required");

            System.out.print("Enter withdrawal amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            withdraw(amount);

        } catch (NumberFormatException | InputMismatchException e) {
            System.out.println("Invalid numeric input.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid request: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction declined: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Card-reader error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("ATM session ended. Please collect your card.");
            scanner.close();
        }
    }
}
