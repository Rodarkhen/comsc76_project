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

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", this.hour, this.minute, this.second);
    }
    
    public int toSeconds() { //takes the data in an object’s properties and combines them into a single unique integer.
        return (this.hour * 3600) + (this.minute * 60) + this.second;
    }
    
    public static Time fromSeconds(int totalSeconds) {
        int hour = totalSeconds / 3600;
        int minute = (totalSeconds % 3600) / 60;
        int second = totalSeconds % 60;
        return new Time(hour, minute, second);
    }

    public void addSeconds(int seconds) {
        int totalSeconds = this.toSeconds() + seconds;
        Time newTime = Time.fromSeconds(totalSeconds);
        this.setTime(newTime.getHour(), newTime.getMinute(), newTime.getSecond());
    }
}

class Customer {
    private int numItems;
    private Time arriveTime; //arrival time of customer in to queue for checkout station 
    private int checkoutDuration; // time user needed for checkout in seconds 
    private Time finishCheckoutTime;
    private static Random random = new Random(); // Random object for generating random numbers

    private static final int TIME_PER_ITEM = 5; // Time to scan each item (seconds)
    private static final int PAYMENT_TIME = 30; // Time to pay (seconds)
    private static final int MAX_ITEMS = 30;
    private static final int MIN_ITEMS = 1;

    public Customer(int arriveTimeInSeconds) {
        this.numItems = random.nextInt(MAX_ITEMS) + MIN_ITEMS;     //number will be randomized in main method
        this.arriveTime = Time.fromSeconds(arriveTimeInSeconds);
        this.checkoutDuration = numItems * TIME_PER_ITEM + PAYMENT_TIME;
    }
    
    /*public Customer(int numItems, Time arriveTime, int checkoutDuration) {
        this.numItems = numItems;     //number will be randomized in main method
        this.arriveTime = arriveTime;
        this.checkoutDuration = checkoutDuration;
    }
    */
    
    public int getNumItems() {
        return this.numItems;  
    }
    
   /* public void setNumItems(){
        this.numItems= random.nextInt(30) + 1; // Generates a number between 1 and 30 (inclusive)
    }*/

    public Time getArriveTime() {
        return this.arriveTime;  
    }
    
    /*public void setArriveTime(Time arriveTime){
        this.arriveTime = arriveTime;
    } */ 

    public int getCheckoutDuration() {
        return this.checkoutDuration;  
    }

    /*// Setter for checkoutDuration
     public void setCheckoutDuration(int checkoutDuration) {
        this.checkoutDuration = checkoutDuration;
        this.totalTime = calculateTotalTime(); // Update totalTime
    }
        */

    /*// Method to calculate total time
    private Time calculateTotalTime() {
        return this.arriveTime.addDuration(this.checkoutDuration);
    }

    // Getter for totalTime
    public Time getTotalTime() {
        return this.totalTime;
    }
        */

    public void setFinishCheckoutTime(Time finishCheckoutTime) {
        this.finishCheckoutTime = finishCheckoutTime;
    }

    public Time getFinishCheckoutTime() {
        return finishCheckoutTime;
    }
    

    // toString method to print the attributes of Customers class
    @Override
    public String toString() {
        return String.format("Customers[numItems=%d, arriveTime=%s, checkoutDuration=%d, finishCheckoutTime=%s]", 
                             numItems, arriveTime.toString(), checkoutDuration, finishCheckoutTime.toString());
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
    private int totalWaitTime;
    private int maxQueueLength;

    // default constructor
    public CheckoutStation() {
        this.queue = new LinkedList<>();
        this.isAvailable = true;
        this.customersServed = 0;
        this.totalWaitTime = 0;
        this.maxQueueLength = 0;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void busy() {
        this.isAvailable = false;
    }

    public void empty() {
        this.isAvailable = true;
    }

    public int getCustomersServed() {
        return customersServed;
    }

    public void setCustomersServed(int customersServed) {
        this.customersServed = customersServed;
    }

    public int getTotalWaitTime() {
        return totalWaitTime;
    }

    public void setTotalWaitTime(int totalWaitTime) {
        this.totalWaitTime = totalWaitTime;
    
    }
    
    public void serveCustomer(Customer customer, int currentTimeInSeconds) {
        isAvailable = false;
        int waitTime = currentTimeInSeconds - customer.getArriveTime().toSeconds();
        totalWaitTime += waitTime;
        customersServed++;
        customer.setFinishCheckoutTime(Time.fromSeconds(currentTimeInSeconds + customer.getCheckoutDuration()));
        queue.add(customer);
        maxQueueLength = Math.max(maxQueueLength, queue.size());
        isAvailable = true;
    } 

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public void addCustomerToQueue(Customer customer) {
        queue.add(customer);
        maxQueueLength = Math.max(maxQueueLength, queue.size());
    }

    public void processQueue(int currentTimeInSeconds) {
        if (!queue.isEmpty() && isAvailable) {
            Customer customer = queue.poll();
            serveCustomer(customer, currentTimeInSeconds);
        }
    }
 /////////////////our code , but optional//////////////////////////////
    public Time averageWaitingTime() {
        if (customersServed == 0) {
            return new Time();
        }
        Time temp = new Time(totalWaitTime / customersServed);
        return temp;
    }

    public Queue<Customer> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Customer> queue) {
        this.queue = queue;
    }
    ///////////////////////////////////////////////////
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



/** 
 *  
 *  High Possibility of adding another new class called storeModel: 
 * a class that we do and tests different models being asked.
 * 
 */


public class Checkout {
    // Constant variables for the models
    final int MODEL_DURATION = 2 * 60 * 60;
    
    public static void main() {
        
    }
}