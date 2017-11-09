package json_loader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import json_loader.Config;

public class FileManager {
	
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
	
	
	public static void purgeDirectory(File dir) {
		File[] paths;
		try{
			paths = dir.listFiles();
			
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
	
	public static void purgeDirectory(String dir) {
		File file = new File(dir);
		purgeDirectory(file);
	}
	
	public static void rmDir(String dir) {
		File file = new File(dir);
		if (file.isDirectory())
			file.delete();
	}
	
	public static String getPath( String filePath){
		File f = new File(filePath);
		
		String absPath = f.getAbsolutePath();
		
		absPath=absPath.substring(0, absPath.lastIndexOf(File.separator));
		return absPath+File.separator;		
	}
}
