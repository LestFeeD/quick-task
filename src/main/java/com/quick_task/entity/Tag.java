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
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tag")
    private Long idTag;

    @Column(name = "name_tag")
    private String nameTag;

    @ManyToMany(mappedBy = "tagSet", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Task> taskSet;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Tag{");
        s.append("idTag=").append(idTag);
        s.append(", nameTag='").append(nameTag).append('\'');
        s.append(", taskSet=").append(taskSet);
        s.append('}');

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(idTag, tag.idTag) && Objects.equals(nameTag, tag.nameTag) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTag, nameTag);
    }
}
