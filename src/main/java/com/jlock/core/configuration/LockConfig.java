package com.jlock.core.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfig {
    @Value("${jlock.default-lock-timeout-minutes:10}")
    private long defaultLockTimeoutMinutes;

    @Bean
    public Duration defaultLockTimeout() {
        // Default 10-minute timeout
        if (defaultLockTimeoutMinutes <= 0)
            defaultLockTimeoutMinutes = 10;

        return Duration.ofMinutes(defaultLockTimeoutMinutes);
    }
}
