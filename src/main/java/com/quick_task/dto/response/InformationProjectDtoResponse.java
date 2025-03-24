package com.quick_task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class InformationProjectDtoResponse {

    private Long idProject;
    private String nameProject;
    private Date startDate;
    private Date endDate;
    private String descriptionProject;
    private String nameStatus;
    private String namePriority;


}
