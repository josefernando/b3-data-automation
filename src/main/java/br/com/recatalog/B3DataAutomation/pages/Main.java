package br.com.recatalog.B3DataAutomation.pages;

import java.util.List;

import org.openqa.selenium.WebElement;

public class Main {
	public static void main(String args[]) {

		HomePage homePage = new HomePage();
		homePage.initialization("CHROME", "http://www.b3.com.br");
		EmpresasListadasSearchPage	empresasListadasSearchPage = homePage.goEmpresasListadasSearchPage();
		EmpresasListadasResultListPage	empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
		List<WebElement> linksToResumoEmpresa = empresasListadasResultListPage.getLinksToDadosDaCompahia();

		for(WebElement we: linksToResumoEmpresa) {
			ResumoEmpresaPage	resumoEmpresaPage = empresasListadasResultListPage.clickOnNomeEmpresa(we);
			
			resumoEmpresaPage.validatePage();
	
			String stockCode = resumoEmpresaPage.empresaCode();
	
			System.out.println(stockCode);
		}
		
		empresasListadasResultListPage.driver.quit();
	}
}