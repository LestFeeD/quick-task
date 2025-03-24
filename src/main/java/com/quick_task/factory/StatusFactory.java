package com.quick_task.factory;

import com.quick_task.dto.response.AllStatusDtoResponse;
import com.quick_task.entity.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusFactory {
    public AllStatusDtoResponse makeStatus(Status status) {
        return AllStatusDtoResponse.builder()
                .nameStatus(status.getNameStatus())
                .roleStatus(status.getStatusRole().getNameStatusRole())
                .build();

    }
}
