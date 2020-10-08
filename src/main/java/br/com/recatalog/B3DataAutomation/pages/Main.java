package br.com.recatalog.B3DataAutomation.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
	
	public List<String> findStockCodes(List<String> fullNames) { 
	
//		Instant previous, current;
		
		empresasListadasResultListPage.driver.switchTo().defaultContent();
		empresasListadasResultListPage.driver.switchTo().frame("bvmf_iframe");
		ResumoEmpresaPage resumoEmpresaPage = new ResumoEmpresaPage();
		
		List<String> stockCodes = new ArrayList<String>();
		
		for(String link :  fullNames) {
			
//			previous = Instant.now();
			WebElement linkToResume = empresasListadasResultListPage.driver.findElement(By.linkText(link));
//			current = Instant.now();
//			System.err.println("WebElement linkToResume: " + ChronoUnit.MILLIS.between(previous,current));


//			previous = Instant.now();
			linkToResume.click();
//			current = Instant.now();
//			System.err.println("linkToResume.click(): " + ChronoUnit.MILLIS.between(previous,current));

			
//			String x = resumoEmpresaPage.empresaCode();
//			stockCodes.add(x);
//			System.err.println(x);
			
//			previous = Instant.now();
			for(String code : resumoEmpresaPage.empresaCodes()) {
				stockCodes.add(code);
				System.err.println(code);
			}
//			current = Instant.now();
//			System.err.println("for: " + ChronoUnit.MILLIS.between(previous,current));


//			previous = Instant.now();
			resumoEmpresaPage.clickOnVoltar();
//			current = Instant.now();
//			System.err.println("resumoEmpresaPage.clickOnVoltar(): " + ChronoUnit.MILLIS.between(previous,current));


//			previous = Instant.now();
			empresasListadasSearchPage.clickOnTodasBtn();
//			current = Instant.now();
//			System.err.println("empresasListadasSearchPage.clickOnTodasBtn(): " + ChronoUnit.MILLIS.between(previous,current));

		}
		
		return stockCodes;
	}
	
/*	
	public static void main(String args[]) {

		Instant start, previous, current;
		start = Instant.now();

		Main m = new Main();
		previous = Instant.now();
		m.setup();
		current = Instant.now();
		
		System.err.println("setup time do início: " + ChronoUnit.MILLIS.between(start,current));
		System.err.println("setup time do anterior: " + ChronoUnit.MILLIS.between(previous,current));

		List<String> fullNames = m.findFullNameTextLinks();
		
		previous = Instant.now();
		for(String name : fullNames) {
			System.err.println(name);
		}
		current = Instant.now();
		
		System.err.println("full names time do início: " + ChronoUnit.SECONDS.between(start,current));
		System.err.println("full names time do anterior: " + ChronoUnit.SECONDS.between(previous,current));
		
//		m.findStockCodes(fullNames);

		
//		m.empresasListadasResultListPage.driver.close();
//		m.empresasListadasResultListPage.driver.quit();
	}
	
*/
	public static void main (String[] args) {
		Instant previous = Instant.now();

		CotacoesPage cot = new CotacoesPage("20200925", 30);
		cot.dailyMain();

		System.err.println("Finished historic processing: " + Date.from(Instant.now()));
		System.err.println("Elapsed time preparaLoadHistorico: " + Duration.between(previous, Instant.now()).getSeconds()/3600 + "h" + Duration.between(previous, Instant.now()).getSeconds()%3600/60 + "m" + Duration.between(previous, Instant.now()).getSeconds()%3600%60 + "s");
	}
}