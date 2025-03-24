package com.quick_task.dto.response;

import lombok.*;

import java.util.Date;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDtoResponse {

    private Long idTask;
    private String nameTask;
    private Date startDate;
    private Date endDate;
    private String descriptionTask;
    private String nameStatus;
    private String nameTag;
    private Long projectId;

}
