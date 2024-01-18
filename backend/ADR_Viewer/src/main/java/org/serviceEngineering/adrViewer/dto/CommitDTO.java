package org.serviceEngineering.adrViewer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CommitDTO {
    private String oid;
    private String committedDate;
    private String message;
}

/*@Getter
@ToString
public class CommitDTO {

    private CommitData commit;

    @Getter
    public class CommitData {
        private Commit commit;

        @Getter
        public class Commit {
            private String oid;
            private String committedDate;
            private String message;
        }
    }

}*/
