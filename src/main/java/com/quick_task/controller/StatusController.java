package com.quick_task.controller;

import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.create.CreateStatusDtoRequest;
import com.quick_task.dto.response.AllStatusDtoResponse;
import com.quick_task.dto.response.ProjectDtoResponse;
import com.quick_task.service.StatusServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Status")
public class StatusController {
    public static final String CREATE_PROJECT = "/status";
    public static final String GET_STATUS = "/all-status-task/{id_task}";


    private final StatusServiceImpl service;

    public StatusController(StatusServiceImpl service) {
        this.service = service;
    }

    @GetMapping(GET_STATUS)
    public List<AllStatusDtoResponse> getAllStatusByTask(@PathVariable("id_task") Long idTask)  {
        return service.getAllStatusForTask(idTask);
    }
    @PostMapping(CREATE_PROJECT)
    public void createCustomStatus(@Valid  @RequestBody CreateStatusDtoRequest dtoRequest)  {
          service.createCustomStatusService(dtoRequest);
    }
}
