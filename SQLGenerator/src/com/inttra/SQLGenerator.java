package com.inttra;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class SQLGenerator
{
	private static final String			QSTN_MARK				= "\\?";
	private static final String			SAMPLE_XLSX_FILE_PATH	= "C:\\\\Work\\2018\\April\\HScode.xlsx";
	private static Map<String, String>	chapters				= new TreeMap<String, String>();
	private static Map<String, String>	chaptersMap				= new TreeMap<String, String>();
	private static Map<String, String>	subChapters				= new TreeMap<String, String>();
	private static Map<String, String>	subChaptersMap			= new TreeMap<String, String>();
	private static Map<String, String>	hsCodes					= new TreeMap<String, String>();

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, InvalidFormatException
	{
		try
		{
			readSheet();
			DBConnector.loadConnection();
			generateChapterSQLs();
			generateSubChapterSQLs();
			generateHSCodeSQLs();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			System.out.println(chapters.size());
			System.out.println(subChapters.size());
			System.out.println(hsCodes.size());
			DBConnector.closeDB();
		}
	}

	private static void generateChapterSQLs() throws IOException, SQLException
	{
		FileWriter chapterInsertSQL = new FileWriter("chaptersInsert.sql");
		FileWriter chapterUpdateSQL = new FileWriter("chaptersUpdate.sql");
		ResultSet rs = null;
		PreparedStatement selectChaptersStmt = null;
		try
		{
			Integer maxChapterValue = DBConnector.getMaxValue(DBConnector.GET_MAX_CHAPTER_ID);

			for (String chapterCode : chapters.keySet())
			{
				selectChaptersStmt = DBConnector.getStatement(DBConnector.CHECK_CHAPTER);
				selectChaptersStmt.setString(1, chapterCode);

				rs = selectChaptersStmt.executeQuery();

				if (rs != null && rs.next())
				{
					String[] tokens = DBConnector.UPDATE_CHAPTER.split(QSTN_MARK);
					String updateStmt = tokens[0] + StringEscapeUtils.escapeSql(chapters.get(chapterCode));
					updateStmt += tokens[1] + chapterCode + tokens[2];

					chapterUpdateSQL.write(updateStmt);
					chapterUpdateSQL.write(System.getProperty("line.separator"));
					chapterUpdateSQL.write(System.getProperty("line.separator"));

					chaptersMap.put(chapterCode, rs.getString("CHAPTER_ID"));
				}
				else
				{
					maxChapterValue++;
					String[] tokens = DBConnector.INSERT_CHAPTER.split(QSTN_MARK);
					String insertStmt = tokens[0] + maxChapterValue;
					insertStmt += tokens[1] + chapterCode;
					insertStmt += tokens[2] + StringEscapeUtils.escapeSql(chapters.get(chapterCode)) + tokens[3];

					chapterInsertSQL.write(insertStmt);
					chapterInsertSQL.write(System.getProperty("line.separator"));
					chapterInsertSQL.write(System.getProperty("line.separator"));

					chaptersMap.put(chapterCode, "" + maxChapterValue);
				}
			}
			System.out.println("SQLs generated");
			chapterInsertSQL.write("commit;");
			chapterUpdateSQL.write("commit;");
		}
		finally
		{
			if (chapterInsertSQL != null)
				chapterInsertSQL.close();

			if (chapterUpdateSQL != null)
				chapterUpdateSQL.close();

			if (rs != null)
				rs.close();

			if (selectChaptersStmt != null)
				selectChaptersStmt.close();
		}
	}

	private static void generateSubChapterSQLs() throws IOException, SQLException
	{
		FileWriter subChapterInsertSQL = new FileWriter("subChaptersInsert.sql");
		FileWriter subChapterUpdateSQL = new FileWriter("subChaptersUpdate.sql");
		ResultSet rs = null;
		PreparedStatement selectSubChaptersStmt = null;
		try
		{
			Integer maxSubChapterValue = DBConnector.getMaxValue(DBConnector.GET_MAX_SUB_CHAPTER_ID);

			for (String subChapterCode : subChapters.keySet())
			{

				selectSubChaptersStmt = DBConnector.getStatement(DBConnector.CHECK_SUB_CHAPTER);
				selectSubChaptersStmt.setString(1, subChapterCode);

				rs = selectSubChaptersStmt.executeQuery();

				if (rs != null && rs.next())
				{
					String[] tokens = DBConnector.UPDATE_SUB_CHAPTER.split(QSTN_MARK);
					String updateStmt = tokens[0] + StringEscapeUtils.escapeSql(subChapters.get(subChapterCode));
					updateStmt += tokens[1] + subChapterCode + tokens[2];

					subChapterUpdateSQL.write(updateStmt);
					subChapterUpdateSQL.write(System.getProperty("line.separator"));
					subChapterUpdateSQL.write(System.getProperty("line.separator"));

					subChaptersMap.put(subChapterCode, rs.getString("SUB_CHAPTER_ID"));
				}
				else
				{
					maxSubChapterValue++;
					String[] tokens = DBConnector.INSERT_SUB_CHAPTER.split(QSTN_MARK);
					String insertStmt = tokens[0] + maxSubChapterValue;
					insertStmt += tokens[1] + chaptersMap.get(subChapterCode.substring(0, 2));
					insertStmt += tokens[2] + subChapterCode;
					insertStmt += tokens[3] + StringEscapeUtils.escapeSql(subChapters.get(subChapterCode)) + tokens[4];

					subChapterInsertSQL.write(insertStmt);
					subChapterInsertSQL.write(System.getProperty("line.separator"));
					subChapterInsertSQL.write(System.getProperty("line.separator"));

					subChaptersMap.put(subChapterCode, "" + maxSubChapterValue);
				}
			}
			System.out.println("SQLs generated");
			subChapterInsertSQL.write("commit;");
			subChapterUpdateSQL.write("commit;");
		}
		finally
		{
			if (subChapterInsertSQL != null)
				subChapterInsertSQL.close();

			if (subChapterUpdateSQL != null)
				subChapterUpdateSQL.close();

			if (rs != null)
				rs.close();

			if (selectSubChaptersStmt != null)
				selectSubChaptersStmt.close();
		}
	}

	private static void generateHSCodeSQLs() throws IOException, SQLException
	{
		FileWriter hsCodeInsertSQL = new FileWriter("hsCodeInsert.sql");
		FileWriter hsCodeUpdateSQL = new FileWriter("hsCodeUpdate.sql");
		ResultSet rs = null;
		PreparedStatement selectHSCodesStmt = null;
		try
		{
			Integer maxHSValue = DBConnector.getMaxValue(DBConnector.GET_MAX_HS_CODE_ID);

			for (String hsCode : hsCodes.keySet())
			{

				selectHSCodesStmt = DBConnector.getStatement(DBConnector.CHECK_HS_CODE);
				selectHSCodesStmt.setString(1, hsCode);

				rs = selectHSCodesStmt.executeQuery();

				if (rs != null && rs.next())
				{
					String[] tokens = DBConnector.UPDATE_HS_CODE.split(QSTN_MARK);
					String updateStmt = tokens[0] + StringEscapeUtils.escapeSql(hsCodes.get(hsCode));
					updateStmt += tokens[1] + hsCode + tokens[2];

					hsCodeUpdateSQL.write(updateStmt);
					hsCodeUpdateSQL.write(System.getProperty("line.separator"));
					hsCodeUpdateSQL.write(System.getProperty("line.separator"));
				}
				else
				{
					maxHSValue++;
					System.out.println(hsCode);
					String[] tokens = DBConnector.INSERT_HS_CODE.split(QSTN_MARK);
					String insertStmt = tokens[0] + maxHSValue;
					insertStmt += tokens[1] + subChaptersMap.get(hsCode.substring(0, 4));
					insertStmt += tokens[2] + chaptersMap.get(hsCode.substring(0, 2));
					insertStmt += tokens[3] + hsCode;
					insertStmt += tokens[4] + StringEscapeUtils.escapeSql(hsCodes.get(hsCode)) + tokens[5];

					hsCodeInsertSQL.write(insertStmt);
					hsCodeInsertSQL.write(System.getProperty("line.separator"));
					hsCodeInsertSQL.write(System.getProperty("line.separator"));
				}
			}
			System.out.println("SQLs generated");
			hsCodeInsertSQL.write("commit;");
			hsCodeUpdateSQL.write("commit;");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (hsCodeInsertSQL != null)
				hsCodeInsertSQL.close();

			if (hsCodeUpdateSQL != null)
				hsCodeUpdateSQL.close();

			if (rs != null)
				rs.close();

			if (selectHSCodesStmt != null)
				selectHSCodesStmt.close();
		}
	}

	private static void readSheet() throws IOException, InvalidFormatException
	{
		// Creating a Workbook from an Excel file (.xls or .xlsx)
		Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

		// Retrieving the number of sheets in the Workbook
		System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

		workbook.forEach(sheet ->
		{
			System.out.println("=> " + sheet.getSheetName());
		});

		Sheet zimSheet = workbook.getSheet("ZIM");
		DataFormatter formatter = new DataFormatter();

		System.out.println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
		zimSheet.forEach(row ->
		{
			if (row.getRowNum() == 0)
				return;

			Cell chapterCode = row.getCell(0);
			Cell chapterDesc = row.getCell(1);

			Cell subChapterCode = row.getCell(2);
			Cell subChapterDesc = row.getCell(3);

			Cell hsCode = row.getCell(4);
			Cell hsDesc = row.getCell(5);

			if ("".equals(formatter.formatCellValue(chapterCode)))
				return;

			String desc = formatter.formatCellValue(chapterDesc).trim();
			if (isAllUpperCase(desc))
			{
				chapters.put(formatter.formatCellValue(chapterCode).trim(), desc.substring(0, 1).toUpperCase() + desc.substring(1).toLowerCase());
			}
			else
				chapters.put(formatter.formatCellValue(chapterCode).trim(), desc);

			desc = formatter.formatCellValue(subChapterDesc).trim();
			if (isAllUpperCase(desc))
			{
				subChapters.put(formatter.formatCellValue(subChapterCode).trim(), desc.substring(0, 1).toUpperCase() + desc.substring(1).toLowerCase());
			}
			else
				subChapters.put(formatter.formatCellValue(subChapterCode).trim(), desc);

			desc = formatter.formatCellValue(hsDesc).trim();
			if (isAllUpperCase(desc))
			{
				hsCodes.put(formatter.formatCellValue(hsCode).trim(), desc.substring(0, 1).toUpperCase() + desc.substring(1).toLowerCase());
			}
			else
				hsCodes.put(formatter.formatCellValue(hsCode).trim(), desc);

		});

		// Closing the workbook
		workbook.close();
	}

	private static boolean isAllUpperCase(String sentence)
	{
		boolean isAllUpperCase = true;
		if (null != sentence && sentence.trim().length() > 0)
		{
			String[] tokens = sentence.split(" ");
			for (String token : tokens)
			{
				token = token.replaceAll("[^A-Za-z]","");
				if (!StringUtils.isAllUpperCase(token))
				{
					isAllUpperCase = false;
					break;
				}
			}
		}

		return isAllUpperCase;
	}
}