package com.inttra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector
{
	static Connection			connection				= null;
	static PreparedStatement	stmt					= null;
	static String				DB_USER					= "XXXXXXXXXXXXXXX";
	static String				DB_PASSWORD				= "XXXXXXXXXXXXXXXXX";
	static String				DB_URL					= "jdbc:oracle:thin:" + DB_USER + "/" + DB_PASSWORD + "@//URL:1521/SID";
	static String				CHECK_CHAPTER			= "select * from inttra.hs_chapter where chapter_code=?";
	static String				CHECK_SUB_CHAPTER		= "select * from inttra.hs_sub_chapter where sub_chapter_code=?";
	static String				CHECK_HS_CODE			= "select * from inttra.hs_code where hs_code=?";
	static String				INSERT_CHAPTER			= "insert into INTTRA.HS_CHAPTER (CHAPTER_ID,CHAPTER_CODE,CHAPTER_DESC,DELETED,CREATED_DATE,CREATED_BY_USER_ID,MODIFIED_DATE,MODIFIED_BY_USER_ID) "
														+ "values (?,'?','?',0,sysdate,854375,sysdate,854375);";
	static String				UPDATE_CHAPTER			= "update INTTRA.HS_CHAPTER set CHAPTER_DESC='?' where chapter_code='?';";
	static String				INSERT_SUB_CHAPTER		= "insert into INTTRA.HS_SUB_CHAPTER (SUB_CHAPTER_ID,CHAPTER_ID,SUB_CHAPTER_CODE,SUB_CHAPTER_DESC,DELETED,CREATED_DATE,CREATED_BY_USER_ID,MODIFIED_DATE,MODIFIED_BY_USER_ID) "
														+ "values (?,?,'?','?',0,sysdate,854375,sysdate,854375);";
	static String				UPDATE_SUB_CHAPTER		= "update INTTRA.HS_SUB_CHAPTER set SUB_CHAPTER_DESC='?' where SUB_CHAPTER_CODE='?';";
	static String				INSERT_HS_CODE			= "insert into INTTRA.HS_CODE (HS_CODE_ID,SUB_CHAPTER_ID,CHAPTER_ID,HS_CODE,HS_CODE_DESC,DELETED,CREATED_DATE,CREATED_BY_USER_ID,MODIFIED_DATE,MODIFIED_BY_USER_ID) "
														+ "values (?,?,?,'?','?',0,sysdate,854375,sysdate,854375);";
	static String				UPDATE_HS_CODE			= "update INTTRA.HS_SUB_CHAPTER set HS_CODE_DESC='?' where HS_CODE='?';";
	static String				GET_MAX_CHAPTER_ID		= "select max(CHAPTER_ID) from INTTRA.HS_CHAPTER";
	static String				GET_MAX_SUB_CHAPTER_ID	= "select max(SUB_CHAPTER_ID) from INTTRA.HS_SUB_CHAPTER";
	static String				GET_MAX_HS_CODE_ID		= "select max(HS_CODE_ID) from INTTRA.HS_CODE";

	protected static Connection loadConnection() throws ClassNotFoundException, SQLException
	{
		System.out.println("Opeing Java DB Connection ------");
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			throw e;
		}

		System.out.println("Oracle JDBC Driver Registered!");

		try
		{
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		}
		catch (SQLException e)
		{

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			throw e;

		}

		if (connection != null)
		{
			System.out.println("You made it, take control your database now!");
		}
		else
		{
			System.out.println("Failed to make connection!");
		}

		return connection;
	}

	protected static PreparedStatement getStatement(String sql) throws SQLException
	{
		stmt = null;
		if (connection != null && !connection.isClosed())
		{
			stmt = connection.prepareStatement(sql);
		}

		return stmt;
	}

	protected static int getMaxValue(String sql) throws SQLException
	{
		int maxValue = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			if (connection != null && !connection.isClosed())
			{
				stmt = connection.createStatement();
				rs = stmt.executeQuery(sql);

				if (rs != null && rs.next()) maxValue = rs.getInt(1);
			}
		}
		finally
		{
			if (stmt != null) stmt.close();
			if (rs != null) rs.close();

		}
		return maxValue;
	}

	protected static void closeDB() throws SQLException
	{
		if (stmt != null)
		{
			stmt.close();
		}

		if (connection != null)
		{
			connection.close();
		}

	}

}
