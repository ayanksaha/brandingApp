package com.lb.brandingApp.common.data.enums;

public enum Status {
    PENDING_APPROVAL("Pending Approval"),
    READY_TO_START("Ready To Start"),
    IN_PROGRESS("In Progress"),
    DONE("Done"),
    REJECTED("Rejected"),
    PENDING("Pending"),
    UNDER_WARRANTY("Under Warranty");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
