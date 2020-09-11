package br.com.recatalog.B3DataAutomation.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class PreparaLoadIntradayTrade {
	
	String data_referencia;
	String codigo_negociacao_papel;
	String acao;
	String preco_negocio;
	String titulos_negociados;
	String hora_negocio;
	String id_negocio;
	String tipo_sessao_pregao;
	String data_pregao;
	
	public PreparaLoadIntradayTrade(String intradayTrade) {
		setData_referencia(intradayTrade);
		setCodigo_negociacao_papel(intradayTrade);
		setAcao(intradayTrade);
		setPreco_negocio(intradayTrade);
		setTitulos_negociados(intradayTrade);
		setHora_negocio(intradayTrade);
		setId_negocio(intradayTrade);
		setTipo_sessao_pregao(intradayTrade);
		setData_pregao(intradayTrade);
	}
	
	public String getData_referencia() {
		return data_referencia;
	}

	public void setData_referencia(String intradayTrade) {
		this.data_referencia = intradayTrade.split(";")[0];
	}

	public String getCodigo_negociacao_papel() {
		return codigo_negociacao_papel;
	}

	public void setCodigo_negociacao_papel(String intradayTrade) {
		this.codigo_negociacao_papel = intradayTrade.split(";")[1];
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String intradayTrade) {
		this.acao = intradayTrade.split(";")[2];
	}

	public String getPreco_negocio() {
		return preco_negocio;
	}

	public void setPreco_negocio(String intradayTrade) {
		this.preco_negocio = intradayTrade.split(";")[3].replace(",", ".");
	}

	public String getTitulos_negociados() {
		return titulos_negociados;
	}

	public void setTitulos_negociados(String intradayTrade) {
		this.titulos_negociados = intradayTrade.split(";")[4];
	}

	public String getHora_negocio() {
		return hora_negocio;
	}

	public void setHora_negocio(String intradayTrade) {
		this.hora_negocio = intradayTrade.split(";")[5];
		this.hora_negocio = hora_negocio.substring(0, 2)
				            + ":"
				            + hora_negocio.substring(2, 4)
				            + ":"
				            + hora_negocio.substring(4, 6)
				            + "."
				            + hora_negocio.substring(6);
	}

	public String getId_negocio() {
		return id_negocio;
	}

	public void setId_negocio(String intradayTrade) {
		this.id_negocio = intradayTrade.split(";")[6];
	}

	public String getTipo_sessao_pregao() {
		return tipo_sessao_pregao;
	}

	public void setTipo_sessao_pregao(String intradayTrade) {
		this.tipo_sessao_pregao = intradayTrade.split(";")[7];
	}

	public String getData_pregao() {
		return data_pregao;
	}

	public void setData_pregao(String intradayTrade) {
		this.data_pregao = intradayTrade.split(";")[8];
	}
	
	public String toCvs() {
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
	
	public String toString() {
		return toCvs();
	}
	
	public static void main(String[] args) throws IOException {
		
		/*
		 * Antes de  rodar com o arquivo ..TXT executar "replace all" no NotePad++
		 * com a seguinte regex [^-.$\n\r\t1234567890-QWERTYUIOPASDFGHJKLÇZXCVBNM/*' ]
		 * com replace de um caracter de espaço, onde a moeda for "CZ$"		 
		 */
		
		File file = new File("C:\\Download\\Bolsa_de_Valores_Dados\\b3_dados\\intraday\\20200910_102025\\TradeIntraday_TIET4_20200903_1.txt");
		
		Path path = Paths.get("C:\\Download\\Bolsa_de_Valores_Dados\\b3_dados\\intraday\\20200910_102025\\TradeIntraday_TIET4_20200903_1.LOAD");
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
		System.out.println(file.getAbsolutePath() + "- linhas: " + count);
		
		sc.close();
	}
}