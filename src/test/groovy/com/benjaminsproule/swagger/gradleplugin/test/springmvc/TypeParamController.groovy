package com.benjaminsproule.swagger.gradleplugin.test.springmvc

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

@RestController
@RequestMapping("/api/type-param")
class TypeParamController <T extends String> {

    @PostMapping
    ResponseEntity<T> post(@Valid @RequestBody final T typeParam) {
        return new ResponseEntity<>(typeParam, HttpStatus.CREATED)
    }
}
