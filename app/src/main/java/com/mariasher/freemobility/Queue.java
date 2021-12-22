package com.mariasher.freemobility;

public class Queue {
    public int currentNumber;
    public int lastNumber;
    public int customerLeft;
    public String averageTime;
    public String timeOfStart;
    public String timeOfNext;


    public Queue() {

    }
    //private constructor

    public Queue(int currentNumber, int lastNumber, int customerLeft, String averageTime,String timeOfStart, String timeOfNext) {
        this.currentNumber = currentNumber;
        this.lastNumber = lastNumber;
        this.customerLeft = customerLeft;
        this.averageTime = averageTime;
        this.timeOfStart = timeOfStart;
        this.timeOfNext = timeOfNext;
    }



}
