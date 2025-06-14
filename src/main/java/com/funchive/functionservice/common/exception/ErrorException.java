package com.funchive.functionservice.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.function.Supplier;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ErrorException extends RuntimeException {
    private final Instant timestamp;
    private final Error error;

    public ErrorException(Error error) {
        super(error.getMessage());
        this.error = error;
        timestamp = Instant.now();
    }

    public static ErrorException of(Error error) {
        return new ErrorException(error);
    }

    public static Supplier<ErrorException> supplierOf(Error error) {
        return () -> new ErrorException(error);
    }
}
