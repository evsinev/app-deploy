package com.acme.appdeploy.servlet.messages;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ErrorResponse {
    String errorId;
    String title;
    String type;
    Integer status;
    ErrorModelDetail detail;

    @Data
    @FieldDefaults(makeFinal = true, level = PRIVATE)
    @Builder
    public static class ErrorModelDetail {
        String path;
        String method;
    }
}
