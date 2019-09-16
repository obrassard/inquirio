package ca.obrassard;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// https://www.youtube.com/watch?v=__QxqLGVDbg
public class DatasourceConfig {
    public static DSLContext getContext() throws SQLException {

        String url = "jdbc:mysql://localhost:3306/inquirio";
        String userName = "root";
        String password = "";

        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        Connection connection = DriverManager.getConnection(url, userName, password);
       return DSL.using(connection, SQLDialect.MYSQL);
    }
}
