/*
*
*/


import java.util.*;
class Time {
    int hour;
    int minute;
    int second;

    // Default constructor
    public Time() {
        this(0, 0, 0);
    }

    // Parameterized constructor
    public Time(int second) {
        this.hour = 0;
        this.minute = 0;
        setSecond(second);
    }
    // Parameterized constructor
    public Time(int hour, int minute, int second) {
        setHour(hour);
        setMinute(minute);
        setSecond(second);
    }
    
    //Getter and setter methods for the attributes
    public int getHour() {
        return this.hour;
    }
    
    public void setHour(int hour) {
        this.hour = hour;
    }
    
    public int getMinute() {
        return this.minute;
    }
    
    public void setMinute(int minute) {
        // if (minute >= 60) {
        //     this.hour += minute / 60; // Add the additional hours to hour
        //     this.minute = minute % 60; // Set the minute to the remainder
        // } else if (minute >= 0 && minute < 60){
        //     this.minute = minute;
        // }
        if (minute >= 0) {
            this.hour += minute / 60; // Add the additional hours to hour
            this.minute = minute % 60; // Set the minute to the remainder
        }
    } 

    public int getSecond() {
        return this.second;
    }
    
    public void setSecond(int second) {
        // if (second >= 60) {
        //     setMinute(second / 60);
        //     this.second = second % 60; // Set the second to the remainder
        // } else if (second >= 0 && second < 60){
        //     this.second = second;
        // }

        if (second >= 0) {
            this.setMinute(second / 60); // Add the additional minutes to minutes
            this.second = second % 60; // Set the second to the remainder
        }
    }
    
    public void setTime(int hour, int minute, int second) {
        setSecond(second);
        setMinute(minute);
        setHour(hour);
    }

    @override
    public String toString() {
        return String.format("%02d:%02d:%02d", this.hour, this.minute, this.second);
    }
    
    public int toSeconds() { //takes the data in an object’s properties and combines them into a single unique integer.
        return (this.hour * 3600) + (this.minute * 60) + this.second;
    }
    
    public int[] fromNumber(int number){//takes an integer parameter and returns an array of integers that represent the properties.
       int [] arr = new int [3]; 
       arr [0] = number / 3600;     // this represents the hour
       arr [1] = (number % 3600) / 60;     // this represents the minute
       arr [2] = number % 60;       // this represents the second
       return arr;
    }

     // Method to add duration (in seconds) to this Time object
     public Time addDuration(int duration) {
        int totalSeconds = this.toSeconds() + duration;
        int[] timeComponents = fromNumber(totalSeconds);
        return new Time(timeComponents[0], timeComponents[1], timeComponents[2]);
    }
}

class Customer {
    private int numItems;
    private Time arriveTime; //arrival time of customer in checkout station in seconds 
    private int checkoutDuration; // time user needed for checkout in seconds 
    private Time finishCheckout;
    private static Random random = new Random(); // Random object for generating random numbers

    public Customer(int numItems, Time arriveTime, int checkoutDuration) {
        this.numItems = numItems;     //number will be randomized in main method
        this.arriveTime = arriveTime;
        this.checkoutDuration = checkoutDuration;
    }
    
    public int getNumItems() {
        return this.numItems;  
    }
    
    public void setNumItems(){
        this.numItems= random.nextInt(30) + 1; // Generates a number between 1 and 30 (inclusive)
    } 

    public Time getArriveTime() {
        return this.arriveTime;  
    }
    
    public void setArriveTime(Time arriveTime){
        this.arriveTime = arriveTime;
    } 

    public int getCheckoutDuration() {
        return this.checkoutDuration;  
    }

    // Setter for checkoutDuration
     public void setCheckoutDuration(int checkoutDuration) {
        this.checkoutDuration = checkoutDuration;
        this.totalTime = calculateTotalTime(); // Update totalTime
    }

    // Method to calculate total time
    private Time calculateTotalTime() {
        return this.arriveTime.addDuration(this.checkoutDuration);
    }

    // Getter for totalTime
    public Time getTotalTime() {
        return this.totalTime;
    }

    // toString method to print the attributes of Customers class
    @Override
    public String toString() {
        return String.format("Customers[numItems=%d, arriveTime=%s, checkoutDuration=%d, totalTime=%s]", 
                             numItems, arriveTime.toString(), checkoutDuration, totalTime.toString());
    }
    
}

/*  class CheckoutStation {
    private int numCustomers;       // number of customers served
    private boolean isAvailable;    // if the checkout station is available
    private double avgCustomer;     // average customer per checkout line
    private Time avgWaitingTime;    // average waiting time to get to the station
    private Time totalTime;         // 
    }
*/

class CheckoutStation {
    private Queue<Customer> queue;
    private boolean isAvailable;
    private int customersServed;
    private Time totalWaitTime;
    private Time avgWaitingTime;
    private int avgCustomer;

    // default constructor
    public CheckoutStation() {
        this.queue = new LinkedList<>();
        this.available = true;
        this.customersServed = 0;
        this.totalWaitTime = new Time();
        this.avgWaitingTime = new Time();
        this.avgCustomer = 0;
    }

    public boolean isAvailable() {
        return available;
    }

    public void busy() {
        this.available = false;
    }

    public void empty() {
        this.available = true;
    }

    public int getCustomersServed() {
        return customersServed;
    }

    public void setCustomersServed(int customersServed) {
        this.customersServed = customersServed;
    }

    public Time getTotalWaitTime() {
        return totalWaitTime;
    }

    public void setTotalWaitTime(Time totalWaitTime) {
        this.totalWaitTime = totalWaitTime;
    
    }
    
    public void serveCustomer(Customer customer, int currentTime) {
        available = false;
        int waitTime = currentTime - customer.getArriveTime().toSeconds();
        totalWaitTime += waitTime;
        customersServed++;
        simulateCheckout(customer.getCheckoutDuration());
        available = true;
    }

    public Time averageWaitingTime() {
        if (customersServed == 0) {
            return new Time();
        }
        Time temp = new Time(totalWaitTime.toSeconds() / customersServed);
        return temp;
    }

    public Queue<Customer> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Customer> queue) {
        this.queue = queue;
    }
}

class Line {
    List<CheckoutStation> stations;

    public Line(int numStations) {
        stations = new ArrayList<>();
        for (int i = 0; i < numStations; i++) {
            stations.add(new CheckoutStation());
        }
    }

    public int size() {
        return stations.size();
    }

    public CheckoutStation getStation(int index) {
        return stations.get(index);
    }
}

public class Checkout {
    // Constant variables for the models
    final int MODEL_DURATION = 2 * 60 * 60;
    
    public static void main() {
        
    }
}