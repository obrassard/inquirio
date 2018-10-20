package ca.obrassard;

import ca.obrassard.jooqentities.tables.Users;
import ca.obrassard.jooqentities.tables.records.UsersRecord;
import ca.obrassard.model.User;
import org.jooq.DSLContext;
import org.jooq.Result;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : InquirioWebService.java
 * Date: 19-10-18
 */

@Path("/")
public class InquirioWebService {

    private DSLContext context;

    public InquirioWebService() throws SQLException {
        this.context = DatasourceConfig.getContext();
    }

    @GET
    public String getVersion(){
        return "Inquirio Web API v1.0<br>" +
                "All systems operational";
    }

    @GET @Path("users")
    public List<User> getUsers(){
        Result<UsersRecord> result = context.selectFrom(Users.USERS).fetch();

        List<User> users = new ArrayList<>();

        for (UsersRecord r: result){
            users.add(new User(r));
        }
        return users;
    }
}
