package com.sonata;

import java.io.BufferedReader;

import java.io.FileReader;

import java.io.IOException;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

public class Comparision {
	public static void main(String[] args) {
		// Define variables for database connection

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
				String sql = "SELECT id, sal, email_id, name FROM employees WHERE id = ?";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				System.out.println("notepadLine : " + notepadLine);
				preparedStatement.setString(1, notepadLine);
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
				} else {
					// No match is found
					System.out.println("No match found for: " + notepadLine);
				}
				resultSet.close();
				preparedStatement.close();
			}
			notepadReader.close();
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
