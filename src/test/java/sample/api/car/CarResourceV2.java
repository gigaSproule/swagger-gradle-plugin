package sample.api.car;


import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: kongchen
 * Date: 5/13/13
 */
@Path("/v2/car.json")
@Api(value = "/v2/car", description = "Operations about cars", position = 2,
    authorizations = @Authorization(value = "oauth2", scopes = {@AuthorizationScope(scope = "car2", description = "nothing")}))
@Produces({"application/json"})
public class CarResourceV2 {
    @GET
    @Path("/{carId}")
    @ApiOperation(value = "Find car by ID", notes = "To get car info by car's Id",
        response = sample.model.Customer.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Car not found")})
    public Response getCarById(
        @ApiParam(value = "ID of car that needs to be fetched", allowableValues = "range[10,20]",
            required = true) @PathParam("carId") String carId)
        throws NotFoundException {
        return Response.noContent().build();
    }
}
