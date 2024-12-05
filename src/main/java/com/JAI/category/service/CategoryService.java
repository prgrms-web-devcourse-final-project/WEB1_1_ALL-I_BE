package com.JAI.category.service;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.category.domain.Category;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    public Category getCategoryByCategoryId(UUID categoryId);

    public UUID addGroupCategory(CreateGroupCategoryServiceReq req);


    public CategoryResDTO createCategory(CategoryCreateReqDTO categoryCreateReqDTO, UUID userId);

    public CategoryResDTO updateCategory(UUID categoryId, CategoryUpdateReqDTO categoryUpdateReqDTO, UUID userId);

    public void deleteCategory(UUID categoryId, UUID userId);

    public List<CategoryResDTO> getCategoryByUserId(UUID userId);

    public void updateGroupCategoryColor(UUID groupId, String name, String color);

    //public GroupCategoryResDTO getCategoryByGroupId(UUID groupId);

    public CategoryResDTO getCategoryByGroupId(UUID groupId);

    public List<CategoryResDTO> getOnlyGroupCategoryByUserId(UUID userId);
}
