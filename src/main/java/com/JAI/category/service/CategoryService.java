package com.JAI.category.service;

import com.JAI.category.domain.Category;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;
import com.JAI.group.domain.Group;

import java.util.UUID;

public interface CategoryService {
    public Category getCategoryById(UUID categoryId);

    UUID addGroupCategory(CreateGroupCategoryServiceReq req);

    void updateGroupCategoryColor(Group group, String color);
}
