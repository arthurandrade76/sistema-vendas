package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoPostgres {
    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DATABASE = "sistema_vendas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do PostgreSQL não foi encontrado na pasta lib!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}