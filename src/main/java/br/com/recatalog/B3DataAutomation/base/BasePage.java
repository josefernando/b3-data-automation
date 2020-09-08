package br.com.recatalog.B3DataAutomation.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;

public class BasePage {
	
	public static WebDriver driver; 
	public static Properties prop;
	
	public BasePage() {
		prop = new Properties();
		try {
			FileInputStream ip = new FileInputStream (
					System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties");
			prop.load(ip);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initialization() {
		initialization("CHROME","http://www.b3.com.br");
	}
	
	public void initialization(String browserName, String url) {
		
		ChromeOptions options = new ChromeOptions();

		// aceita os popups e alerts = ACCEPT
		options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("download.default_directory", prop.getProperty("download.dir"));
		prefs.put("profile.default_content_setting_values.automatic_downloads", 1);

		options.setExperimentalOption("prefs", prefs);
		
		if(browserName.equalsIgnoreCase("CHROME")) {
			System.setProperty("webdriver.chrome.driver", ".\\drivers\\chromedriver.exe");
			driver = new ChromeDriver(options);
		} 
		else if(browserName.equalsIgnoreCase("FIREFOX")) {
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + prop.getProperty("driver.firefox"));
			driver = new FirefoxDriver(options);
		}
		
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(Integer.parseInt(prop.getProperty("pageloadtimeout")), TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(Integer.parseInt(prop.getProperty("implicitlywait")), TimeUnit.SECONDS);
		
		driver.get(url);		
	}
}