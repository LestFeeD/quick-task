package com.quick_task.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment_project")
public class CommentProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comment_project")
    private Long idCommentProject;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "add_comment_date")
    private LocalDateTime addCommentDate;

    @ManyToOne
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentProject that = (CommentProject) o;
        return Objects.equals(idCommentProject, that.idCommentProject) && Objects.equals(commentText, that.commentText) && Objects.equals(addCommentDate, that.addCommentDate) && Objects.equals(webUser, that.webUser) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCommentProject, commentText, addCommentDate, webUser, project);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("CommentProject{");
        s.append("idCommentProject=").append(idCommentProject);
        s.append(", commentText='").append(commentText).append('\'');
        s.append(", addCommentDate=").append(addCommentDate);
        s.append(", webUser=").append(webUser);
        s.append(", project=").append(project);
        s.append('}');

        return s.toString();
    }
}
