package com.JAI.category.service;

import com.JAI.category.domain.Category;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;

import java.util.UUID;

public interface CategoryService {
    public Category getCategoryById(UUID categoryId);

    UUID addGroupCategory(CreateGroupCategoryServiceReq req);
}
