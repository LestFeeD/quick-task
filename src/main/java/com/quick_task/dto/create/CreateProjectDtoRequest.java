package com.quick_task.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateProjectDtoRequest {
    @NotBlank
    @Size(max = 40, min = 1)
    private String nameProject;

    private Date startDate;

    private Date endDate;

    private String descriptionProject;

    private Long idPriority;

}
