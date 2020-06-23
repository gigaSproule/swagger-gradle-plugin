package com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.jaxrs

import com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.model.MultiApiSourceCommonResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.model.MultiApiSourceParentTwoResponseModel
import io.swagger.annotations.Api
import io.swagger.annotations.Info
import io.swagger.annotations.License
import io.swagger.annotations.SwaggerDefinition

import javax.ws.rs.GET
import javax.ws.rs.Path

@Api
@SwaggerDefinition(info = @Info(title = "TwoApiTitle", version = "Two", license = @License(name = "TwoLicense")))
@Path("/TwoApi")
class TestResourceForMultiApiSource_Two {

    @GET
    MultiApiSourceParentTwoResponseModel getStuff() {
        MultiApiSourceParentTwoResponseModel multiApiSourceParentTwo = new MultiApiSourceParentTwoResponseModel()
        multiApiSourceParentTwo.setMultiApiSourceCommonResponseModel(new MultiApiSourceCommonResponseModel())
        multiApiSourceParentTwo.getMultiApiSourceCommonResponseModel().setCommonProperty("twocommon")
        return multiApiSourceParentTwo
    }
}
