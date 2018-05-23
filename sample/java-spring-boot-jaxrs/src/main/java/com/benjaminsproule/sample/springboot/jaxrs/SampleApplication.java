package com.benjaminsproule.sample.springboot.jaxrs;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class SampleApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new SampleApplication()
            .configure(new SpringApplicationBuilder(SampleApplication.class))
            .run(args);
    }

}
