package br.com.recatalog.B3DataAutomation.pages;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;

public class ResumoEmpresaPage extends BasePage{
	
	/*
	 * DESEMPENHO: o COMANDO: 'driver.switchTo().frame' É MUUUITO CUSTOSO!!
	 *
	 */	
	public ResumoEmpresaPage() { 
		super();
	}
	
	public Boolean validatePage() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		driver.switchTo().defaultContent();
		driver.switchTo().frame("bvmf_iframe");
		driver.switchTo().frame("ctl00_contentPlaceHolderConteudo_iframeCarregadorPaginaExterna");
		
		List<WebElement> label = driver.findElements(By.xpath("//a[contains(text(),'Dados da Companhia')]"));
		return label.size() > 0;
	}
	
	public void clickOnVoltar() {
		driver.switchTo().defaultContent();
		driver.switchTo().frame("bvmf_iframe");
		
		WebElement voltar = driver.findElement(By.xpath("//input[@id='ctl00_botaoNavegacaoVoltar']"));
		voltar.click();
	}
	
	public String empresaCode() {
		driver.switchTo().defaultContent();
		driver.switchTo().frame("bvmf_iframe");
		driver.switchTo().frame("ctl00_contentPlaceHolderConteudo_iframeCarregadorPaginaExterna");

		
		// alterar porque pode ter mais que 1 código pra mesma empresa
		return driver.findElement(By.xpath("//a[@class='LinkCodNeg']")).getText();
	}
	
	public List<String> empresaCodes() {
		
//		Instant previous, current;
		
//		previous = Instant.now();
		driver.switchTo().defaultContent();
//		current = Instant.now();
//		System.err.println("switchTo 1: " + ChronoUnit.MILLIS.between(previous,current));

//		previous = Instant.now();
		driver.switchTo().frame("bvmf_iframe");
//		current = Instant.now();
//		System.err.println("switchTo 2: " + ChronoUnit.MILLIS.between(previous,current));

//		previous = Instant.now();
		driver.switchTo().frame("ctl00_contentPlaceHolderConteudo_iframeCarregadorPaginaExterna");
//		current = Instant.now();
//		System.err.println("switchTo 3: " + ChronoUnit.MILLIS.between(previous,current));

		List<String> list = new ArrayList<String>();

		
//		previous = Instant.now();
///		List<WebElement> wes = driver.findElements(By.xpath("//a[@class='LinkCodNeg']"));
		List<WebElement> wes = driver.findElements(By.xpath("//div[@id='accordionDados' ]//a[@class='LinkCodNeg']"));

//		current = Instant.now();
//		System.err.println("wes: " + ChronoUnit.MILLIS.between(previous,current));
		
		for(WebElement w : wes) {
			list.add(w.getText());
		}
		
		// alterar porque pode ter mais que 1 código pra mesma empresa
		return list;
	}	
}