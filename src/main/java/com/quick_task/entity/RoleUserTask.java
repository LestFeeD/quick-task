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
@Table(name = "role_user_task")
public class RoleUserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role_user_task")
    private Long idRoleUserTask;

    @ManyToOne
    @JoinColumn(name = "id_role_project_task")
    private RoleProjectTask roleProjectTask;

    @ManyToOne
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @ManyToOne
    @JoinColumn(name = "id_task")
    private Task task;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("RoleUserTask{");
        s.append("idRoleUserTask=").append(idRoleUserTask);
        s.append(", roleProjectTask=").append(roleProjectTask);
        s.append(", webUser=").append(webUser);
        s.append(", task=").append(task);
        s.append('}');
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleUserTask that = (RoleUserTask) o;
        return Objects.equals(idRoleUserTask, that.idRoleUserTask) && Objects.equals(roleProjectTask, that.roleProjectTask) && Objects.equals(webUser, that.webUser) && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRoleUserTask, roleProjectTask, webUser, task);
    }
}
