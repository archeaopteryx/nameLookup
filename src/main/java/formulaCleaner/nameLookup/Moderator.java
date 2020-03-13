package formulaCleaner.nameLookup;

import java.io.File;

import javax.swing.JOptionPane;

public class Moderator {
	
	public void run() {
		DBconfig.checkSettingsFile();
		
		NameLookupGUI input = new NameLookupGUI();
		int skipRowsLook = input.get_skipRowsLook();
		int skipRowsFile = input.get_skipRowsFile();
		int nameInLook = input.get_nameInLook();
		int nameInFile = input.get_nameInFile();
		int nameOutFile = input.get_nameOutFile();
		int casFile  = input.get_casFile();
		String nameInManu = input.get_nameInManu();
		String nameOutManu = input.get_nameOutManu();
		String casManu = input.get_casManu();
		File lookFile = input.get_lookFile();
		File sourceFile = input.get_sourceFile();
		
		int lookTab = checkLookTab(skipRowsLook, nameInLook, lookFile);
		int manuTab = checkManuTab(nameInManu, nameOutManu, casManu);
		int fileTab = checkFileTab(skipRowsFile, nameInFile, nameOutFile, casFile, sourceFile);
		
		int whichTab = lookTab*manuTab*fileTab;
		if (whichTab == -1) {
			JOptionPane.showMessageDialog(null, "Could not determine which method to run. Closing.");
			System.exit(0);
		}
		char method = callMethod(lookTab, manuTab, fileTab);
		
		switch(method){
			case 'L': 
				new Lookup(skipRowsLook, nameInLook, lookFile);
				break;
			case 'M':
				ManuUpdate.update(nameInManu, nameOutManu, casManu);
				break;
			case 'F': 
				new FileUpdate(skipRowsFile, nameInFile, nameOutFile, casFile, sourceFile);
				break;
			default:
				JOptionPane.showMessageDialog(null, "unknown method");
		}
	}
	
	private int checkLookTab(int skip, int nameIn, File file) {
		if(skip==-1 || nameIn==-1 ||file==null) {
			return -1;
		}
		return 1;
	}
	
	private int checkManuTab(String nameIn, String nameOut, String cas) {
		if(nameIn.length()==0 || nameOut.length()==0 || cas.length()==0) {
			return -1;
		}
		return 1;
	}
	
	private int checkFileTab(int skip, int nameIn, int nameOut, int cas, File file) {
		if(skip==-1 || nameIn == -1 || nameOut == -1 ||cas == -1 || file == null) {
			return -1;
		}
		return 1;
	}
	
	private char callMethod(int look, int manu, int file) {
		if (look > 0) {
			return 'L';
		}
		else if (manu > 0) {
			return 'M';
		}
		else if (file > 0) {
			return 'F';
		}
		return ' ';
	}
	
	public static void main(String[] args) {
		new Moderator().run();
	}

}
