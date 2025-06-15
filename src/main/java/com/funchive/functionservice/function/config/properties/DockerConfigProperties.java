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
    private int connectTimeoutSeconds;
    private int readTimeoutSeconds;
    private int runTimeoutSeconds;
    private int buildTimeoutSeconds;
}
