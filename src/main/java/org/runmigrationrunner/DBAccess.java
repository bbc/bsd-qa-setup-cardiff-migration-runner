package org.runmigrationrunner;

import java.sql.*;
import java.util.Scanner;

public class DBAccess {

    private Connection CONN;
    private String USERNAME;
    private String PASSWORD;
    private String DBURL;
    private Scanner scanner;

    public DBAccess(String DBURL) {
        this.CONN = null;
        this.USERNAME = null;
        this.PASSWORD = null;
        this.DBURL = DBURL;
        this.scanner = new Scanner(System.in);
    }

    public void promptUserCredentials() {
        System.out.println("Please enter your USERNAME for QAW1 DB ...");
        USERNAME = scanner.nextLine();
        System.out.println("you entered : " + USERNAME);
        System.out.println("Please enter your PASSWORD for QAW1 DB ...");
        PASSWORD = scanner.nextLine();
    }

    public void connectDB() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            promptUserCredentials();
            CONN = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
            if (CONN != null) {
                System.out.println("Connected to QAW1 ...");
            }
        } catch (SQLException ex) {
            throw new SQLException("You have entered wrong credentials, try again ...");
        } catch ( ClassNotFoundException ex) {
            throw new ClassNotFoundException("Oracle driver used incorrect ...");
        }
    }

    public String runQuery(String query){
        String result = null;
        try{
            Statement stmt = CONN.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                result = rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void runUpdate(String update){
        try{
            Statement stmt = CONN.createStatement();

            ResultSet rs = stmt.executeQuery(update);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void disconnectDB(){
        try {
            if (CONN != null && !CONN.isClosed()) {
                CONN.close();
                System.out.println("QAW1 DB disconnected ... ");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
