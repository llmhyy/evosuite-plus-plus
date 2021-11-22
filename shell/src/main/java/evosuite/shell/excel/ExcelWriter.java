/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.shell.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author LLT
 *
 */
public class ExcelWriter {
	protected Workbook workbook;
	private File file;
	
	protected ExcelWriter() {
	}
	
	public ExcelWriter(File file) {
		reset(file);
	}

	public void reset(File file) {
		this.file = file;
		try {
			if (!file.exists()) {
				initFromNewFile(file);
				writeWorkbook();
			} else {
				try {
					InputStream inp = new FileInputStream(file);
					workbook = WorkbookFactory.create(inp);					
				}
				catch(Exception e) {
					file.delete();
					initFromNewFile(file);
					writeWorkbook();
				}
			}
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
	}
	
	protected void initFromNewFile(File file) {
		workbook = new XSSFWorkbook();
	}

	public Sheet createSheet(String name) {
		return workbook.createSheet(name);
	}
	
	public Sheet createSheet(String name, String[] headers, int headerRowIdx) {
		Sheet sheet = createSheet(name);
		initDataSheetHeader(sheet, headers, headerRowIdx);
		return sheet;
	}
	
	public void initDataSheetHeader(Sheet sheet, String[] headers, int headerRowIdx) {
		Row headerRow = newDataSheetRow(sheet, headerRowIdx);
		int idx = 0;
		for (String header : headers) {
			headerRow.createCell(idx++).setCellValue(header);
		}
	}
	
	protected Row newDataSheetRow(Sheet dataSheet, int headerRowIdx) {
		return dataSheet.createRow(headerRowIdx);
	}
	
	public void writeWorkbook() throws IOException{
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			workbook.write(out); 
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void addCell(Row row, int cellIdx, double value) {
		if (!Double.isNaN(value)) {
			row.createCell(cellIdx).setCellValue(value);
		} 
	}

	public void addCell(Row row, int cellIdx, String value) {
		row.createCell(cellIdx).setCellValue(value);
	}
	
	public Sheet getSheet(String name, String[] headers, int headerRowIdx) {
		Sheet sheet = workbook.getSheet(name);
		if (sheet == null) {
			sheet = createSheet(name, headers, headerRowIdx);
		}
		return sheet;
	}

	public void writeSheet(String sheetName, List<List<Object>> data) throws IOException {
		Sheet sheet = getSheet(sheetName);
		int rowNum = sheet.getLastRowNum() + 1;
		for (List<Object> rowData : data) {
			rowNum = fillRowData(sheet, rowNum, rowData);
		}
		
		writeWorkbook();
	}
	
	// Overloaded version that allows for comments as well.
	public void writeSheet(String sheetName, List<List<Object>> data, List<List<Object>> comments) throws IOException {
		Sheet sheet = getSheet(sheetName);
		int rowNum = sheet.getLastRowNum() + 1;
		
		// More performant than iterating over the lists by indices.
		ListIterator<List<Object>> rowDataIterator = data.listIterator();
		ListIterator<List<Object>> rowCommentsIterator = comments.listIterator();
		while (rowDataIterator.hasNext()) {
			List<Object> rowData = rowDataIterator.next();
			List<Object> rowComments = rowCommentsIterator.next();
			rowNum = fillRowData(sheet, rowNum, rowData, rowComments);
		}
		
		writeWorkbook();
	}

	private int fillRowData(Sheet sheet, int rowNum, List<Object> rowData) {
		Row row = sheet.createRow(rowNum++);
		int cellNum = 0;
		for (Object value : rowData) {
			if (value instanceof Boolean) {
				row.createCell(cellNum).setCellValue((Boolean)value);
			} else if (value instanceof String) {
				row.createCell(cellNum).setCellValue((String)value);
			} else if (value instanceof Number) {
				row.createCell(cellNum).setCellValue(Double.valueOf(value.toString()));
			}
			cellNum++;
		}
		return rowNum;
	}
	
	private void setCommentForCell(Cell cell, String commentText) {
		Sheet sheet = cell.getSheet();
		Row row = cell.getRow();
		Drawing drawingPatriarch = sheet.createDrawingPatriarch();
		CreationHelper creationHelper = workbook.getCreationHelper();
		
		// Where to show the comment box
		ClientAnchor clientAnchor = creationHelper.createClientAnchor();
		clientAnchor.setCol1(cell.getColumnIndex());
		clientAnchor.setCol2(cell.getColumnIndex() + 1);
		clientAnchor.setRow1(row.getRowNum());
		clientAnchor.setRow2(row.getRowNum() + 1);
		
		
		// Creating the comment, adding text and attaching it to the cell.
		Comment comment = drawingPatriarch.createCellComment(clientAnchor);
		RichTextString commentTextAsRichTextString = new XSSFRichTextString(commentText);
		comment.setString(commentTextAsRichTextString);
		comment.setAuthor("Apache POI");
		
		cell.setCellComment(comment);
	}
	
	private int fillRowData(Sheet sheet, int rowNum, List<Object> rowData, List<Object> rowComments) throws IllegalArgumentException {
		if (rowData.size() != rowComments.size()) {
			throw new IllegalArgumentException("Sizes of row data and row comments do not match.");
		}
		
		Row row = sheet.createRow(rowNum++);
		int cellNum = 0;
		for (int i = 0; i < rowData.size(); i++) {
			Object value = rowData.get(i);
			Object commentValue = rowComments.get(i);
			String commentValueAsString = commentValue.toString();
			boolean isCommentValueEmpty = (commentValueAsString.isEmpty());
			
			if (value instanceof Boolean) {
				row.createCell(cellNum).setCellValue((Boolean)value);
			} else if (value instanceof String) {
				row.createCell(cellNum).setCellValue((String)value);
			} else if (value instanceof Number) {
				row.createCell(cellNum).setCellValue(Double.valueOf(value.toString()));
			}
			
			boolean isCellExists = (row.getCell(cellNum) != null);
			
			if (!isCommentValueEmpty && isCellExists) {
				setCommentForCell(row.getCell(cellNum), commentValueAsString);
			}
			
			cellNum++;
		}
		
		return rowNum;
	}
	
	private int fillRowData(CellStyle cellStyle, Sheet sheet, int rowNum, List<Object> rowData) {
		Row row = sheet.createRow(rowNum++);
		row.setRowStyle(cellStyle);
		int cellNum = 0;
		for (Object value : rowData) {
			Cell cell = row.createCell(cellNum++);
			if (value instanceof Boolean) {
				cell.setCellValue((Boolean)value);
			} else if (value instanceof String) {
				cell.setCellValue((String)value);
			} else if (value instanceof Number) {
				cell.setCellValue(Double.valueOf(value.toString()));
			}
			cell.setCellStyle(cellStyle);
		}
		return rowNum;
	}

	public void writeSheet(String sheetName, List<List<Object>> data, IndexedColors backgroundColor,
			IndexedColors fontColor) throws IOException {
		CellStyle cellStyle = workbook.createCellStyle();
		if (backgroundColor != null) {
			cellStyle.setFillBackgroundColor(backgroundColor.index);
		}
		if (fontColor != null) {
			Font font = workbook.createFont();
			font.setColor(fontColor.getIndex());
			cellStyle.setFont(font);
		}
		Sheet sheet = getSheet(sheetName);
		int rowNum = sheet.getLastRowNum() + 1;
		for (List<Object> rowData : data) {
			fillRowData(cellStyle, sheet, rowNum++, rowData);
		}
		
		writeWorkbook();
	}
	
	private Sheet getSheet(String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			sheet = workbook.createSheet(sheetName);
		}
		return sheet;
	}
	
	public File getFile() {
		return file;
	}
	
	// Debug method to check for POI versions
	// See https://poi.apache.org/help/faq.html#faq-N10006
	private void printApachePoiVersion() {
		ClassLoader classloader = org.apache.poi.poifs.filesystem.POIFSFileSystem.class.getClassLoader();
		URL res = classloader.getResource(
		             "org/apache/poi/poifs/filesystem/POIFSFileSystem.class");
		String path = res.getPath();
		System.out.println("POI Core came from " + path);

		classloader = org.apache.poi.ooxml.POIXMLDocument.class.getClassLoader();
		res = classloader.getResource("org/apache/poi/ooxml/POIXMLDocument.class");
		path = res.getPath();
		System.out.println("POI OOXML came from " + path);
	}
}
