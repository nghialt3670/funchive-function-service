package com.funchive.functionservice.common.model.dto;

import com.funchive.functionservice.common.exception.Error;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class ResponseBody<T> implements Serializable
{
    private String code;
    private String message;
    private T data;

    public static <T> ResponseBody<T> of(Error error)
    {
        return ResponseBody.<T>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .data(null)
                .build();
    }

    public static ResponseBody<String> of(Error error, Exception exception)
    {
        return ResponseBody.<String>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .data(exception.getMessage())
                .build();
    }

    public static <T> ResponseBody<T> of(String code, String message)
    {
        return ResponseBody.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ResponseBody<T> of(String code, String message, T data)
    {
        return ResponseBody.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseBody<T> of(String message, T data)
    {
        return ResponseBody.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseBody<T> of(T data)
    {
        return ResponseBody.<T>builder()
                .code("SUCCESS")
                .message("Success")
                .data(data)
                .build();
    }
}
