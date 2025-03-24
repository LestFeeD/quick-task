package com.quick_task.dto.update;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateProjectDtoRequest {
    @Size(min = 1,max = 40)

    private String nameProject;

    private Date startDate;

    private Date endDate;
    @Size(min = 1,max = 100)
    private String descriptionProject;

    private Long idStatus;

    private Long idPriority;

    private Long oldNamePriority;




}
