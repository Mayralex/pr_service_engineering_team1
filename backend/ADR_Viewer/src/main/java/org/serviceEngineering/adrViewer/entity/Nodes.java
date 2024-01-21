package org.serviceEngineering.adrViewer.entity;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nodes {
    private String oid;
    private String committedDate;
    private String message;
    private String status;
}
