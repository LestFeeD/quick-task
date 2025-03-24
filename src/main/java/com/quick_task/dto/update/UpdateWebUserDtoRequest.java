package com.quick_task.dto.update;

import com.quick_task.validation.ExtendedEmailValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UpdateWebUserDtoRequest {
    @Size(min = 3,max = 25)
    private String nameUser;
    @ExtendedEmailValidator
    private String mailUser;
    @Size(min = 5,max = 25)
    private String password;
}
