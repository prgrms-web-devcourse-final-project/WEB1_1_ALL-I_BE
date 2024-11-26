package com.JAI.category.service;

import com.JAI.category.domain.Category;

import java.util.UUID;

public interface CategoryService {
    public Category getCategoryById(UUID categoryId);
}
