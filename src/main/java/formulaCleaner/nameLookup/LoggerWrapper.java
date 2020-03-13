package formulaCleaner.nameLookup;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerWrapper {
	
	public static final Logger myLogger =  Logger.getLogger("loggerName");
	public static LoggerWrapper instance = null;
	
	public static LoggerWrapper getInstance() {
		if(instance == null) {
			prepareLogger();
			instance = new LoggerWrapper();
		}
		return instance;
	}
	
	private static void prepareLogger() {
		FileHandler myFileHandler;
		try {
			myFileHandler = new FileHandler("logTest-%u-%g.txt", 50000, 3, true);
			myFileHandler.setFormatter(new SimpleFormatter());
			myLogger.addHandler(myFileHandler);
			myLogger.setUseParentHandlers(false);
			//myLogger.setLevel(Level.SEVERE);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
