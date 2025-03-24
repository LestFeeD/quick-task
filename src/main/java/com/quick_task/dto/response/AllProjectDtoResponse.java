package com.quick_task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class AllProjectDtoResponse {

    private Long idProject;
    private String nameProject;
    private String nameStatus;
    private String namePriority;
}
