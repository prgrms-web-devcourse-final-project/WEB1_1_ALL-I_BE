package com.JAI.category.service;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.domain.Category;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;
import com.JAI.group.domain.Group;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    public Category getCategoryById(UUID categoryId);

    UUID addGroupCategory(CreateGroupCategoryServiceReq req);

    void updateGroupCategoryColor(UUID groupId, String color);

    public CategoryResDTO createCategory(CategoryCreateReqDTO categoryCreateReqDTO, UUID userId);

    public Category updateCategory(CategoryUpdateReqDTO categoryUpdateReqDTO, UUID userId);

    public void deleteCategory(UUID categoryId);

    public List<Category> getCategory(UUID userId);
}
