package ca.obrassard;

import ca.obrassard.exception.APIRequestException;
import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : InquirioExceptionMapper.java
 * Date: 23-10-18
 */
@Provider
public class InquirioExceptionMapper implements ExceptionMapper<APIRequestException> {
    @Override
    public javax.ws.rs.core.Response toResponse(APIRequestException ex) {
        ex.printStackTrace();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity( new Gson().toJson(ex.toResponse())).build();
    }
}
