package com.lb.brandingApp.common.mapper;

import com.lb.brandingApp.auth.data.entities.Permission;
import com.lb.brandingApp.auth.data.models.common.PermissionDto;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.entities.Area;
import com.lb.brandingApp.common.data.entities.Dimension;
import com.lb.brandingApp.common.data.entities.Quantity;
import com.lb.brandingApp.common.data.enums.Currency;
import com.lb.brandingApp.common.data.enums.UOM;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CommonMapper {
    public Set<PermissionDto> mapPermissions(Set<Permission> permissionSet) {
        return permissionSet.stream().map(permission -> {
            PermissionDto permissionDto = new PermissionDto();
            permissionDto.setPermissionName(permission.getPermissionName());
            permissionDto.setHttpResource(permission.isHttpResource());
            permissionDto.setHttpMethod(permission.getHttpMethod());
            permissionDto.setResourceUri(permission.getResourceUri());
            return permissionDto;
        }).collect(Collectors.toSet());
    }

    public Amount mapAmount(Double value) {
        Amount amount = new Amount();
        amount.setValue(value);
        amount.setCurrency(Currency.INR);
        return amount;
    }

    public Area mapArea(double value) {
        Area area = new Area();
        area.setValue(value);
        area.setUnit(UOM.SQUARE_FEET);
        return area;
    }

    public Quantity mapQuantity(Integer value) {
        Quantity quantity = new Quantity();
        quantity.setValue(value);
        quantity.setUom(UOM.EACH);
        return quantity;
    }

    public Dimension mapDimension(Double length, Double width) {
        Dimension dimension = new Dimension();
        dimension.setUnit(UOM.FEET);
        dimension.setLength(length);
        dimension.setWidth(width);
        return dimension;
    }
}
