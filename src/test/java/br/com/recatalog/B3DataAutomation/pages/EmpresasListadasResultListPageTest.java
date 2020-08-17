package br.com.recatalog.B3DataAutomation.pages;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmpresasListadasResultListPageTest {
	HomePage homePage;
	EmpresasListadasSearchPage empresasListadasSearchPage;
	EmpresasListadasResultListPage empresasListadasResultListPage;
	
	@BeforeMethod
	public void setup() {
		homePage = new HomePage();
		homePage.initialization("CHROME", "http://www.b3.com.br");
		empresasListadasSearchPage = homePage.goEmpresasListadasSearchPage();
		empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
	}
	
	@Test(priority = 1)
	public void validateTest() {
		Assert.assertTrue(empresasListadasResultListPage.validatePage());
	}
	
//	@AfterMethod
//	public void exit() {
//		empresasListadasResultListPage.driver.quit();
//	}
}