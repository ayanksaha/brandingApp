package com.lb.brandingApp.category.data.projections;

import com.lb.brandingApp.common.data.entities.ImageData;

public interface CategorySummary {
    Long getId();
    String getName();
    ImageData getIcon();
}
