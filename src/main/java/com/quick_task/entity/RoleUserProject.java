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
@Table(name = "role_user_project")
public class RoleUserProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role_user_project")
    private Long idRoleUserProject;

    @ManyToOne
    @JoinColumn(name = "id_role_project_task")
    private RoleProjectTask roleProjectTask;

    @ManyToOne
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("RoleUserProject{");
        s.append("idRoleUserProject=").append(idRoleUserProject);
        s.append(", roleProjectTask=").append(roleProjectTask);
        s.append(", webUser=").append(webUser);
        s.append(", project=").append(project);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleUserProject that = (RoleUserProject) o;
        return Objects.equals(idRoleUserProject, that.idRoleUserProject) && Objects.equals(roleProjectTask, that.roleProjectTask) && Objects.equals(webUser, that.webUser) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRoleUserProject, roleProjectTask, webUser, project);
    }
}
