package com.quick_task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Optional;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "status_project")
public class StatusProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_project")
    private Long idStatusProject;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "id_left_project_status")
    private StatusProject idLeftProjectStatus;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "id_right_project_status")
    private StatusProject idRightProjectStatus;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("StatusProject{");
        s.append("idStatusProject=").append(idStatusProject);
        s.append(", idLeftTaskStatusId=").append(idLeftProjectStatus != null ? idLeftProjectStatus.getIdStatusProject() : null);
        s.append(", idRightTaskStatusId=").append(idRightProjectStatus != null ? idRightProjectStatus.getIdStatusProject() : null);

        s.append(", status=").append(status);
        s.append(", project=").append(project);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusProject that = (StatusProject) o;
        return Objects.equals(idStatusProject, that.idStatusProject) &&
                Objects.equals(idLeftProjectStatus != null ? idLeftProjectStatus.getIdStatusProject() : null,
                that.idLeftProjectStatus != null ? that.idLeftProjectStatus.getIdStatusProject() : null) &&
                Objects.equals(idRightProjectStatus != null ? idRightProjectStatus.getIdStatusProject() : null,
                        that.idRightProjectStatus != null ? that.idRightProjectStatus.getIdStatusProject() : null)
                && Objects.equals(status, that.status) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idStatusProject,
                idLeftProjectStatus != null ? idLeftProjectStatus.getIdStatusProject() : null,
                idRightProjectStatus != null ? idRightProjectStatus.getIdStatusProject() : null,
                status, project);
    }
}
