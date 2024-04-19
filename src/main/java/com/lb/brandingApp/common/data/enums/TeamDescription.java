package com.lb.brandingApp.common.data.enums;

import lombok.Getter;
import lombok.experimental.Accessors;

public enum TeamDescription {
    DEVELOPER("Developer"),
    SYS_ADMIN("System Admin"),
    ADMIN("Admin"),
    HOD("Head Of The Department"),
    APPROVAL("Approval"),
    DESIGNING("Designing"),
    PRINTING("Printing"),
    FRAMING("Framing"),
    DISPATCH("Dispatch"),
    INSTALLATION("Installation"),
    VERIFICATION("Verification");

    @Getter
    @Accessors(fluent = true)
    private final String description;

    TeamDescription(String teamDescription) {
        this.description = teamDescription;
    }

}
