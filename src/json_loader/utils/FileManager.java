package json_loader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileManager.java
 * 
 * Class to:
 * 	Copy files moving them to another directory
 * 	Delete files, delete all files in directories and delete directories
 *  Get the absolute path of a given file
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class FileManager {
	
	/**
	 * Copy a file into a destination directory that is placed inside
	 * the temp folder. The temp folder is used to unzip a zip file
	 * containing several items (i.e., materials) 
	 * 
	 * @param filename is the file to be copied
	 * @param dest is the relative path to the destination subdirectory
	 * 	The file is copied in the path temp folder/destination directory
	 * @throws IOException
	 */
	public static void copy(String filename, String dest) throws IOException{
		File sourceFile = new File(filename);
        File destinationFile = new File(Config.TEMP_FOLDER+sourceFile.getName());
		
		FileInputStream fileInputStream = new FileInputStream(sourceFile);
        FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);

        int bufferSize;
        byte[] bufffer = new byte[512];
        while ((bufferSize = fileInputStream.read(bufffer)) > 0) {
            fileOutputStream.write(bufffer, 0, bufferSize);
        }
        fileInputStream.close();
        fileOutputStream.close();
		
	}
	
	/**
	 * It deletes recursively all the files and subdirectories hanging on
	 * the directory specified as argument
	 * 
	 * @param dir is the File object representing the directory to empty
	 */
	public static void purgeDirectory(File dir) {
		File[] paths;
		try{
			paths = dir.listFiles();
			
			if (paths==null)
				//The folder is already clean
				return;
			
			 for(File f:paths) {
				 if (f.isDirectory()) 
					 purgeDirectory(f);
	        f.delete();
	    }
		}catch (Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * It deletes recursively all the files and subdirectories hanging on
	 * the directory specified as argument
	 * 
	 * @param dir is the string representing the path to the directory to empty
	 */
	public static void purgeDirectory(String dir) {
		File file = new File(dir);
		purgeDirectory(file);
	}
	
	/**
	 * Deletes a directory
	 * 
	 * @param dir is the string representing the path to the directory to delete
	 */
	public static void rmDir(String dir) {
		File file = new File(dir);
		if (file.isDirectory())
			file.delete();
	}
	
	/**
	 * It returns the absolute file path given an string representing a path
	 *  
	 * @param filePath is the file path
	 * @return the corresponding absolute path
	 */
	public static String getPath( String filePath){
		File f = new File(filePath);
		
		String absPath = f.getAbsolutePath();
		
		absPath=absPath.substring(0, absPath.lastIndexOf(File.separator));
		return absPath+File.separator;		
	}
}
