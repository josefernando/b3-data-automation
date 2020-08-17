package br.com.recatalog.B3DataAutomation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;

public class EmpresasListadasResultListPage extends BasePage{
	
	public EmpresasListadasResultListPage() {
		super();
	}
	
	public Boolean validatePage() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		WebElement  v = driver.findElement(By.xpath("//span[@class='label'][contains(text(),'Resultados da Busca')]"));
		return v != null;
	}
	
	public List<WebElement> getLinksToDadosDaCompahia(){
//		WebDriver iFrameDriver = Util.switchTo(driver, "bvmf_iframe");
		return  driver.findElements(By.cssSelector("tr.GridRow_SiteBmfBovespa td > a"));
	}
}