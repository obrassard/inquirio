package ca.obrassard;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : DatasourceConfig.java
 * Date: 19-10-18
 */

// https://www.youtube.com/watch?v=__QxqLGVDbg
public class DatasourceConfig {
    public static DSLContext getContext() throws SQLException {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:mysql://debian.obrassard.ca:3306");
//        config.setSchema("inquirio");
//        config.setUsername("inquirioauth");
//        config.setPassword("5kvvpz4HiktywVcq");

        String userName = "inquirioauth";
        String password = "5kvvpz4HiktywVcq";
        String url = "jdbc:mysql://debian.obrassard.ca:3306/inquirio";

        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        Connection connection = DriverManager.getConnection(url, userName, password);
       return DSL.using(connection, SQLDialect.MYSQL);
    }
}