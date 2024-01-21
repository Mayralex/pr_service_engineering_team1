package org.serviceEngineering.adrViewer.entity;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommitResponse {
    private Files[] files;
}


