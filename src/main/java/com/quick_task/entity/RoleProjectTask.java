package com.quick_task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_project_task")
public class RoleProjectTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role_project_task")
    private Long idRoleProjectTask;

    @Column(name = "name_role_project_task")
    private String nameRoleProjectTask;

    @OneToMany(mappedBy = "roleProjectTask", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<RoleUserProject> roleUserProjectSet;

    @OneToMany(mappedBy = "roleProjectTask", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<RoleUserTask> roleUserTasks;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("RoleProjectTask{");
        s.append("idRoleProjectTask=").append(idRoleProjectTask);
        s.append(", nameRoleProjectTask='").append(nameRoleProjectTask).append('\'');
        s.append(", roleUserProjectSet=").append(roleUserProjectSet);
        s.append(", roleUserTasks=").append(roleUserTasks);
        s.append('}');
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleProjectTask that = (RoleProjectTask) o;
        return Objects.equals(idRoleProjectTask, that.idRoleProjectTask) && Objects.equals(nameRoleProjectTask, that.nameRoleProjectTask) && Objects.equals(roleUserProjectSet, that.roleUserProjectSet) && Objects.equals(roleUserTasks, that.roleUserTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRoleProjectTask, nameRoleProjectTask, roleUserProjectSet, roleUserTasks);
    }
}
