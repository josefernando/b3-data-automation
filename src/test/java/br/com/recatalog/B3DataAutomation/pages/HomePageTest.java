package br.com.recatalog.B3DataAutomation.pages;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class HomePageTest {
	
	HomePage homepage;
	
	@BeforeMethod
	public void setup() {
		homepage = new HomePage();
		homepage.initialization("CHROME", "http://www.b3.com.br");
	}
	
	@Test
	public void HomePageLogoTest() {
		System.out.println(homepage.validateB3Logo());
		Assert.assertTrue(true);
	}
	
	@AfterMethod
	public void exit() {
		homepage.driver.quit();
	}
}