package br.com.recatalog.B3DataAutomation.util;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;

import org.openqa.selenium.WebDriver;

public class Util {
	
	public static WebDriver switchTo(WebDriver driver, String iFrameId) {
		return  driver.switchTo().frame(iFrameId); 
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
    
//    public static void main(String[] args) throws IOException {
//        String fileZip = "src/main/resources/unzipTest/compressed.zip";
//        File destDir = new File("src/main/resources/unzipTest");
//        byte[] buffer = new byte[1024];
//        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
//        ZipEntry zipEntry = zis.getNextEntry();
//        while (zipEntry != null) {
//            File newFile = newFile(destDir, zipEntry);
//            FileOutputStream fos = new FileOutputStream(newFile);
//            int len;
//            while ((len = zis.read(buffer)) > 0) {
//                fos.write(buffer, 0, len);
//            }
//            fos.close();
//            zipEntry = zis.getNextEntry();
//        }
//        zis.closeEntry();
//        zis.close();
//    }    
}
