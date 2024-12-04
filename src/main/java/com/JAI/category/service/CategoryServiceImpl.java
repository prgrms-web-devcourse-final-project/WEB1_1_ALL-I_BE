package com.JAI.category.service;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.exception.CategoryNotOwnerException;
import com.JAI.category.mapper.CategoryConverter;
import com.JAI.category.domain.Category;
import com.JAI.category.exception.CategoryNotFoundException;
import com.JAI.category.repository.CategoryRepository;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;
import com.JAI.group.domain.Group;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.repository.GroupRepository;
import com.JAI.user.domain.User;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CategoryConverter categoryConverter;

    @Override
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID를 가진 카테고리를 찾을 수 없습니다.", categoryId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UUID addGroupCategory(CreateGroupCategoryServiceReq req) {
        //dto에서 받은 그룹아이디로 그룹 찾기
        // TODO :: 여기 예외처리
        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("해당 ID의 그룹을 찾을 수 없습니다."));

        //dto에서 받은 유저아이디로 유저 찾기
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다."));

        Category category =
                categoryConverter.toCategoryEntity(req.getName(), req.getColor(), user, group);

        categoryRepository.save(category);

        return category.getCategoryId();
    }

    @Override
    public void updateGroupCategoryColor(UUID groupId, String color) {
        //일단 그룹 카테고리 찾아
        Category category = categoryRepository.findByGroup_GroupId(groupId)
                .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리를 찾을 수 없습니다."));

        //매개변수의 color값과 다르면 update
        if(!category.getColor().equals(color)) {
            category.updateCategoryColor(color);
            categoryRepository.save(category);
        }
    }

    // 카테고리 생성
    @Override
    public CategoryResDTO createCategory(CategoryCreateReqDTO categoryCreateReqDTO, UUID userId) {
        //dto에서 받은 유저아이디로 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다."));

        if (!userId.equals(categoryCreateReqDTO.getUserId())) {
            throw new CategoryNotOwnerException("다른 사용자의 카테고리를 생성할 수 없습니다.");
        }

        Category category = categoryConverter.categoryCreateReqDTOtoCategoryEntity(categoryCreateReqDTO, user);
        categoryRepository.save(category);

        return categoryConverter.categoryToCategoryResDTO(category);
    }

    @Override
    public Category updateCategory(CategoryUpdateReqDTO categoryUpdateReqDTO, UUID userId) {
        return null;
    }

    @Override
    public void deleteCategory(UUID categoryId) {

    }

    @Override
    public List<Category> getCategory(UUID userId) {
        return List.of();
    }

}
