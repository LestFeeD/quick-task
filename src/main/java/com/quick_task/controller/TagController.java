package com.quick_task.controller;

import com.quick_task.dto.response.AllTagsDtoResponse;
import com.quick_task.service.TagServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@Tag(name = "Tag")
public class TagController {
    private final TagServiceImpl tagService;

    public static final String All_TAG_TASK = "/task/{id_task}/tags";
    public static final String DELETE_TAG_TASK = "/task/{id_task}/tags/{id_tag}";



    public TagController(TagServiceImpl tagService) {
        this.tagService = tagService;
    }
    @PostMapping(DELETE_TAG_TASK)
    public void deleteTagFromTask(@PathVariable(value = "id_task") Long idTask, @PathVariable(value = "id_tag") Long idTag)  {
        tagService.deleteTagFromTask(idTask, idTag);
    }
    @GetMapping(All_TAG_TASK)
    public CompletableFuture<List<AllTagsDtoResponse>> allTasks(@PathVariable(value = "id_task") Long idTask)  {
        return  tagService.findTagsByTask( idTask);
    }
}
