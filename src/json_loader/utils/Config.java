package json_loader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;

/**
 * 
 * Config.java
 * 
 * Class for hardcoded application parameters
 * TEMP_FOLDER: Folder where zips containing json files are unzipped.
 * 
 * Add more params if necessary
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Config {
	private static Logger l = LoggerFactory.getLogger(Config.class);
	
	public static String TEMP_FOLDER=null;		//The folder where unzip zip files	
	public static String BACKUP_FOLDER=null;	//The folder where backups files are allocated
	public static String PG_HOME=null;			//The folder where poostgreSQL is installed (to invoke pg_dump for backup)
	
	/**	
	 * Load the configuration variables from res/config.json file
	 *  
	 * @throws RuntimeException
	 * 	1) If the configuration file can't be found
	 * 	2) If the configuration file is not a json encoded in utf-8
	 * 	3) If any among the expected app variables is missing
	 *  4) If any among the variables representing a directory turns out to point an not-existing directory
	 */ 
	public static void loadConfig(){
		try{
			String file_name="res/config.json";
		    InputStream is;
		    
			try {
				is = new FileInputStream(file_name);
			} catch (FileNotFoundException e) {				
	        	throw new LoaderException(LoaderException.MISSING_CONFIG_FILE);
			}
            
			String jsonTxt;
            try {
				jsonTxt = IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
            	throw new LoaderException(LoaderException.BAD_CONFIG_FILE);
            }
            				
			JSONObject obj = new JSONObject(jsonTxt);
			TEMP_FOLDER = obj.getString("TEMP_FOLDER");
			if (!isDir(TEMP_FOLDER)){
				throw new LoaderException(LoaderException.MISSING_TEMP_FOLDER);
			}
				
			BACKUP_FOLDER = obj.getString("BACKUP_FOLDER");
			if (!isDir(BACKUP_FOLDER)){
				throw new LoaderException(LoaderException.MISSING_BACKUP_FOLDER);
			}	
			
			PG_HOME = obj.getString("PG_HOME");
			if (!isDir(PG_HOME)){
				throw new LoaderException(LoaderException.MISSING_PG_HOME_FOLDER);
			}
				
		} catch (LoaderException e) {
			l.error(e.getMessage());
        	throw new RuntimeException();
		}	
	}
	
	/**	
	 * Checks if the argument is an existing directory
	 * Returns false if this directory does not exists of the argument is null
	 * 
	 * @param dirStr the directory to check 
	 * @return true if the string represents an existing directory
	 */	
	private static boolean isDir(String dirStr){
		if (dirStr==null)
			return false;
		else {
			File dir = new File(dirStr);
	        if (!dir.exists()) {
	        	return false;
	        }
		}
		
		return true;			
	}
}
