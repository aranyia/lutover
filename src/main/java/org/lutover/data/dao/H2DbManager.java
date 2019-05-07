package org.lutover.data.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DbManager {

    private static Connection conn;

    private static Statement statement;

    public static void createDB() throws ClassNotFoundException, SQLException {
        createConnection();
        initializeTables();
    }

    public static void closeConnection() throws SQLException {
        statement.close();
        conn.close();
    }

    static Statement getStatement() {
        return statement;
    }

    private static void createConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:lutover");
        statement = conn.createStatement();
    }

    private static void initializeTables() throws SQLException {
        statement.execute("CREATE TABLE TRANSACTION(" +
                "reference VARCHAR2(64) UNIQUE NOT NULL, createdat TIMESTAMP DEFAULT SYSTIMESTAMP, " +
                "source_acc VARCHAR2(48), target_acc VARCHAR2(48), currency VARCHAR2(3), " +
                "amount NUMBER(38,2), fxrate NUMBER(38,6), status VARCHAR2(64))");

        statement.execute("CREATE TABLE ACCOUNT(" +
                "id VARCHAR2(48) UNIQUE NOT NULL, currency VARCHAR2(3) NOT NULL, balance NUMBER(38,2) DEFAULT 0)");

        statement.execute("CREATE TABLE FX_RATE(" +
                "source_currency VARCHAR2(3) NOT NULL, target_currency VARCHAR2(3) NOT NULL, fxrate NUMBER(38,6) NOT NULL)");

        statement.execute("INSERT INTO ACCOUNT VALUES('RV10000101', 'USD', 270.50)");
        statement.execute("INSERT INTO ACCOUNT VALUES('RV10000102', 'AED', 4560.00)");
        statement.execute("INSERT INTO ACCOUNT VALUES('RV10000103', 'USD', 1400.00)");
        statement.execute("INSERT INTO ACCOUNT VALUES('RV10000104', 'HUF', 360000.00)");

        statement.execute("INSERT INTO FX_RATE VALUES ('AED','HUF',78.5835)");
        statement.execute("INSERT INTO FX_RATE VALUES ('AED','USD',0.272294)");
        statement.execute("INSERT INTO FX_RATE VALUES ('HUF','AED',0.012725)");
        statement.execute("INSERT INTO FX_RATE VALUES ('HUF','USD',0.003465)");
        statement.execute("INSERT INTO FX_RATE VALUES ('USD','AED',3.6725)");
        statement.execute("INSERT INTO FX_RATE VALUES ('USD','HUF',288.598)");
    }
}