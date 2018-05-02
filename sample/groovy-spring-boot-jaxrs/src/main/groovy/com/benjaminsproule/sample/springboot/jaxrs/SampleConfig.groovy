package com.benjaminsproule.sample.springboot.jaxrs

import org.glassfish.jersey.server.ResourceConfig
import org.springframework.context.annotation.Configuration

@Configuration
class SampleConfig extends ResourceConfig {
    SampleConfig() {
        register(SampleResource.class)
    }
}
