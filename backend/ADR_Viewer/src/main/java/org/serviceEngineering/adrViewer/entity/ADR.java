package org.serviceEngineering.adrViewer.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ADR")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString
public class ADR {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "adr_id", nullable = false)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "context", length =  999999)
    private String context;

    @Column(name = "decision", length =  999999)
    private String decision;

    @Column(name = "status")
    private String status;

    @Column(name = "consequences", length =  999999)
    private String consequences;

    @OneToMany(mappedBy = "adr", cascade = CascadeType.ALL)
    @Column(name = "artifacts")
    private List<Artifact> artifacts;

    @OneToMany(mappedBy = "adr", cascade = CascadeType.ALL)
    @Column(name = "relations")
    private List<Relation> relations;

    @Column(name = "date")
    private String date;

    @Column(name = "commit")
    private String commit;

    @Column(name = "import_task_id")
    private int importTaskId;

}
