package com.serviceEngineering.ADR_Viewer.entity;

import com.serviceEngineering.ADR_Viewer.div.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ADR {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private int id;

    private String title;
    private String context;
    private String decision;
    private Status status;
    private String consequences;
    private String artifacts; //TODO: find correct type
    private String relations; //TODO: find correct type ----> probably custom type Relation

}
