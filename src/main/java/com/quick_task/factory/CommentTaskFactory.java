package com.quick_task.factory;

import com.quick_task.dto.response.AllTaskCommentDtoResponse;
import com.quick_task.entity.CommentTask;
import org.springframework.stereotype.Component;

@Component
public class CommentTaskFactory {
    public AllTaskCommentDtoResponse makeCommentTaskDto(CommentTask commentTask) {

        return AllTaskCommentDtoResponse.builder()
                .idComment(commentTask.getIdCommentTask())
                .commentText(commentTask.getCommentText())
                .addCommentDate(commentTask.getAddCommentDate())
                .nameUser(commentTask.getWebUser().getNameUser())
                .build();
    }
}
