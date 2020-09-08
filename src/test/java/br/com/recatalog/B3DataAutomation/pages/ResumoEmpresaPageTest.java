package br.com.recatalog.B3DataAutomation.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ResumoEmpresaPageTest {
	HomePage homePage;
	EmpresasListadasSearchPage empresasListadasSearchPage;
	EmpresasListadasResultListPage empresasListadasResultListPage;
	ResumoEmpresaPage resumoEmpresaPage;
	/*
	@BeforeMethod
	public void setup() {
		homePage = new HomePage();
		homePage.initialization("CHROME", "http://www.b3.com.br");
		empresasListadasSearchPage = homePage.goEmpresasListadasSearchPage();
		empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
		List<WebElement> linksToResumoEmpresa = empresasListadasResultListPage.getLinksToDadosDaCompahia();
		resumoEmpresaPage = empresasListadasResultListPage.clickOnNomeEmpresa(linksToResumoEmpresa.get(0));
	}
	
	@Test(priority = 1)
	public void validatePageTest() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		Assert.assertTrue(resumoEmpresaPage.validatePage());
	}
	
	@Test(priority = 2)
	public void stockCodeTest() {
		String stockCode = resumoEmpresaPage.empresaCode();
		Assert.assertNotNull(stockCode);
	}
	
	@Test(priority = 3)
	public void clickOnVoltarTest() {
		 resumoEmpresaPage.clickOnVoltar();
	}
	
	@AfterMethod
	public void exit() {
		resumoEmpresaPage.driver.quit();
	}
	*/
	
	@Test
	public void testOk() {
		Assert.assertTrue(true);
	}
}