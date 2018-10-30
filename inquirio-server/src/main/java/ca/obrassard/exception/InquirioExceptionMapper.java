package ca.obrassard.exception;

import ca.obrassard.exception.APIRequestException;
import com.google.gson.Gson;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
        if (ex.error == APIErrorCodes.Forbidden){
            System.err.println("Un utilisateur authentifié à tenté d'accéder à une ressource non authorisée");
        } else if (ex.error == APIErrorCodes.AccesDenied){
            System.err.println("Un utilisateur non authentifié à tenté d'accéder à une ressource non authorisée");
        }
        else if (ex.targetAttribute == null || ex.targetAttribute.equals("")){
            System.err.println(ex.getMessage()+" : "+ ex.error);
        } else {
            System.err.println(ex.getMessage()+". Violation : "+ ex.error + ". L'erreur est survenue sur le champs : " + ex.targetAttribute);
        }

        if (ex.error == APIErrorCodes.Forbidden || ex.error == APIErrorCodes.AccesDenied){
            return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON)
                    .entity( new Gson().toJson(ex.toResponse())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
                    .entity( new Gson().toJson(ex.toResponse())).build();
        }

    }
}
