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
@Table(name = "Artifact")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "artifact_id", nullable = false)
    private int id;

    @Column(name = "artifact_name", length = 999999)
    private String name;

    @ManyToOne
    @JoinColumn(name = "adr_id")
    @JsonIgnore
    private ADR adr;
}
