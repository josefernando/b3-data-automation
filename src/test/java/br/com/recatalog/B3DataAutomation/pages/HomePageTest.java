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
	
	@Test(priority = 1)
	public void HomePageTitleTest() {
		Assert.assertTrue(homepage.validatePageTitle());
	}
	
	@Test(priority = 2)
	public void HomePageLogoTest() {
		Assert.assertTrue(homepage.validateB3Logo());
	}
	
	@AfterMethod
	public void exit() {
		homepage.driver.quit();
	}
}