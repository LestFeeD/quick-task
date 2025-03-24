package com.quick_task.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllTaskByProjectDtoResponse {
    private Long idProject;
    private String nameProject;
    private List<TaskDtoResponse> tasks;

}
