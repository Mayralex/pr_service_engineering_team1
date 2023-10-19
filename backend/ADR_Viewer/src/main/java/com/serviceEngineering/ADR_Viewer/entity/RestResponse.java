package com.serviceEngineering.ADR_Viewer.entity;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse {
    private String name;
    private String path;
    private String download_url;
    private String type;
    private String content;
    private String encoding;
}
