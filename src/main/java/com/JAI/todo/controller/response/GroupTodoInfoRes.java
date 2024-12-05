package com.JAI.todo.controller.response;

import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.group.controller.response.GroupListRes;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class GroupTodoInfoRes {
    List<GroupListRes> groups;
    List<CategoryResDTO> groupCategories;
    List<GroupTodoRes> groupTodos;
}
