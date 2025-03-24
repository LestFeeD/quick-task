package com.quick_task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_project")
    private Long idProject;

    @Column(name = "name_project")
    private String nameProject;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
    @Column(name = "start_date")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "description_project")
    private String descriptionProject;

    @OneToMany(mappedBy = "project",  cascade = CascadeType.PERSIST)
    private Set<StatusProject> statusProjectSet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_priority")
    private Priority priority;

    @OneToMany(mappedBy = "project",   cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Task> taskSet;


    @OneToMany(mappedBy = "project",   cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<ProjectParticipants> projectParticipantsSet;

    @OneToMany(mappedBy = "project",  cascade = CascadeType.PERSIST)
    private Set<CommentProject> commentProjectSet;

    @OneToMany(mappedBy = "project",  cascade = CascadeType.PERSIST)
    private Set<RoleUserProject> roleUserProjectSet;



    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Project{");
        s.append("idProject=").append(idProject);
        s.append(", nameProject='").append(nameProject).append('\'');
        s.append(", startDate=").append(startDate);
        s.append(", endDate=").append(endDate);
        s.append(", descriptionProject='").append(descriptionProject).append('\'');
        s.append(", statusProjectSet=").append(statusProjectSet);
        s.append(", priority=").append(priority);
        s.append(", projectParticipantsSet=").append(projectParticipantsSet);
        s.append(", commentProjectSet=").append(commentProjectSet);
        s.append(", roleUserProjectSet=").append(roleUserProjectSet);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(idProject, project.idProject) && Objects.equals(nameProject, project.nameProject) && Objects.equals(startDate, project.startDate) && Objects.equals(endDate, project.endDate) && Objects.equals(descriptionProject, project.descriptionProject) && Objects.equals(statusProjectSet, project.statusProjectSet) && Objects.equals(priority, project.priority) && Objects.equals(projectParticipantsSet, project.projectParticipantsSet) && Objects.equals(commentProjectSet, project.commentProjectSet)
                && Objects.equals(roleUserProjectSet, project.roleUserProjectSet );
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProject, nameProject, startDate, endDate, descriptionProject, statusProjectSet, priority, projectParticipantsSet, commentProjectSet, roleUserProjectSet);
    }
}
