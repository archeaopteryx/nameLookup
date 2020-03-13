package formulaCleaner.nameLookup;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

//import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;

public class FragLookup {
	private static final int NAME_INDEX = Lookup.NAME_INDEX;
	private static final int CAS_INDEX = Lookup.CAS_INDEX;
	
	public static String[] fragLookup(String nameIn) throws IOException {
		
		class SilentDriver extends HtmlUnitDriver {
			SilentDriver(com.gargoylesoftware.htmlunit.BrowserVersion version, boolean enableJavascript) {
				super(version, enableJavascript);
				this.getWebClient().setCssErrorHandler(new SilentCssErrorHandler());
			}
		}
		
		String searchName = nameIn.replaceAll(" ", "+");
		String urlBase = "http://www.thegoodscentscompany.com/search3.php?qName=";
		String startURL = urlBase+searchName;
		SilentDriver driver = new SilentDriver(BrowserVersion.CHROME, true);
		driver.get(startURL);
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		pause();
		String[] values = {"",""};
		String directLookup ="";
		if(driver.findElementsById("tableList2").size()==0) {
			driver.close();
			return values;
		}
		try {
			//Element entry = doc.selectFirst("a.lstw4");
			WebElement nextLookup = driver.findElement(By.xpath("//table[2]/tbody/tr[1]/td[2]/a"));
			values[NAME_INDEX] = nextLookup.getText();
			String ref = nextLookup.getAttribute("onclick");
			String[] components = ref.split("[']");
			directLookup += components[1];
		} catch (NullPointerException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.WARNING, "Something went wrong with the first step of search for "+nameIn);
		} finally {
			driver.close();
		}
		SilentDriver nextDriver = new SilentDriver(BrowserVersion.CHROME, true);
		nextDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		try {
			String url = "http://www.thegoodscentscompany.com/"+directLookup;
			nextDriver.get(url);
			nextDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			List<WebElement> radw8Boxes = nextDriver.findElementsByClassName("radw8");
			ListIterator<WebElement> iterator = radw8Boxes.listIterator();
			while(iterator.hasNext()) {
				WebElement element = iterator.next();
				if(element.getText().equals("CAS Number:")) {
					WebElement sibling = element.findElement(By.xpath("../td[2]"));
					System.out.println(sibling.getText());
					values[CAS_INDEX] = sibling.getText();
					return values;
				}
			}
		} catch(NullPointerException e) {
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.WARNING, e.toString());
		} finally {
			nextDriver.close();
		}
		return values;
	}
	
	private static void pause() {
		try {
			Thread.sleep(200);
		}
		catch (InterruptedException e){
			LoggerWrapper.getInstance();
			LoggerWrapper.myLogger.log(Level.WARNING, e.toString());
			throw new RuntimeException(e.toString());
		}
	}
	

}
