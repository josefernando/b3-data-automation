package br.com.recatalog.B3DataAutomation.pages;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HomePageTest {
	
	HomePage homePage;
	
	@BeforeMethod
	public void setup() {
		homePage = new HomePage();
		homePage.initialization("CHROME", "http://www.b3.com.br");
	}
	
	@Test(priority = 1)
	public void HomePageTitleTest() {
		Assert.assertTrue(homePage.validatePageTitle());
	}
	
	@Test(priority = 2)
	public void HomePageLogoTest() {
		Assert.assertTrue(homePage.validateB3Logo());
	}
	
	@AfterMethod
	public void exit() {
		homePage.driver.quit();
	}
}