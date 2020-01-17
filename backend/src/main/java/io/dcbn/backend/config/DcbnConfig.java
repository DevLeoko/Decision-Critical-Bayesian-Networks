package io.dcbn.backend.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DcbnConfig {

    // Number of Time Steps
    public static final int TIME_STEPS = 5;
    // Length of a step in milli seconds
    public static final long TIME_STEP_LENGTH = 300000;
}
