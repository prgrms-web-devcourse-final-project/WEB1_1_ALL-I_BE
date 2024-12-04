package com.JAI.category.controller;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResDTO>> getCategories(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(categoryService.getCategoryByUserId(user.getUserId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResDTO> createCategory(@AuthenticationPrincipal CustomUserDetails user, @RequestBody @Valid CategoryCreateReqDTO categoryCreateReqDTO) {
        return ApiResponse.onCreateSuccess(categoryService.createCategory(categoryCreateReqDTO, user.getUserId()));
    }

    @PatchMapping("{category_id}")
    public ApiResponse<CategoryResDTO> updateCategory(@PathVariable("category_id") UUID categoryId, @RequestBody @Valid CategoryUpdateReqDTO categoryUpdateReqDTO, @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println(categoryUpdateReqDTO);
        return ApiResponse.onSuccess(categoryService.updateCategory(categoryId, categoryUpdateReqDTO, user.getUserId()));
    }

    @DeleteMapping("{category_id}")
    public ApiResponse<?> deleteCategory(@PathVariable("category_id") UUID categoryId, @AuthenticationPrincipal CustomUserDetails user) {
        categoryService.deleteCategory(categoryId, user.getUserId());
        return ApiResponse.onSuccess();
    }
}
