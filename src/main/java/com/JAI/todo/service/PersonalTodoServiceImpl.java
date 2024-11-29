package com.JAI.todo.service;

import com.JAI.category.domain.Category;
import com.JAI.category.repository.CategoryRepository;
import com.JAI.todo.controller.request.PersonalTodoCreateReq;
import com.JAI.todo.controller.request.PersonalTodoStateReq;
import com.JAI.todo.controller.response.PersonalTodoListRes;
import com.JAI.todo.controller.response.PersonalTodoStateRes;
import com.JAI.todo.converter.PersonalTodoConverter;
import com.JAI.todo.domain.PersonalTodo;
import com.JAI.todo.repository.PersonalTodoRepository;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PersonalTodoServiceImpl implements PersonalTodoService{

    private final PersonalTodoRepository personalTodoRepository;
    private final CategoryRepository categoryRepository;
    private final PersonalTodoConverter personalTodoConverter;

    @Override
    @Transactional
    public void createPersonalTodo(PersonalTodoCreateReq req, CustomUserDetails user) {
        //카테고리 유효한지 체크
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));

        //해당 카테고리가 로그인한 유저의 카테고리인지 체크
        validateCategoryOwner(user, category);

        personalTodoRepository.save(personalTodoConverter.toPersonalTodoCreateEntity(req, user.getUser(), category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalTodoListRes> getTodoListForMonth(String year, String month, CustomUserDetails user) {

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<PersonalTodo> personalTodos =
                personalTodoRepository.findAllByDateBetweenAndUser_UserId(startDate, endDate, user.getUserId());

        return personalTodos.stream()
                .map(personalTodoConverter::toPersonalTodoListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePersonalTodo(UUID todoId, CustomUserDetails user) {
        //투두 검색
        PersonalTodo personalTodo = personalTodoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 투두를 찾을 수 없습니다."));
        // 유저 검증
        validatePersonalTodoOwner(user, personalTodo);

        //투두 삭제
        personalTodoRepository.delete(personalTodo);
    }

    @Override
    public PersonalTodoStateRes updatePersonTodoState(UUID todoId, PersonalTodoStateReq req, CustomUserDetails user) {
        //todo 존재 여부 확인
        PersonalTodo personalTodo = personalTodoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 투두를 찾을 수 없습니다."));

        //같은 유저인지 확인
        validatePersonalTodoOwner(user, personalTodo);

        //값 업데이트
        personalTodo.updatePersonalTodoState(req.isState());

        return personalTodoConverter.toPersonalTodoStateDTO(personalTodo);
    }


    public void validatePersonalTodoOwner(CustomUserDetails user, PersonalTodo personalTodo){
        UUID currentUserId = user.getUser().getUserId();
        UUID personalTodoOwnerUserId = personalTodo.getUser().getUserId();
        if(!currentUserId.equals(personalTodoOwnerUserId)){
            throw new IllegalArgumentException("해당 사용자의 투두가 아닙니다.");
        }
    }

    public void validateCategoryOwner(CustomUserDetails user, Category category) {
        UUID currentUserId = user.getUser().getUserId();
        UUID categoryOwnerUserId = category.getUser().getUserId();
        if(!currentUserId.equals(categoryOwnerUserId)){
            throw new IllegalArgumentException("해당 사용자의 카테고리가 아닙니다.");
        }
    }
}
