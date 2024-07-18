import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class oneToMany {
    private ArrayList<Register> registers;
    private Line line;
    private int numberOfRegisters = 6;
    
    public oneToMany(){
        registers = new ArrayList<>();
        for(int i = 0; i < numberOfRegisters; ++i){
            registers.add(new Register());
        }

        line = new Line();
    }

    private void run(){
        while(maxTime(registers) < 2.0){
            for (Register register : registers) {
                if (!register.occupied()){
                    Customer customer = line.push();
                    register.accept(customer);
                    register.checkOut(customer);
                }
            }
        }
    }

    private void close(){
        int totalCustomers = 0;
        for(Register register : registers){
            totalCustomers += register.customersServed;
        }
        System.out.printf("%s%d", "Total customers: ", totalCustomers);
    }
    
    public static double maxTime(ArrayList<Register> registers){
        double time = Collections.max(registers).totalTime();
        System.out.printf("Max time: %.2f\n", time);
        return time;
    }

    public static void main(String[] args) {
        oneToMany store = new oneToMany();
        store.run();
        store.close();
        
    }



}

class Customer{
    private int items;

    Customer(){
        Random random = new Random();
        this.items = random.nextInt(30);
    }

    int getItems(){
        return this.items;
    }

}

class Register implements Comparable<Register>{
    boolean occupied;
    double totalTime;
    int customersServed;
    int itemsProcessed;

    Register(){
        occupied = false;
        customersServed = 0;
        itemsProcessed = 0;
        totalTime = 0.0;
    }
    
    boolean occupied(){
        return this.occupied;
    }

    int itemsProcessed(){
        return this.itemsProcessed;
    }

    int customersServed(){
        return this.customersServed;
    }

    double totalTime(){
        return this.totalTime/60.0;    
    }

    void accept(Customer customer){
        if(!this.occupied){
            this.occupied = true;
            checkOut(customer);
        }
        else{
            System.out.println("Checkout is occupied!");
        }
    }

    void checkOut(Customer customer){
        int items = customer.getItems();
        Random random = new Random();

        for(int i = 0; i < items; i++){
            this.totalTime += 30.0*random.nextDouble();
        }
        this.customersServed++;
        this.itemsProcessed += customer.getItems();
        this.occupied = false;
    }

    @Override
    public int compareTo(Register other){
        return Double.compare(this.totalTime(), other.totalTime());
    }
}

class Line{
    Queue<Customer> line;
    Random random;
    Line(){
        this.line = new Queue<Customer>();
        this.random = new Random();
        for(int i = 0; i < 5; ++i){
            line.enqueue(new Customer());
        }
    }

    void add(Customer customer){
        line.enqueue(customer);
    }

    Customer push(){
        int genCustomers = random.nextInt(3);
        for(int i = 0; i < genCustomers; ++i){
            add(new Customer());
        }
        return line.dequeue();
    }
}
