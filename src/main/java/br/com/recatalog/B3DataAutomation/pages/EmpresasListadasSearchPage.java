package br.com.recatalog.B3DataAutomation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;

public class EmpresasListadasSearchPage extends BasePage {
	
	public EmpresasListadasSearchPage() {
		super();
	}
	
	public Boolean validatePage() {
		WebElement  v = driver.findElement(By.xpath("//div[@class='titulo-internas']//h1[contains(text(),'Ações')]"));
		return v != null;
	}
}