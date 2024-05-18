package com.enigma.proplybackend.model.response;

import lombok.Data;

@Data
public class ApiErrorResponse {
    private final String errorCode;
    private final String message;
    private final Integer statusCode;
    private final String statusName;
    private final String path;
    private final String method;
}
