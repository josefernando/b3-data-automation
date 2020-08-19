package br.com.recatalog.B3DataAutomation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;

public class ResumoEmpresaPage extends BasePage{
	
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

		return driver.findElement(By.xpath("//a[@class='LinkCodNeg']")).getText();
	}	
}