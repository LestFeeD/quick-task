package com.quick_task.dto.create;

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
public class CreateWebUserDtoRequest {
    @Size(min = 3,max = 25)
    @NotBlank
    private String nameUser;
    @Size(min = 3,max = 25)
    @NotBlank
    @ExtendedEmailValidator
    private String mailUser;
    @Size(min = 5,max = 25)
    @NotBlank
    private String password;
}
