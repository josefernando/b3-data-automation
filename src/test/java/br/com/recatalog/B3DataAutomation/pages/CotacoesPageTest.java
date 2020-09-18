package br.com.recatalog.B3DataAutomation.pages;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CotacoesPageTest {

	CotacoesPage cotacoesPage;
	
	@BeforeMethod
	public void setup() {
		List<String> codes = Arrays.asList("MGLU3");
		cotacoesPage = new CotacoesPage(codes, "20200409");
		cotacoesPage.initialization("CHROME", "http://www.b3.com.br/pt_br/market-data-e-indices/servicos-de-dados/market-data/cotacoes/cotacoes/");
	}
	
	@Test(priority = 1)
	public void validateTest() {
		Assert.assertTrue(cotacoesPage.validatePage());
	}
	
	@Test(priority = 2)
	public void downloadTest() {
		cotacoesPage.downloadNegociosByStockUntilNow();
		Assert.assertTrue(true);
	}
	
	@AfterMethod
	public void exit() {
//		cotacoesPage.driver.quit();
	}
}
