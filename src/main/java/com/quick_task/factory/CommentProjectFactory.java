package com.quick_task.factory;

import com.quick_task.dto.response.AllCommentDtoResponse;
import com.quick_task.dto.response.ProjectDtoResponse;
import com.quick_task.entity.CommentProject;
import com.quick_task.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class CommentProjectFactory {

    public AllCommentDtoResponse makeCommentProjectDto(CommentProject commentProject) {

        return AllCommentDtoResponse.builder()
                .idComment(commentProject.getIdCommentProject())
                .commentText(commentProject.getCommentText())
                .addCommentDate(commentProject.getAddCommentDate())
                .nameUser(commentProject.getWebUser().getNameUser())
                .build();
    }
}
