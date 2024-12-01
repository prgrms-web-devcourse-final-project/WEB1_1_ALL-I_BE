package com.JAI.category.converter;

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
}
