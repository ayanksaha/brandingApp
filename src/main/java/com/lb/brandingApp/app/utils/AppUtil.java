package com.lb.brandingApp.app.utils;

import com.lb.brandingApp.app.constants.ApplicationConstants;
import com.lb.brandingApp.auth.data.entities.Permission;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.entities.Area;
import com.lb.brandingApp.common.data.entities.Dimension;
import com.lb.brandingApp.common.data.entities.Quantity;
import com.lb.brandingApp.common.data.enums.Currency;
import com.lb.brandingApp.common.data.enums.TeamDescription;
import com.lb.brandingApp.common.data.enums.UOM;
import com.lb.brandingApp.auth.data.models.common.PermissionDto;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AppUtil {

    public static TeamDescription getTeamDescriptionByDescription(String description) {
        return Arrays.stream(TeamDescription.values()).filter(
                teamDescription -> teamDescription.description().equalsIgnoreCase(description)
        ).findFirst().orElseThrow(() -> new RuntimeException(ApplicationConstants.TEAM_DESCRIPTION_NOT_FOUND));
    }

    public static double calculateArea(Dimension dimension) {
        return (dimension.getLength() * dimension.getWidth());
    }
}
