package com.benjaminsproule.swagger.gradleplugin.test.multispring.controller;

import com.benjaminsproule.swagger.gradleplugin.test.multispring.controller.api.ApiCommon;
import com.benjaminsproule.swagger.gradleplugin.test.multispring.controller.api.OneParent;
import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@SwaggerDefinition(info = @Info(title = "OneApiTitle", version = "One", license = @License(name = "OneLicence")))
@RestController
@RequestMapping(path = "OneApi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class OneController {

    @GetMapping
    public OneParent getStuff() {
        OneParent oneParent = new OneParent();
        oneParent.setApiCommon(new ApiCommon());
        oneParent.getApiCommon().setCommonProperty("onecommon");
        return oneParent;
    }
}
