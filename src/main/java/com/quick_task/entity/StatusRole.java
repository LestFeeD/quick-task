package com.quick_task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "status_role")
public class StatusRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_role")
    private Long idStatusRole;

    @Column(name = "name_status_role")
    private String nameStatusRole;

    @Column(name = "order_status_role")
    private Integer orderStatusRole;


    @OneToMany(mappedBy = "statusRole", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Status> statusSet;

}
