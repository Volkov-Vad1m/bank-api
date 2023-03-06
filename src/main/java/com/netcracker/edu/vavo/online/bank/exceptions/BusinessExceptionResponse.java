package com.netcracker.edu.vavo.online.bank.exceptions;

import lombok.Data;

import java.util.Map;

@Data
public class BusinessExceptionResponse {

    private String code;
    private Integer status;
    private String message;
    private Map<String, Object> details;

    public static BusinessExceptionResponse fromException(BusinessException exception) {
        BusinessExceptionResponse result = new BusinessExceptionResponse();
        result.code = exception.getCode();
        result.details = exception.getDetails();
        result.message = exception.getMessage();
        result.status = exception.getHttp();
        return result;
    }
}