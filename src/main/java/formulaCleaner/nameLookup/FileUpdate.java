package formulaCleaner.nameLookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileUpdate {
	private static final int ORIG_INDEX = 0;
	private static final int FIND_NAME_INDEX = 1;
	private static final int FIND_CAS_INDEX = 2;
	
	FileUpdate(int skip, int nameIn, int nameOut, int cas, File file) {
		try {
			ArrayList<String[]> valueList = getList(skip, nameIn, nameOut, cas, file);
			updater(valueList);
		}catch (FileNotFoundException e) {
			System.out.println("file not found exception");
		}
		
	}
	
	private ArrayList<String[]> getList (int skip, int nameIn, int nameOut, int cas, File file) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ArrayList<String[]> valuesList = new ArrayList<String[]>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			int lastRow = sheet.getLastRowNum();
			for (int i = skip; i< lastRow-skip; i++) {
				String[] values = new String[3];
				Row row = sheet.getRow(i);
				String testCell = row.getCell(nameIn).getStringCellValue();
				if (testCell != null && testCell.length()>0) {
					values[ORIG_INDEX] = row.getCell(nameIn).getStringCellValue();
					values[FIND_NAME_INDEX] = row.getCell(nameOut).getStringCellValue();
					values[FIND_CAS_INDEX] = row.getCell(cas).getStringCellValue();
					valuesList.add(values);
				}
			}
			wb.close();
			fis.close();
			return valuesList;
		}catch(IOException e) {
			System.out.println("IOException");
		}
		return null;
	}
	
	private void updater(ArrayList<String[]> valueList) throws FileNotFoundException {
		String dbFile = "nameLookupDB.xlsx";
		FileInputStream fis = new FileInputStream(dbFile);
		//int len = valueList.size();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet nameInSheet = wb.getSheetAt(0);
			XSSFSheet nameOutSheet = wb.getSheetAt(1);
			XSSFSheet casSheet = wb.getSheetAt(2);
			
			for(String[] value : valueList) {
				if (value[ORIG_INDEX] == null) System.exit(0); //always exiting, npe
				int bucket = BucketHash.getBucket(value[ORIG_INDEX]);
				Row row = nameInSheet.getRow(bucket);
				Row nameOutRow = nameOutSheet.getRow(bucket);
				Row casRow = casSheet.getRow(bucket);
				int numCells = row.getPhysicalNumberOfCells(); 
				if (numCells == 1) {
					row.createCell(numCells, CellType.STRING).setCellValue(value[ORIG_INDEX]);
					nameOutRow.createCell(numCells).setCellValue(value[FIND_NAME_INDEX]);
					casRow.createCell(numCells).setCellValue(value[FIND_CAS_INDEX]);
				}
				else{
					boolean found = false;
					for(int j=1; j<numCells; j++) {
						if(row.getCell(j).getStringCellValue().equals(value[ORIG_INDEX])) {
							nameOutRow.getCell(j).setCellValue(value[FIND_NAME_INDEX]);
							casRow.getCell(j).setCellValue(value[FIND_CAS_INDEX]);
							found = true;
							break;
						}
					}
					if (found == false) {
						row.createCell(numCells).setCellValue(value[ORIG_INDEX]);
						nameOutRow.createCell(numCells).setCellValue(value[FIND_NAME_INDEX]);
						casRow.createCell(numCells).setCellValue(value[FIND_CAS_INDEX]);
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
		}catch(IOException ex) {
			System.out.println("IO Exception");
		}
		
	}
	
	/*public static void main(String[] args) {
		new FileUpdate(skip, nameIn, nameOut, cas, file);
	}*/

}
