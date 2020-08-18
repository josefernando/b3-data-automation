package br.com.recatalog.B3DataAutomation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;
import br.com.recatalog.B3DataAutomation.util.Util;

public class ResumoEmpresaPage extends BasePage{
	
	WebDriver iFrameDriver;
	
	public ResumoEmpresaPage() {
		super();
	}
	
	public Boolean validatePage() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		WebDriver iFrameDriver = Util.switchTo(driver, "ctl00_contentPlaceHolderConteudo_iframeCarregadorPaginaExterna");
		WebElement label = iFrameDriver.findElement(By.xpath("//a[contains(text(),'Dados da Companhia')]"));
		return label != null;
	}
	
	public void clickOnVoltar() {
		WebElement voltar = driver.findElement(By.xpath("//input[@id='ctl00_botaoNavegacaoVoltar']"));
		voltar.click();
//		return new EmpresasListadasSearchPage();
	}
	
	public String empresaCode() {
		WebDriver iFrameDriver = Util.switchTo(driver, "ctl00_contentPlaceHolderConteudo_iframeCarregadorPaginaExterna");
		return iFrameDriver.findElement(By.xpath("//a[@class='LinkCodNeg']")).getText();
	}
}