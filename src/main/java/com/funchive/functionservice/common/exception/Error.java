package com.funchive.functionservice.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Error
{
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "Entity not found"),
    NULL_USER_ID("NULL_USER_ID", "Null user id"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error");

    public static final Map<Error, Integer> HTTP_STATUS = new HashMap<>();

    static
    {
        HTTP_STATUS.put(Error.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND.value());
        HTTP_STATUS.put(Error.NULL_USER_ID, HttpStatus.BAD_REQUEST.value());
        HTTP_STATUS.put(Error.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private final String code;
    private final String message;

    Error(String code, String message)
    {
        this.code = code;
        this.message = message;
    }

}
