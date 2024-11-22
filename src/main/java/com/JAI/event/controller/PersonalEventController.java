package com.JAI.event.controller;

import com.JAI.event.DTO.PersonalCreateEventReqDTO;
import com.JAI.event.service.PersonalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PersonalEventController {
    private final PersonalEventService personalEventService;

    @GetMapping
    public List<Object> personalEvent() {
        return null;
    }

    @PostMapping
    public void createPersonalEvent(@RequestBody PersonalCreateEventReqDTO personalCreateEventReqDTO) {
        personalEventService.createPersonalEvent(personalCreateEventReqDTO);
    }

    @PatchMapping
    public void updatePersonalEvent() {

    }

    @DeleteMapping
    public void deletePersonalEvent() {

    }
}
