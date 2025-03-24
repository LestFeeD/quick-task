package com.quick_task.dto.update;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCommentProjectDtoRequest {
    @NotBlank
    @Size(max = 100, min = 1)
    private String commentText;

}
