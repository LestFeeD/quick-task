package com.quick_task.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebUserDtoResponse {

    private Long userId;
    private String nameUser;
    private String mailUser;
    private String password;
}
