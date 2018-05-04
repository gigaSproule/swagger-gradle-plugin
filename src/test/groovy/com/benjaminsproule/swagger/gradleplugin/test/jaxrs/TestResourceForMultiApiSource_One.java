package com.benjaminsproule.swagger.gradleplugin.test.jaxrs;

import com.benjaminsproule.swagger.gradleplugin.test.model.MultiApiSourceCommonResponseModel;
import com.benjaminsproule.swagger.gradleplugin.test.model.MultiApiSourceParentOneResponseModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Api
@SwaggerDefinition(info = @Info(title = "OneApiTitle", version = "One", license = @License(name = "OneLicence")))
@Path("OneApi")
public class TestResourceForMultiApiSource_One {

    @GET
    public MultiApiSourceParentOneResponseModel getStuff() {
        MultiApiSourceParentOneResponseModel multiApiSourceParentOne = new MultiApiSourceParentOneResponseModel();
        multiApiSourceParentOne.setMultiApiSourceCommonResponseModel(new MultiApiSourceCommonResponseModel());
        multiApiSourceParentOne.getMultiApiSourceCommonResponseModel().setCommonProperty("onecommon");
        return multiApiSourceParentOne;
    }
}
