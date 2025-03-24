package com.quick_task.dto.update;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChangePositionProject {
    private Long projectId;
    private Long newStatusId;
    private Long leftProjectId;
    private Long rightProjectId;
}
