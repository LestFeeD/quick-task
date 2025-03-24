package com.quick_task.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_participants")
public class TaskParticipants {

    @EmbeddedId
    private IdTaskParticipants idTaskParticipants;

    @ManyToOne
    @MapsId("idTask")
    @JoinColumn(name = "id_task")
    private Task task;

    @ManyToOne
    @MapsId("idWebUser")
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @Column(name = "owner_task")
    private Integer ownerTask;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskParticipants that = (TaskParticipants) o;
        return Objects.equals(idTaskParticipants, that.idTaskParticipants) && Objects.equals(task, that.task) && Objects.equals(webUser, that.webUser) && Objects.equals(ownerTask, that.ownerTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTaskParticipants, task, webUser, ownerTask);
    }
}
