package formulaCleaner.nameLookup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ManuUpdate {
	
	static void update(String nameIn, String nameOut, String cas) {
		try {
			updater(nameIn, nameOut, cas);
		} catch(FileNotFoundException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, "Database file not found");
			System.exit(0);
		}
	}
	
	private static void updater(String nameIn, String nameOut, String cas)  throws FileNotFoundException {
		//String dbFile = "nameLookupDB.xlsx";
		String dbFile = DBconfig.getDBLocation();
		FileInputStream fis = new FileInputStream(dbFile);
		
		try {
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			int origIndex = wb.getSheetIndex("Sheet1");
			int findNameIndex = wb.getSheetIndex("Sheet2");
			int findCasIndex = wb.getSheetIndex("Sheet3");
			XSSFSheet nameInSheet = wb.getSheetAt(origIndex);
			XSSFSheet nameOutSheet = wb.getSheetAt(findNameIndex);
			XSSFSheet casSheet = wb.getSheetAt(findCasIndex);
			
			int bucket = BucketHash.getBucket(nameIn);
			Row row = nameInSheet.getRow(bucket);
			Row nameOutRow = nameOutSheet.getRow(bucket);
			Row casRow = casSheet.getRow(bucket);
			int numCells = row.getPhysicalNumberOfCells(); 
			if (numCells == 1) {
				row.createCell(numCells, CellType.STRING).setCellValue(nameIn);
				nameOutRow.createCell(numCells).setCellValue(nameOut);
				casRow.createCell(numCells).setCellValue(cas);
			}
			else{
				boolean found = false;
				for(int i=1; i<numCells; i++) {
					if(row.getCell(i).getStringCellValue().equals(nameIn)) {
						nameOutRow.getCell(i).setCellValue(nameOut);
						casRow.getCell(i).setCellValue(cas);
						found = true;
						break;
					}
				}
				if (found == false) {
					row.createCell(numCells).setCellValue(nameIn);
					nameOutRow.createCell(numCells).setCellValue(nameOut);
					casRow.createCell(numCells).setCellValue(cas);
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
			LoggerWrapper.myLogger.log(Level.SEVERE, "IOException");
		}
		
	}
}
