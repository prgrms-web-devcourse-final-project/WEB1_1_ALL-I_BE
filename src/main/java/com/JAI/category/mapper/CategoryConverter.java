package com.JAI.category.mapper;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.domain.Category;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;
import com.JAI.group.domain.Group;
import com.JAI.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryConverter {
    //DTO -> Entity
    public Category toCategoryEntity(String name, String color, User user, Group group){
        Category category = Category.create(
                name,
                color,
                user,
                group
        );
        return category;
    }

    //Service -> Service
    public CreateGroupCategoryServiceReq toCreateGroupCategoryServiceReq(Group group, String color, User user) {
        return CreateGroupCategoryServiceReq.builder()
                .name(group.getName())
                .color(color)
                .userId(user.getUserId())
                .groupId(group.getGroupId())
                .build();
    }

    public Category categoryCreateReqDTOtoCategoryEntity(CategoryCreateReqDTO categoryCreateReqDTO, User user) {
        return Category.builder()
                .name(categoryCreateReqDTO.getName())
                .color(categoryCreateReqDTO.getColor())
                .user(user)
                .build();
    }

    public CategoryResDTO categoryToCategoryResDTO(Category category) {
        return CategoryResDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .color(category.getColor())
                .userId(category.getUser().getUserId())
                .groupId(category.getGroup() == null ? null : category.getGroup().getGroupId())
                .build();
    }

    public Category categoryUpdateReqDTOToCategory(CategoryUpdateReqDTO categoryUpdateReqDTO, Category category) {
        return Category.builder()
                .categoryId(category.getCategoryId())
                .name(categoryUpdateReqDTO.getName() != null ? categoryUpdateReqDTO.getName() : category.getName())
                .color(categoryUpdateReqDTO.getColor() != null ? categoryUpdateReqDTO.getColor() : category.getColor())
                .user(category.getUser())
                .group(category.getGroup())
                .build();
    }
}
