package json_loader.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * 
 * Unzipper.java
 * Class to unzip files from a zip file
 * (The info from several materials can be provided in a zip file)
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Unzipper {

   /**
	* 
	* main method containing examples about using this class
	* 
	* @param args
	* @throws IOException
	*/
	public static void main(String[] args) throws IOException {
		Unzipper u = new Unzipper();
		u.unzip("data/FeGe.zip", "/toDelete");		
		
		System.out.println("END----------");
	}
	
	/**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts all the files and directories structure from
     * a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * 
     * @param zipFilePath file+path of the zip file
     * @param destDirectory path where the zip is decompressed
     * @throws IOException if the zip file or the destination directory can't be found
     * or any other problem about them (e.g., permission problems).
     */
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    
    /**
     * Extracts a single file from a zip file
     * 
     * @param zipIn Stream from the zip file
     * @param filePath is the file to be extracted
     * @throws IOException 
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
