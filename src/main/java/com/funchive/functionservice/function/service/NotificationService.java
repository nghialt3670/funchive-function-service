package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.model.dto.CompilationResultDto;
import com.funchive.functionservice.function.model.dto.ExecutionResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public void notifyCompilationStarted(String functionId) {
        try {
            String destination = "/topic/compilation/" + functionId;

            Map<String, Object> notification = Map.of(
                    "type", "COMPILATION_STARTED",
                    "functionId", functionId,
                    "timestamp", System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend(destination, notification);
            log.info("Sent compilation start notification for function {} to {}", functionId, destination);

        } catch (Exception e) {
            log.error("Failed to send compilation start notification for function {}: {}", functionId, e.getMessage());
        }
    }

    @Async
    public void notifyCompilationComplete(String functionId, CompilationResultDto result) {
        try {
            String destination = "/topic/compilation/" + functionId;

            Map<String, Object> notification = Map.of(
                    "type", "COMPILATION_COMPLETE",
                    "functionId", functionId,
                    "success", result.isSuccess(),
                    "duration", result.getDuration(),
                    "timestamp", System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend(destination, notification);
            log.info("Sent compilation notification for function {} to {}", functionId, destination);

        } catch (Exception e) {
            log.error("Failed to send compilation notification for function {}: {}", functionId, e.getMessage());
        }
    }

    @Async
    public void notifyExecutionStarted(String functionId) {
        try {
            String destination = "/topic/execution/" + functionId;

            Map<String, Object> notification = Map.of(
                    "type", "EXECUTION_STARTED",
                    "functionId", functionId,
                    "timestamp", System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend(destination, notification);
            log.info("Sent execution start notification for function {} to {}", functionId, destination);

        } catch (Exception e) {
            log.error("Failed to send execution start notification for function {}: {}", functionId, e.getMessage());
        }
    }

    @Async
    public void notifyExecutionComplete(String functionId, ExecutionResultDto result) {
        try {
            String destination = "/topic/execution/" + functionId;

            Map<String, Object> notification = Map.of(
                    "type", "EXECUTION_COMPLETE",
                    "functionId", functionId,
                    "success", result.isSuccess(),
                    "duration", result.getDuration(),
                    "timestamp", System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend(destination, notification);
        } catch (Exception e) {
            log.error("Failed to send execution complete notification for function {}: {}", functionId, e.getMessage());
        }
    }


} 