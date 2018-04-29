package com.benjaminsproule.swagger.gradleplugin.test.multispring.controller;

import com.benjaminsproule.swagger.gradleplugin.test.multispring.controller.api.ApiCommon;
import com.benjaminsproule.swagger.gradleplugin.test.multispring.controller.api.TwoParent;
import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@SwaggerDefinition(info = @Info(title = "TwoApiTitle", version = "Two", license = @License(name = "TwoLicense")))
@RestController
@RequestMapping(path = "TwoApi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TwoController {

    @GetMapping
    public TwoParent getStuff() {
        TwoParent twoParent = new TwoParent();
        twoParent.setApiCommon(new ApiCommon());
        twoParent.getApiCommon().setCommonProperty("twocommon");
        return twoParent;
    }
}
