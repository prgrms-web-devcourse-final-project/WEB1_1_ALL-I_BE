package com.JAI.category.service;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.CategoryUpdateReqDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
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
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CategoryConverter categoryConverter;
    private final ConversionService conversionService;

    @Override
    public Category getCategoryByCategoryId(UUID categoryId) {
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
    public void updateGroupCategoryColor(UUID groupId, String name, String color) {
        //일단 그룹 카테고리 찾아
        Category category = categoryRepository.findByGroup_GroupId(groupId)
                .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리를 찾을 수 없습니다."));

        category.updateCategoryInfo(name, color);

        categoryRepository.save(category);
    }

    // 카테고리 생성
    @Override
    public CategoryResDTO createCategory(CategoryCreateReqDTO categoryCreateReqDTO, UUID userId) {
        //dto에서 받은 유저아이디로 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다."));

        Category category = categoryConverter.categoryCreateReqDTOtoCategoryEntity(categoryCreateReqDTO, user);
        categoryRepository.save(category);

        return categoryConverter.categoryToCategoryResDTO(category);
    }

    @Override
    public CategoryResDTO updateCategory(UUID categoryId, CategoryUpdateReqDTO categoryUpdateReqDTO, UUID userId) {
        Category exiestedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("해당 Id의 카테고리를 찾을 수 없습니다."));

        if (!exiestedCategory.getUser().getUserId().equals(userId)) {
            throw new CategoryNotOwnerException("다른 사용자의 카테고리를 수정할 수 없습니다.");
        }

        Category updatedCategory = categoryConverter.categoryUpdateReqDTOToCategory(categoryUpdateReqDTO, exiestedCategory);
        categoryRepository.save(updatedCategory);

        return categoryConverter.categoryToCategoryResDTO(updatedCategory);
    }

    // 새로 카테고리0 만들어서 거기에 해당 투두, 일정 할당하기
    @Override
    public void deleteCategory(UUID categoryId, UUID userId) {
        Category exiestedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("해당 Id의 카테고리를 찾을 수 없습니다."));

        if (!exiestedCategory.getUser().getUserId().equals(userId)) {
            throw new CategoryNotOwnerException("다른 사용자의 카테고리를 수정할 수 없습니다.");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryResDTO> getCategoryByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다."));

        // 사용자의 개인 카테고리
        List<Category> personalCategories = categoryRepository.findByUser_UserId(userId);

        // 그룹 카테고리
        List<Category> groupCategories = categoryRepository.findGroupCategoriesByUserId(userId);

        return Stream.concat(personalCategories.stream(),
                        groupCategories.stream())
                .map(categoryConverter::categoryToCategoryResDTO)
                .toList();
    }

    @Override
    public GroupCategoryResDTO getCategoryByGroupId(UUID groupId) {
        // 그룹 카테고리 탐색
        return categoryConverter.categoryToGroupCategoryResDTO(categoryRepository.findByGroup_GroupId(groupId)
                .orElseThrow(() -> new CategoryNotFoundException("해당 그룹의 카테고리를 찾을 수 없습니다.")));
    }

    @Override
    public List<GroupCategoryResDTO> getOnlyGroupCategoryByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다."));

        return categoryRepository.findGroupCategoriesByUserId(userId)
                .stream()
                .map(categoryConverter::categoryToGroupCategoryResDTO)
                .toList();
    }
}