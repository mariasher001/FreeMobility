package com.mariasher.freemobility;

public class CustomerAccessKey {
    public boolean queueStart;
    public String adminIdKey;
    public int removedNumber;

    public CustomerAccessKey() {
    }

    public CustomerAccessKey(boolean queueStart, String adminIdKey, int removedNumber) {
        this.queueStart = queueStart;
        this.adminIdKey = adminIdKey;
        this.removedNumber = removedNumber;
    }
}
