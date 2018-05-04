package com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.springmvc

import com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.model.MultiApiSourceCommonResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.model.MultiApiSourceParentOneResponseModel
import io.swagger.annotations.Api
import io.swagger.annotations.Info
import io.swagger.annotations.License
import io.swagger.annotations.SwaggerDefinition
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api
@SwaggerDefinition(info = @Info(title = "OneApiTitle", version = "One", license = @License(name = "OneLicence")))
@RestController
@RequestMapping(path = "/OneApi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class TestResourceForMultiApiSource_One {

    @GetMapping
    MultiApiSourceParentOneResponseModel getStuff() {
        MultiApiSourceParentOneResponseModel multiApiSourceParentOne = new MultiApiSourceParentOneResponseModel()
        multiApiSourceParentOne.setMultiApiSourceCommonResponseModel(new MultiApiSourceCommonResponseModel())
        multiApiSourceParentOne.getMultiApiSourceCommonResponseModel().setCommonProperty("onecommon")
        return multiApiSourceParentOne
    }
}
