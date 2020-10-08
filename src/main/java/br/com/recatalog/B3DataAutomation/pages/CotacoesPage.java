package br.com.recatalog.B3DataAutomation.pages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.beust.jcommander.ParameterException;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

import br.com.recatalog.B3DataAutomation.base.BasePage;
import br.com.recatalog.B3DataAutomation.util.PreparaLoadIntradayTrade;
import br.com.recatalog.B3DataAutomation.util.Util;

public class CotacoesPage extends BasePage {
	
	String dateIn;  // yyyymmdd
	List<String> stockCodes;
	List<String> retryStockCodes;
	Set<String> historicoStockCodes;
	
//	Date nextStartTime;
//	Date nextEndTime;
	
	int interval;

	List<String> intradayHistoricoPaths;
	
	public CotacoesPage(String dateIn, int interval) {
		super();
		this.dateIn = dateIn;
		this.retryStockCodes = new ArrayList<String>();
		this.historicoStockCodes = new HashSet<String>();
		this.intradayHistoricoPaths = new ArrayList<String>();
		this.interval = interval;
	}
	
	public CotacoesPage(String dateIn) {
		super();
		this.dateIn = dateIn;
		this.retryStockCodes = new ArrayList<String>();
		this.historicoStockCodes = new HashSet<String>();
		this.intradayHistoricoPaths = new ArrayList<String>();
		this.interval = 20;
	}
	
	public CotacoesPage(List<String> stockCodes , String dateIn) {
		super();
		this.dateIn = dateIn;
		this.stockCodes = stockCodes;
		this.retryStockCodes = new ArrayList<String>();
		this.historicoStockCodes = new HashSet<String>();
		this.intradayHistoricoPaths = new ArrayList<String>();
		this.interval = 20;
	}
	
	public CotacoesPage(List<String> stockCodes , String dateIn, int interval) {
		super();
		this.dateIn = dateIn;
		this.stockCodes = stockCodes;
		this.retryStockCodes = new ArrayList<String>();
		this.historicoStockCodes = new HashSet<String>();
		this.intradayHistoricoPaths = new ArrayList<String>();
		this.interval = interval;
	}
	
	public CotacoesPage() {
		super();
	}
	
	public Boolean validatePage() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		WebElement  v = driver.findElement(By.xpath("//div[@class='titulo-internas']//h1[contains(text(),'Cotações')]"));
//...ou com cssSelector		WebElement  v = driver.findElement(By.cssSelector("div.titulo-internas > h1"));
		return v != null;
	}
	
	public void setStockCodes(List<String> codes) {
		 this.stockCodes = codes;
	}
	
	public List<String> listaDir() {
        PowerShell powerShell = PowerShell.openSession();
		String command = "Get-ChildItem '" + downloadDir + "' -Filter *.LOAD | select -expand fullname";
//		String command = "Get-ChildItem '" + "C:\\Download\\Bolsa_de_Valores_Dados\\b3_dados\\intraday\\20200917_084611" + "' -Filter *.LOAD | select -expand fullname";
        	
        PowerShellResponse response = powerShell.executeCommand(command);
        String outpp = response.getCommandOutput();
        
        String[] files = outpp.split(System.getProperty("line.separator"));
        
//        for(String a : files) {
//        	System.out.println(a);
//        }
        
        powerShell.close();
        return Arrays.asList(files);
	}
	
	public List<String> concatFiles(String folder) {
        PowerShell powerShell = PowerShell.openSession();
		String command = "Get-ChildItem '" + downloadDir + "' -Filter *.LOAD | select -expand fullname";
        
//		String concatCommand = Get-ChildItem -Filter *.txt 
//				
//				| Select-Object -ExpandProperty FullName | Import-Csv | Export-Csv .\combinedcsvs.csv -NoTypeInformation -Append
//		
		
        PowerShellResponse response = powerShell.executeCommand(command);
        String outpp = response.getCommandOutput();
        
        String[] files = outpp.split(System.getProperty("line.separator"));
        
        powerShell.close();
        return Arrays.asList(files);
	}
	
	public List<String> listaDirHistorico() {
        PowerShell powerShell = PowerShell.openSession();
		String command = "Get-ChildItem '" + downloadDir + "' -Filter *.LOAD | select -expand fullname";
        	
        PowerShellResponse response = powerShell.executeCommand(command);
        String outpp = response.getCommandOutput();
        
        String[] files = outpp.split(System.getProperty("line.separator"));
        
        powerShell.close();
        return Arrays.asList(files);
	}	
	
	public void unzipFolder() {
//		https://stackoverflow.com/questions/35453580/executing-a-powershell-command-in-eclipse
		
        PowerShell powerShell = PowerShell.openSession();
        
		String command = "Get-ChildItem '" + downloadDir + "' -Filter *.zip | Expand-Archive -DestinationPath '" +  downloadDir + "' -Force";

        PowerShellResponse response = powerShell.executeCommand(command);
        
//        get-childItem '{downloadDir}' -include *.zip | foreach ($_) {remove-item $_.fullname}

        System.out.println("Proceses are:" + response.getCommandOutput());

        powerShell.close();
	}
	
	public void preparaLoad() {
		
		for(File f : new File(downloadDir).listFiles()) {
			if(!f.getPath().endsWith("txt")) continue;
		
		try {
//		File file = new File("C:\\Download\\Bolsa_de_Valores_Dados\\b3_dados\\intraday\\20200910_102025\\TradeIntraday_TIET4_20200903_1.txt");
		File file = f; 
		
		Path path = Paths.get(f.getCanonicalPath().replace("txt", "LOAD"));
		BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
		
		long count = 0;
		
		Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name());
		writer.write(sc.nextLine() + System.getProperty("line.separator"));

//		PreparaLoadIntradayTrade pp = null;
		while (sc.hasNextLine()){
			String line = null;
			line = sc.nextLine();
			count++;
//			pp = new PreparaLoadIntradayTrade(line);
//			writer.write(pp.toCvs() + System.getProperty("line.separator"));
		}

		writer.flush();
		System.out.println(file.getAbsolutePath() + " - linhas: " + count);
		
		sc.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		}
	}


	private void preparaLoadDaily() {
		String[] start_end = getProcessInterval(interval);
		
		for(File f : new File(downloadDir).listFiles()) {
			if(!f.getPath().endsWith("txt")) continue;
		
		try {
		File file = f; 
		
		Path path = Paths.get(f.getCanonicalPath().replace("txt", "LOAD"));
		BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
		
		long count = 0;
		
		Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name());
		writer.write(sc.nextLine() + System.getProperty("line.separator"));

		PreparaLoadIntradayTrade pp = null;
		while (sc.hasNextLine()){
			String line = null;
			line = sc.nextLine();
			count++;
			pp = new PreparaLoadIntradayTrade(line);
			if(doProcess(pp,start_end) == false) continue;

			writer.write(pp.toCvs() + System.getProperty("line.separator"));
		}

		writer.flush();
		System.out.println(file.getAbsolutePath() + " - linhas: " + count);
		
		sc.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	
	public boolean doProcess(PreparaLoadIntradayTrade pp, String[] durationInterval) {
		Boolean ret = true;
		Date nextStartTime = null;
		Date nextEndTime = null;
		
//---  inicio regra 1 - código da ação
		ret = ret && pp.getCodigo_negociacao_papel()
				.matches("[a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z][345678][F]?|[a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z]11");

		if(!ret) return false;
//---  fim regra 1 - código da ação
		
//--- inicio regra 2
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS"); 

		if(nextStartTime == null) {
			try {
				nextStartTime = format.parse(durationInterval[0]);
				nextEndTime = format.parse(durationInterval[1]);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}

		Date tradeTime   = null;

		try {
			tradeTime = format.parse(pp.getHora_negocio());
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

		if(tradeTime.getTime() >= nextStartTime.getTime() && tradeTime.getTime() < nextEndTime.getTime()) ret = ret && true;
		else ret = ret && false;
//--- inicio regra 2

		return ret;		
	}
	
	
	public boolean doProcess1(String line, String[] durationInterval) {
/*------------- line --------------------------------
2020-09-23;TF693;0;11,000;10;024059557;10;1;2020-09-23
*/		
		Boolean ret = true;
		Date nextStartTime = null;
		Date nextEndTime = null;
		
		String stockCodeIn = line.split(";")[1];
		String tradeTimeIn = line.split(";")[5];
		
//---  inicio regra 1 - código da ação
		ret = stockCodeIn
				.matches("[a-zA-Z09][a-zA-Z09][a-zA-Z09][a-zA-Z09][345678][F]?|[a-zA-Z09][a-zA-Z09][a-zA-Z09][a-zA-Z09]11");

		if(ret == false) return false;
//---  fim regra 1 - código da ação
		
//--- inicio regra 2
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS"); 
		SimpleDateFormat formatIn = new SimpleDateFormat("HHmmssSSS"); 

		if(nextStartTime == null) {
			try {
				nextStartTime = format.parse(durationInterval[0]);
				nextEndTime = format.parse(durationInterval[1]);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}

		Date validTradeTime = null;
		try {
			validTradeTime = formatIn.parse(tradeTimeIn);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

		if(validTradeTime.getTime() >= nextStartTime.getTime() && validTradeTime.getTime() < nextEndTime.getTime()) ret = ret && true;
		else ret = ret && false;
//--- inicio regra 2

		return ret;		
	}	
	
	private Path preparaLoadHistorico() {

/****		
 remove os códigos anteriores 
 e inclui apenas os códigos que
 tiveram negócio no período horário específico	
***/ 	
//		historicoStockCodes.clear();
		Instant previous = Instant.now();
		
		String inPath = prop.getProperty("download.dir") + "\\historico\\TradeIntraday_" + dateIn + "_1.txt";

		String[] start_end = getProcessIntervalHistoric(interval);
		String fileSulfix  = "_" + start_end[1].replaceAll("[:.]", "");
		
		System.out.println("Input file: " + inPath + "...");
		
		Path outPath = null;
		try {
		File file = new File(inPath);
		
		outPath = Paths.get(file.getCanonicalPath().replace(".txt", fileSulfix + ".LOAD"));
		BufferedWriter writer = Files.newBufferedWriter(outPath, Charset.forName("UTF-8"));
		
		long count = 0;
		
		Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name());
		writer.write(sc.nextLine() + System.getProperty("line.separator"));

		PreparaLoadIntradayTrade pp = null;
		
		while (sc.hasNextLine()){
			String line = null;
			line = sc.nextLine();
			count++;

/* ------------------- line example -----------------------
2020-09-23;TF693;0;11,000;10;024059557;10;1;2020-09-23
***/
			String stockCode = line.split(";")[1];
            if(doProcess1(line,start_end) == false) continue;

			historicoStockCodes.add(stockCode);

			writer.write(formatCvs(line) + System.getProperty("line.separator"));
		}

		writer.flush();
		System.out.println(file.getAbsolutePath() + " - linhas: " + count);
		
		sc.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Elapsed time preparaLoadHistorico: " + Duration.between(previous, Instant.now()).getSeconds()/60 + "m" + Duration.between(previous, Instant.now()).getSeconds()%60 + "s");
		return outPath;
	}
	
	
	private String formatCvs(String intradayTrade) {
		String data_referencia = intradayTrade.split(";")[0];
		
		String codigo_negociacao_papel = intradayTrade.split(";")[1];
		
		String acao = intradayTrade.split(";")[2];
		
		String preco_negocio = intradayTrade.split(";")[3].replace(",", ".");
		
		String titulos_negociados = intradayTrade.split(";")[4];
		
		String hora_negocio = intradayTrade.split(";")[5];
		
		hora_negocio = hora_negocio.substring(0, 2)
				            + ":"
				            + hora_negocio.substring(2, 4)
				            + ":"
				            + hora_negocio.substring(4, 6)
				            + "."
				            + hora_negocio.substring(6);
		
		String id_negocio  = intradayTrade.split(";")[6];
		
		String tipo_sessao_pregao = intradayTrade.split(";")[7];
		
		String data_pregao = intradayTrade.split(";")[8];
		
		return data_referencia + ";" +
		 codigo_negociacao_papel + ";" +
		 acao + ";" +
		 preco_negocio + ";" +
		 titulos_negociados + ";" +
		 hora_negocio + ";" +
		 id_negocio + ";" +
		 tipo_sessao_pregao + ";" +
		 data_pregao;
	}
	
	public void findOportunityHistoric(int interval) {
		Connection conn = Util.getMySqlConnection();
		// (stock_code varchar(12), start_date varchar(10), pinterval int, OUT error_code int, OUT error_msg varchar(255))
		String procedureCall = "{CALL FIND_OPORTUNITY_INTRADAY_BY_STOCK_HISTORIC(?,?,?,?,?)}";
		
		TreeSet<String> sortedCodes = new TreeSet<String>(historicoStockCodes);
		
		for(String scode : sortedCodes) {
			try {
				CallableStatement stmt = conn.prepareCall(procedureCall);
		         stmt.setString(1, scode);  
		         stmt.setString(2, dateIn);  
		         stmt.setInt(3, interval);  
		         stmt.registerOutParameter(4, Types.INTEGER);
		         stmt.registerOutParameter(5, Types.VARCHAR);
		         
		         //Execute stored procedure
		         stmt.execute();
		         
		         // Get Out and InOut parameters
		         System.out.println("Code: " + scode);
		         System.out.println("Error Code: "  + stmt.getInt(4));
		         System.out.println("Error Msg: "  + stmt.getString(5));			
		         System.out.println("===========================");			

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] getProcessIntervalHistoric(int interval) {
		Connection conn = Util.getMySqlConnection();
		String next_start_time = null;
		String next_end_time = null;

		// IN pstart_date varchar(10), IN pinterval time(3), OUT pnext_start_time varchar(12), OUT pnext_end_time varchar(12)
		String procedureCall = "{CALL GET_NEXT_TIME_CONTROL_BY_STOCK_HISTORIC(?,?,?,?)}";
		
			try {
				CallableStatement stmt = conn.prepareCall(procedureCall);
		         stmt.setString(1, dateIn);  
		         stmt.setInt(2, interval);  
		         stmt.registerOutParameter(3, Types.VARCHAR);
		         stmt.registerOutParameter(4, Types.VARCHAR);
		         
		         //Execute stored procedure
		         stmt.execute();
		         
		         next_start_time = stmt.getString(3); // format: HH:MM:SS.LLL
		         next_end_time   = stmt.getString(4);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new String[] { next_start_time , next_end_time};  
	}	
	
	
	
	public String[] getProcessInterval(int interval) {
		Connection conn = Util.getMySqlConnection();
		String next_start_time = null;
		String next_end_time = null;

		// IN pstart_date varchar(10), IN pinterval time(3), OUT pnext_start_time varchar(12), OUT pnext_end_time varchar(12)
		String procedureCall = "{CALL GET_NEXT_TIME_CONTROL_BY_STOCK(?,?,?,?)}";
		
			try {
				CallableStatement stmt = conn.prepareCall(procedureCall);
		         stmt.setString(1, dateIn);  
		         stmt.setInt(2, interval);  
		         stmt.registerOutParameter(3, Types.VARCHAR);
		         stmt.registerOutParameter(4, Types.VARCHAR);
		         
		         //Execute stored procedure
		         stmt.execute();
		         
		         next_start_time = stmt.getString(3); // format: HH:MM:SS.LLL
		         next_end_time   = stmt.getString(4);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new String[] { next_start_time , next_end_time};  
	}	
	
	public void findOportunity() {
		if(interval <= 0) {
			try {
				throw new ParameterException("Interval less or equals zero: " + interval);
			} catch (ParameterException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				return;
			}
		}
		
		Connection conn = Util.getMySqlConnection();
		// (stock_code varchar(12), start_date varchar(10), pinterval int, OUT error_code int, OUT error_msg varchar(255))
		String procedureCall = "{CALL FIND_OPORTUNITY_INTRADAY_BY_STOCK(?,?,?,?,?)}";

		
		for(String scode : stockCodes) {
			try {
				CallableStatement stmt = conn.prepareCall(procedureCall);
		         stmt.setString(1, scode);  
		         stmt.setString(2, dateIn);  
		         stmt.setInt(3, interval);  
		         stmt.registerOutParameter(4, Types.INTEGER);
		         stmt.registerOutParameter(5, Types.VARCHAR);
		         
		         //Execute stored procedure
		         stmt.execute();
		         
		         // Get Out and InOut parameters
		         System.out.println("Code: " + scode);
		         System.out.println("Error Code: "  + stmt.getInt(4));
		         System.out.println("Error Msg: "  + stmt.getString(5));			
		         System.out.println("===========================");			

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void downloadNegociosByStockUntilNow() {
		
		if(stockCodes == null) {
			System.out.println("Será utilizado o último arquivo de cotações gerado do dia");
		}
		
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyyMMdd"); 
		SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd/MM/yyyy"); 

		Date date = null;
		try {
			date = dateFormatIn.parse(dateIn);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		String data = dateFormatOut.format(date);
		
		driver.switchTo().defaultContent(); //new
		driver.switchTo().frame("bvmf_iframe");
		
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		
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
	        
	        Instant previousCode = Instant.now();
	        
			stockCodeEl = driver.findElement(By.cssSelector("input[type='text']"));
			previous = Instant.now();
			stockCodeEl.sendKeys(Keys.CONTROL + "a");
			stockCodeEl.sendKeys(Keys.DELETE);
			stockCodeEl.sendKeys(code);
			stockCodeEl.sendKeys(Keys.ENTER);

			WebDriverWait wait0 = new WebDriverWait(driver, 10);

			wait0.ignoring(StaleElementReferenceException.class)
			.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit' and @class='button expand']")));
			
			pesquisarBtn = driver.findElement(By.xpath("//input[@type='submit' and @class='button expand']"));

			js.executeScript("arguments[0].click()", pesquisarBtn);
			
			WebDriverWait wait = new WebDriverWait(driver, 7);
			
			WebElement el = wait.ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//h3[@id='label-nao-encontrado'] | //a[contains(text(),'Ativo pesquisado')])")));

			try {
				if(el.getTagName().equalsIgnoreCase("h3")) {
					System.out.println("Sem processamento até o momento: " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
					retryStockCodes.add(code);
					continue;
				}
			} catch (StaleElementReferenceException e0) {
				System.out.println("Sem processamento até o momento (StaleElementReferenceException) : " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				retryStockCodes.add(code);
				continue;				
			}
			
			try {
			ativoPesquisadoLink = wait.ignoring(StaleElementReferenceException.class, NoSuchElementException.class)
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Ativo pesquisado')]")));
			} catch (TimeoutException e) {
				System.out.println("Timeout: " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				retryStockCodes.add(code);
				continue;
			}
			
			ativoPesquisadoLink = el;
			
// Para se evitar o erro abaixo no comando:  ativoPesquisadoLink.click()
//			Exception in thread "main" org.openqa.selenium.ElementClickInterceptedException: element click intercepted: Element <a href="https://arquivos.b3.com.br/apinegocios/tickercsv/ADHM3/2020-09-17">...</a> is not clickable at point (135, 490). Other element would receive the click: <div class="modal fade show" role="dialog" tabindex="-1" style="display: block;">...</div>

			try {
			js.executeScript("arguments[0].click()", ativoPesquisadoLink);
			} catch (StaleElementReferenceException e0) {
				System.out.println("Sem processamento até o momento (StaleElementReferenceException js.executeScript) : " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				retryStockCodes.add(code);
				continue;				
			} catch (TimeoutException e1) {
				System.out.println("Sem processamento até o momento (TimeoutException js.executeScript) : " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				retryStockCodes.add(code);
				continue;				
			}

			System.out.println("Code : " + code + " - " + Duration.between(previousCode,Instant.now()).toSeconds() + "." + Duration.between(previousCode,Instant.now()).toNanosPart());
		}
		
		
		// Tentativa de acessar os papéis não acessados por timeout e outras exceptions
		retryDownloadNegociosByStockUntilNow();
		
//		driver.close();
//		driver.quit();
	}
	
	public void retryDownloadNegociosByStockUntilNow() {
		
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyyMMdd"); 
		SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd/MM/yyyy"); 

		Date date = null;
		try {
			date = dateFormatIn.parse(dateIn);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		String data = dateFormatOut.format(date);
		
		driver.switchTo().defaultContent(); //new
		driver.switchTo().frame("bvmf_iframe");
		
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		
		WebElement dataEl = driver.findElement(By.xpath("//div[@class='DayPickerInput']/input"));
		dataEl.sendKeys(Keys.CONTROL + "a");
		dataEl.sendKeys(Keys.DELETE);
		dataEl.sendKeys(data);
		dataEl.sendKeys(Keys.ENTER);
		
		Instant previous = Instant.now();

		WebElement stockCodeEl = null;
		WebElement pesquisarBtn = null;
		WebElement ativoPesquisadoLink = null;
		
		for(String code : retryStockCodes) {
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        
	        Instant previousCode = Instant.now();
	        
			stockCodeEl = driver.findElement(By.cssSelector("input[type='text']"));
			previous = Instant.now();
			stockCodeEl.sendKeys(Keys.CONTROL + "a");
			stockCodeEl.sendKeys(Keys.DELETE);
			stockCodeEl.sendKeys(code);
			stockCodeEl.sendKeys(Keys.ENTER);

			WebDriverWait wait0 = new WebDriverWait(driver, 10);

			wait0.ignoring(StaleElementReferenceException.class)
			.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit' and @class='button expand']")));
			
			pesquisarBtn = driver.findElement(By.xpath("//input[@type='submit' and @class='button expand']"));

			js.executeScript("arguments[0].click()", pesquisarBtn);
			
			WebDriverWait wait = new WebDriverWait(driver, 7);
			
			WebElement el = wait.ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//h3[@id='label-nao-encontrado'] | //a[contains(text(),'Ativo pesquisado')])")));

			try {
				if(el.getTagName().equalsIgnoreCase("h3")) {
					System.out.println("Retry Sem processamento até o momento: " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
					continue;
				}
			} catch (StaleElementReferenceException e0) {
				System.out.println("Retry Sem processamento até o momento (StaleElementReferenceException) : " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				continue;				
			}
			
			try {
			ativoPesquisadoLink = wait.ignoring(StaleElementReferenceException.class, NoSuchElementException.class)
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Ativo pesquisado')]")));
			} catch (TimeoutException e) {
				System.out.println("Retry Timeout: " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				continue;
			}
			
			ativoPesquisadoLink = el;
			
// Para se evitar o erro abaixo no comando:  ativoPesquisadoLink.click()
//			Exception in thread "main" org.openqa.selenium.ElementClickInterceptedException: element click intercepted: Element <a href="https://arquivos.b3.com.br/apinegocios/tickercsv/ADHM3/2020-09-17">...</a> is not clickable at point (135, 490). Other element would receive the click: <div class="modal fade show" role="dialog" tabindex="-1" style="display: block;">...</div>

			try {
			js.executeScript("arguments[0].click()", ativoPesquisadoLink);
			} catch (StaleElementReferenceException e0) {
				System.out.println("Retry Sem processamento até o momento (StaleElementReferenceException js.executeScript) : " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				continue;				
			}catch (TimeoutException e1) {
				System.out.println("Retry Sem processamento até o momento (TimeoutException js.executeScript) : " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
				continue;				
			}

			System.out.println("Retry Code : " + code + " - " + Duration.between(previousCode,Instant.now()).toSeconds() + "." + Duration.between(previousCode,Instant.now()).toNanosPart());
		}
	}
	
	public void dailyMain() {
		Instant start = Instant.now();
		List<String> codes = Arrays.asList(
//				"AALR3",
//				"AALR3F",
//				"ABCB4",
//				"ABCB4F",
//				"ABCP11",
//				"ABEV3",
//				"ABEV3F",
//				"AFLT3F",
//				"AGRO3",
//				"AGRO3F",
//				"ALMI11",
//				"ALPA4",
//				"ALPA4F",
//				"ALPK3",
//				"ALPK3F",
//				"ALSO3",
//				"ALSO3F",
//				"ALUP11",
//				"ALUP3",
//				"ALUP4",
//				"ALUP4F",
//				"ALZR11",
//				"AMAR3",
//				"AMAR3F",
//				"AMBP3F",
//				"ANIM3",
//				"ANIM3F",
//				"APER3F",
//				"ARZZ3",
//				"ARZZ3F",
//				"ATMP3F",
//				"ATOM3",
//				"ATOM3F",
//				"AZEV3",
//				"AZEV3F",
//				"AZEV4",
//				"AZEV4F",
//				"AZUL4",
//				"AZUL4F",
				"B3SA3",
//				"BARI11",
//				"BAZA3F",
//				"BBAS3",
//				"BBAS3F",
//				"BBDC3",
//				"BBDC3F",
				"BBDC4",
				"BBDC4F",
//				"BBPO11",
//				"BBRC11",
//				"BBRK3",
//				"BBRK3F",
//				"BBSE3",
//				"BBSE3F",
//				"BCFF11",
//				"BCIA11",
//				"BCRI11",
//				"BEEF11",
				"BEEF3",
				"BEEF3F",
//				"BEES3",
//				"BEES3F",
//				"BEES4F",
//				"BIDI11",
//				"BIDI3",
//				"BIDI3F",
//				"BIDI4",
//				"BIDI4F",
//				"BIOM3",
//				"BIOM3F",
//				"BKBR3",
//				"BKBR3F",
//				"BMEB4F",
//				"BMGB4",
//				"BMGB4F",
//				"BNBR3F",
//				"BOBR4",
//				"BOBR4F",
				"BOVA11",
//				"BOVV11",
//				"BPAC11",
//				"BPAC5",
//				"BPAN4",
//				"BPAN4F",
//				"BPFF11",
//				"BPRP11",
//				"BRAP4",
//				"BRAP4F",
//				"BRCO11",
//				"BRCR11",
//				"BRDT3",
//				"BRDT3F",
//				"BRFS3",
//				"BRFS3F",
				"BRKM3",
				"BRKM3F",
				"BRKM5",
				"BRKM5F",
//				"BRML3",
//				"BRML3F",
//				"BRPR3",
//				"BRPR3F",
//				"BRSR3F",
//				"BRSR6",
//				"BRSR6F",
//				"BRZP11",
//				"BSEV3",
//				"BSEV3F",
//				"BTLG11",
				"BTOW3",
				"BTOW3F",
//				"BTTL3",
//				"BTTL3F",
//				"CAMB3",
//				"CAMB3F",
//				"CAML3",
//				"CAML3F",
//				"CARD3",
//				"CARD3F",
//				"CARE11",
//				"CBOP11",
//				"CCPR3",
//				"CCRO3",
//				"CCRO3F",
//				"CEAB3",
//				"CEAB3F",
//				"CEDO4F",
//				"CEOC11",
//				"CESP6",
//				"CESP6F",
//				"CGAS3F",
//				"CGAS5F",
//				"CGRA3F",
//				"CGRA4",
//				"CGRA4F",
//				"CIEL3",
//				"CIEL3F",
//				"CMIG3",
//				"CMIG3F",
//				"CMIG4",
//				"CMIG4F",
//				"CNTO3",
//				"CNTO3F",
//				"COCE5",
//				"COCE5F",
//				"COGN3",
//				"COGN3F",
//				"CPFE3",
//				"CPFE3F",
//				"CPLE3",
//				"CPLE3F",
//				"CPLE6",
//				"CPLE6F",
//				"CPTS11",
//				"CRFB3",
//				"CRFB3F",
//				"CRFF11",
//				"CSAN3",
//				"CSAN3F",
//				"CSMG3",
//				"CSMG3F",
//				"CSNA3",
//				"CSNA3F",
//				"CTNM3",
//				"CTNM4",
//				"CTNM4F",
//				"CTSA3",
//				"CTSA4",
//				"CTSA4F",
//				"CTXT11",
//				"CVCB3",
//				"CVCB3F",
//				"CXRI11",
//				"CYRE3",
//				"CYRE3F",
//				"DIRR3",
//				"DIRR3F",
//				"DIVO11",
//				"DMMO11",
//				"DMMO3",
//				"DMMO3F",
//				"DOHL4",
//				"DOHL4F",
//				"DTEX3",
//				"DTEX3F",
//				"EALT4",
//				"EALT4F",
//				"ECOR3",
//				"ECOR3F",
//				"EDGA11",
//				"EGIE3",
//				"EGIE3F",
//				"ELET3",
//				"ELET3F",
//				"ELET6",
//				"ELET6F",
//				"EMBR3",
//				"EMBR3F",
//				"ENAT3",
//				"ENAT3F",
//				"ENBR3",
//				"ENBR3F",
//				"ENEV3",
//				"ENEV3F",
//				"ENGI11",
//				"ENGI3",
//				"ENGI3F",
//				"ENGI4",
//				"ENGI4F",
//				"EQPA3",
//				"EQPA3F",
//				"EQTL3",
//				"EQTL3F",
//				"ETER3",
//				"ETER3F",
//				"EUCA3F",
//				"EUCA4",
//				"EUCA4F",
//				"EVEN3",
//				"EVEN3F",
//				"EZTC3",
//				"EZTC3F",
//				"FCFL11",
//				"FESA4",
//				"FESA4F",
//				"FEXC11",
//				"FHER3",
//				"FIIB11",
//				"FIVN11",
//				"FLMA11",
//				"FLRY3",
//				"FLRY3F",
//				"FNAM11",
//				"FNOR11",
//				"FRAS3",
//				"FRAS3F",
//				"FRTA3",
//				"FVBI11",
//				"FVPQ11",
//				"GFSA3",
//				"GFSA3F",
//				"GGBR3",
//				"GGBR3F",
//				"GGBR4",
//				"GGBR4F",
//				"GGRC11",
//				"GNDI3",
//				"GNDI3F",
//				"GOAU3",
//				"GOAU3F",
//				"GOAU4",
//				"GOAU4F",
//				"GOLL4",
//				"GOLL4F",
//				"GPCP3",
//				"GPCP3F",
//				"GRLV11",
//				"GRND3",
//				"GRND3F",
//				"GSFI11",
//				"GTWR11",
//				"GUAR3",
//				"GUAR3F",
//				"HAGA3F",
//				"HAGA4",
//				"HAGA4F",
//				"HAPV3",
//				"HAPV3F",
//				"HBOR3",
//				"HBOR3F",
//				"HCTR11",
//				"HFOF11",
//				"HGBS11",
//				"HGCR11",
//				"HGFF11",
//				"HGLG11",
//				"HGRE11",
//				"HGRU11",
//				"HGTX3",
//				"HGTX3F",
//				"HLOG11",
//				"HOOT4F",
//				"HRDF11",
//				"HSML11",
//				"HTMX11",
//				"HYPE3",
//				"HYPE3F",
//				"IBFF11",
//				"IDVL3",
//				"IDVL3F",
//				"IGBR3",
//				"IGBR3F",
//				"IGTA3",
//				"IGTA3F",
//				"IMAB11",
//				"IRBR3",
//				"IRBR3F",
//				"IRDM11",
//				"ITSA3",
//				"ITSA3F",
//				"ITSA4",
//				"ITSA4F",
//				"ITUB3",
//				"ITUB3F",
//				"ITUB4",
//				"ITUB4F",
//				"IVVB11",
//				"JBDU4",
//				"JBSS3",
//				"JBSS3F",
//				"JHSF3",
//				"JHSF3F",
//				"JPSA3",
//				"JPSA3F",
//				"JRDM11",
//				"JSLG3",
//				"JSLG3F",
//				"JSRE11",
//				"KDIF11",
//				"KEPL3",
//				"KEPL3F",
//				"KFOF11",
//				"KLBN11",
//				"KLBN3",
//				"KLBN3F",
//				"KLBN4",
//				"KLBN4F",
//				"KNCR11",
//				"KNIP11",
//				"KNRE11",
//				"KNRI11",
//				"LAME3",
//				"LAME3F",
				"LAME4",
				"LAME4F",
//				"LCAM3",
//				"LCAM3F",
//				"LEVE3",
//				"LEVE3F",
//				"LGCP11",
//				"LIGT3",
//				"LIGT3F",
//				"LINX3",
//				"LINX3F",
//				"LLIS3",
//				"LLIS3F",
//				"LOGG3",
//				"LOGG3F",
//				"LOGN3",
//				"LOGN3F",
//				"LPSB3",
//				"LPSB3F",
//				"LREN3",
//				"LREN3F",
//				"LUGG11",
//				"LUPA3",
//				"LUPA3F",
//				"LVBI11",
//				"LWSA3",
//				"LWSA3F",
//				"MALL11",
//				"MAXR11",
//				"MCCI11",
//				"MDIA3",
//				"MDIA3F",
//				"MDNE3",
//				"MDNE3F",
//				"MEAL3",
//				"MEAL3F",
//				"MFII11",
//				"MGEL4",
//				"MGFF11",
				"MGLU3",
				"MGLU3F",
//				"MILS3",
//				"MILS3F",
//				"MMXM11",
//				"MMXM3",
//				"MORE11",
//				"MOVI3",
//				"MOVI3F",
//				"MRFG3",
//				"MRFG3F",
//				"MRVE3",
//				"MRVE3F",
//				"MTRE3",
//				"MTRE3F",
//				"MULT3",
//				"MULT3F",
//				"MWET3F",
//				"MWET4",
//				"MWET4F",
//				"MXRF11",
//				"MYPK3",
//				"MYPK3F",
//				"NEOE3",
//				"NEOE3F",
//				"NSLU11",
//				"NTCO3",
//				"NTCO3F",
//				"NUTR3F",
//				"NVHO11",
//				"ODPV3",
//				"ODPV3F",
//				"OFSA3F",
//				"OIBR3",
//				"OIBR3F",
				"OIBR4",
				"OIBR4F",
//				"OMGE3",
//				"OMGE3F",
//				"OUFF11",
//				"OUJP11",
//				"PARD3",
//				"PARD3F",
//				"PATC11",
//				"PATI4",
//				"PATI4F",
//				"PCAR3",
//				"PCAR3F",
//				"PDGR3",
//				"PDTC3",
//				"PDTC3F",
//				"PETR3",
//				"PETR3F",
//				"PETR4",
//				"PETR4F",
//				"PFRM3",
//				"PFRM3F",
//				"PIBB11",
//				"PINE4",
//				"PINE4F",
//				"PLAS3",
//				"PLAS3F",
//				"PLCR11",
//				"PMAM3",
//				"PMAM3F",
//				"PNVL3",
//				"PNVL3F",
//				"PNVL4",
//				"PNVL4F",
//				"POMO3",
//				"POMO3F",
//				"POMO4",
//				"POMO4F",
//				"PORD11",
//				"POSI3",
//				"POSI3F",
//				"PRIO3",
//				"PRIO3F",
//				"PRNR3",
//				"PRNR3F",
//				"PSSA3",
//				"PSSA3F",
//				"PSVM11",
//				"PTBL3",
//				"PTBL3F",
//				"PTNT4",
//				"PTNT4F",
//				"QAGR11",
//				"QUAL3",
//				"QUAL3F",
//				"RADL3",
//				"RADL3F",
//				"RAIL3",
//				"RAIL3F",
//				"RANI3",
//				"RANI3F",
//				"RANI4F",
//				"RAPT4",
//				"RAPT4F",
//				"RBED11",
//				"RBFF11",
//				"RBIV11",
//				"RBRD11",
//				"RBRF11",
//				"RBRP11",
//				"RBRR11",
//				"RBVA11",
//				"RBVO11",
//				"RCFA11",
//				"RCRB11",
//				"RCSL4",
//				"RCSL4F",
//				"RDNI3",
//				"RDNI3F",
//				"RECT11",
//				"REDE3F",
//				"RENT3",
//				"RENT3F",
//				"RLOG3",
//				"RLOG3F",
//				"RNEW3F",
//				"RNEW4",
//				"RNGO11",
//				"ROMI3",
//				"ROMI3F",
//				"RPAD3F",
//				"RPMG3",
//				"RPMG3F",
//				"RSID3",
//				"RSID3F",
//				"SADI11",
//				"SANB11",
//				"SANB3",
//				"SANB3F",
//				"SANB4",
//				"SANB4F",
//				"SAPR11",
//				"SAPR3",
//				"SAPR3F",
//				"SAPR4",
//				"SAPR4F",
//				"SARE11",
//				"SBSP3",
//				"SBSP3F",
//				"SCAR3",
//				"SDIL11",
//				"SEER3",
//				"SEER3F",
//				"SGPS3",
//				"SHOW3",
//				"SHOW3F",
//				"SHUL4",
//				"SHUL4F",
//				"SLCE3",
//				"SLCE3F",
//				"SLED3",
//				"SLED3F",
//				"SLED4",
//				"SLED4F",
//				"SMAC11",
//				"SMAL11",
//				"SMLS3",
//				"SMLS3F",
//				"SMTO3",
//				"SMTO3F",
//				"SNSY5",
//				"SNSY5F",
//				"SPTW11",
//				"SPXI11",
//				"SQIA3",
//				"SQIA3F",
//				"STBP3",
//				"STBP3F",
//				"SULA11",
//				"SULA3F",
//				"SULA4F",
//				"SUZB3",
//				"SUZB3F",
//				"TAEE11",
//				"TAEE3",
//				"TAEE3F",
//				"TAEE4",
//				"TAEE4F",
//				"TASA3",
//				"TASA3F",
//				"TASA4",
//				"TASA4F",
//				"TCNO3F",
//				"TCNO4",
//				"TCNO4F",
//				"TCSA3",
//				"TCSA3F",
//				"TECN3",
//				"TECN3F",
//				"TEKA4F",
//				"TELB3F",
//				"TELB4",
//				"TELB4F",
//				"TEND3",
//				"TEND3F",
//				"TEPP11",
//				"TESA3",
//				"TESA3F",
//				"TGAR11",
//				"TGMA3",
//				"TGMA3F",
//				"TIET11",
//				"TIET3",
//				"TIET3F",
//				"TIET4",
//				"TIET4F",
//				"TIMP3",
//				"TIMP3F",
//				"TORD11",
//				"TOTS3",
//				"TOTS3F",
//				"TPIS3",
//				"TPIS3F",
//				"TRIS3",
//				"TRIS3F",
//				"TRPL3",
//				"TRPL3F",
//				"TRPL4",
//				"TRPL4F",
//				"TRXF11",
//				"TUPY3",
//				"TUPY3F",
//				"TXRX4F",
//				"UBSR11",
//				"UCAS3",
//				"UCAS3F",
//				"UGPA3",
//				"UGPA3F",
//				"UNIP3F",
//				"UNIP6",
//				"UNIP6F",
//				"USIM3",
//				"USIM3F",
//				"USIM5",
//				"USIM5F",
//				"USIM6F",
//				"VALE3",
//				"VALE3F",
//				"VGIP11",
//				"VGIR11",
//				"VIGT11",
//				"VILG11",
//				"VINO11",
//				"VISC11",
//				"VIVA3",
//				"VIVA3F",
//				"VIVR3",
//				"VIVR3F",
//				"VIVT3",
//				"VIVT3F",
//				"VIVT4",
//				"VIVT4F",
//				"VLID3",
//				"VLID3F",
//				"VRTA11",
//				"VTLT11",
//				"VULC3",
//				"VULC3F",
				"VVAR3",
				"VVAR3F",
//				"WEGE3",
//				"WEGE3F",
//				"WHRL4",
//				"WHRL4F",
//				"WIZS3",
//				"WIZS3F",
//				"WLMM3F",
//				"WLMM4",
//				"XBOV11",
//				"XPCM11",
//				"XPIN11",
//				"XPLG11",
//				"XPML11",
//				"XPPR11",
//				"XPSF11",
//				"XTED11",
				"YDUQ3",
				"YDUQ3F"
// ===================================================================				
//				"AALR3",
//				"ABCB4",
//				"ABEV3",
//				"AGRO3",
//				"ALPA4",
//				"ALPK3",
//				"ALSO3",
//				"ALUP3",
//				"ALUP4",
//				"AMAR3",
//				"ANIM3",
//				"ARZZ3",
//				"ATOM3",
//				"AZEV3",
//				"AZEV4",
//				"AZUL4",
//				"BBAS3",
//				"BBDC3",
//				"BBDC4",
//				"BBRK3",
//				"BBSE3",
//				"BEEF3",
//				"BEES3",
//				"BIDI3",
//				"BIDI4",
//				"BIOM3",
//				"BKBR3",
//				"BMGB4",
//				"BOBR4",
//				"BPAC5",
//				"BPAN4",
//				"BRAP4",
//				"BRDT3",
//				"BRFS3",
//				"BRKM3",
//				"BRKM5",
//				"BRML3",
//				"BRPR3",
//				"BRSR6",
//				"BSEV3",
//				"BTOW3",
//				"BTTL3",
//				"CAMB3",
//				"CAML3",
//				"CARD3",
//				"CCPR3",
//				"CCRO3",
//				"CEAB3",
//				"CESP6",
//				"CGRA4",
//				"CIEL3",
//				"CMIG3",
//				"CMIG4",
//				"CNTO3",
//				"COCE5",
//				"COGN3",
//				"CPFE3",
//				"CPLE3",
//				"CPLE6",
//				"CRFB3",
//				"CSAN3",
//				"CSMG3",
//				"CSNA3",
//				"CTNM3",
//				"CTNM4",
//				"CTSA3",
//				"CTSA4",
//				"CVCB3",
//				"CYRE3",
//				"DIRR3",
//				"DMMO3",
//				"DOHL4",
//				"DTEX3",
//				"EALT4",
//				"ECOR3",
//				"EGIE3",
//				"ELET3",
//				"ELET6",
//				"EMBR3",
//				"ENAT3",
//				"ENBR3",
//				"ENEV3",
//				"ENGI3",
//				"ENGI4",
//				"EQPA3",
//				"EQTL3",
//				"ETER3",
//				"EUCA4",
//				"EVEN3",
//				"EZTC3",
//				"FESA4",
//				"FHER3",
//				"FLRY3",
//				"FRAS3",
//				"FRTA3",
//				"GFSA3",
//				"GGBR3",
//				"GGBR4",
//				"GNDI3",
//				"GOAU3",
//				"GOAU4",
//				"GOLL4",
//				"GPCP3",
//				"GRND3",
//				"GUAR3",
//				"HAGA4",
//				"HAPV3",
//				"HBOR3",
//				"HGTX3",
//				"HYPE3",
//				"IDVL3",
//				"IGBR3",
//				"IGTA3",
//				"IRBR3",
//				"ITSA3",
//				"ITSA4",
//				"ITUB3",
//				"ITUB4",
//				"JBDU4",
//				"JBSS3",
//				"JHSF3",
//				"JPSA3",
//				"JSLG3",
//				"KEPL3",
//				"KLBN3",
//				"KLBN4",
//				"LAME3",
//				"LAME4",
//				"LCAM3",
//				"LEVE3",
//				"LIGT3",
//				"LINX3",
//				"LLIS3",
//				"LOGG3",
//				"LOGN3",
//				"LPSB3",
//				"LREN3",
//				"LUPA3",
//				"LWSA3",
//				"MDIA3",
//				"MDNE3",
//				"MEAL3",
//				"MGEL4",
//				"MGLU3",
//				"MILS3",
//				"MMXM3",
//				"MOVI3",
//				"MRFG3",
//				"MRVE3",
//				"MTRE3",
//				"MULT3",
//				"MWET4",
//				"MYPK3",
//				"NEOE3",
//				"NTCO3",
//				"ODPV3",
//				"OIBR3",
//				"OIBR4",
//				"OMGE3",
//				"PARD3",
//				"PATI4",
//				"PCAR3",
//				"PDGR3",
//				"PDTC3",
//				"PETR3",
//				"PETR4",
//				"PFRM3",
//				"PINE4",
//				"PLAS3",
//				"PMAM3",
//				"PNVL3",
//				"PNVL4",
//				"POMO3",
//				"POMO4",
//				"POSI3",
//				"PRIO3",
//				"PRNR3",
//				"PSSA3",
//				"PTBL3",
//				"PTNT4",
//				"QUAL3",
//				"RADL3",
//				"RAIL3",
//				"RANI3",
//				"RAPT4",
//				"RCSL4",
//				"RDNI3",
//				"RENT3",
//				"RLOG3",
//				"RNEW4",
//				"ROMI3",
//				"RPMG3",
//				"RSID3",
//				"SANB3",
//				"SANB4",
//				"SAPR3",
//				"SAPR4",
//				"SBSP3",
//				"SCAR3",
//				"SEER3",
//				"SGPS3",
//				"SHOW3",
//				"SHUL4",
//				"SLCE3",
//				"SLED3",
//				"SLED4",
//				"SMLS3",
//				"SMTO3",
//				"SNSY5",
//				"SQIA3",
//				"STBP3",
//				"SUZB3",
//				"TAEE3",
//				"TAEE4",
//				"TASA3",
//				"TASA4",
//				"TCNO4",
//				"TCSA3",
//				"TECN3",
//				"TELB4",
//				"TEND3",
//				"TESA3",
//				"TGMA3",
//				"TIET3",
//				"TIET4",
//				"TIMP3",
//				"TOTS3",
//				"TPIS3",
//				"TRIS3",
//				"TRPL3",
//				"TRPL4",
//				"TUPY3",
//				"UCAS3",
//				"UGPA3",
//				"UNIP6",
//				"USIM3",
//				"USIM5",
//				"VALE3",
//				"VIVA3",
//				"VIVR3",
//				"VIVT3",
//				"VIVT4",
//				"VLID3",
//				"VULC3",
//				"VVAR3",
//				"WEGE3",
//				"WHRL4",
//				"WIZS3",
//				"WLMM4",
//				"YDUQ3"				
				);
				
		setStockCodes(codes);
		
		initialization("CHROME","http://www.b3.com.br/pt_br/market-data-e-indices/servicos-de-dados/market-data/cotacoes/cotacoes");
		
		downloadNegociosByStockUntilNow();
		
		unzipFolder();
		
		preparaLoadDaily();
		
		Connection conn = Util.getMySqlConnection();
		 
		System.out.println("Elapsed time: " + Duration.between(start,
		Instant.now()).getSeconds()/60 + "m" + Duration.between(start,
		Instant.now()).getSeconds()%60 + "s");
		
		Util.truncateMySql("tb_intraday_trade_daily", conn);
		
		for(String f : listaDir()) {
			Util.LoadDataInFileMySql(f, "tb_intraday_trade_daily" ,conn);
		    System.out.println(f); 
		 } 
		
		 try {
			 conn.close(); 
		 } catch (SQLException e) {
			 	e.printStackTrace();
		  }
		
		System.out.println("Elapsed time: " + Duration.between(start,Instant.now()).getSeconds()/60 + "m" + Duration.between(start,Instant.now()).getSeconds()%60 + "s");
		
		// 10:00:00
		start = Instant.now();
		findOportunity();
		System.out.println("Elapsed time: " + Duration.between(start, Instant.now()).getSeconds()/60 + "m" + Duration.between(start, Instant.now()).getSeconds()%60 + "s");
	
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void historicMain(String dateIn, int interval) {
		this.dateIn = dateIn;
		this.interval = interval;
		historicMain();
	}
	
	public void historicMain(int interval) {
		this.interval = interval;
		historicMain();
	}
	
	public void historicMain() {
		Instant start = Instant.now();
		
		Path path = preparaLoadHistorico();
		
		Connection conn = Util.getMySqlConnection();
		Util.truncateMySql("tb_intraday_trade_daily_historic", conn);
		String ff=null;
		try {
			ff = path.toFile().getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loading tabela tb_intraday_trade_daily_historic from: " + ff);
		Util.LoadDataInFileMySql(ff, "tb_intraday_trade_daily_historic" ,conn);
	    System.out.println(ff);		

		start = Instant.now();
		findOportunityHistoric(interval);
		System.out.println("Elapsed time: " + Duration.between(start, Instant.now()).getSeconds()/60 + "m" + Duration.between(start, Instant.now()).getSeconds()%60 + "s");

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void execHistorico(String data, int interval) {
		CotacoesPage cot = new CotacoesPage(data, interval);
		for(int i = 0 ; i < 34; i++ ) {
			cot.historicMain();
		}
	}

	public static void main (String[] args) {
//		CotacoesPage cot = new CotacoesPage("20200925", 30);
//		cot.dailyMain();
		Instant previous = Instant.now();
		System.err.println("Starting historic processing: " + Date.from(previous));
		execHistorico("20200817", 15);	
		System.err.println("Finished historic processing: " + Date.from(Instant.now()));
		System.err.println("Elapsed time preparaLoadHistorico: " + Duration.between(previous, Instant.now()).getSeconds()/3600 + "h" + Duration.between(previous, Instant.now()).getSeconds()%3600/60 + "m" + Duration.between(previous, Instant.now()).getSeconds()%3600%60 + "s");

//		execHistorico("20200917", 15);	
//		execHistorico("20200916", 15);	
	}
}