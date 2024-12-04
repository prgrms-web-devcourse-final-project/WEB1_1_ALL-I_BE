package com.JAI.event.DTO.response;

import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.group.controller.response.GroupListRes;
import lombok.Getter;

import java.util.List;

@Getter
public class GetAllGroupSomeoneEventResDTO {
    private List<GroupListRes> groups;

    private List<CategoryResDTO> groupCategories;

    private List<AllGroupSomeoneEventResDTO> groupEvents;

    public GetAllGroupSomeoneEventResDTO(List<GroupListRes> groups,
                                         List<CategoryResDTO> groupCategories,
                                         List<AllGroupSomeoneEventResDTO> groupEvents) {
        this.groups = groups;
        this.groupCategories = groupCategories;
        this.groupEvents = groupEvents;
    }
}
