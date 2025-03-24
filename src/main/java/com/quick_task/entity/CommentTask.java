package com.quick_task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "comment_task")
public class CommentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comment_task")
    private Long idCommentTask;

    @Column(name = "comment_text")
    private String commentText;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
    @Column(name = "add_comment_date")
    private LocalDateTime addCommentDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
    @Column(name = "update_comment_date")
    private LocalDateTime updateCommentDate;

    @ManyToOne
    @JoinColumn(name = "id_web_user")
    private WebUser webUser;

    @ManyToOne
    @JoinColumn(name = "id_task")
    private Task task;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("CommentTask{");
        s.append("idCommentTask=").append(idCommentTask);
        s.append(", commentText='").append(commentText).append('\'');
        s.append(", addCommentDate=").append(addCommentDate);
        s.append(", webUser=").append(webUser);
        s.append(", task=").append(task);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentTask that = (CommentTask) o;
        return Objects.equals(idCommentTask, that.idCommentTask) && Objects.equals(commentText, that.commentText) && Objects.equals(addCommentDate, that.addCommentDate) && Objects.equals(webUser, that.webUser) && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCommentTask, commentText, addCommentDate, webUser, task);
    }
}
