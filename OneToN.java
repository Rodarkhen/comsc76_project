import java.util.ArrayList;
import java.util.Random;

public class OneToN{
    public static void main(String[] args) {
        OneToN store = new OneToN();
        store.run();
        store.close();
    }
    private final Line line;
    private final ArrayList<Register> registers;
    private final Random random;
    private final int numberOfRegisters = 6;
    
    public OneToN(){
        line = new Line();
        random = new Random();
        registers = new ArrayList<>();

        for(int i = 0; i < numberOfRegisters; ++i){
            registers.add(new Register(i+1));
        }
    }

    public void run(){
        // For loop is our clock that runs every second (7200 seconds = 2 hours)
        for(int i = 0; i < 7200; ++i){
            // Every 50 seconds call genCustomer() which has a 1/3 chance of generating 1 to 3 customers
            if(i % 50 == 0){
                genCustomer();
            }
            // look for a register that is not occupied and checkout a customer at that register
            // should break if the line is empty or the registers are full
            for(Register register : registers){
                if(!line.isEmpty()){
                    register.checkout(line.pop(), i);
                }
            }

        }
        System.out.println("Store is closing!");
    }

    public void close(){
        int customersServed = 0;
        for (Register register : registers){
            customersServed += register.getCustomersServed();
        }
        System.out.printf("Served %d customers", customersServed);
    }
    
    private void genCustomer(){
        int numberOfCustomers = 1 + random.nextInt(4); // Anywhere from 0 to 3 people enter the store
        if(random.nextDouble() < 0.33){
            for(int i = 0; i < numberOfCustomers; ++i){
                line.add(new Customer());
                System.out.println("Customer queued up in line");
            }
        }
    }

 

    class Customer{
        private final int items;

        Customer(){
            Random random = new Random();
            this.items = 10 + random.nextInt(20);
        }

        int getItems(){
            return this.items;
        }
    }

    class Register{
        private boolean occupied;
        private int readyTime;
        private int customersServed;
        private final int id;
        Register(int id){
            this.occupied = false;
            this.id = id;
            readyTime = 0;
        }
        boolean occupied(){
            return this.occupied;
        }

        void checkout(Customer customer, int timeAccepted){
            int checkOutTime = 10*customer.getItems();
            if(timeAccepted >= readyTime){
                this.occupied = false;
            }

            if(!occupied()){
                this.occupied = true;
                this.readyTime = timeAccepted + checkOutTime;
                customersServed++;
                System.out.printf("Processing customer with %d items at register %d. Will reopen in %.2f minutes\n", customer.getItems(), this.id, checkOutTime / 60.0);
            }
            else{
                System.out.printf("Register %d is occupied!\n", this.id);
            }
        }

        int getCustomersServed(){
            return this.customersServed;
        }

    }

    class Line{
        private final Queue<Customer> line;
        private int customers;

        Line(){
            line = new Queue<>();
            customers = 0;
        }

        void add(Customer customer){
            line.enqueue(customer);
            customers++;
        }

        Customer pop(){
            customers--;
            return line.dequeue();
        }

       boolean isEmpty(){
            return line.size() == 0;
        }
    }
}