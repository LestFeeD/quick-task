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
@Table(name = "priority")
public class Priority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_priority")
    private Long idPriority;

    @Column(name = "name_priority")
    private String namePriority;

    @OneToMany(mappedBy = "priority", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Project> projectSet;

    @ManyToOne
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Priority{");
        s.append("idPriority=").append(idPriority);
        s.append(", namePriority='").append(namePriority).append('\'');
        s.append(", projectSet=").append(projectSet);
        s.append(", projectSet=").append(webUser);
        s.append('}');
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;
        return Objects.equals(idPriority, priority.idPriority) && Objects.equals(namePriority, priority.namePriority)
                && Objects.equals(projectSet, priority.projectSet) && Objects.equals(webUser, priority.webUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPriority, namePriority, projectSet, webUser);
    }
}
