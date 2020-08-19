package br.com.recatalog.B3DataAutomation.pages;

import java.util.List;

import org.openqa.selenium.WebElement;

public class Main {
		
	HomePage homePage;
	EmpresasListadasSearchPage empresasListadasSearchPage;
	EmpresasListadasResultListPage empresasListadasResultListPage;
	ResumoEmpresaPage resumoEmpresaPage;
	
	public void setup() {
		homePage = new HomePage();
		homePage.initialization("CHROME", "http://www.b3.com.br");
		empresasListadasSearchPage = homePage.goEmpresasListadasSearchPage();
		empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
//		List<WebElement> linksToResumoEmpresa = empresasListadasResultListPage.getLinksToDadosDaCompahia();
//		resumoEmpresaPage = empresasListadasResultListPage.clickOnNomeEmpresa(linksToResumoEmpresa.get(0));
	}
	
	public void loadStockCode(WebElement we) {
			we.click();
	}
	
	public static void main(String args[]) {
		Main m = new Main();
		m.setup();
//		String stockCode = m.resumoEmpresaPage.empresaCode();
//		System.out.println(stockCode);
//		m.resumoEmpresaPage.driver.quit();
		
		for(WebElement we: m.empresasListadasResultListPage.getLinksToDadosDaCompahia()) {
			System.out.println(we.getText());
			m.resumoEmpresaPage = m.empresasListadasResultListPage.clickOnNomeEmpresa(we);
			System.out.println(m.resumoEmpresaPage.empresaCode());
			m.resumoEmpresaPage.clickOnVoltar();
			m.empresasListadasSearchPage.clickOnTodasBtn();
		}
		m.resumoEmpresaPage.driver.quit();
	}
}