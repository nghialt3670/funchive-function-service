package com.funchive.functionservice.pipeline.model.dao;

public enum ExecutionStatus {
    IDLE,
    PENDING,
    EXECUTING,
    CANCELING,
    CANCELED,
    FAILED,
    TIMEOUT,
    EXECUTED,
}
