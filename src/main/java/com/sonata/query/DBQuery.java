package com.sonata.query;

public class DBQuery {

	public static String INSERT_TABLE1_QRY = "INSERT INTO table1 (xid, batch_date, created_date, record_count, file_path) VALUES (?,?,?,?,?)";
	public static String INSERT_TABLE2_QRY = "INSERT INTO table2 (xid, table1_xid, id, name, sal, email, created_date) VALUES (?,?,?,?,?,?,?)";
	public static String TABLE1_QRY="SELECT id, name, sal, email_id FROM employees WHERE id = ? and upper(name)=? and sal=? and upper(email_id)=?";
	public static String TABLE2_QRY="SELECT card_number, card_holder_name, available_bal, card_limit, used_bal, sal FROM card_details WHERE card_number = ? and upper(card_holder_name)=?";
}
