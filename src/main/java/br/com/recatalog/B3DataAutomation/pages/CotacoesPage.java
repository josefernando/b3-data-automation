package br.com.recatalog.B3DataAutomation.pages;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
		
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		
		WebElement dataEl = driver.findElement(By.xpath("//div[@class='DayPickerInput']/input"));
		dataEl.sendKeys(Keys.CONTROL + "a");
		dataEl.sendKeys(Keys.DELETE);
		dataEl.sendKeys(data);
		dataEl.sendKeys(Keys.ENTER);
		
		Instant previous = Instant.now();

		WebElement stockCodeEl = null;
		WebElement pesquisarBtn = null;
		WebElement ativoPesquisadoLink = null;
		
		for(String code : stockCodes) {
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        
			stockCodeEl = driver.findElement(By.cssSelector("input[type='text']"));
			previous = Instant.now();
			stockCodeEl.sendKeys(Keys.CONTROL + "a");
			stockCodeEl.sendKeys(Keys.DELETE);
			stockCodeEl.sendKeys(code);
			stockCodeEl.sendKeys(Keys.ENTER);

			WebDriverWait wait0 = new WebDriverWait(driver, 10);
			
//			wait0.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='modal-body']")));
//			wait0.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//a[@data-dropdown='Section-2']")));
//			wait0.until(ExpectedConditions.visibilityOf(pesquisarBtn));
			wait0.ignoring(StaleElementReferenceException.class)
			.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit' and @class='button expand']")));
				pesquisarBtn = driver.findElement(By.xpath("//input[@type='submit' and @class='button expand']"));

				js.executeScript("arguments[0].click()", pesquisarBtn);
			try {
				String var = "//a[contains(@href,'" + code + "')]";
				ativoPesquisadoLink = driver.findElement(By.xpath(var));

				WebDriverWait wait = new WebDriverWait(driver, 10);
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(var)));
				ativoPesquisadoLink.click();
			} catch(NoSuchElementException e) {
				System.out.println("Sem processamento para a ação: " + code  );
				continue;
			} catch(TimeoutException e) {
				System.err.println("TimeoutException ");
				e.printStackTrace();
				break;
			}catch (ElementClickInterceptedException e) {
				e.printStackTrace();
				continue;
			}
			System.out.println("Code : " + code + " " + ChronoUnit.MILLIS.between(previous,Instant.now()));
		}
		
//		driver.close();
//		driver.quit();
	}

	
	public static void main (String[] args) {
		CotacoesPage cot = new CotacoesPage();
		cot.initialization("CHROME", "http://www.b3.com.br/pt_br/market-data-e-indices/servicos-de-dados/market-data/cotacoes/cotacoes");
		List<String> codes = Arrays.asList(
				"ADHM3",
				"TIET11",
				"TIET3",
				"TIET4",
				"AFLT3",
				"RPAD3",
				"ALSO3",
				"ALPK3",
				"ALPA3",
				"APER3",
				"ALUP11",
				"ALUP3",
				"ALUP4",
				"ABEV3",
				"AMBP3",
				"CBEE3",
				"ANIM3",
				"ARZZ3",
				"CRFB3",
				"ATOM3",
				"AZEV3",
				"AZUL4",
				"BTOW3",
				"B3SA3",
				"BAHI3",
				"BMGB4",
				"BIDI11",
				"BIDI3",
				"BIDI4",
				"BEES3",
				"BEES4",
				"BDLL3",
				"BDLL4",
				"BTTL3",
				"BALM3",
				"BALM4",
				"BBSE3",
				"BBML3",
				"ABCB4",
				"BRIV3",
				"BRIV4",
				"BAZA3",
				"BBDC3",
				"BBDC4",
				"BBAS11",
				"BBAS3",
				"BPAC11",
				"BPAC3",
				"BGIP3",
				"BGIP4",
				"BPAR3"
				);
		cot.downloadNegociosByStockUntilNow(codes, "03/09/2020");
	}
}