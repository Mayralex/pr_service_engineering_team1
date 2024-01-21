package org.serviceEngineering.adrViewer.entity;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Files {
    private String sha;
    private String filename;
    private String status;
    private String raw_url;
}
