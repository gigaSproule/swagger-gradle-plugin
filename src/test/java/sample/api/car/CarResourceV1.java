package sample.api.car;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: kongchen
 * Date: 5/13/13
 */
@Path("/car.json")
@Api(value = "/car", description = "Operations about cars", position = 1, protocols = "http")
@Produces({"application/json"})
public class CarResourceV1 {
    @GET
    @Path("/{carId}")
    @ApiOperation(value = "Find car by ID", notes = "To get car info by car's Id",
        response = sample.model.Car.class, position = 2,
        authorizations = @Authorization(value = "oauth2", scopes = {@AuthorizationScope(scope = "car1", description = "car1 des get")}))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Car not found")})
    @ApiImplicitParams(value = {@ApiImplicitParam(name = "ETag", paramType = "response_header", value = "version", dataType = "string")})
    public Response getCarById(
        @ApiParam(value = "ID of car that needs to be fetched", allowableValues = "range[1,10]",
            required = true) @PathParam("carId") String carId,
        @ApiParam(allowableValues = "application/json, application/*")
        @HeaderParam("Accept") MediaType accept,
        @ApiParam(name = "e")
        @QueryParam("e") String e)
        throws NotFoundException {
        return Response.noContent().build();
    }

    @GET
    @ApiOperation(value = "search cars", notes = "Search cars by query",
        response = sample.model.Car.class, responseContainer = "List", position = 1,
        authorizations =
        @Authorization(value = "oauth2", scopes = {@AuthorizationScope(scope = "car1", description = "car1 des")}))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Bad query")})
    public Response getCars(
        @ApiParam(allowableValues = "application/json, application/*")
        @HeaderParam("Accept") MediaType accept,
        @ApiParam(name = "query")
        @QueryParam("q") String q)
        throws NotFoundException {
        return Response.noContent().build();
    }

    @DELETE
    @ApiOperation(value = "remove a car", position = 4)
    @ApiResponses(value = {@ApiResponse(code = 403, message = "version not match")})
    public void deleteCar(
        @ApiParam(name = "version")
        @HeaderParam(value = "version")
        String version,
        @ApiParam("carId")
        @QueryParam("id")
        String carid
    ) {
    }
}
