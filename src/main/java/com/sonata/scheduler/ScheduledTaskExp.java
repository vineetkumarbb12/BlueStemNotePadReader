package com.sonata.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sonata.db.DBManager;
import com.sonata.query.DBQuery;

public class ScheduledTaskExp {

	private static String INPUT_FILE_PATH="D:/Bluestem/Input-File/NotepadPoc.txt";
	private static String OUTPUT_FILE_PATH="D:/TextPadReader/Result";

	static private String HTMLTemplate = "<!doctype html> <html> <head> <title>Query Results</title> <style> .styled-table { border-collapse: collapse; margin: 25px 0; font-size: 0.9em; font-family: sans-serif; min-width: 400px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.15); } .styled-table thead tr { background-color: #009879; color: #ffffff; text-align: left; } .styled-table th, .styled-table td { padding: 12px 15px; width: 250px;} .styled-table tbody tr { border-bottom: 1px solid #dddddd; } .styled-table tbody tr:nth-of-type(even) { background-color: #f3f3f3; } .styled-table tbody tr:last-of-type { border-bottom: 2px solid #009879; } .styled-table tbody tr.active-row { font-weight: bold; color: #009879; } </style> </head> <body> <div> <b style='color: brown;padding-left:45%;font-size: 22px;'><label>Test Result</label><label style='padding-left:35%;'>@@DATE@@</label></b></div> <div><table class='styled-table'> <thead> <tr> <th>ID</th> <th>Name</th> <th>Salary</th> <th>Is Present In table</th> <th>Table Name</th> </tr> </thead> <tbody> @@SUCCESS_ROW_DATA@@ @@FAIL_ROW_DATA@@ </tbody> </table> </div> </body> </html>";

	public static void main(String[] args) {
		try {
			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			startScheduler(scheduler);
			//Thread.sleep(10 * 60 * 1000);
			//stopScheduler(scheduler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startScheduler(ScheduledExecutorService scheduler) {
		System.out.println("Inside startScheduler");
		try {
			// Schedule the task to run every 2 seconds with an initial delay of 0 seconds.
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					String successRowData="";
					String failRowData="";
					String notepadLine=null;
					Connection connection = null;
					BufferedReader notepadReader = null;
					try {
						DBManager dbManager = new DBManager();
						connection = dbManager.getConnection();
						notepadReader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
						List<String> fileData = new ArrayList<>();
						boolean headerFlag = true;
						while ((notepadLine = notepadReader.readLine()) != null) {
							if(headerFlag) {
								headerFlag = false;
							} else {
								fileData.add(notepadLine);
							}
						}
						notepadReader.close();
						System.out.println("fileData : " + fileData.size());

						for (int i = 0; i < fileData.size(); i++) {
							notepadLine = fileData.get(i);

							String sql = DBQuery.TABLE1_QRY;
							PreparedStatement preparedStatement = connection.prepareStatement(sql);
							System.out.println("notepadLine : " + notepadLine);

							String[] dataArr = notepadLine.split(",");
							System.out.println("dataArr[0] : " + dataArr[0]);

							preparedStatement.setString(1, dataArr[0]);
							preparedStatement.setString(2, dataArr[1].toUpperCase().trim());
							ResultSet resultSet = preparedStatement.executeQuery();

							ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
							String tableName = resultSetMetaData.getTableName(4);
							System.out.println("Name of the table : "+ tableName);

							if (resultSet.next()) {
								// A match is found
								String id = resultSet.getString("id");
								String sal = resultSet.getString("sal");
								String email_id = resultSet.getString("email_id");
								String name = resultSet.getString("name");
								// Perform your comparison logic here
								System.out.println("Match found for: " + notepadLine);
								System.out.println("Database Data: " + id + ", " + sal + ", " + email_id + ", " + name);
								successRowData = successRowData + "<tr><td>"+resultSet.getString("id")+"</td><td>"+resultSet.getString("name")+"</td><td>"+dataArr[2]+"</td><td style='color:green'>true</td><td>"+tableName+"</td></tr>";
							} else {
								// No match is found
								System.out.println("No match found for: " + notepadLine);
								failRowData = failRowData + "<tr><td>"+dataArr[0]+"</td><td>"+dataArr[1]+"</td><td>"+dataArr[2]+"</td><td style='color:red'>false</td><td>"+tableName+"</td></tr>";
							}
							if(resultSet!=null) {
								resultSet.close();
								resultSet=null;
							}
							if(preparedStatement!=null) {
								preparedStatement.close();
								preparedStatement=null;
							}
						}

						Date date = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
						HTMLTemplate=HTMLTemplate.replace("@@DATE@@", formatter.format(date));
						HTMLTemplate=HTMLTemplate.replace("@@SUCCESS_ROW_DATA@@", successRowData);
						HTMLTemplate=HTMLTemplate.replace("@@FAIL_ROW_DATA@@", failRowData);

						SimpleDateFormat fileNameFormat = new SimpleDateFormat("dd-MMM-yyyy_HHmmss");
						String dayAndTime = fileNameFormat.format(new Date());
						String resultFilePath= OUTPUT_FILE_PATH +"/Result_"+dayAndTime+".html";
						File file = new File(resultFilePath);
						if(!file.exists()) {
							file.createNewFile();
						}
						FileWriter myWriter = new FileWriter(file);
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
					System.out.println("******************* Done ***********************");
				}
			}, 0, 10, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stopScheduler(ScheduledExecutorService scheduler) {
		System.out.println("Inside stopScheduler");
		// Shutdown the scheduler to stop it when done.
		scheduler.shutdown();
	}
}
