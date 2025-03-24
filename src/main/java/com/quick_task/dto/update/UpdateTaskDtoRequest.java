package com.quick_task.dto.update;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateTaskDtoRequest {
    @Size(min = 1,max = 40)
    private String nameTask;

    private Date startDate;

    private Date endDate;
    @Size(min = 1,max = 100)
    private String descriptionTask;

    private Long idOldTag;

    private Long idNewTag;

    private Long idStatus;
}
