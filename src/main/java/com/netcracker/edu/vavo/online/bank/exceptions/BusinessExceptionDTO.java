package com.netcracker.edu.vavo.online.bank.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessExceptionDTO {
    private String code;
    private Integer http;
    private String message;
}