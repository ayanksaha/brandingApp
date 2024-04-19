package com.lb.brandingApp.common.data.enums;

import lombok.Getter;
import lombok.experimental.Accessors;

public enum UOM {
    EACH("Each"), FEET("Feet"), SQUARE_FEET("Square Feet");

    @Getter
    @Accessors(fluent = true)
    private final String description;

    UOM(String uomDescription) {
        this.description = uomDescription;
    }
}
