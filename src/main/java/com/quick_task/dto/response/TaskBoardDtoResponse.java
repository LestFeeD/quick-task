package com.quick_task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class TaskBoardDtoResponse {

    private Long idTask;
    private String nameTask;
    private Long idProject;
    private String nameProject;
    private String namePriority;

}
