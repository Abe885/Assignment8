package com.coderscampus.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiThreadedApp {
    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(24);
        ConcurrentHashMap<Integer, Integer> uniqueNumberOccurrences = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> numberProcessFutures = new ArrayList<>();

        String message = "App Starting";
        System.out.println(message);
        System.out.println("Using Thread-" + Thread.currentThread().getName());

        Assignment8 dataProcessor = new Assignment8();

        for (int i = 0; i < 1000; i++) {
            CompletableFuture<Void> numbersTask = CompletableFuture.supplyAsync(() -> dataProcessor.getNumbers(), executorService)
                    .thenAcceptAsync(numbers -> {
                        System.out.println("Current Thread: " + Thread.currentThread().getName());
                        for (Integer number : numbers) {
                            uniqueNumberOccurrences.compute(number, (key, val) -> (val == null) ? 1 : val + 1);
                        }
                    }, executorService)
                    .exceptionally(e -> {
                        System.out.println("Error in CompletableFuture: " + e.getMessage());
                        return null;
                    });
            numberProcessFutures.add(numbersTask);
        }
        CompletableFuture<Void> allDone = CompletableFuture.allOf(numberProcessFutures.toArray(new CompletableFuture[0]));
        allDone.join();

        executorService.shutdown();

        System.out.println("Size of numberCounts: " + uniqueNumberOccurrences.size());
        uniqueNumberOccurrences.forEach((key, value) -> System.out.println(key + " = " + value));
    }
}