package com.quick_task.service;

import com.quick_task.dto.create.CreateStatusDtoRequest;
import com.quick_task.dto.response.AllStatusDtoResponse;
import com.quick_task.dto.update.UpdateCustomStatusDtoRequest;

import java.util.List;

public interface StatusService {
    void createCustomStatusService(CreateStatusDtoRequest createStatusDtoRequest);
    void updateCustomStatusService(UpdateCustomStatusDtoRequest dtoRequest);

    List<AllStatusDtoResponse> getAllStatusForTask(Long idTask);
}
