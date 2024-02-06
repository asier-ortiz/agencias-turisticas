package util;

import com.db4o.ObjectContainer;
import controller.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String ORACLE_USERNAME = "asier";
    private static final String ORACLE_PASSWORD = "asier";
    private static final String SQLITE_URL = "jdbc:sqlite:";
    private static final String SQLITE_PATH = ".\\agenciasturisticas.s3db";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/agenciasturisticas";
    private static final String MYSQL_USERNAME = "asier";
    private static final String MYSQL_PASSWORD = "asier";
    public static int dbOption;
    public static DataBase dataBase;

    public static void setDbOption(int dbOption) {
        DBConnectionUtil.dbOption = dbOption;
    }

    public static void setDataBase(DataBase dataBase) {
        DBConnectionUtil.dataBase = dataBase;
    }

    public static Connection getConexion() {
        Connection connection = null;
        switch (dbOption) {
            case 1 -> {
                try {
                    Class.forName("oracle.jdbc.OracleDriver");
                    connection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
                } catch (SQLException | ClassNotFoundException ex) {
                    DialogManagerUtil.showErrorDialog("Error en la conexión de la base de datos");
                }
                return connection;
            }
            case 2 -> {
                try {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection(SQLITE_URL + SQLITE_PATH);
                } catch (SQLException | ClassNotFoundException ex) {
                    DialogManagerUtil.showErrorDialog("Error en la conexión de la base de datos");
                }
                return connection;
            }
            case 3 -> {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
                } catch (SQLException | ClassNotFoundException ex) {
                    DialogManagerUtil.showErrorDialog("Error en la conexión de la base de datos");
                }
                return connection;
            }
        }
        return connection;
    }

    public static ObjectContainer getDataBaseContainer() {
        return DB4OUtil.getDataBaseContainer();
    }
}