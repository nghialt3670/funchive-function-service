package com.funchive.functionservice.function.config;

import com.funchive.functionservice.function.config.properties.DockerConfigProperties;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class DockerConfig {
    private final DockerConfigProperties dockerConfigProperties;

    @Bean
    public DockerClientConfig dockerClientConfig() {
        log.info("Configuring Docker client with host: {}", dockerConfigProperties.getHost());
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerConfigProperties.getHost())
                .withDockerTlsVerify(dockerConfigProperties.isTlsVerify())
                .build();
    }

    @Bean
    public DockerClient dockerClient(DockerClientConfig config) {
        // Use configurable timeouts for Windows Docker Desktop compatibility
        // Image pulling, and compilation can take longer on Windows systems
        OkDockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .connectTimeout(dockerConfigProperties.getConnectTimeoutSeconds())    // Configurable connect timeout
                .readTimeout(dockerConfigProperties.getReadTimeoutSeconds())         // Configurable read timeout
                .build();

        DockerClient client = DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();

        try {
            // Test Docker connection with a simple ping
            log.info("Testing Docker connection...");
            client.pingCmd().exec();
            log.info("✓ Docker client successfully connected to: {}", dockerConfigProperties.getHost());
            log.info("✓ Docker client configured with timeouts - Connect: {}s, Read: {}s",
                    dockerConfigProperties.getConnectTimeoutSeconds(),
                    dockerConfigProperties.getReadTimeoutSeconds());
        } catch (Exception e) {
            log.error("✗ Failed to connect to Docker daemon at {}: {}", dockerConfigProperties.getHost(), e.getMessage());
            log.error("✗ Please ensure Docker Desktop is running and the named pipe is accessible");
            log.error("✗ For Windows users, verify Docker Desktop is set to use Linux containers");
            log.error("✗ Try running 'docker ps' in command line to verify Docker is working");
        }

        return client;
    }
} 