package com.quick_task.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class IdProjectParticipants implements Serializable {
    @Column(name = "id_project")
    private Long idProject;

    @Column(name = "id_web_user")
    private Long idWebUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdProjectParticipants that = (IdProjectParticipants) o;
        return Objects.equals(idProject, that.idProject) && Objects.equals(idWebUser, that.idWebUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProject, idWebUser);
    }
}
