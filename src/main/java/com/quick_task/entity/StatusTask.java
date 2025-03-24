package com.quick_task.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.Objects;
import java.util.Optional;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "status_task")
public class StatusTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_task")
    private Long idStatusTask;

    @OneToOne
    @JoinColumn(name = "id_left_task_status")
    private StatusTask idLeftTaskStatus;

    @OneToOne
    @JoinColumn(name = "id_right_task_status")
    private StatusTask idRightTaskStatus;

    @ManyToOne
    @JoinColumn(name = "id_status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_task")
    private Task task;


    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append("StatusTask{");
        s.append("idStatusTask=").append(idStatusTask);

        s.append(", idLeftTaskStatusId=").append(idLeftTaskStatus != null ? idLeftTaskStatus.getIdStatusTask() : null);
        s.append(", idRightTaskStatusId=").append(idRightTaskStatus != null ? idRightTaskStatus.getIdStatusTask() : null);

        s.append(", status=").append(status != null ? status.getIdStatus() : null);
        s.append(", task=").append(task != null ? task.getIdTask() : null);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusTask that = (StatusTask) o;
        return Objects.equals(idStatusTask, that.idStatusTask) &&
                Objects.equals(idLeftTaskStatus != null ? idLeftTaskStatus.getIdStatusTask() : null,
                        that.idLeftTaskStatus != null ? that.idLeftTaskStatus.getIdStatusTask() : null) &&
                Objects.equals(idRightTaskStatus != null ? idRightTaskStatus.getIdStatusTask() : null,
                        that.idRightTaskStatus != null ? that.idRightTaskStatus.getIdStatusTask() : null) &&
                Objects.equals(status, that.status)
                && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idStatusTask,
                idLeftTaskStatus != null ? idLeftTaskStatus.getIdStatusTask() : null,
                idRightTaskStatus != null ? idRightTaskStatus.getIdStatusTask() : null,
                status,
                task);
    }
}
