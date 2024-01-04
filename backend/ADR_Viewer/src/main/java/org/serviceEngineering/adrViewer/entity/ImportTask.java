package org.serviceEngineering.adrViewer.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ImportTask")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString
public class ImportTask {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "finished")
    private Boolean finished;

    @Column(name = "date")
    private String date;

    @Column(name = "repo_owner")
    private String repoOwner;

    @Column(name = "reponame")
    private String repoName;

    @Column(name = "directory_path")
    private String directoryPath;

    @Column(name = "branch")
    private String branch;

    public ImportTask(Boolean finished, String date, String repoOwner, String repoName, String directoryPath, String branch) {
        this.finished = finished;
        this.date = date;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.directoryPath = directoryPath;
        this.branch = branch;
    }
}
