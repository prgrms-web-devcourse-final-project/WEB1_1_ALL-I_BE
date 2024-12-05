package com.JAI.event.DTO.response;

import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.group.controller.response.GroupListRes;
import lombok.Getter;

import java.util.List;

@Getter
public class GetGroupEventResDTO {
    private List<GroupListRes> group;

    private List<GroupCategoryResDTO> groupCategory;

    private List<GroupEventResDTO> groupEvents;

    public GetGroupEventResDTO(List<GroupListRes> group,
                               List<GroupCategoryResDTO> groupCategory,
                               List<GroupEventResDTO> groupEvents) {
        this.group = group;
        this.groupCategory = groupCategory;
        this.groupEvents = groupEvents;
    }
}
