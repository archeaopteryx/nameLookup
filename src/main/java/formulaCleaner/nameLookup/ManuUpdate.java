package formulaCleaner.nameLookup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
			JOptionPane.showMessageDialog(null, "Database file was not found");
			System.exit(0);
		}
	}
	
	private static void updater(String nameIn, String nameOut, String cas)  throws FileNotFoundException {
		String dbFile = "nameLookupDB.xlsx";
		FileInputStream fis = new FileInputStream(dbFile);
		
		try {
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet nameInSheet = wb.getSheetAt(0);
			XSSFSheet nameOutSheet = wb.getSheetAt(1);
			XSSFSheet casSheet = wb.getSheetAt(2);
			
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
		}catch(IOException e) {
			JOptionPane.showMessageDialog(null,"IOException");
		}
		
	}
}
