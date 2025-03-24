package com.quick_task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class AllCommentDtoResponse {

    private Long idComment;
    private String commentText;
    private LocalDateTime addCommentDate;
    private String nameUser;
}
