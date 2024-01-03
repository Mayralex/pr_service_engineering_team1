package org.serviceEngineering.adrViewer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ADRPageDTO {
    List<ADR> data;
    PaginationInfo paginationInfo;

    @Getter
    @AllArgsConstructor
    public static class PaginationInfo {
        int pageOffset;
        int pageCount;
        int limit;
    }
}