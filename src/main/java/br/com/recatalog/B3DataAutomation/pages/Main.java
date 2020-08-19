package br.com.recatalog.B3DataAutomation.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Main {
		
	WebDriver driver;
	HomePage homePage;
	EmpresasListadasSearchPage empresasListadasSearchPage;
	EmpresasListadasResultListPage empresasListadasResultListPage;
	ResumoEmpresaPage resumoEmpresaPage;
	
	public void setup() {
		homePage = new HomePage();
		homePage.initialization("CHROME", "http://www.b3.com.br");
		empresasListadasSearchPage = homePage.goEmpresasListadasSearchPage();
//		empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
	}
	
	public List<String> findFullNameTextLinks() {
		empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
		
		empresasListadasResultListPage.driver.switchTo().defaultContent();
		empresasListadasResultListPage.driver.switchTo().frame("bvmf_iframe");
		
//		List<WebElement> weFullNames = empresasListadasResultListPage.driver.findElements(By.cssSelector("tr.GridRow_SiteBmfBovespa  td > a"));
		List<WebElement> weFullNames = empresasListadasResultListPage.driver.findElements(By.xpath("//tr[@class='GridRow_SiteBmfBovespa GridBovespaItemStyle'  or @class='GridAltRow_SiteBmfBovespa GridBovespaAlternatingItemStyle']/td[1]/a"));

		List<String> fullNames = new ArrayList<String>();
		for(WebElement we : weFullNames) {
			fullNames.add(we.getText());
		}
		return fullNames;
	}
	
	
	public List<String> findStockCodes() { // ...Testar
		empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
		
		empresasListadasResultListPage.driver.switchTo().defaultContent();
		empresasListadasResultListPage.driver.switchTo().frame("bvmf_iframe");
		
		List<String> stockCodes = new ArrayList<String>();
		
		for(String link : findFullNameTextLinks()) {
			WebElement linkToResume = empresasListadasResultListPage.driver.findElement(By.linkText(link));
			ResumoEmpresaPage resumoEmpresaPage = empresasListadasResultListPage.clickOnNomeEmpresa(linkToResume);
			stockCodes.add(resumoEmpresaPage.empresaCode());
			resumoEmpresaPage.clickOnVoltar();
			empresasListadasResultListPage = empresasListadasSearchPage.clickOnTodasBtn();
			
			empresasListadasResultListPage.driver.switchTo().defaultContent();
			empresasListadasResultListPage.driver.switchTo().frame("bvmf_iframe");
		}
		
		return stockCodes;
	}
	
	
	
	public static void main(String args[]) {
		Main m = new Main();
		m.setup();

		List<String> fullNames = m.findFullNameTextLinks();
		
		for(String name : fullNames) {
			System.err.println(name);
		}
		
//		List<String> linkNames = m.empresasListadasResultListPage.getTextLinks();
		
//		for(String code : m.findStockCodes()) {
//			System.err.println(code);
//		}
		
		m.empresasListadasResultListPage.driver.close();
		m.empresasListadasResultListPage.driver.quit();
	}
}