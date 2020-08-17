package br.com.recatalog.B3DataAutomation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;

public class HomePage extends BasePage {
	
	//Page Factory - OR
	WebElement b3Logo;
	
	public HomePage() {
		super();
//		findElements();
	}

	public String getPageTitle() {
		return driver.getTitle();
	}
	
	public Boolean validatePageTitle() {
		return driver.getTitle().contains("A Bolsa do Brasil");
	}

	public Boolean validateB3Logo() {
		b3Logo = driver.findElement(By.cssSelector("img[src*='logo-b3.png']"));
		return b3Logo.isDisplayed();
	}
	
	public EmpresasListadasSearchPage goEmpresasListadasSearchPage() {
		WebElement empresasListadasLink = 
		driver.findElement(By.xpath("//a[.//h2[contains(text(),'Empresas listadas')]]"));
		empresasListadasLink.click();
		return new EmpresasListadasSearchPage();
	}
}