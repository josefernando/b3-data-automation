package br.com.recatalog.B3DataAutomation.pages;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmpresasListadasSearchPageTest {
	HomePage homePage;
	EmpresasListadasSearchPage empresasListadasSearchPage;
	EmpresasListadasResultListPage empresasListadasResultListPage;
	
	@BeforeMethod
	public void setup() {
		homePage = new HomePage();
		homePage.initialization("CHROME", "http://www.b3.com.br");
		empresasListadasSearchPage = homePage.goEmpresasListadasSearchPage();
	}
	
	@Test(priority = 1)
	public void HomePageTitleTest() {
		Assert.assertTrue(empresasListadasSearchPage.validatePage());
	}
	
//	@Test(priority = 2)
//	public void clickOnTodasBtnTest() {
//		empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
//		Assert.assertTrue(empresasListadasResultListPage.validatePage());
//	}
	
	@AfterMethod
	public void exit() {
		empresasListadasSearchPage.driver.quit();
	}
}