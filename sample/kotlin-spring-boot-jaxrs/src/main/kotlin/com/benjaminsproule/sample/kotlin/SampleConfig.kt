package com.benjaminsproule.sample.kotlin;

import org.glassfish.jersey.server.ResourceConfig
import org.springframework.context.annotation.Configuration

@Configuration
class SampleConfig : ResourceConfig() {
    init {
        this.register(SampleResource::class)
    }
}
