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
public class FullInformationTaskDtoResponse {

    private Long idTask;
    private String nameTask;
    private Date startDate;
    private Date endDate;
    private String descriptionTask;
    private Long idProject;
    private String nameStatus;
    private List<TagDto> tags;
    private List<Long> commentTask;
}
