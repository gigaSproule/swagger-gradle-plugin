package com.benjaminsproule.sample.scala

import org.glassfish.jersey.server.ResourceConfig
import org.springframework.context.annotation.Configuration

@Configuration
class SampleConfig extends ResourceConfig {
    register(classOf[SampleResource])
}
