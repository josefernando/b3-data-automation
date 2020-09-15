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
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

import br.com.recatalog.B3DataAutomation.base.BasePage;
import br.com.recatalog.B3DataAutomation.util.PreparaLoadIntradayTrade;
import br.com.recatalog.B3DataAutomation.util.Util;

public class CotacoesPage extends BasePage {
	
	public CotacoesPage() {
		super();
	}
	
	public Boolean validatePage() { // ref.: https://www.seleniumeasy.com/selenium-tutorials/examples-for-xpath-and-css-selectors
		WebElement  v = driver.findElement(By.xpath("//div[@class='titulo-internas']//h1[contains(text(),'Cotações')]"));
//...ou com cssSelector		WebElement  v = driver.findElement(By.cssSelector("div.titulo-internas > h1"));
		return v != null;
	}
	
	public List<String> listaDir() {
        PowerShell powerShell = PowerShell.openSession();
		String command = "Get-ChildItem '" + downloadDir + "' -Filter *.LOAD | select -expand fullname";
		
        PowerShellResponse response = powerShell.executeCommand(command);
        String outpp = response.getCommandOutput();
        
        String[] files = outpp.split(System.getProperty("line.separator"));
        
//        for(String a : files) {
//        	System.out.println(a);
//        }
        
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

		PreparaLoadIntradayTrade pp = null;
		while (sc.hasNextLine()){
			String line = null;
			line = sc.nextLine();
			count++;
			pp = new PreparaLoadIntradayTrade(line);
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
	
	public void findOportunity(List<String> stockCodes, String data, int interval) {
		
		Connection conn = Util.getMySqlConnection();
		// (stock_code varchar(12), start_date varchar(10), pinterval int, OUT error_code int, OUT error_msg varchar(255))
		String procedureCall = "{CALL FIND_OPORTUNITY_INTRADAY_BY_STOCK(?,?,?,?,?)}";
		for(String scode : stockCodes) {
			try {
				CallableStatement stmt = conn.prepareCall(procedureCall);
		         stmt.setString(1, scode);  
		         stmt.setString(2, data);  
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
	}
	
	public void downloadNegociosByStockUntilNow(List<String> stockCodes, String data) {
		driver.switchTo().defaultContent(); //new
		driver.switchTo().frame("bvmf_iframe");
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
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
			
//			wait0.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='modal-body']")));
//			wait0.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//a[@data-dropdown='Section-2']")));
//			wait0.until(ExpectedConditions.visibilityOf(pesquisarBtn));
			wait0.ignoring(StaleElementReferenceException.class)
			.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit' and @class='button expand']")));
				pesquisarBtn = driver.findElement(By.xpath("//input[@type='submit' and @class='button expand']"));

				js.executeScript("arguments[0].click()", pesquisarBtn);
			try {
				String var = "//a[contains(@href,'" + code + "')]";
				List<WebElement> ativoPesquisadoLinks = driver.findElements(By.xpath(var));
				if(ativoPesquisadoLinks.isEmpty()) {
					System.out.println("Sem processamento até o momento: " + code + " " + Duration.between(previousCode,Instant.now()).getSeconds() + " seconds" );
					continue;
				}
				
//				List<WebElement> ativoPesquisadoLinks = driver.findElements(By.xpath("(//a[contains(text(),'Ativo pesquisado')] | //h3[@id='label-nao-encontrado'])") );
//				if(!ativoPesquisadoLinks.isEmpty()) {
////					System.out.println(ativoPesquisadoLinks.get(0).getTagName());
//					if(ativoPesquisadoLinks.get(0).getTagName().equalsIgnoreCase("a")) 
//						ativoPesquisadoLink = ativoPesquisadoLinks.get(0);
//					else {
//						System.out.println("Sem processamento para a ação: " + code);
//						continue;
//					}
//				}
				
//				ativoPesquisadoLink = driver.findElement(By.xpath(var));
				ativoPesquisadoLink = ativoPesquisadoLinks.get(0);
 
//				WebDriverWait wait = new WebDriverWait(driver, 10);
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(var)));
				ativoPesquisadoLink.click();
			} catch(NoSuchElementException e) {
				System.out.println("Erro: Sem processamento para a ação: " + code  );
				continue;
			} 
			/*
			 * catch(StaleElementReferenceException e) {
			 * System.out.println("Erro: StaleElementReferenceException: " + code );
			 * continue; }
			 */ catch(TimeoutException e) {
				System.err.println("TimeoutException ");
				e.printStackTrace();
				break;
			}catch (ElementClickInterceptedException e) {
				e.printStackTrace();
				continue;
			}
			System.out.println("Code : " + code + " - " + Duration.between(previousCode,Instant.now()).toSeconds() + "." + Duration.between(previousCode,Instant.now()).toNanosPart());
		}
		
//		driver.close();
//		driver.quit();
	}

	public static void main (String[] args) {
		
		Instant start = Instant.now();
		
		CotacoesPage cot = new CotacoesPage();
		cot.initialization("CHROME", "http://www.b3.com.br/pt_br/market-data-e-indices/servicos-de-dados/market-data/cotacoes/cotacoes");
		List<String> codes = Arrays.asList(
//				"ADHM3",
//				"TIET11",
//				"TIET3",
//				"TIET4",
//				"AFLT3",
//				"RPAD3",
//				"RPAD5",
//				"RPAD6",
//				"ALSO3",
//				"ALPK3",
//				"ALPA3",
//				"ALPA4",
//				"APER3",
//				"ALUP11",
//				"ALUP3",
//				"ALUP4",
//				"ABEV3",
//				"AMBP3",
//				"CBEE3",
//				"ANIM3",
//				"ARZZ3",
//				"CRFB3",
//				"ATOM3",
//				"AZEV3",
//				"AZEV4",
//				"AZUL4",
//				"BTOW3",
//				"B3SA3",
//				"BAHI3",
//				"BMGB4",
//				"BIDI11",
//				"BIDI3",
//				"BIDI4",
//				"BEES3",
//				"BEES4",
//				"BDLL3",
//				"BDLL4",
//				"BTTL3",
//				"BALM3",
//				"BALM4",
//				"BBSE3",
//				"BBML3",
//				"ABCB4",
//				"BRIV3",
//				"BRIV4",
//				"BAZA3",
//				"BBDC3",
//				"BBDC4",
//				"BBAS11",
//				"BBAS3",
//				"BPAC11",
//				"BPAC3",
//				"BPAC5",
//				"BGIP3",
//				"BGIP4",
//				"BPAR3",
//				"BRSR3",
//				"BRSR5",
//				"BRSR6",
//				"IDVL3",
//				"IDVL4",
//				"BMIN3",
//				"BMIN4",
//				"BMEB3",
//				"BMEB4",
//				"BNBR3",
//				"BPAN4",
//				"PINE3",
//				"PINE4",
//				"SANB11",
//				"SANB3",
//				"SANB4",
//				"BMKS3",
//				"BIOM3",
//				"BSEV3",
//				"BKBR3",
//				"BOBR3",
//				"BOBR4",
//				"BRML3",
//				"BRPR3",
//				"BRAP3",
//				"BRAP4",
//				"BBRK3",
//				"AGRO3",
//				"BRKM3",
//				"BRKM5",
//				"BRKM6",
//				"BSLI3",
//				"BSLI4",
//				"BRFS3",
//				"BRQB3",
//				"CAMB3",
//				"CAML3",
//				"CCRO3",
//				"CEAB3",
//				"MAPT3",
//				"MAPT4",
//				"ELET3",
//				"ELET5",
//				"ELET6",
//				"CLSC3",
//				"CLSC4",
//				"AALR3",
//				"CESP3",
//				"CESP5",
//				"CESP6",
//				"PCAR3",
//				"CASN3",
//				"CASN4",
//				"GPAR3",
//				"CEGR3",
//				"CEEB3",
//				"CEEB5",
//				"CEEB6",
//				"CEBR3",
//				"CEBR5",
//				"CEBR6",
//				"CMIG3",
//				"CMIG4",
//				"CEPE3",
//				"CEPE5",
//				"CEPE6",
//				"COCE3",
//				"COCE5",
//				"COCE6",
//				"CSRN3",
//				"CSRN5",
//				"CSRN6",
//				"CEED3",
//				"CEED4",
//				"EEEL3",
//				"EEEL4",
//				"FESA3",
//				"FESA4",
//				"CEDO3",
//				"CEDO4",
//				"CGAS3",
//				"CGAS5",
//				"HBTS3",
//				"HBTS5",
//				"HBTS6",
//				"HGTX3",
//				"CATA3",
//				"CATA4",
//				"LCAM3",
//				"MSPA3",
//				"MSPA4",
//				"CPLE3",
//				"CPLE5",
//				"CPLE6",
//				"PEAB3",
//				"PEAB4",
//				"SBSP3",
//				"CSMG3",
//				"SAPR11",
//				"SAPR3",
//				"SAPR4",
//				"CSAB3",
//				"CSAB4",
//				"CSNA3",
//				"CTNM3",
//				"CTNM4",
//				"CTSA3",
//				"CTSA4",
//				"CTSA8",
//				"CIEL3",
//				"CMSA3",
//				"CMSA4",
//				"CNSY3",
//				"COGN3",
//				"ODER3",
//				"ODER4",
//				"BRGE11",
//				"BRGE3",
//				"BRGE5",
//				"BRGE6",
//				"BRGE7",
//				"BRGE8",
//				"CALI3",
//				"CALI4",
//				"TEND3",
//				"CORR3",
//				"CORR4",
//				"RLOG3",
//				"CSAN3",
//				"CPFE3",
//				"CRDE3",
//				"CARD3",
//				"CTCA3",
//				"TRPL3",
//				"TRPL4",
//				"CVCB3",
//				"CYRE3",
//				"CCPR3",
//				"DMVF3",
//				"DASA3",
//				"PNVL3",
//				"PNVL4",
//				"DIRR3",
//				"DOHL3",
//				"DOHL4",
//				"DMMO3",
//				"DTCY3",
//				"DTCY4",
//				"DTEX3",
//				"ECOR3",
//				"ENBR3",
//				"EALT3",
//				"EALT4",
//				"EKTR3",
//				"EKTR4",
//				"LIPR3",
//				"EMAE3",
//				"EMAE4",
//				"EMBR3",
//				"ECPR3",
//				"ECPR4",
//				"ENAT3",
//				"ENMT3",
//				"ENMT4",
//				"ENGI11",
//				"ENGI3",
//				"ENGI4",
//				"ENEV3",
//				"EGIE3",
//				"EQTL3",
//				"EQPA3",
//				"EQPA5",
//				"EQPA6",
//				"EQPA7",
//				"ETER3",
//				"EUCA3",
//				"EUCA4",
//				"EVEN3",
//				"BAUH3",
//				"BAUH4",
//				"EZTC3",
//				"VSPT3",
//				"VSPT4",
//				"FHER3",
//				"CRIV3",
//				"CRIV4",
//				"FNCN3",
//				"FLRY3",
//				"FLEX3",
//				"FRAS3",
//				"GFSA3",
//				"GSHP3",
//				"GGBR3",
//				"GGBR4",
//				"GOLL4",
//				"GPIV33",
//				"GPCP3",
//				"GPCP4",
//				"CGRA3",
//				"CGRA4",
//				"GRND3",
//				"SOMA3",
//				"CNTO3",
//				"GUAR3",
//				"HAGA3",
//				"HAGA4",
//				"HAPV3",
//				"HBOR3",
//				"HETA3",
//				"HETA4",
//				"HOOT3",
//				"HOOT4",
//				"HYPE3",
//				"IGBR3",
//				"IGSN3",
//				"IGTA3",
//				"JBDU3",
//				"JBDU4",
//				"ROMI3",
//				"INEP3",
//				"INEP4",
//				"PARD3",
//				"INNT3",
//				"MEAL3",
//				"FIGE3",
//				"FIGE4",
//				"MYPK3",
//				"RANI3",
//				"RANI4",
//				"IRBR3",
//				"ITUB3",
//				"ITUB4",
//				"ITSA3",
//				"ITSA4",
//				"JBSS3",
//				"JPSA3",
//				"JHSF3",
//				"JFEN3",
//				"JOPA3",
//				"JOPA4",
//				"JSLG3",
//				"CTKA3",
//				"CTKA4",
//				"KEPL3",
//				"KLBN11",
//				"KLBN3",
//				"KLBN4",
//				"LMED3",
//				"LIGT3",
//				"LINX3",
//				"RENT3",
//				"LWSA3",
//				"LOGG3",
//				"LOGN3",
//				"LAME3",
//				"LAME4",
//				"LJQQ3",
//				"LREN3",
//				"LPSB3",
//				"LUPA3",
//				"MDIA3",
//				"MSRO3",
//				"MGLU3",
//				"LEVE3",
//				"MGEL3",
//				"MGEL4",
//				"ESTR3",
//				"ESTR4",
//				"POMO3",
//				"POMO4",
//				"MRFG3",
//				"AMAR3",
//				"MERC3",
//				"MERC4",
//				"FRIO3",
//				"MTIG3",
//				"MTIG4",
//				"GOAU3",
//				"GOAU4",
//				"RSUL3",
//				"RSUL4",
//				"MTSA3",
//				"MTSA4",
//				"MILS3",
//				"MMAQ3",
//				"MMAQ4",
//				"BEEF3",
//				"MNPR3",
//				"MTRE3",
//				"MMXM3",
//				"MOAR3",
//				"MDNE3",
//				"MOVI3",
//				"MRVE3",
//				"MULT3",
//				"MNDL3",
//				"NTCO3",
//				"NEOE3",
//				"NORD3",
//				"NRTQ3",
//				"GNDI3",
//				"ODPV3",
//				"OIBR3",
//				"OIBR4",
//				"OMGE3",
//				"OSXB3",
//				"OFSA3",
//				"PDTC3",
//				"PATI3",
//				"PATI4",
//				"PMAM3",
//				"PTBL3",
//				"PDGR3",
//				"PRIO3",
//				"BRDT3",
//				"PETR3",
//				"PETR4",
//				"PTNT3",
//				"PTNT4",
//				"PLAS3",
//				"PPAR3",
//				"FRTA3",
//				"PSSA3",
//				"POSI3",
//				"PPLA11",
//				"PTCA11",
//				"PTCA3",
//				"PRNR3",
//				"PFRM3",
//				"QUAL3",
//				"QUSW3",
//				"RADL3",
//				"RAPT3",
//				"RAPT4",
//				"RCSL3",
//				"RCSL4",
//				"REDE3",
//				"RPMG3",
//				"RNEW11",
//				"RNEW3",
//				"RNEW4",
//				"GEPA3",
//				"GEPA4",
//				"RDNI3",
//				"RSID3",
//				"RAIL3",
//				"SNSY3",
//				"SNSY5",
//				"SNSY6",
//				"STBP3",
//				"SCAR3",
//				"SMTO3",
//				"AHEB3",
//				"AHEB5",
//				"AHEB6",
//				"SLED3",
//				"SLED4",
//				"SHUL3",
//				"SHUL4",
//				"SEER3",
//				"APTI3",
//				"APTI4",
//				"SQIA3",
//				"SLCE3",
//				"SMFT3",
//				"SMLS3",
//				"SOND3",
//				"SOND5",
//				"SOND6",
//				"SPRI3",
//				"SPRI5",
//				"SPRI6",
//				"SGPS3",
//				"STTR3",
//				"STKF3",
//				"SULA11",
//				"SULA3",
//				"SULA4",
//				"NEMO3",
//				"NEMO5",
//				"NEMO6",
//				"SUZB3",
//				"SHOW3",
//				"TASA3",
//				"TASA4",
//				"TECN3",
//				"TCSA3",
//				"TCNO3",
//				"TCNO4",
//				"TGMA3",
//				"TEKA3",
//				"TEKA4",
//				"TKNO3",
//				"TKNO4",
//				"TELB3",
//				"TELB4",
//				"VIVT3",
//				"VIVT4",
//				"TESA3",
//				"TIMP3",
//				"TOTS3",
//				"TPIS3",
//				"TAEE11",
//				"TAEE3",
//				"TAEE4",
//				"LUXM3",
//				"LUXM4",
//				"TRIS3",
//				"CRPG3",
//				"CRPG5",
//				"CRPG6",
//				"TUPY3",
//				"UGPA3",
//				"UCAS3",
//				"UNIP3",
//				"UNIP5",
//				"UNIP6",
//				"USIM3",
//				"USIM5",
//				"USIM6",
//				"VALE3",
//				"VLID3",
//				"VVAR3",
//				"VIVA3",
//				"VIVR3",
//				"VULC3",
//				"WEGE3",
//				"MWET3",
//				"MWET4",
//				"WHRL3",
//				"WHRL4",
//				"WIZS3",
//				"WLMM3",
//				"WLMM4",
//				"YDUQ3"
//====================================================
				"ADHM3",
				"TIET11",
				"TIET3",
				"TIET4",
				"AFLT3",
				"RPAD5",
				"RPAD6",
				"ALSO3",
				"ALPK3",
				"ALPA3",
				"ALPA4",
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
				"AZEV4",
				"AZUL4",
				"BTOW3",
				"B3SA3",
				"BMGB4",
				"BIDI11",
				"BIDI3",
				"BIDI4",
				"BEES3",
				"BEES4",
				"BDLL4",
				"BTTL3",
				"BALM3",
				"BBSE3",
				"ABCB4",
				"BAZA3",
				"BBDC3",
				"BBDC4",
				"BBAS3",
				"BPAC11",
				"BPAC3",
				"BPAC5",
				"BGIP4",
				"BRSR3",
				"BRSR6",
				"IDVL3",
				"IDVL4",
				"BMIN3",
				"BMIN4",
				"BMEB4",
				"BNBR3",
				"BPAN4",
				"PINE4",
				"SANB11",
				"SANB3",
				"SANB4",
				"BMKS3",
				"BIOM3",
				"BSEV3",
				"BKBR3",
				"BOBR4",
				"BRML3",
				"BRPR3",
				"BRAP3",
				"BRAP4",
				"BBRK3",
				"AGRO3",
				"BRKM3",
				"BRKM5",
				"BRFS3",
				"CAMB3",
				"CAML3",
				"CCRO3",
				"CEAB3",
				"MAPT4",
				"ELET3",
				"ELET6",
				"CLSC4",
				"AALR3",
				"CESP6",
				"PCAR3",
				"GPAR3",
				"CEEB3",
				"CMIG3",
				"CMIG4",
				"COCE3",
				"COCE5",
				"FESA4",
				"CEDO3",
				"CEDO4",
				"CGAS5",
				"HGTX3",
				"LCAM3",
				"CPLE3",
				"CPLE6",
				"SBSP3",
				"CSMG3",
				"SAPR11",
				"SAPR3",
				"SAPR4",
				"CSNA3",
				"CTNM3",
				"CTNM4",
				"CTSA3",
				"CTSA4",
				"CIEL3",
				"COGN3",
				"TEND3",
				"RLOG3",
				"CSAN3",
				"CPFE3",
				"CARD3",
				"TRPL3",
				"TRPL4",
				"CVCB3",
				"CYRE3",
				"CCPR3",
				"DMVF3",
				"DASA3",
				"PNVL3",
				"PNVL4",
				"DIRR3",
				"DMMO3",
				"DTCY3",
				"DTEX3",
				"ECOR3",
				"ENBR3",
				"EALT3",
				"EALT4",
				"EMAE4",
				"EMBR3",
				"ENAT3",
				"ENMT3",
				"ENMT4",
				"ENGI11",
				"ENGI3",
				"ENGI4",
				"ENEV3",
				"EGIE3",
				"EQTL3",
				"EQPA3",
				"ETER3",
				"EUCA3",
				"EUCA4",
				"EVEN3",
				"EZTC3",
				"FHER3",
				"CRIV3",
				"FLRY3",
				"FRAS3",
				"GFSA3",
				"GSHP3",
				"GGBR3",
				"GGBR4",
				"GOLL4",
				"GPIV33",
				"GPCP3",
				"CGRA3",
				"CGRA4",
				"GRND3",
				"SOMA3",
				"CNTO3",
				"GUAR3",
				"HAGA3",
				"HAGA4",
				"HAPV3",
				"HBOR3",
				"HYPE3",
				"IGBR3",
				"IGTA3",
				"JBDU3",
				"JBDU4",
				"ROMI3",
				"INEP3",
				"INEP4",
				"PARD3",
				"MEAL3",
				"MYPK3",
				"RANI3",
				"RANI4",
				"IRBR3",
				"ITUB3",
				"ITUB4",
				"ITSA3",
				"ITSA4",
				"JBSS3",
				"JPSA3",
				"JHSF3",
				"JSLG3",
				"CTKA3",
				"KEPL3",
				"KLBN11",
				"KLBN3",
				"KLBN4",
				"LIGT3",
				"LINX3",
				"RENT3",
				"LWSA3",
				"LOGG3",
				"LOGN3",
				"LAME3",
				"LAME4",
				"LJQQ3",
				"LREN3",
				"LPSB3",
				"LUPA3",
				"MDIA3",
				"MGLU3",
				"LEVE3",
				"ESTR4",
				"POMO3",
				"POMO4",
				"MRFG3",
				"AMAR3",
				"MTIG4",
				"GOAU3",
				"GOAU4",
				"MTSA4",
				"MILS3",
				"BEEF3",
				"MNPR3",
				"MTRE3",
				"MMXM3",
				"MDNE3",
				"MOVI3",
				"MRVE3",
				"MULT3",
				"MNDL3",
				"NTCO3",
				"NEOE3",
				"GNDI3",
				"ODPV3",
				"OIBR3",
				"OIBR4",
				"OMGE3",
				"OFSA3",
				"PDTC3",
				"PATI4",
				"PMAM3",
				"PTBL3",
				"PDGR3",
				"PRIO3",
				"BRDT3",
				"PETR3",
				"PETR4",
				"PTNT3",
				"PTNT4",
				"PLAS3",
				"FRTA3",
				"PSSA3",
				"POSI3",
				"PPLA11",
				"PRNR3",
				"PFRM3",
				"QUAL3",
				"RADL3",
				"RAPT3",
				"RAPT4",
				"RCSL3",
				"RCSL4",
				"REDE3",
				"RPMG3",
				"RNEW11",
				"RNEW3",
				"RNEW4",
				"GEPA4",
				"RDNI3",
				"RSID3",
				"RAIL3",
				"SNSY3",
				"SNSY5",
				"STBP3",
				"SCAR3",
				"SMTO3",
				"AHEB3",
				"SLED3",
				"SLED4",
				"SHUL4",
				"SEER3",
				"SQIA3",
				"SLCE3",
				"SMLS3",
				"SOND5",
				"SOND6",
				"SGPS3",
				"SULA11",
				"SULA3",
				"SULA4",
				"SUZB3",
				"SHOW3",
				"TASA3",
				"TASA4",
				"TECN3",
				"TCSA3",
				"TCNO3",
				"TCNO4",
				"TGMA3",
				"TEKA3",
				"TEKA4",
				"TELB4",
				"VIVT3",
				"VIVT4",
				"TESA3",
				"TIMP3",
				"TOTS3",
				"TPIS3",
				"TAEE11",
				"TAEE3",
				"TAEE4",
				"LUXM4",
				"CRPG3",
				"CRPG6",
				"UGPA3",
				"UCAS3",
				"UNIP3",
				"UNIP5",
				"UNIP6",
				"USIM3",
				"USIM5",
				"VALE3",
				"VLID3",
				"VVAR3",
				"VIVA3", 
				"VIVR3",
				"VULC3",
				"WEGE3",
				"MWET4",
				"WHRL3",
				"WHRL4",
				"WIZS3",
				"WLMM3",
				"WLMM4",
				"YDUQ3"
				);
		cot.downloadNegociosByStockUntilNow(codes, "15/09/2020");
		cot.unzipFolder();
		cot.preparaLoad();
		
		Connection conn = Util.getMySqlConnection();
		
		System.out.println("Elapsed time: " + Duration.between(start, Instant.now()).getSeconds()/60 + "m" + Duration.between(start, Instant.now()).getSeconds()%60 + "s");
		
		Util.truncateMySql("tb_intraday_trade_daily", conn);
		
		for(String f : cot.listaDir()) {
			Util.LoadDataInFileMySql(f, conn);
			System.out.println(f);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Elapsed time: " + Duration.between(start, Instant.now()).getSeconds()/60 + "m" + Duration.between(start, Instant.now()).getSeconds()%60 + "s");

		cot.findOportunity(codes, "20200915", 15);
		
		System.out.println("Elapsed time: " + Duration.between(start, Instant.now()).getSeconds()/60 + "m" + Duration.between(start, Instant.now()).getSeconds()%60 + "s");

	}
}