package com.lb.brandingApp.common.data.enums;

import java.time.Duration;

public enum TimeUnit {
    DAYS("Days"),
    WEEKS("Weeks"),
    MONTHS("Months"),
    YEARS("Years");

    private final String name;

    TimeUnit(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
