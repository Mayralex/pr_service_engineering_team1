package org.serviceEngineering.adrViewer.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.serviceEngineering.adrViewer.div.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ADR")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ADR {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Context", length =  999999)
    private String context;

    @Column(name = "Decision", length =  999999)
    private String decision;

    @Column(name = "Status")
    private Status status;

    @Column(name = "Consequences", length =  999999)
    private String consequences;

    @Column(name = "Artifacts", length =  999999)
    private String artifacts; //TODO: find correct type

    @Column(name = "Relations", length =  999999)
    private String relations; //TODO: find correct type ----> probably custom type Relation

}
