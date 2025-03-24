package com.quick_task.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCustomStatusDtoRequest {
    @NotBlank
    @Size(max = 100, min = 1)
    private String nameStatus;
    private Long idWebUser;
    private Long idStatusRole;
}
