package com.quick_task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_task")
    private Long idTask;

    @Column(name = "name_task")
    @JsonProperty("nameTask")
    private String nameTask;

    @Column(name = "description_task")
    private String descriptionTask;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @OneToMany(mappedBy = "task", cascade = CascadeType.PERSIST)
    @JsonProperty("commentTaskSet")
    private Set<CommentTask> commentTaskSet;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "tag_task",
            joinColumns = @JoinColumn(name = "id_task"),
            inverseJoinColumns = @JoinColumn(name = "id_tag")
    )
    @JsonIgnore
    private Set<Tag> tagSet;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

    @OneToMany(mappedBy = "task", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<TaskParticipants> taskParticipantsSet;

    @OneToMany(mappedBy = "task", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<StatusTask> statusTasks;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Task{");
        s.append("idTask=").append(idTask);
        s.append(", nameTask='").append(nameTask).append('\'');
        s.append(", descriptionTask='").append(descriptionTask).append('\'');
        s.append(", startDate=").append(startDate);
        s.append(", endDate=").append(endDate);
        s.append(", project=").append(project);
        s.append(", commentTaskSet=").append(commentTaskSet);
        s.append(", tagSet=").append(tagSet);
        s.append(", taskParticipantsSet=").append(taskParticipantsSet);
        s.append(", statusTasks=").append(statusTasks);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(idTask, task.idTask) && Objects.equals(nameTask, task.nameTask)
                && Objects.equals(project, task.project)  && Objects.equals(descriptionTask, task.descriptionTask)
                && Objects.equals(startDate, task.startDate) && Objects.equals(endDate, task.endDate)
                && Objects.equals(commentTaskSet, task.commentTaskSet)
                && Objects.equals(tagSet, task.tagSet) && Objects.equals(taskParticipantsSet, task.taskParticipantsSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTask, nameTask, descriptionTask, startDate, endDate, project, commentTaskSet,
                tagSet, taskParticipantsSet);
    }
}
