import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

/*
  MISSING:
  - AVG WAITING TIME
  - THE 2 remaining models
*/
public class OneToN{
    public static void main(String[] args) {
        OneToN store = new OneToN();
        store.run();
        store.close();
    }
    private final Line line;
    private final ArrayList<CheckoutStation> stations;
    private final Random random;
    private final int numberOfStations = 6;
    
    public OneToN(){
        line = new Line();
        random = new Random();
        stations = new ArrayList<>();

        for(int i = 1; i <= numberOfStations; ++i){
            stations.add(new CheckoutStation(i));
        }
    }

    public void run(){
        final int MODEL_DURATION = 7200; // (7200 seconds = 2 hours)
        final int CUSTOMER_FREQ = 50;
        
        int maxCustomers = line.getCustomers();
        // For loop is our clock that runs every second (7200 seconds = 2 hours)
        for(int i = 0; i < MODEL_DURATION; ++i){
            // Every 50 seconds call genCustomer() which has a 1/3 chance of generating 1 to 3 customers
            if(i % CUSTOMER_FREQ == 0){
                genCustomer();
            }
            if(line.getCustomers() > maxCustomers){
                maxCustomers = line.getCustomers();
            }
            // look for a register that is not occupied and checkout a customer at that register
            for(CheckoutStation station : stations){
                if(!line.isEmpty()){
                    station.checkout(line.pop(), i);
                }
            }

        }
        System.out.println("Store is closing!");
        System.out.printf("Max queue length: %d\n", maxCustomers);
    }

    public void close(){
        int customersServed = 0;
        for (CheckoutStation station : stations){
            customersServed += station.getCustomersServed();
        }
        System.out.printf("Served %d customers\n", customersServed);
    }
    
    private void genCustomer() {
        final int MAX_PEOPLE = 4;
        final int MIN_PEOPLE = 1;
        final double PROBABILITY = 0.33;

        int numberOfCustomers = MIN_PEOPLE + random.nextInt(MAX_PEOPLE); // Anywhere from 0 to 3 people enter the store
        if(random.nextDouble() < PROBABILITY){
            for(int i = 0; i < numberOfCustomers; ++i){
                line.add(new Customer());
                System.out.println("Customer queued up in line");
            }
        }
    }

    class Customer{
        private int numItems;

        private static final int MAX_ITEMS = 20;
        private static final int MIN_ITEMS = 10;
        
        Customer(){
            //Random random = new Random();
            this.numItems = MIN_ITEMS + random.nextInt(MAX_ITEMS);
        }

        int getItems(){
            return this.numItems;
        }
    }

    class CheckoutStation{
        private final int id;
        private int customersServed;
        private boolean isBusy; //checkout station is busy
        private int waitingTime; 
        /* Clock time that register will be open at 
        * example: Customer joins register at 50 with 1 item
        *           register will be ready at 60 <-- waitingTime
        */

        CheckoutStation(int id){
            this.isBusy = false;
            this.id = id;
            waitingTime = 0;
        }
        
        boolean getIsBusy(){
            return this.isBusy;
        }

        void checkout(Customer customer, int currentTime){
            int checkOutTime = 10 * customer.getItems();
            if(currentTime >= waitingTime){
                this.isBusy = false;
            }

            if(!getIsBusy()){
                this.isBusy = true;
                this.waitingTime = currentTime + checkOutTime;
                customersServed++;
                System.out.printf("Processing customer with %d items at register %d. Will reopen in %.2f minutes\n", customer.getItems(), this.id, checkOutTime / 60.0);
            } else {
                System.out.printf("Register %d is occupied!\n", this.id);
            }
        }

        int getCustomersServed(){
            return this.customersServed;
        }

    }

    class Line{
        private final Queue<Customer> line;

        Line(){
            line = new Queue<>();
        }

        void add(Customer customer){
            line.enqueue(customer);
        }

        Customer pop(){
            return line.dequeue();
        }

       boolean isEmpty(){
            return line.size() == 0;
        }

        int getCustomers(){
            return line.size();
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
        this.items = new ArrayList<T>();
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
    public String toString() {
        
        if (!this.items.isEmpty()) {
            String arrString = this.items.toString();
            return "tail ->" + arrString + "-> head";
        } else {
            return "<<empty queue>>";
        }
    }
}

}