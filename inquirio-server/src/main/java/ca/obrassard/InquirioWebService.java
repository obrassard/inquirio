package ca.obrassard;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : InquirioWebService.java
 * Date: 19-10-18
 */

@Path("/")
public class InquirioWebService {
    @GET @Path("/")
    public String getVersion(){
        return "Inquirio Web API v1.0<br>" +
                "All systems operational";
    }
}
