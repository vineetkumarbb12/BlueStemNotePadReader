package com.sonata;

import java.io.BufferedReader;

import java.io.FileReader;

import java.io.FileWriter;

import java.io.IOException;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

public class Comparision {
	
	static private String HTMLTemplate = "<!doctype html><html><head><title>Query Results</title><style> table, th, td { border: 1px solid black; border-collapse: collapse; }</style></head><body><div align='center'><table><tr><th>ID</th><th>Name</th><th>Is Present In table</th><th>Table Name</th></tr>@@ROW_DATA@@</table></div></body></html>";
	
	public static void main(String[] args) {
		// Define variables for database connection
		String rowData="";
		Connection connection = null;
		try {
			// Class.forName("com.mysql.jdbc.Driver");
			DBManager dbManager = new DBManager();
			// Establish a database connection
			connection = dbManager.getConnection();
			// Read data from the Notepad file
			BufferedReader notepadReader = new BufferedReader(new FileReader("D:/TextPadReader/Notepad1.txt"));
			String notepadLine;
			while ((notepadLine = notepadReader.readLine()) != null) {
				// Retrieve data from the database for comparison
				String sql = "SELECT id, sal, email_id, name FROM employees WHERE id = ? and upper(name)=?";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				System.out.println("notepadLine : " + notepadLine);
				
				String[] dataArr = notepadLine.split(",");
				System.out.println("dataArr[0] : " + dataArr[0]);
				
				preparedStatement.setString(1, dataArr[0]);
				preparedStatement.setString(2, dataArr[1].toUpperCase().trim());
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					// A match is found
					String id = resultSet.getString("id");
					String sal = resultSet.getString("sal");
					String email_id = resultSet.getString("email_id");
					String name = resultSet.getString("name");
					// Perform your comparison logic here
					System.out.println("Match found for: " + notepadLine);
					System.out.println("Database Data: " + id + ", " + sal + ", " + email_id + ", " + name);
					rowData = rowData + "<tr><td>"+resultSet.getString("id")+"</td><td>"+resultSet.getString("name")+"</td><td>true</td><td>employees</td></tr>";
				} else {
					// No match is found
					System.out.println("No match found for: " + notepadLine);
				}
				resultSet.close();
				preparedStatement.close();
			}
			notepadReader.close();
			
			HTMLTemplate=HTMLTemplate.replace("@@ROW_DATA@@", rowData);
			
			String resultFilePath= "D:/TextPadReader/Result/Result.html";
			FileWriter myWriter = new FileWriter(resultFilePath);
		    myWriter.write(HTMLTemplate);
		    myWriter.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
