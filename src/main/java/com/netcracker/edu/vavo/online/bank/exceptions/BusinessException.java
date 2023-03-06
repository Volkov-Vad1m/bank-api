package com.netcracker.edu.vavo.online.bank.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private final String code;
    private final Integer http;
    private final Map<String, Object> details;

    BusinessException(String code, String message, Integer http, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.http = http;
        this.details = Objects.requireNonNullElse(details, Map.of());
    }



}