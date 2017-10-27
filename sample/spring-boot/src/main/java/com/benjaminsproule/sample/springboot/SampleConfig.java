package com.benjaminsproule.sample.springboot;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleConfig extends ResourceConfig {
    public SampleConfig() {
        register(SampleResource.class);
    }
}
