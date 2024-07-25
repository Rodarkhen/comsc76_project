/* Name: Alazne Ramos Flores
 * Date code was written: 07/20/2024
 * Program Description: Draft for Queue Project Model 2: n lines for customers,
 * with one checkout station per line. Customers go to the line with the fewest number
 * of customers.
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Model2Checkout {
    static class GroceryStore {
        int numStations;
        Queue<Integer>[] checkoutLines;
        Queue<Integer> stationLine;
        Random random;

        int customersServed;
        int maxLineLength;
        long totalWaitingTime;
        long startTime;


        public GroceryStore(int n) {
            numStations = n;
            stationLine = new LinkedList<>();
            checkoutLines = new Queue[numStations];
            for (int i = 0; i < numStations; i++) {
                checkoutLines[i] = new LinkedList<>();
            }
            random = new Random();
            customersServed = 0;
            maxLineLength = 0;
            totalWaitingTime = 0;
            startTime = System.currentTimeMillis();
        }

        public void assignAndCheckout() {
            if (random.nextDouble() < 0.3) {
                int customer = random.nextInt(5) + 1;
                int shortestLineIndex = findShortestLineIndex(checkoutLines);
                int shortestLineWaitTime = checkoutLines[shortestLineIndex].isEmpty() ? 0 : checkoutLines[shortestLineIndex].peek();
                totalWaitingTime += shortestLineWaitTime;
                checkoutLines[shortestLineIndex].add(shortestLineWaitTime + customer);
            }

            // Process customers at each station
            for (int i = 0; i < numStations; i++) {
                if (!checkoutLines[i].isEmpty()) {
                    int finishTime = checkoutLines[i].peek();
                    if (finishTime <= (int) (System.currentTimeMillis() - startTime) / 1000) {
                        // Customer is done
                        checkoutLines[i].poll();
                        customersServed++;
                    }
                }
            }

            int currentMaxLineLength = 0;
            for (int i = 0; i < numStations; i++) {
                currentMaxLineLength = Math.max(currentMaxLineLength, checkoutLines[i].size());
            }
            maxLineLength = Math.max(maxLineLength, currentMaxLineLength);
        }

        private int findShortestLineIndex(Queue<Integer>[] lines) {
            int minIndex = 0;
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].size() < lines[minIndex].size()) {
                    minIndex = i;
                }
            }
            return minIndex;
        }

        public int getCustomersServed() {
            return customersServed;
        }

        public int getMaxLineLength() {
            return maxLineLength;
        }

        public double getAverageWaitingTime() {
            if (customersServed == 0) {
                return 0.0;
            }
            return (double) totalWaitingTime / customersServed;
        }
    }

    public static void main(String[] args) {
        int numStations = 6;

        GroceryStore store = new GroceryStore(numStations);

        long simulationEndTime = System.currentTimeMillis() + 2 * 60 * 60 * 1000;


        while (System.currentTimeMillis() < simulationEndTime) {
            store.assignAndCheckout();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nModel 2 (n lines for customers, with one checkout station per line, shortest queue):");
        System.out.println("Number of customers served: " + store.getCustomersServed());
        System.out.println("Maximum queue length observed: " + store.getMaxLineLength());
        System.out.println("Average customer waiting time (seconds): " + store.getAverageWaitingTime());

    }
}
