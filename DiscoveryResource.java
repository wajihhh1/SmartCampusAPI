package com.smartcampus.exception.mapper;
 
import com.smartcampus.exception.SensorUnavailableException;
 
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
 

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
 
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "403");
        error.put("error", "Forbidden");
        error.put("message", exception.getMessage());
        error.put("hint", "Change sensor status to ACTIVE before posting new readings.");
 
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
 