package com.quick_task.factory;

import com.quick_task.dto.response.*;
import com.quick_task.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectFactory {

    public ProjectDtoResponse makeProjectDto(Project project) {

        return ProjectDtoResponse.builder()
                .idProject(project.getIdProject())
                .nameProject(project.getNameProject())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .namePriority(project.getPriority() != null ? project.getPriority().getNamePriority() : null)
                .build();
    }

    public AllProjectDtoResponse makeAllProjectDto(Project project, String statusNames) {
        return AllProjectDtoResponse.builder()
                .idProject(project.getIdProject())
                .nameProject(project.getNameProject())
                .nameStatus(statusNames)
                .namePriority(project.getPriority().getNamePriority())
                .build();
    }

    public InformationProjectDtoResponse makeInformationProjectDto(Project project, StatusProject statusProject) {
/*        List<Long> commentList = (commentProjects != null && !commentProjects.isEmpty())
                ? commentProjects.stream()
                .map(CommentProject::getIdCommentProject)
                .toList()
                : Collections.emptyList();*/

        return InformationProjectDtoResponse.builder()
                .idProject(project.getIdProject())
                .nameProject(project.getNameProject())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .descriptionProject(project.getDescriptionProject())
                .nameStatus(statusProject.getStatus().getNameStatus())
                .build();
    }
}
