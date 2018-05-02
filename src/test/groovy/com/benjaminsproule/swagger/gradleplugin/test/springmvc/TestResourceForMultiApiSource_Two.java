package com.benjaminsproule.swagger.gradleplugin.test.springmvc;

import com.benjaminsproule.swagger.gradleplugin.test.model.MultiApiSourceCommonResponseModel;
import com.benjaminsproule.swagger.gradleplugin.test.model.MultiApiSourceParentTwoResponseModel;
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
public class TestResourceForMultiApiSource_Two {

    @GetMapping
    public MultiApiSourceParentTwoResponseModel getStuff() {
        MultiApiSourceParentTwoResponseModel multiApiSourceParentTwo = new MultiApiSourceParentTwoResponseModel();
        multiApiSourceParentTwo.setMultiApiSourceCommonResponseModel(new MultiApiSourceCommonResponseModel());
        multiApiSourceParentTwo.getMultiApiSourceCommonResponseModel().setCommonProperty("twocommon");
        return multiApiSourceParentTwo;
    }
}
