import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

class FileResult {
    final String fileName;
    final boolean success;
    final double score;
    final String message;

    FileResult(String fileName, boolean success, double score, String message) {
        this.fileName = fileName;
        this.success = success;
        this.score = score;
        this.message = message;
    }
}

public class ResultProcessor {
    static FileResult process(Path file) {
        try {
            String content = Files.readString(file).trim();
            if (content.isEmpty()) throw new IllegalArgumentException("Empty score file");
            double score = Double.parseDouble(content);
            if (score < 0 || score > 100)
                throw new IllegalArgumentException("Score must be between 0 and 100");
            return new FileResult(file.toString(), true, score, "Processed");
        } catch (IOException e) {
            return new FileResult(file.toString(), false, 0, "File read error: " + e.getMessage());
        } catch (NumberFormatException e) {
            return new FileResult(file.toString(), false, 0, "Corrupt numeric data");
        } catch (IllegalArgumentException e) {
            return new FileResult(file.toString(), false, 0, e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        int poolSize = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        Path directory = Paths.get(args.length > 1 ? args[1] : "scores");
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        Queue<FileResult> results = new ConcurrentLinkedQueue<>();
        List<Future<FileResult>> futures = new ArrayList<>();

        long start = System.nanoTime();
        try {
            try (var paths = Files.list(directory)) {
                paths.filter(Files::isRegularFile).forEach(path ->
                    futures.add(executor.submit(() -> process(path))));
            }

            for (Future<FileResult> future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException e) {
                    results.add(new FileResult("unknown", false, 0,
                            "Unexpected task failure: " + e.getCause()));
                }
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        }
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        long successful = results.stream().filter(r -> r.success).count();
        long failed = results.size() - successful;
        System.out.println("Pool size: " + poolSize);
        System.out.println("Total processed: " + results.size());
        System.out.println("Successful: " + successful);
        System.out.println("Failed: " + failed);
        System.out.println("Elapsed time (ms): " + elapsedMs);
        for (FileResult r : results)
            if (!r.success) System.out.println("FAILED " + r.fileName + ": " + r.message);
    }
}
