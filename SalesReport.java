import java.io.*;
import java.util.*;

public class SalesReport {
    // Robust version: try-with-resources.
    static void generateReport(String[] inputFiles, String outputFile) throws IOException {
        long total = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String file : inputFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            total += Long.parseLong(line.trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number in " + file + ": " + line);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Could not read " + file + ": " + e.getMessage());
                }
            }
            writer.write("Total sales: " + total);
            writer.newLine();
        }
    }

    // Comparison version: explicit finally close.
    static void generateReportManual(String[] inputFiles, String outputFile) throws IOException {
        long total = 0;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            for (String file : inputFiles) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            total += Long.parseLong(line.trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number in " + file + ": " + line);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Could not read " + file + ": " + e.getMessage());
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { System.err.println("Reader close failed: " + e); }
                    }
                }
            }
            writer.write("Total sales: " + total);
            writer.newLine();
        } finally {
            if (writer != null) {
                try { writer.close(); }
                catch (IOException e) { System.err.println("Writer close failed: " + e); }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java SalesReport input1.txt input2.txt ...");
            return;
        }
        generateReport(args, "summary.txt");
        System.out.println("Report generated using try-with-resources.");
    }
}
