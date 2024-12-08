package com.JAI.category.controller;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.user.service.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
    @Operation(summary = "사용자의 카테고리 조회(개인 + 그룹)", description = "사용자 카테고리 전체 조회 API")
    public ApiResponse<List<CategoryResDTO>> getCategories(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(categoryService.getCategoryByUserId(user.getUserId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "개인 카테고리 생성", description = "카테고리 생성 API")
    @Parameters({
            @Parameter(name = "name", description = "카테고리 이름(NOT NULL)", example = "운동"),
            @Parameter(name = "color", description = "카테고리 색깔(NOT NULL)", example = "#000000")}
    )
    public ApiResponse<CategoryResDTO> createCategory(@AuthenticationPrincipal CustomUserDetails user, @RequestBody @Valid CategoryCreateReqDTO categoryCreateReqDTO) {
        return ApiResponse.onCreateSuccess(categoryService.createCategory(categoryCreateReqDTO, user.getUserId()));
    }

    @PatchMapping("{category_id}")
    @Operation(summary = "개인 카테고리 수정", description = "카테고리 수정 API")
    @Parameters({
            @Parameter(name = "categoryId", description = "카테고리 아이디", example = "514220be-71d8-4efc-8649-4a2a3a076f46"),
            @Parameter(name = "name", description = "카테고리 이름", example = "운동"),
            @Parameter(name = "color", description = "카테고리 색깔", example = "#000000")}
    )
    public ApiResponse<CategoryResDTO> updateCategory(@PathVariable("category_id") UUID categoryId, @RequestBody @Valid CategoryUpdateReqDTO categoryUpdateReqDTO, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(categoryService.updateCategory(categoryId, categoryUpdateReqDTO, user.getUserId()));
    }

    @Operation(summary = "개인 카테고리 삭제 / 그룹장의 그룹 카테고리 삭제", description = "카테고리 삭제 API")
    @DeleteMapping("{category_id}")
    public ApiResponse<?> deleteCategory(@PathVariable("category_id") UUID categoryId, @AuthenticationPrincipal CustomUserDetails user) {
        categoryService.deleteCategory(categoryId, user.getUserId());
        return ApiResponse.onSuccess();
    }
}
