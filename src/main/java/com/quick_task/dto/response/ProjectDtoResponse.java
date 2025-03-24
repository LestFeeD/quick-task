package com.quick_task.dto.response;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDtoResponse {

    private Long idProject;
    private String nameProject;
    private String nameStatus;
    private Date startDate;
    private Date endDate;
    private String namePriority;



}
