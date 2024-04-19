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

    public static Set<PermissionDto> mapPermissions(Set<Permission> permissionSet) {
        return permissionSet.stream().map(permission -> {
            PermissionDto permissionDto = new PermissionDto();
            permissionDto.setPermissionName(permission.getPermissionName());
            permissionDto.setHttpResource(permission.isHttpResource());
            permissionDto.setHttpMethod(permission.getHttpMethod());
            permissionDto.setResourceUri(permission.getResourceUri());
            return permissionDto;
        }).collect(Collectors.toSet());
    }

    public static TeamDescription getTeamDescriptionByDescription(String description) {
        return Arrays.stream(TeamDescription.values()).filter(
                teamDescription -> teamDescription.description().equalsIgnoreCase(description)
        ).findFirst().orElseThrow(() -> new RuntimeException(ApplicationConstants.TEAM_DESCRIPTION_NOT_FOUND));
    }

    public static Amount mapAmount(Double value) {
        Amount amount = new Amount();
        amount.setValue(value);
        amount.setCurrency(Currency.INR);
        return amount;
    }

    public static double calculateArea(Dimension dimension) {
        return (dimension.getLength() * dimension.getWidth());
    }

    public static Area mapArea(double value) {
        Area area = new Area();
        area.setValue(value);
        area.setUnit(UOM.SQUARE_FEET);
        return area;
    }

    public static Quantity mapQuantity(Integer value) {
        Quantity quantity = new Quantity();
        quantity.setValue(value);
        quantity.setUom(UOM.EACH);
        return quantity;
    }

    public static Dimension mapDimension(Double length, Double width) {
        Dimension dimension = new Dimension();
        dimension.setUnit(UOM.FEET);
        dimension.setLength(length);
        dimension.setWidth(width);
        return dimension;
    }
}
