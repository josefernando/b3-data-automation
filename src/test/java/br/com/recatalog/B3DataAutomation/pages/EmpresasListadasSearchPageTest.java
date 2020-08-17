package br.com.recatalog.B3DataAutomation.pages;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmpresasListadasSearchPageTest {
	HomePage homePage;
	EmpresasListadasSearchPage empresasListadasSearchPage;
	
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
}
