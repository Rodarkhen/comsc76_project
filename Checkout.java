import java.util.*;

public class Checkout {
    public static void main(String[] args) {
        System.out.println("Model 1 running.....");
        CheckoutModel store1 = new Model1();
        store1.run();
        store1.close();
        System.out.println("\n==========================================================");

        System.out.println("Model 2 running........");
        CheckoutModel store2 = new Model2();
        store2.run();
        store2.close();
        System.out.println("\n==========================================================");

        System.out.println("Model 3 running........");
        CheckoutModel store3 = new Model3();
        store3.run();
        store3.close();
    }
}

abstract class CheckoutModel {
    protected boolean debugMode = false;
    // total duration that the model will operate in seconds
    final int MODEL_DURATION = 7200; // (7200 seconds = 2 hours)
    // The range of items that customers can hold
    private static final int MAX_ITEMS = 20;
    private static final int MIN_ITEMS = 10;

    protected final int MAX_PEOPLE = 4;
    protected final int MIN_PEOPLE = 1;
    protected final double PROBABILITY = 0.33;
    final int CUSTOMER_ARRIVAL_INTERVAL = 50;

    // Variables to the CheckoutModel class
    protected final ArrayList<Line> lines;
    protected final ArrayList<CheckoutStation> stations;
    protected final Random random;
    protected final int numberOfStations = 6;
    protected int time;
    protected int timeToEnd; // checkout duration
    
    private final int modelNum;
    public CheckoutModel(int modelNum) {
        lines = new ArrayList<>();
        stations = new ArrayList<>();
        random = new Random();
        timeToEnd = 0;
        this.modelNum = modelNum;

        if (this.modelNum == 1) {
            lines.add(new Line());
            for (int i = 1; i <= numberOfStations; ++i) {
                stations.add(new CheckoutStation(i));
            }
        } else if (this.modelNum > 1) {
            for (int i = 1; i <= numberOfStations; ++i) {
                lines.add(new Line(i));
                stations.add(new CheckoutStation(i));
            }
        }
    }

    public void run() {
        int maxCustomers = getTotalCustomers();
        // For loop is our clock that runs every second (7200 seconds = 2 hours)
        System.out.printf("Store is opening (%s)\n", Time(time));
        System.out.println("..........");
        for (time = 0; time < MODEL_DURATION; ++time) {
            // Every 50 seconds call genCustomer() which has a 1/3 chance of generating 1 to
            // 3 customers
            if (time % CUSTOMER_ARRIVAL_INTERVAL == 0) {
                genCustomer();
            }
            if (getTotalCustomers() > maxCustomers) {
                maxCustomers = getTotalCustomers();
            }
            // look for a register that is not occupied and checkout a customer at that register
            
            if(modelNum == 1){
                for (CheckoutStation station : stations) { // ATTENTION
                    timeToEnd += station.checkout(lines, time);
                }
            }
            else if (modelNum > 1){
                for (int i = 0; i < numberOfStations; ++i){
                    timeToEnd += stations.get(i).checkout(lines.get(i), time);
                }
            }
        }
        System.out.printf("Store is closing! (%s)\n", Time(time));
        System.out.printf("Max queue length: %d\n", maxCustomers);
    }

    protected abstract void genCustomer();

    public void close() {
        int customersServed = 0;
        System.out.println("---------------------------------------------------------");
        System.out.println("Station \tCustomers Served\tAvg Waiting Time");
        System.out.println("---------------------------------------------------------");
        for (CheckoutStation station : stations) {
            customersServed += station.getCustomersServed();
            System.out.printf("%4d\t  %14d\t\t  %10s\n", station.getId(), station.getCustomersServed(),
                    Time(station.getTotalTimeSpent()
                            / (station.getCustomersServed() == 0 ? 1 : station.getCustomersServed())));
        }
        System.out.println("---------------------------------------------------------");
        System.out.printf("Served %d customers\n", customersServed);
        System.out.printf("Average waiting time: %s\n", Time(timeToEnd / (customersServed == 0 ? 1 : customersServed)));
    }

    protected String Time(int seconds) {
        int minutes = seconds / 60;
        int hours = 0;
        seconds %= 60;
        if (minutes >= 60) {
            hours = minutes / 60;
            minutes %= 60;
        }

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // ATTENTION
    protected int getTotalCustomers() {
        int totalCustomers = 0;
        for (Line line : lines) {
            totalCustomers += line.getCustomers();
        }
        return totalCustomers;
    }

    class Customer {
        private final int numItems;
        private final int timeStart;
        private final Random random;

        Customer(int time) {
            this.random = new Random();
            this.timeStart = time;
            this.numItems = MIN_ITEMS + this.random.nextInt(MAX_ITEMS);
        }

        int getItems() {
            return this.numItems;
        }

        int timeSpentInLine(int time) {
            return time - this.timeStart;
        }
    }

    class CheckoutStation {
        private final int id;
        private int customersServed;
        private int totalTimeSpent;
        private boolean isBusy; // checkout station is busy
        private int waitingTime;
        private final int SCAN_TIME = 10; 
        private final int PAY_TIME = 20;

        CheckoutStation(int id) {
            this.isBusy = false;
            this.id = id;
            this.waitingTime = 0;
            this.totalTimeSpent = 0;
        }

        boolean getIsBusy() {
            return this.isBusy;
        }

        int checkout(ArrayList<Line> lines, int currentTime) {
            if (currentTime >= waitingTime) {
                this.isBusy = false;
            }
            
            if (!getIsBusy() && !lines.isEmpty()) {
                for (Line line : lines) {
                    if (!line.isEmpty()) {
                        Customer customer = line.pop();
                        int checkOutTime = (SCAN_TIME * customer.getItems()) + PAY_TIME;
                        this.isBusy = true;
                        this.waitingTime = currentTime + checkOutTime;
                        this.totalTimeSpent += customer.timeSpentInLine(waitingTime);
                        customersServed++;
                        
                        if (debugMode)
                            System.out.printf(
                                    "Processing customer with %d items at register %d (%s). Will reopen @ %s\n",
                                    customer.getItems(), this.id, Time(currentTime), Time(waitingTime));
                        
                        return customer.timeSpentInLine(waitingTime);
                    }
                }
            }
            return 0;
        }

        int checkout(Line line, int currentTime) {
            if (currentTime >= waitingTime) {
                this.isBusy = false;
            }
            
            if (!getIsBusy() && !lines.isEmpty()) {
                if (!line.isEmpty()) {
                    Customer customer = line.pop();
                    int checkOutTime = (SCAN_TIME * customer.getItems()) + PAY_TIME;
                    this.isBusy = true;
                    this.waitingTime = currentTime + checkOutTime;
                    this.totalTimeSpent += customer.timeSpentInLine(waitingTime);
                    customersServed++;
                    
                    if (debugMode)
                        System.out.printf(
                                "Processing customer from line %d with %d items at register %d (%s). Will reopen @ %s\n",
                                line.getID(), customer.getItems(), this.id, Time(currentTime), Time(waitingTime));
                    
                    return customer.timeSpentInLine(waitingTime);
                }
            }
            return 0;
        }
        int getCustomersServed() {
            return this.customersServed;
        }

        int getTotalTimeSpent() {
            return this.totalTimeSpent;
        }

        int getId() {
            return this.id;
        }
    }

    class Line {
        private final Queue<Customer> line;
        private final int id;
        Line(){
            line = new Queue<>();
            this.id = 1;
        }
        Line(int i) {
            line = new Queue<>();
            this.id = i;
        }

        void add(Customer customer) {
            line.enqueue(customer);
        }

        Customer pop() {
            return line.dequeue();
        }

        boolean isEmpty() {
            return line.size() == 0;
        }

        int getCustomers() {
            return line.size();
        }
        int getID(){
            return this.id;
        }
    }
}

class Model1 extends CheckoutModel {
    public Model1() {
        super(1);
    }

    @Override
    public void genCustomer() {
        int numberOfCustomers = MIN_PEOPLE + random.nextInt(MAX_PEOPLE); // Anywhere from 0 to 3 people enter the store
        if (random.nextDouble() < PROBABILITY) {
            for (int i = 0; i < numberOfCustomers; ++i) {
                lines.get(0).add(new Customer(this.time));
            }
            if (debugMode) {
                if (numberOfCustomers > 1) {
                    System.out.printf("%d customers queued up in line (%s)\n", numberOfCustomers, Time(time));
                } else if (numberOfCustomers == 1) {
                    System.out.printf("Customer queued up in line (%s)\n", Time(time));
                }
            }
        }
    }
}

class Model2 extends CheckoutModel {
    public Model2() {
        super(2);
    }

    @Override
    public void genCustomer() {
        int numberOfCustomers = MIN_PEOPLE + random.nextInt(MAX_PEOPLE); // Anywhere from 1 to 4 people enter the store
        if (random.nextDouble() < PROBABILITY) {
            for (int i = 0; i < numberOfCustomers; ++i) {
                // Find the line with the fewest number of customers
                Line shortestLine = lines.get(0);
                for (Line line : lines) {
                    if (line.getCustomers() < shortestLine.getCustomers()) {
                        shortestLine = line;
                    }
                }
                shortestLine.add(new Customer(this.time));
            }
            if (debugMode) {
                if (numberOfCustomers > 1) {
                    System.out.printf("%d customers queued up in line (%s)\n", numberOfCustomers, Time(time));
                } else if (numberOfCustomers == 1) {
                    System.out.printf("Customer queued up in line (%s)\n", Time(time));
                }
            }
        }
    }
}

class Model3 extends CheckoutModel {
    public Model3() {
        super(3);
    }

    @Override
    public void genCustomer() {
        int numberOfCustomers = MIN_PEOPLE + random.nextInt(MAX_PEOPLE); // Anywhere from 0 to 3 people enter the store

        if (random.nextDouble() < PROBABILITY) {
            for (int i = 0; i < numberOfCustomers; ++i) {
                int randomLineIndex = random.nextInt(numberOfStations);
                lines.get(randomLineIndex).add(new Customer(this.time));
            }
            if (debugMode) {
                if (numberOfCustomers > 1) {
                    System.out.printf("%d customers queued up in line (%s)\n", numberOfCustomers, Time(time));
                } else if (numberOfCustomers == 1) {
                    System.out.printf("Customer queued up in line (%s)\n", Time(time));
                }
            }
        }
    }
}

class Queue<T> {
    /*
     * The tail of the queue is at the beginning
     * of the ArrayList; the head is the last item
     */
    ArrayList<T> items;

    /*
     * Create a new Queue
     */
    public Queue() {
        this.items = new ArrayList<>();
    }

    /*
     * Returns true if there are no items in the queue;
     * false otherwise.
     */
    public boolean isEmpty() {
        return (this.items.isEmpty());
    }

    /*
     * Add an item to the tail of the queue
     */
    public void enqueue(T item) {
        this.items.add(0, item);
    }

    /*
     * Remove the item at the head of the queue and return it.
     * If the queue is empty, throws an exception.
     */
    public T dequeue() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return this.items.remove(this.size() - 1);
    }

    /*
     * Return the item at the head of the queue, but do not remove it.
     * If the queue is empty, throws an exception.
     */
    public T peek() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return this.items.get(this.size() - 1);
    }

    /*
     * Returns the number of items in the queue.
     */
    public int size() {
        return this.items.size();
    }

    /*
     * Convert to string as an array from tail to head
     */
    @Override
    public String toString() {
        if (!this.items.isEmpty()) {
            String arrString = this.items.toString();
            return "tail ->" + arrString + "-> head";
        } else {
            return "<<empty queue>>";
        }
    }
}