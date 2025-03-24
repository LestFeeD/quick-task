package com.quick_task.factory;

import com.quick_task.dto.response.*;
import com.quick_task.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskFactory {
    public TaskDtoResponse makeTaskDto(Task task) {

        return TaskDtoResponse.builder()
                .idTask(task.getIdTask())
                .nameTask(task.getNameTask())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .descriptionTask(task.getDescriptionTask())
                .nameTag(task.getNameTask())
                .projectId(task.getProject() != null && task.getProject().getIdProject() != null ? task.getProject().getIdProject() : null)
                .build();
    }


    public FullInformationTaskDtoResponse makeInformationTaskDto(Task task,  StatusTask statusTask) {
    /*    List<Long> commentList = (commentTasks != null && !commentTasks.isEmpty())
                ? commentTasks.stream()
                .map(CommentTask::getIdCommentTask)
                .collect(Collectors.toList())
                : Collections.emptyList();*/

   /*     List<TagDto> tags = (tagsList != null && !tagsList.isEmpty())
                ? tagsList.stream()
                .map(tag -> new TagDto(tag.getIdTag(), tag.getNameTag()))
                .toList()
                : Collections.emptyList();*/


        return FullInformationTaskDtoResponse.builder()
                .idTask(task.getIdTask())
                .nameTask(task.getNameTask())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .descriptionTask(task.getDescriptionTask())
                .nameStatus(statusTask.getStatus().getNameStatus())
                .idProject(task.getProject() != null ? task.getProject().getIdProject() : null)
                .build();
    }
    public TaskBoardDtoResponse makeTaskBoardDto(Task task) {

        return TaskBoardDtoResponse.builder()
                .idTask(task.getIdTask())
                .nameTask(task.getNameTask())
                .idProject(task.getProject().getIdProject())
                .nameProject(task.getProject().getNameProject())
                .namePriority(task.getProject().getPriority().getNamePriority())
                .build();
    }


}
