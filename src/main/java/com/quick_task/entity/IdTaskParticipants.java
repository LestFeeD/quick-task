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
public class IdTaskParticipants implements Serializable {

    @Column(name = "id_task")
    private Long idTask;

    @Column(name = "id_web_user")
    private Long idWebUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdTaskParticipants that = (IdTaskParticipants) o;
        return Objects.equals(idTask, that.idTask) && Objects.equals(idWebUser, that.idWebUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTask, idWebUser);
    }
}
