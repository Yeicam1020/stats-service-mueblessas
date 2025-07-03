package com.mueblessas.stats.web.statistics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsResponse<T> {
    private int code;
    private String message;
    private T data;
}
