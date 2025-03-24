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
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status")
    private Long idStatus;

    @Column(name = "name_status")
    private String nameStatus;

    @Column(name = "is_default")
    private Integer isDefault;

    @ManyToOne
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @OneToMany(mappedBy = "status", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<StatusProject> statusProjects;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private Set<StatusTask> statusTasks;

    @ManyToOne
    @JoinColumn(name = "id_status_role")
    private StatusRole statusRole;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Status{");
        s.append("idStatus=").append(idStatus);
        s.append(", nameStatus='").append(nameStatus).append('\'');
        s.append(", statusProjects=").append(statusProjects);
        s.append(", statusTasks=").append(statusTasks);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(idStatus, status.idStatus) && Objects.equals(nameStatus, status.nameStatus) && Objects.equals(isDefault, status.isDefault) && Objects.equals(webUser, status.webUser) && Objects.equals(statusProjects, status.statusProjects) && Objects.equals(statusTasks, status.statusTasks)  && Objects.equals(statusRole, status.statusRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idStatus, nameStatus, isDefault, webUser, statusProjects, statusTasks, statusRole);
    }
}
