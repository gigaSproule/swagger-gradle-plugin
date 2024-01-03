package com.benjaminsproule.sample.springboot.mvc;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SampleResource {

    @GetMapping(path = "/home", produces = "application/json")
    @ApiOperation(value = "Return hello message")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("{\"Hello\": \"World!\"}");
    }

    @PostMapping(path = "/transform")
    @ApiOperation(value = "Transform enum")
    public ResponseEntity<EnumWrapper<AEnum>> transform(EnumWrapper<DEnum> enumWrapper) {
        return ResponseEntity.ok(new EnumWrapper<>());
    }
}
