package com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.jaxrs

import com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.model.MultiApiSourceCommonResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.model.MultiApiSourceParentOneResponseModel
import io.swagger.annotations.Api
import io.swagger.annotations.Info
import io.swagger.annotations.License
import io.swagger.annotations.SwaggerDefinition

import javax.ws.rs.GET
import javax.ws.rs.Path

@Api
@SwaggerDefinition(info = @Info(title = "OneApiTitle", version = "One", license = @License(name = "OneLicence")))
@Path("/OneApi")
class TestResourceForMultiApiSource_One {

    @GET
    MultiApiSourceParentOneResponseModel getStuff() {
        MultiApiSourceParentOneResponseModel multiApiSourceParentOne = new MultiApiSourceParentOneResponseModel()
        multiApiSourceParentOne.setMultiApiSourceCommonResponseModel(new MultiApiSourceCommonResponseModel())
        multiApiSourceParentOne.getMultiApiSourceCommonResponseModel().setCommonProperty("onecommon")
        return multiApiSourceParentOne
    }
}
