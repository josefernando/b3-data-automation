package br.com.recatalog.B3DataAutomation.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;

public class Util {
	
	public static Connection getMySqlConnectionAndLoad() {
		String url = "jdbc:mysql://localhost:3306/b3?useTimezone=true&serverTimezone=America/Sao_Paulo";
		String user = "root";
		String password = "root";
		
		Connection conn = null;
		
		try {
//			Class.forName("com.mysql.jdbc.Driver"); NÃO PRECISA MAIS
			conn = DriverManager.getConnection(url , user , password);
			System.out.println("Connection is Successful to database: " + url);
			
			Statement statement = conn.createStatement();
			
			String filePathIn = "C:/Download/Bolsa_de_Valores_Dados/b3_dados/intraday/20200912_091831/TradeIntraday_TIET11_20200910_1.LOAD";
			
		    String loadCommand = "LOAD DATA INFILE '" + filePathIn + "' "
		    		               + " INTO TABLE tb_intraday_trade_daily FIELDS TERMINATED BY ';' LINES TERMINATED BY '\r\n' IGNORE 1 lines "
		    		               + " (  data_referencia , codigo_negociacao_papel , acao , preco_negocio , titulos_negociados , hora_negocio , id_negocio , tipo_sessao_pregao , data_pregao ) ";
			
			int insertedLines = statement.executeUpdate( loadCommand );

			System.out.println("Lines inseridas: " + insertedLines);
			
//			String query = "select count(*) from tb_intraday_trade_daily";
//			
//			Statement stmt = conn.createStatement();
//			
//			ResultSet rs = stmt.executeQuery(query);
//		    while (rs.next()) {
//		        int id = rs.getInt(1);
//		        // print the results
//		        System.out.println("Lines : " + id);
//		    }		

		    conn.close();
			
		} /*
			 * catch (ClassNotFoundException e) { e.printStackTrace(); }
			 */catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static Connection getMySqlConnection() {
		String url = "jdbc:mysql://localhost:3306/b3?useTimezone=true&serverTimezone=America/Sao_Paulo";
		String user = "root";
		String password = "root";
		
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(url , user , password);
			System.out.println("Connection is Successful to database: " + url);
			
			} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static Connection LoadDataInFileMySql(String filePathIn, Connection conn) {
//		String url = "jdbc:mysql://localhost:3306/b3?useTimezone=true&serverTimezone=America/Sao_Paulo";
//		String user = "root";
//		String password = "root";
		
//		Connection conn = null;
		
		try {
////			Class.forName("com.mysql.jdbc.Driver"); NÃO PRECISA MAIS
//			conn = DriverManager.getConnection(url , user , password);
//			System.out.println("Connection is Successful to database: " + url);
			
			Statement statement = conn.createStatement();
			
			filePathIn = filePathIn.replace("\\", "/");
			
//			String filePathIn = "C:/Download/Bolsa_de_Valores_Dados/b3_dados/intraday/20200912_091831/TradeIntraday_TIET11_20200910_1.LOAD";
			
		    String loadCommand = "LOAD DATA INFILE '" + filePathIn + "' "
		    		               + " INTO TABLE tb_intraday_trade_daily FIELDS TERMINATED BY ';' LINES TERMINATED BY '\r\n' IGNORE 1 lines "
		    		               + " (  data_referencia , codigo_negociacao_papel , acao , preco_negocio , titulos_negociados , hora_negocio , id_negocio , tipo_sessao_pregao , data_pregao ) ";
			
			int insertedLines = statement.executeUpdate( loadCommand );

			System.out.println("Lines inseridas: " + insertedLines);

	//    conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static Connection truncateMySql(String tableName, Connection conn) {
		
		try {
			Statement statement = conn.createStatement();
		    String sqlCommand = " TRUNCATE " + tableName ;
		    
		    String sqlCommit = "  commit";
			statement.executeUpdate( sqlCommand );
			statement.executeUpdate( sqlCommit );

			System.out.println("Truncated table: " + tableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        
        return destFile;
    }
    
    public static void main(String[] args) {
    	Connection conn = getMySqlConnection();
    	truncateMySql("tb_intraday_trade_daily", conn);
    	try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}