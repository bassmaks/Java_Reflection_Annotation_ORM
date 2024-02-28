package edu.school21.repositories;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

public class RepositoryJdbcImpl {

    public RepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;


    public boolean createTable() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();

            // Drop table if exists
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS \"user\" CASCADE;");

            // Create table if not exists
            statement.execute("CREATE TABLE IF NOT EXISTS \"user\" (\n" +
                    "\tuser_id SERIAL PRIMARY KEY NOT NULL,\n" +
                    "\tfirstName VARCHAR (20) NOT NULL UNIQUE,\n" +
                    "\tlastName VARCHAR (20),\n" +
                    "\tage INTEGER\n" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }
    public void close() {
        if (dataSource instanceof Closeable) {
            try {
                ((Closeable) dataSource).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) dataSource).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
