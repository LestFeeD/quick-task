package com.quick_task.factory;

import com.quick_task.dto.response.ProjectDtoResponse;
import com.quick_task.dto.response.WebUserDtoResponse;
import com.quick_task.entity.Project;
import com.quick_task.entity.WebUser;
import org.springframework.stereotype.Component;

@Component
public class WebUserFactory {
    public WebUserDtoResponse makeUserDto(WebUser webUser) {
        return WebUserDtoResponse.builder()
                .userId(webUser.getIdWebUser())
                .nameUser(webUser.getNameUser())
                .mailUser(webUser.getMailUser())
                .password(webUser.getPasswordUser())
                .build();
    }

}
