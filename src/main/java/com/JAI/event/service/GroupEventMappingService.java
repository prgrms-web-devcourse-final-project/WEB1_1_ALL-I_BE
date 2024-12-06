package com.JAI.event.service;

import com.JAI.event.domain.GroupEventMapping;

import java.util.List;
import java.util.UUID;

public interface GroupEventMappingService {
    GroupEventMapping findById(UUID groupEventId, UUID groupId, UUID userId);
}
