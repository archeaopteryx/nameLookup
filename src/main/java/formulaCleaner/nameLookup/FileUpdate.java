package formulaCleaner.nameLookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileUpdate {
	private int origIndex = 0;
	private int findNameIndex = 1;
	private int findCasIndex = 2;
	
	FileUpdate(int skip, int nameIn, int nameOut, int cas, File file) {
		try {
			ArrayList<String[]> valueList = getList(skip, nameIn, nameOut, cas, file);
			updater(valueList);
		}catch (FileNotFoundException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		}
		
	}
	
	private ArrayList<String[]> getList (int skip, int nameIn, int nameOut, int cas, File file) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ArrayList<String[]> valuesList = new ArrayList<String[]>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			origIndex = wb.getSheetIndex("Sheet1");
			findNameIndex = wb.getSheetIndex("Sheet2");
			findCasIndex = wb.getSheetIndex("Sheet3");
			XSSFSheet sheet = wb.getSheetAt(origIndex);
			int lastRow = sheet.getLastRowNum();
			for (int i = skip; i< lastRow-skip; i++) {
				String[] values = new String[3];
				Row row = sheet.getRow(i);
				String testCell = row.getCell(nameIn).getStringCellValue();
				if (testCell != null && testCell.length()>0) {
					values[origIndex] = row.getCell(nameIn).getStringCellValue();
					values[findNameIndex] = row.getCell(nameOut).getStringCellValue();
					values[findCasIndex] = row.getCell(cas).getStringCellValue();
					valuesList.add(values);
				}
			}
			wb.close();
			fis.close();
			return valuesList;
		}catch(IOException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		}
		return null;
	}
	
	private void updater(ArrayList<String[]> valueList) throws FileNotFoundException {
		//String dbFile = "nameLookupDB.xlsx";
		String dbFile = DBconfig.getDBLocation();
		FileInputStream fis = new FileInputStream(dbFile);
		try {
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet nameInSheet = wb.getSheetAt(0);
			XSSFSheet nameOutSheet = wb.getSheetAt(1);
			XSSFSheet casSheet = wb.getSheetAt(2);
			
			for(String[] value : valueList) {
				if (value[origIndex] == null) System.exit(0); 
				int bucket = BucketHash.getBucket(value[origIndex]);
				Row row = nameInSheet.getRow(bucket);
				Row nameOutRow = nameOutSheet.getRow(bucket);
				Row casRow = casSheet.getRow(bucket);
				int numCells = row.getPhysicalNumberOfCells(); 
				if (numCells == 1) {
					row.createCell(numCells, CellType.STRING).setCellValue(value[origIndex]);
					nameOutRow.createCell(numCells).setCellValue(value[findNameIndex]);
					casRow.createCell(numCells).setCellValue(value[findCasIndex]);
				}
				else{
					boolean found = false;
					for(int j=1; j<numCells; j++) {
						if(row.getCell(j).getStringCellValue().equals(value[origIndex])) {
							nameOutRow.getCell(j).setCellValue(value[findNameIndex]);
							casRow.getCell(j).setCellValue(value[findCasIndex]);
							found = true;
							break;
						}
					}
					if (found == false) {
						row.createCell(numCells).setCellValue(value[origIndex]);
						nameOutRow.createCell(numCells).setCellValue(value[findNameIndex]);
						casRow.createCell(numCells).setCellValue(value[findCasIndex]);
					}
				}	
			}
			FileOutputStream fos = new FileOutputStream(dbFile);
			wb.write(fos);
			wb.close();
			fos.close();
			fis.close();
			JOptionPane.showMessageDialog(null, "Finished updating");
			System.exit(0);
		}catch(IOException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		}
		
	}
	
	/*public static void main(String[] args) {
		new FileUpdate(skip, nameIn, nameOut, cas, file);
	}*/

}
