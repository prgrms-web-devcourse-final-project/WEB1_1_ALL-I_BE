package com.JAI.category.service;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.domain.Category;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    public Category getCategoryByCategoryId(UUID categoryId);

    UUID addGroupCategory(CreateGroupCategoryServiceReq req);

    void updateGroupCategoryColor(UUID groupId, String color);

    public CategoryResDTO createCategory(CategoryCreateReqDTO categoryCreateReqDTO, UUID userId);

    public CategoryResDTO updateCategory(CategoryUpdateReqDTO categoryUpdateReqDTO, UUID userId);

    public void deleteCategory(UUID categoryId);

    public List<CategoryResDTO> getCategory(UUID userId);
}
