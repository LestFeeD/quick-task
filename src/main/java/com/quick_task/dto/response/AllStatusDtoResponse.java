package com.quick_task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class AllStatusDtoResponse {
    private String nameStatus;
    private String roleStatus;
}
