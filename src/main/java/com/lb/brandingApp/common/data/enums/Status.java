package com.lb.brandingApp.common.data.enums;

public enum Status {
    PENDING_APPROVAL("Pending Aprroval"), READY_TO_START("Ready To Start"), IN_PROGRESS("In Progress"), DONE("Done"), REJECTED("Rejected"), PENDING("Pending");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
