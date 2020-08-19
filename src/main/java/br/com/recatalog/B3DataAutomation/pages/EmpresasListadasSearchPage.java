package br.com.recatalog.B3DataAutomation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;

public class EmpresasListadasSearchPage extends BasePage {
	
	public EmpresasListadasSearchPage() {
		super();
	}
	
	public Boolean validatePage() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		WebElement  v = driver.findElement(By.xpath("//div[@class='titulo-internas']//h1[contains(text(),'Ações')]"));
//...ou com cssSelector		WebElement  v = driver.findElement(By.cssSelector("div.titulo-internas > h1"));
		return v != null;
	}
	
	public EmpresasListadasResultListPage clickOnTodasBtn() {
		driver.switchTo().frame("bvmf_iframe");
		WebElement todasBtn = driver.findElement(By.xpath("//input[@type='button'][@value='Todas']"));
		todasBtn.click();
		return new EmpresasListadasResultListPage();
	}
}