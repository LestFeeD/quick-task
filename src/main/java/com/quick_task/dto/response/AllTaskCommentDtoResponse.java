package com.quick_task.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class AllTaskCommentDtoResponse {
    private Long idComment;
    private String commentText;
    private LocalDateTime addCommentDate;
    private String nameUser;
}
