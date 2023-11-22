package org.serviceEngineering.adrViewer.div;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GraphqlRequestBody {

    private String query;
    private Object variables;
}
