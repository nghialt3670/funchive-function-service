package com.funchive.functionservice.common.config.handler;

import com.funchive.functionservice.common.exception.Error;
import com.funchive.functionservice.common.exception.ErrorException;
import com.funchive.functionservice.common.model.dto.ResponseBody;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ResponseBody<ObjectUtils.Null>> handleErrorException(ErrorException exception)
    {
        return ResponseEntity
                .status(Error.HTTP_STATUS.get(exception.getError()))
                .body(ResponseBody.of(exception.getError()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseBody<String>> handleRuntimeException(RuntimeException exception)
    {
        return ResponseEntity
                .status(Error.HTTP_STATUS.get(Error.INTERNAL_SERVER_ERROR))
                .body(ResponseBody.of(Error.INTERNAL_SERVER_ERROR, exception));
    }
}

