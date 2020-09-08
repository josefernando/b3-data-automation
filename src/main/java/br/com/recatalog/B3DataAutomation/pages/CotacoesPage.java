package br.com.recatalog.B3DataAutomation.pages;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import br.com.recatalog.B3DataAutomation.base.BasePage;

public class CotacoesPage extends BasePage {
	
	public CotacoesPage() {
		super();
	}
	
	public Boolean validatePage() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		WebElement  v = driver.findElement(By.xpath("//div[@class='titulo-internas']//h1[contains(text(),'Cotações')]"));
//...ou com cssSelector		WebElement  v = driver.findElement(By.cssSelector("div.titulo-internas > h1"));
		return v != null;
	}
	
	public void downloadNegociosByStockUntilNow(List<String> stockCodes, String data) {
		driver.switchTo().defaultContent(); //new
		driver.switchTo().frame("bvmf_iframe");
		
		WebElement dataEl = driver.findElement(By.xpath("//div[@class='DayPickerInput']/input"));
		dataEl.sendKeys(Keys.CONTROL + "a");
		dataEl.sendKeys(Keys.DELETE);
		dataEl.sendKeys(data);
		dataEl.sendKeys(Keys.ENTER);
		
		WebElement stockCodeEl = driver.findElement(By.cssSelector("input[type='text']"));
		for(String code : stockCodes) {
			stockCodeEl.sendKeys(Keys.CONTROL + "a");
			stockCodeEl.sendKeys(Keys.DELETE);
			stockCodeEl.sendKeys(code);
			stockCodeEl.sendKeys(Keys.ENTER);
			WebElement pesquisarBtn = driver.findElement(By.cssSelector("input[type='submit']"));
			pesquisarBtn.click();
			try {
				WebElement ativoPesquisadoLink = driver.findElement(By.linkText("Ativo pesquisado"));
				ativoPesquisadoLink.click();
			} catch(StaleElementReferenceException e) {
				System.out.println("Sem processamento para a ação: " + code  );
				continue;
			} catch(NoSuchElementException e) {
				System.out.println("Sem processamento para a ação: " + code  );
				continue;
			} catch(TimeoutException e) {
				System.err.println("TimeoutException ");
				e.printStackTrace();
				break;
			}
		}
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		driver.close();
		driver.quit();
	}

	
	public static void main (String[] args) {
		CotacoesPage cot = new CotacoesPage();
		cot.initialization("CHROME", "http://www.b3.com.br/pt_br/market-data-e-indices/servicos-de-dados/market-data/cotacoes/cotacoes");
		System.out.println(cot.validatePage());
		List<String> codes = Arrays.asList("PETR4","VVAR3");
		cot.downloadNegociosByStockUntilNow(codes, "04/09/2020");
	}
}