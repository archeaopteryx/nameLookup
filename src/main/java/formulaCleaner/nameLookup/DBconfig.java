package formulaCleaner.nameLookup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;

//import javax.swing.JOptionPane;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DBconfig {
	
	private static final String LINUX_DIR = "/.NameLookupConfig";
	private static final String FNAME = "NameLookupSettings.txt";
	private static final String NIX = "nix";
	private static final String WIN = "win";
	

	static void checkSettingsFile() {
		if(isNix()) {
			checkLinux();
		}
		else if (isWin()) {
			checkWindows();
		}
		else {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.INFO, "Unsupported OS");
			System.exit(0);
		}
	}
	
	private static void checkWindows() {
		String settingsDir = System.getenv("APPDATA");
		String filePath = settingsDir+"\\" +FNAME;
		File setFile = new File(filePath);
		String defaultLocation = System.getProperty("user.dir")+ "\\nameLookupDB.xlsx";
		if (setFile.exists()) {
			return;
		}
		else {
			newSettingsFile(filePath, defaultLocation);
			/*try {
				initializeDB(defaultLocation);
			}catch(IOException e) {
				LoggerWrapper.getInstance();
				LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
				JOptionPane.showMessageDialog(null,"Something went wrong creating the database file");
			}*/
		}
	}
	
	private static void checkLinux() {
		String home = System.getProperty("user.home");
		String path = home + LINUX_DIR + "/" + FNAME;
		String defaultLocation = System.getProperty("user.dir")+"/nameLookupDB.xlsx";
		File dir = new File(home+LINUX_DIR);
		File setFile = new File(path);
		if (dir.exists() && setFile.exists()) {
			return;
		}
		/*else if (dir.exists() && !setFile.exists()) {
			newSettingsFile(path, defaultLocation);
			try {
				initializeDB(defaultLocation);
			}catch(IOException e) {
				LoggerWrapper.getInstance();
				LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
				JOptionPane.showMessageDialog(null,"Something went wrong creating the database file");
			}
		}*/
		else {
			new File(home+LINUX_DIR).mkdir();
			newSettingsFile(path, defaultLocation);
		}
	}

	static String getDBLocation() {
		String settingsFilePath = "";
		if (isNix()) {
			settingsFilePath += System.getProperty("user.home")+LINUX_DIR+"/"+ FNAME;
		}
		else if (isWin()) {
			settingsFilePath += System.getenv("APPDATA")+"\\"+FNAME;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(settingsFilePath));
			String dbLocation = reader.readLine();
			reader.close();
			return dbLocation;
		}catch (IOException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		}
		return null;
	}
	
	static String getDBPath() {
		String dbLoc = getDBLocation();
		String dbPath = "";
		if(isNix()) {
			for(int i=0; i<dbLoc.lastIndexOf("/"); i++) {
				dbPath+= dbLoc.charAt(i);
			}
		}
		if(isWin()) {
			for(int i=0; i<dbLoc.lastIndexOf("\\"); i++) {
				dbPath+=dbLoc.charAt(i);
			}
		}
		return dbPath;
	}
	
	
	static void initializeDB(){
		String defaultLoc = getDBLocation();
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet nameInSheet = wb.createSheet("Sheet1");
		for(int i=0; i < BucketHash.N_BUCKETS; i++) {
			nameInSheet.createRow(i);
			nameInSheet.getRow(i).createCell(0).setCellValue("None");
		}
		XSSFSheet nameOutSheet = wb.createSheet("Sheet2");
		for(int i=0; i < BucketHash.N_BUCKETS; i++) {
			nameOutSheet.createRow(i);
			nameOutSheet.getRow(i).createCell(0).setCellValue("None");
		}
		XSSFSheet casSheet = wb.createSheet("Sheet3");
		for(int i=0; i < BucketHash.N_BUCKETS; i++) {
			casSheet.createRow(i);
			casSheet.getRow(i).createCell(0).setCellValue("None");
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(defaultLoc);
			wb.write(fos);
			wb.close();
		} catch (FileNotFoundException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		} catch (IOException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		} 
		try {
			PrintWriter wr = new PrintWriter (
					new BufferedWriter ( 
							new FileWriter("Hello.txt")));
			wr.println("hello world!");
			wr.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	static void update(String newPath) {
		String settingsFilePath = "";
		if (isNix()) {
			settingsFilePath += System.getProperty("user.home")+LINUX_DIR+"/"+ FNAME;
		}
		else if (isWin()) {
			settingsFilePath += System.getenv("APPDATA")+"\\" + FNAME;
		}
		try {
			Writer writer = new BufferedWriter(new FileWriter(settingsFilePath, false));
			writer.write(newPath);
			writer.close();
		}catch(IOException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		}
	}
	
	
	private static void newSettingsFile(String path, String dbLocation) {
		try {
			Writer writer = new BufferedWriter(new FileWriter(path));
			writer.write(dbLocation);
			writer.close();
		}catch(IOException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.SEVERE, e.toString());
		}
	}
	
	private static boolean isNix() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf(NIX)>=0 ||os.indexOf("nux")>=0) {
			return true;
		}
		return false;
	}
	
	private static boolean isWin() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf(WIN)>=0) {
			return true;
		}
		return false;
	}

	
}
