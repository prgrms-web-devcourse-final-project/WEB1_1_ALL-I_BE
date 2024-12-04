package com.JAI.todo.controller.response;

import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.group.controller.response.GroupListRes;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class AllGroupTodoRes {

    private GroupListRes group;

    private GroupCategoryResDTO groupCategory;

    private List<GroupTodoRes> groupTodos;
}
