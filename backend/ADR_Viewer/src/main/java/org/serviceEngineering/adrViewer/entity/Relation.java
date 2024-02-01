package org.serviceEngineering.adrViewer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Relation")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString
public class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "relation_id", nullable = false)
    private int id;

    @Column(name = "relation_type", length = 999999)
    private String type;

    @Column(name = "affected_adr", length = 999999)
    private String affectedAdr;

    @ManyToOne
    @JoinColumn(name = "adr_id")
    @JsonIgnore
    private ADR adr;
}
