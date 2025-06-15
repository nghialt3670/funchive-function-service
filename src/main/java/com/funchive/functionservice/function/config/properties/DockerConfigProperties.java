package com.funchive.functionservice.function.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "docker")
public class DockerConfigProperties {
    private String host;
    private boolean tlsVerify;

    // Timeout configurations for better Windows compatibility and large image pulls
    private int connectTimeoutSeconds = 120;        // 2 minutes for connection
    private int readTimeoutSeconds = 3600;          // 1 hour for large image pulls
    private int containerTimeoutSeconds = 600;      // 10 minutes for container execution
    private int imageBuildTimeOutSeconds = 3600;     // 1 hour for pulling large images
}
