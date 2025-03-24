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
@Table(name = "project_participants")
public class ProjectParticipants {

    @EmbeddedId
    private IdProjectParticipants idProjectParticipants;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idProject")
    @JoinColumn(name = "id_project")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idWebUser")
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @Column(name = "owner_project")
    private Integer ownerProject;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectParticipants that = (ProjectParticipants) o;
        return Objects.equals(idProjectParticipants, that.idProjectParticipants) && Objects.equals(project, that.project) && Objects.equals(webUser, that.webUser) && Objects.equals(ownerProject, that.ownerProject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProjectParticipants, project, webUser, ownerProject);
    }
}
