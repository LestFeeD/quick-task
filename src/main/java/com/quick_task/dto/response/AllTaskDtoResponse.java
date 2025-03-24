package com.quick_task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class AllTaskDtoResponse {

    private Long idTask;
    private String nameTask;
    private String nameProject;
    private String nameTag;
}
