package com.JAI.event.service;

import com.JAI.event.domain.GroupEventMapping;
import com.JAI.event.repository.GroupEventMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupEventMappingServiceImpl implements GroupEventMappingService {
    private final GroupEventMappingRepository groupEventMappingRepository;

    @Override
    public GroupEventMapping findById(UUID groupEventId, UUID groupId, UUID userId) {
        return groupEventMappingRepository.findById(groupEventId, groupId, userId);
    }
}
