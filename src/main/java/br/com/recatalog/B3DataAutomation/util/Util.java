package br.com.recatalog.B3DataAutomation.util;

import org.openqa.selenium.WebDriver;

public class Util {
	
	public static WebDriver switchTo(WebDriver driver, String iFrameId) {
		return  driver.switchTo().frame(iFrameId);
	}
}
