package com.JAI.category.service;

import com.JAI.category.domain.Category;
import com.JAI.category.repository.CategoryRepository;
import com.JAI.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID를 가진 카테고리를 찾을 수 없습니다.", categoryId));
    }
}
