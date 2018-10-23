package ca.obrassard;

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
public class InquirioExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public javax.ws.rs.core.Response toResponse(Exception ex) {
        ex.printStackTrace();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity( new Gson().toJson(ex)).build();
    }
}
