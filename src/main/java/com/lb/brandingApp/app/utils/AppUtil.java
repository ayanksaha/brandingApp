package com.lb.brandingApp.app.utils;

import com.lb.brandingApp.app.constants.ApplicationConstants;
import com.lb.brandingApp.common.data.entities.Dimension;
import com.lb.brandingApp.common.data.enums.TeamDescription;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class AppUtil {

    public static TeamDescription getTeamDescriptionByDescription(String description) {
        return Arrays.stream(TeamDescription.values()).filter(
                teamDescription -> teamDescription.description().equalsIgnoreCase(description)
        ).findFirst().orElseThrow(() -> new RuntimeException(ApplicationConstants.TEAM_DESCRIPTION_NOT_FOUND));
    }

    public static double calculateArea(Dimension dimension, int qtyValue) {
        return BigDecimal.valueOf((dimension.getLength() * dimension.getWidth()) * qtyValue)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
