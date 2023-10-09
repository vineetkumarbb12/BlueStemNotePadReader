package com.sonata.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sonata.db.DBManager;
import com.sonata.query.DBQuery;

public class FileProcess {

	// private static String
	// INPUT_FILE_PATH="D:/TextPadReader/Input-File/NotepadPoc.csv";
	private static String INPUT_FILE_PATH = "D:/TextPadReader/Input-File/NotepadPileLine.txt";
	// private static String
	// INPUT_FILE_PATH="D:/TextPadReader/Input-File/NotdpadFixedWidthFile.txt";
	private static String OUTPUT_FILE_PATH = "D:/TextPadReader/Result";
	private static String FILE_TYPE = "PIPE_LINE"; // CSV / FIXED_WIDTH / PIPE_LINE
	private static boolean isFileHeaderAvailable = false;

	static private String HTMLTemplate = "<!doctype html> <html> <head> <title>Query Results</title> <style> .styled-table { border-collapse: collapse; margin: 25px 0; font-size: 0.9em; font-family: sans-serif; min-width: 400px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.15); } .styled-table thead tr { background-color: #009879; color: #ffffff; text-align: left; } .styled-table th, .styled-table td { padding: 12px 15px; width: 250px;} .styled-table tbody tr { border-bottom: 1px solid #dddddd; } .styled-table tbody tr:nth-of-type(even) { background-color: #f3f3f3; } .styled-table tbody tr:last-of-type { border-bottom: 2px solid #009879; } .styled-table tbody tr.active-row { font-weight: bold; color: #009879; } </style> </head> <body> <div> <b><label style='color: brown;padding-left:45%;font-size: 22px;'>Test Result</label><label style='color: blue;padding-left:20%;font-size: 16px;'>Test Date & Time: @@DATE@@</label></b></div> <div><table class='styled-table'> <thead> <tr> <th>ID</th> <th>Name</th> <th>Salary</th> <th>Email</th> <th>Is Present In table</th> <th>Table Name</th> </tr> </thead> <tbody> @@SUCCESS_ROW_DATA@@ @@FAIL_ROW_DATA@@ </tbody> </table> </div> </body> </html>";

	public static boolean fileProcessing() {
		System.out.println("Inside fileProcessing");
		boolean flag = false;
		String successRowData = "";
		String failRowData = "";
		String notepadLine = null;
		Connection connection = null;
		BufferedReader notepadReader = null;
		String[] dataArr = null;
		String[] dataArr1 = null;
		try {
			DBManager dbManager = new DBManager();
			connection = dbManager.getConnection();
			// Step-I input file reading
			notepadReader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
			List<String> fileData = new ArrayList<>();
			while ((notepadLine = notepadReader.readLine()) != null) {
				if (isFileHeaderAvailable) {
					isFileHeaderAvailable = false;
				} else {
					fileData.add(notepadLine);
				}
			}
			notepadReader.close();
			System.out.println("fileData size : " + fileData.size());
			System.out.println("fileData : " + fileData);

			// Step-II insert records count in first table
			int table1Seq = fetchNextSequenceValue(connection, "table1_sequence");
			boolean tbl1InsRes = insertTable1(connection, table1Seq, fileData.size(), INPUT_FILE_PATH);
			System.out.println("tbl1InsRes : " + tbl1InsRes);
			
			//Step-III insert records data in second table
			for (int i = 0; i < fileData.size(); i++) {
				notepadLine = fileData.get(i);
				if (FILE_TYPE.equalsIgnoreCase("CSV")) {
					dataArr1 = notepadLine.split(",");
				} else if (FILE_TYPE.equalsIgnoreCase("PIPE_LINE")) {
					dataArr1 = notepadLine.split("\\|");
				} else if (FILE_TYPE.equalsIgnoreCase("FIXED_WIDTH")) {
					dataArr1 = notepadLine.split(",");
				}

				System.out.println("dataArr size : " + dataArr1.length);
				System.out.println("dataArr[0] : " + dataArr1[0]);
				System.out.println("dataArr[1] : " + dataArr1[1]);
				System.out.println("dataArr[2] : " + dataArr1[2]);
				System.out.println("dataArr[3] : " + dataArr1[3]);
				boolean tbl2InsRes = insertTable2(connection, table1Seq, dataArr1[0], dataArr1[1], dataArr1[2], dataArr1[3]);
				System.out.println("tbl2InsRes : " + tbl2InsRes);
			}
			
			
			//Step-IV data search pending
			for (int i = 0; i < fileData.size(); i++) {
				notepadLine = fileData.get(i);

				String sql = DBQuery.TABLE1_QRY;
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				System.out.println("notepadLine : " + notepadLine);

				if (FILE_TYPE.equalsIgnoreCase("CSV")) {
					dataArr = notepadLine.split(",");
				} else if (FILE_TYPE.equalsIgnoreCase("PIPE_LINE")) {
					dataArr = notepadLine.split("\\|");
				} else if (FILE_TYPE.equalsIgnoreCase("FIXED_WIDTH")) {
					dataArr = notepadLine.split(",");
				}

				System.out.println("dataArr size : " + dataArr.length);
				System.out.println("dataArr[0] : " + dataArr[0]);
				System.out.println("dataArr[1] : " + dataArr[1]);
				System.out.println("dataArr[2] : " + dataArr[2]);
				System.out.println("dataArr[3] : " + dataArr[3]);

				preparedStatement.setString(1, dataArr[0].trim());
				preparedStatement.setString(2, dataArr[1].toUpperCase().trim());
				preparedStatement.setString(3, dataArr[2].trim());
				preparedStatement.setString(4, dataArr[3].toUpperCase().trim());
				ResultSet resultSet = preparedStatement.executeQuery();

				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				String tableName = resultSetMetaData.getTableName(4);
				System.out.println("Name of the table : " + tableName);

				if (resultSet.next()) {
					// A match is found
					String id = resultSet.getString("id");
					String sal = resultSet.getString("sal");
					String emailId = resultSet.getString("email_id");
					String name = resultSet.getString("name");
					// Perform your comparison logic here
					System.out.println("Match found for: " + notepadLine);
					System.out.println("Database Data: " + id + ", " + sal + ", " + emailId + ", " + name);
					successRowData = successRowData + "<tr><td>" + id + "</td><td>" + name + "</td><td>" + sal + "</td><td>" + emailId + "</td><td style='color:green'>true</td><td>" + tableName + "</td></tr>";
				} else {
					// No match is found
					System.out.println("No match found for: " + notepadLine);
					failRowData = failRowData + "<tr><td>" + dataArr[0] + "</td><td>" + dataArr[1] + "</td><td>" + dataArr[2] + "</td><td>" + dataArr[3] + "</td><td style='color:red'>false</td><td>Not present in table</td></tr>";
				}
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			}

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
			HTMLTemplate = HTMLTemplate.replace("@@DATE@@", formatter.format(new Date()));
			HTMLTemplate = HTMLTemplate.replace("@@SUCCESS_ROW_DATA@@", successRowData);
			HTMLTemplate = HTMLTemplate.replace("@@FAIL_ROW_DATA@@", failRowData);

			SimpleDateFormat fileNameFormat = new SimpleDateFormat("dd-MMM-yyyy_HHmmss");
			String dayAndTime = fileNameFormat.format(new Date());
			String resultFilePath = OUTPUT_FILE_PATH + "/Result_" + dayAndTime + ".html";
			File file = new File(resultFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(HTMLTemplate);
			myWriter.close();
			flag = true;
		} catch (Exception e) {
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
		System.out.println("******************* Done ***********************");
		return flag;
	}

	public static int fetchNextSequenceValue(Connection connection, String sequenceName) {
		int nextValue = -1;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			DBManager dbManager = new DBManager();
			connection = dbManager.getConnection();
			stmt = connection.prepareCall("SELECT FETECH_SEQ_VALUE('"+sequenceName+"')");

			rs = stmt.executeQuery();
		    while (rs.next()) {
		        nextValue = rs.getInt(1); // Retrieve the next value from the result set
		        System.out.println("Next Value: " + nextValue);
		    }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception e2) {
			}
		}
		return nextValue;
	}

	public static boolean insertTable1(Connection connection, int table1Seq,  int count, String filePath) {
		boolean flag = false;
		PreparedStatement stmt = null;
		try {
			String insertQry = DBQuery.INSERT_TABLE1_QRY;
			stmt = connection.prepareStatement(insertQry);
			stmt.setInt(1, table1Seq);
			stmt.setDate(2, new java.sql.Date(new Date().getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
			stmt.setInt(4, count);
			stmt.setString(5, filePath);

			int rowsAffected = stmt.executeUpdate();
			System.out.println("rowsAffected : " + rowsAffected);
			if (rowsAffected > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception e2) {
			}
		}
		return flag;
	}
	
	public static boolean insertTable2(Connection connection, int table1Seq,  String id, String name, String sal, String email) {
		boolean flag = false;
		PreparedStatement stmt = null;
		try {
			int table2Seq = fetchNextSequenceValue(connection, "table2_sequence");
			
			String insertQry = DBQuery.INSERT_TABLE2_QRY;
			stmt = connection.prepareStatement(insertQry);
			stmt.setInt(1, table2Seq);
			stmt.setInt(2, table1Seq);
			stmt.setInt(3, Integer.parseInt(id));
			stmt.setString(4, name);
			stmt.setString(5, sal);
			stmt.setString(6, email);
			stmt.setTimestamp(7, new java.sql.Timestamp(new Date().getTime()));

			int rowsAffected = stmt.executeUpdate();
			System.out.println("rowsAffected : " + rowsAffected);
			if (rowsAffected > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception e2) {
			}
		}
		return flag;
	}
}
