package com.quick_task.service;

import com.quick_task.dto.response.AllTagsDtoResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TagService {
    void deleteTagFromTask(Long idTask, Long idTag);
    CompletableFuture<List<AllTagsDtoResponse>> findTagsByTask(Long idTask);

}
