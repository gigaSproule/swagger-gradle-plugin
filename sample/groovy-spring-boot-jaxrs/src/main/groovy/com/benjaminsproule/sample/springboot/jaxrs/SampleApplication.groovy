package com.benjaminsproule.sample.springboot.jaxrs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class SampleApplication extends SpringBootServletInitializer {

    static void main(String[] args) {
        new SampleApplication()
            .configure(new SpringApplicationBuilder(SampleApplication.class))
            .run(args)
    }

}
