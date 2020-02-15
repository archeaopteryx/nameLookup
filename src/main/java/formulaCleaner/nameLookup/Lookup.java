package formulaCleaner.nameLookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.List;
//import java.util.ListIterator;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 * Hello world!
 *
 */
public class Lookup 
{
	private int skipRows, nameInCol, sheetNum;//nameOutCol, casOutCol
//	private File file;
	private static final int N_BUCKETS = 100;
	static final int NAME_INDEX = 0;
	static final int CAS_INDEX = 1;
	private final String OUT_DIR = System.getProperty("user.dir");
	private final String IN_FILE = "fileIn";

/*	public Lookup() {
		this.skipRows = 1;
		this.nameInCol = 0;
		//this.nameOutCol = nameOutCol;
		//this.casOutCol = casOutCol;
		this.sheetNum = 0;
		//this.file = "TEST_1.xlsx";
		run();
	}
*/	
	public void run() {
		this.skipRows=1;
		this.nameInCol=0;
		this.sheetNum=0;
		try {
			ArrayList<String> nameInList = nameInList();
			String[][] nameAndCASList = lookup(nameInList);
			output(nameAndCASList);
			
		}catch (IOException ex) {
			System.out.println(ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	private ArrayList<String> nameInList() throws IOException{
		ArrayList<String> nameInList = new ArrayList<String>();
		FileInputStream fis = new FileInputStream("test_2.xlsx");
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		try {
			XSSFSheet sheet = wb.getSheetAt(sheetNum);
			for(Row row : sheet) {
				try {
					Cell cell = row.getCell(nameInCol);
					String nameIn = cell.getStringCellValue().trim();
					String name = nameIn.toLowerCase();
					nameInList.add(name);
				}catch(NullPointerException npe) {
					nameInList.add("");
				}
			}
		}catch(IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, "Sheet does not exist! Exiting...");
			System.exit(0);
		}finally {
			wb.close();
			fis.close();
		}
		return nameInList;
	}
	
	private ArrayList<String> namesToTry(String nameIn) {
		Node endNode = Node.generateSubNames(nameIn);
		ArrayList<String> lookupNames = Node.iterateNodes(endNode);
		return lookupNames;
	}
	
	private String[][] lookup (ArrayList<String> namesIn) throws IOException {
		int numberEntries = namesIn.size();
		String[][] foundNameAndCAS = new String[numberEntries][2];
		//String dbFile = Settings.getDBLocation();
		String dbFile = "nameLookupDB.xlsx";
		FileInputStream fis = new FileInputStream(dbFile);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet nameInSheet = wb.getSheetAt(0);
		XSSFSheet nameSheet = wb.getSheetAt(1);
		XSSFSheet casSheet = wb.getSheetAt(2);
		int counter=-1;
		boolean found;
		for(String nameIn : namesIn) {
			counter++;
			found=false;
			foundNameAndCAS[counter][NAME_INDEX]="";
			foundNameAndCAS[counter][CAS_INDEX]="";
			if(nameIn.length() == 0) {
				found=true;
			}
			int bucket = getBucket(nameIn);
			Row row = nameInSheet.getRow(bucket);
			if(row.getCell(1)!=null && found==false) {
				int lastCell = row.getLastCellNum();
				for(int i=0; i<lastCell; i++) {
					Cell cell = row.getCell(i);
					if (nameIn.equals(cell.getStringCellValue())) {
						foundNameAndCAS[counter][NAME_INDEX] = nameSheet.getRow(bucket).getCell(i).getStringCellValue();
						foundNameAndCAS[counter][CAS_INDEX] = casSheet.getRow(bucket).getCell(i).getStringCellValue();
						found=true;
						break;
					}
				}
			}
			if(found==false) {
				ArrayList<String> lookupNames = namesToTry(nameIn);
				for (String name : lookupNames) {
					String[] temp = FragLookup.fragLookup(name);
					if (!temp[NAME_INDEX].equals("") && !temp[CAS_INDEX].equals("")) {
						foundNameAndCAS[counter][NAME_INDEX] = temp[NAME_INDEX];
						foundNameAndCAS[counter][CAS_INDEX]= temp[CAS_INDEX];
						break;
					}
				}
				if(!foundNameAndCAS[counter][NAME_INDEX].equals("") && !foundNameAndCAS[counter][CAS_INDEX].equals("")) {
					int index = row.getLastCellNum();
					row.createCell(index).setCellValue(nameIn);
					Row nameCellRow = nameSheet.getRow(bucket);
					nameCellRow.createCell(index).setCellValue(foundNameAndCAS[counter][NAME_INDEX]);
					Row casCellRow = casSheet.getRow(bucket);
					casCellRow.createCell(index).setCellValue(foundNameAndCAS[counter][CAS_INDEX]);
				}
			}
		}
		FileOutputStream fileOut = new FileOutputStream(dbFile);
		wb.write(fileOut);
		wb.close();
		fis.close();
		return foundNameAndCAS;
	}
	
	private void output(String[][] nameAndCAS) throws IOException {
		String os = System.getProperty("os.name").toLowerCase();
		String fileOutName="";
		if(os.indexOf("win") > -1) {
			fileOutName += OUT_DIR +"\\"+IN_FILE+"-output.xlsx";
		}
		else if(os.indexOf("nix") > -1 || os.indexOf("nux")>-1) {
			fileOutName += OUT_DIR +"/"+IN_FILE+"-output.xlsx";
		}
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Sheet1");
		for (int i = 0; i<nameAndCAS.length; i++) {
			Row row = sheet.createRow(i);
			row.createCell(0, CellType.STRING).setCellValue(nameAndCAS[i][NAME_INDEX]);
			row.createCell(1, CellType.STRING).setCellValue(nameAndCAS[i][CAS_INDEX]);
		}
		FileOutputStream fileOut = new FileOutputStream(fileOutName);
		wb.write(fileOut);
		wb.close();
		
		String msg = "Finished!";
		JOptionPane.showMessageDialog(null, msg);
	}


	private int getBucket(String nameIn) {
		int hashValue = hashFunction(nameIn);
		int bucket = hashValue % N_BUCKETS;
		return bucket;
	}
	
	private int hashFunction(String nameIn) {
		int hash = 0;
		String name = nameIn.toUpperCase();
		if (name.length() >7) {
			for(int i=0; i<7; i++) {
				hash += name.charAt(i)*17;
			}
		}
		else {
			for (int i=0; i<name.length(); i++) {
				hash+= name.charAt(i);
			}
		}
		return hash;
	}


    public static void main(String[] args )
    {
        new Lookup().run();
    }
}
