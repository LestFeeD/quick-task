package com.quick_task.dto.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChangePositionTask {

    private Long taskId;
    private Long newStatusId;
    private Long leftTaskId;
    private Long rightTaskId;
}
