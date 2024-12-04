package com.JAI.event.DTO.response;

import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.group.controller.response.GroupListRes;
import lombok.Getter;

import java.util.List;

@Getter
public class GetOneGroupEventResDTO {
    private GroupListRes group;
    private GroupCategoryResDTO groupCategory;
    private List<?> groupEvents;

    public GetOneGroupEventResDTO(GroupListRes group, GroupCategoryResDTO groupCategory, List<?> groupEvents) {
        this.group = group;
        this.groupCategory = groupCategory;
        this.groupEvents = groupEvents;
    }
}
