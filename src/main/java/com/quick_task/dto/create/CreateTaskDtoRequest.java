package com.quick_task.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskDtoRequest {
    @Size(max = 40, min = 1)
    @NotBlank
    private String nameTask;
    private Long idProject;

}
