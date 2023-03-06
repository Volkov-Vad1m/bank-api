package com.netcracker.edu.vavo.online.bank.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BusinessExceptionManager {

    private static final String ISE_CODE = "ERR-000";
    private static final String ISE_MESSAGE = "Internal Server Error";

    private final Map<String, BusinessExceptionDTO> exceptions = new HashMap<>(128);

    @Value("classpath:/errors.yaml")
    private Resource errorsConfig;

    @PostConstruct
    @SneakyThrows
    private void loadErrors() {
        ObjectMapper mapper = new YAMLMapper();
        List<BusinessExceptionDTO> items = mapper.readValue(errorsConfig.getInputStream(),
                new TypeReference<>() {});

        exceptions.put(ISE_CODE, new BusinessExceptionDTO(ISE_CODE, 500, ISE_MESSAGE));
        exceptions.putAll(
                items.stream().collect(Collectors.toMap(BusinessExceptionDTO::getCode, Function.identity())));


    }

    public void throwsException(String code, Map<String, Object> details) {
        BusinessExceptionDTO exceptionDTO = exceptions.get(code);
        if(exceptionDTO == null) {
            exceptionDTO = exceptions.get(ISE_CODE);

        }

        throw new BusinessException(
                code,
                exceptionDTO.getMessage(),
                exceptionDTO.getHttp(),
                details
        );

    }
}
