package com.quick_task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class StatusProjectBoardDtoResponse {
    private Long idStatus;
    private String nameStatus;
    private List<AllProjectDtoResponse> projectDtoResponses;
}
