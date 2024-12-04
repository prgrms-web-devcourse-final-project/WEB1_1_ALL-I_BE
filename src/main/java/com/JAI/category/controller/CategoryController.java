package com.JAI.category.controller;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<?> getCategories(@AuthenticationPrincipal CustomUserDetails user) {
        return null;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResDTO> createCategory(@AuthenticationPrincipal CustomUserDetails user, @RequestBody CategoryCreateReqDTO categoryCreateReqDTO) {
        return ApiResponse.onCreateSuccess(categoryService.createCategory(categoryCreateReqDTO, user.getUserId()));
    }

    @PatchMapping
    public ApiResponse<?> updateCategory(@PathVariable("category_id") UUID categoryId, @AuthenticationPrincipal CustomUserDetails user) {
        return null;
    }

    @DeleteMapping
    public ApiResponse<?> deleteCategory(@PathVariable("category_id") UUID categoryId, @AuthenticationPrincipal CustomUserDetails user) {
        return null;
    }
}
