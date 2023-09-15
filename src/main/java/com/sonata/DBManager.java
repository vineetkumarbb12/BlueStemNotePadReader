package com.sonata;

import java.sql.*;

public class DBManager {
    public static void main(String[] args) {
    	Connection con = null;
        try {
        	DBManager dbManager = new DBManager();
            con = dbManager.getConnection();
            System.out.println("con is : " + con);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        	 try {
                 if(con != null) {
                	 con.close();
                	 con=null;
                 }
             } catch(Exception e) {
                 e.printStackTrace();
             } 
        }
    }
    
    Connection getConnection() {
    	Connection con =null;
    	try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/fullstacks","root","");
        } catch(Exception e) {
            e.printStackTrace();
        }
    	return con;
    }
}
