package com.quick_task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "web_user")
public class WebUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_web_user")
    private Long idWebUser;

    @Column(name = "name_user")
    private String nameUser;

    @Column(name = "mail_user")
    private String mailUser;

    @Column(name = "temporary_mail")
    private String temporaryMail;


    @Column(name = "password_user")
    private String passwordUser;

    @Column(name = "activated")
    private Integer activated;

    @OneToMany(mappedBy = "webUser", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<ProjectParticipants> projectParticipantsSet;

    @OneToMany(mappedBy = "webUser", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<CommentProject> commentProjectSet;

    @OneToMany(mappedBy = "webUser", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<CommentTask> commentTaskSet;


    @OneToMany(mappedBy = "webUser", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<TaskParticipants> taskParticipantsSet;

    @OneToMany(mappedBy = "webUser", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Status> statusSet;

    @OneToMany(mappedBy = "webUser",cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Priority> prioritySet;

    @OneToMany(mappedBy = "webUser", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<ConfirmationToken> confirmationTokens;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("WebUser{");
        s.append("idWebUser=").append(idWebUser);
        s.append(", nameUser='").append(nameUser).append('\'');
        s.append(", mailUser='").append(mailUser).append('\'');
        s.append(", temporaryMail='").append(mailUser).append('\'');
        s.append(", passwordUser='").append(passwordUser).append('\'');
        s.append(", activated='").append(activated).append('\'');
        s.append(", projectParticipantsSet=").append(projectParticipantsSet);
        s.append(", commentProjectSet=").append(commentProjectSet);
        s.append(", commentTaskSet=").append(commentTaskSet);
        s.append(", taskParticipantsSet=").append(taskParticipantsSet);
        s.append(", prioritySet=").append(prioritySet);
        s.append(", confirmationTokens=").append(confirmationTokens);

        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebUser webUser = (WebUser) o;
        return Objects.equals(idWebUser, webUser.idWebUser) && Objects.equals(nameUser, webUser.nameUser)
                && Objects.equals(mailUser, webUser.mailUser)
                && Objects.equals(temporaryMail, webUser.temporaryMail)
                && Objects.equals(passwordUser, webUser.passwordUser) && Objects.equals(projectParticipantsSet, webUser.projectParticipantsSet)
                && Objects.equals(commentProjectSet, webUser.commentProjectSet) && Objects.equals(commentTaskSet, webUser.commentTaskSet)
                && Objects.equals(activated, webUser.activated) && Objects.equals(taskParticipantsSet, webUser.taskParticipantsSet)
                && Objects.equals(statusSet, webUser.statusSet)
                && Objects.equals(prioritySet, webUser.prioritySet)  && Objects.equals(confirmationTokens, webUser.confirmationTokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idWebUser, nameUser, mailUser, temporaryMail, passwordUser, activated, projectParticipantsSet, commentProjectSet, commentTaskSet, taskParticipantsSet, statusSet, prioritySet, confirmationTokens);
    }
}
