/*
 *      Copyright (C) 2017-2018 UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056  see the file COPYING.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 */

package json_loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.dao.Attached_files;
import json_loader.dao.DBitem;
import json_loader.error_handling.LoaderException;
import json_loader.utils.Backup;
import json_loader.utils.Config;
import json_loader.utils.ConnectionPool;
import json_loader.utils.FileManager;
import json_loader.utils.Unzipper;

/**
 * Loader.java
 * 
 * Main class for the project. See doc on main method.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Loader {
	
	private static Logger l = LoggerFactory.getLogger(Loader.class);;	
	
	/**	
	 * Main method for this class.
	 * 
	 * @param args
	 * It accepts an only one mandatory argument pointing the file to load.
	 * The file can contain the information about one or more items (i.e., materials)
	 * Such argument comes in two flavors;
	 * 
	 *  A) It could be a single json file.
	 *   A json file can contain a single json object representing an only item/material
	 *   or a json array representing several items/materials.
	 *   
	 *   It is assumed that related files ( png, cig, CONTCAR etc ...)
	 *   are located in the same path than such json file.
	 *   
	 *   For example:
	 *  java -cp "NOVAMAG-Java-Loader.jar;lib/*" json_loader.Loader F:\demo\FeNi_L10_v3.json
	 *  
	 *  B) And typically it also could be a zip file
	 *  	The zip file can contain a subdirectories hierarchy
	 *  	Each subdirectory can contain several subdirectories and/or several json files
	 *  	If a json file is related to some additional files ( png, cig, CONTCAR etc ...)
	 *      such files must be located in the same directory than such json file within the zip.
	 *  
	 *    For example:
	 *  java -cp "NOVAMAG-Java-Loader.jar;lib/*" json_loader.Loader F:\demo\FeNi_L10_v3.zip  
	 * 
	 * The message from LoaderException.MISSING_ARG is printed if no command line argument is provided
	 */
	public static void main(String[] args){
		
		try{
			
			if (args==null||args.length==0)
				throw new LoaderException(LoaderException.MISSING_ARG);
			
			String fileName = args[0]; 
			//Load configuration from res/config.json file
			Config.loadConfig();
			Backup.doBackup();
			
			Loader l = new Loader();							
			l.parseFile(fileName);
			
			Backup.doBackup();
			
		} catch ( Exception e ){
			l.error(e.getMessage());			
		} 
				
	}
	
	/**	
	 * Parse the file pointed by the argument, which is supposed to be a json
	 * file or a zip file.
	 * 
	 * If the file is a zip, it unzip it and then process
	 * all json files in the zip. It also deletes the temporary
	 * directory used in the decompression.
	 * 
	 * @param fileName the path+file to be processed 
	 * @throws IOException if the file can't be found or opened
	 * @return the number of materials succesfully loaded
	 */	
	public int parseFile(String fileName) throws IOException{
		int n=0;
		
		String extension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		
		//System.out.println(extension);
		
		switch (extension){
			case "json":
				System.out.println("Loading a "+extension+" file...");
				String currPath = FileManager.getPath(fileName);
				Attached_files.setCurrPath(currPath);
				
				Loader l = new Loader();
				n=l.parseJSONfile(fileName);
				
				printReport(fileName, n);
				
				break;
				
			case "zip":
				System.out.println("Loading a "+extension+" file...");	
								
				Unzipper u = new Unzipper();
				Long now=System.currentTimeMillis();
				u.unzip(fileName, Config.TEMP_FOLDER+now);
				
				n=processUnizipped(Config.TEMP_FOLDER+now);
				printReport(fileName, n);
				
				FileManager.purgeDirectory(Config.TEMP_FOLDER+now);
				FileManager.rmDir(Config.TEMP_FOLDER+now);				
				
				break;
			case "gzip":
			case "tar":
				System.out.println(extension+" is not still supported in this version");
				break;
			default:
				System.out.println(extension+"files are not supported in this version");
				break;
		}
		
		return n;
	}
	
	/**
	 * Class that load a zip file containing materials
	 * 
	 * @param filename the path to the temp directory where was unzipped 
	 * a zip file containing json files with several materials in each one.
	 * @return the number of materials in the zip succesfully loaded
	 * @throws IOException if the file can't be found or opened
	 */
	public int  processUnizipped(String filename) throws IOException{
		return processUnizipped(filename,0);
	}
	
	/**
	 * It traverses recursively a subdirectory tree and load all json files it finds
	 * into the database. Such subdirectory is a temporary one that comes from zip
	 * decompression (a zip cointaining several materials).
	 * 
	 * @param filename the path to the temp directory where was unzipped 
	 * a zip file containing json files with several materials in each one.
	 * @param lastN is a counter of the number of materials in the zip succesfully loaded in 
	 * the moment of this recursive invocation to the method
	 * @return the number of materials in the zip succesfully loaded
	 * @throws IOException if the file can't be found or opened
	 */
	private int  processUnizipped(String filename, int lastN) throws IOException{
		File[] paths;
		int n=0;
		Attached_files.setCurrPath(filename+"/");
		
		if (lastN>0)
			n=lastN;
		
		File f_root = new File(filename);
		paths = f_root.listFiles();
		
		for(File f:paths) {
			if (f.isDirectory()){
				System.out.println("BEGIN Directory: "+f.getAbsolutePath()+"\t n="+n);				
				n=processUnizipped(f.getAbsolutePath(),n);
				System.out.println("END Directory: "+f.getAbsolutePath()+"\t n="+n);
			} else {
				String inner_file_name = f.getAbsolutePath();
				String extension = inner_file_name.substring(
						inner_file_name.lastIndexOf(".")+1, inner_file_name.length());
				
				//System.out.println(extension);
				if (extension.equalsIgnoreCase("JSON")){
					System.out.println(""+n+"\t"+f.getAbsolutePath());
				
					Loader l = new Loader();
					n+=l.parseJSONfile(inner_file_name);
				}
			}
		}
		
		return n;
	}
	
	/**
	 * It loads a single json file with one or several materials
	 * 
	 * @param fileName the path+filename to a json file containing materials
	 * @return the number of materials succesfully loaded (if the file contains
	 * a json array, then it cointains one material per array element, if the file
	 * contains a json object it only contains a single material)
	 * @throws IOException if the file can't be found or opened
	 */
	public int parseJSONfile( String fileName ) throws IOException{
		
		InputStream is = new FileInputStream(fileName);
		String jsonTxt = IOUtils.toString(is, "UTF-8");		
		
		//System.out.println(jsonTxt);
		JSONObject obj = null;
		String firstChar = String.valueOf(jsonTxt.charAt(0));
		int n=0;
		
		ConnectionPool p = null;
        Connection con = null;
		
		try {
			
			p=ConnectionPool.getInstance();
		    con = p.getConnection();
			
	        if (firstChar.equalsIgnoreCase("{")) {
	        	obj = new JSONObject(jsonTxt);
	        	
	            JSONparser jp = new JSONparser();
	            jp.parseJSON(obj);
	            
	            DBitem item = jp.getItem();
	            item.insert(con, true);
	            n=1;
	            
	        } else {
	        	JSONArray a_obj=new JSONArray(jsonTxt);   
			
	        	for (int i = 0, size = a_obj.length(); i < size; i++)
	            {
	              obj = a_obj.getJSONObject(i);
	        	
	              JSONparser jp = new JSONparser();
	              jp.parseJSON(obj);
	              //System.out.println(jp.getItem());
	              DBitem item = jp.getItem();
	              item.insert(con, false);
	              n=i+1;
	            }
	        }
        	
        	con.commit();
        		
    	} catch (SQLException e) {
			l.error(e.getMessage());
			e.printStackTrace();
		} finally {
			p.close(con);
			is.close(); 
		}
         
		return n;
	
	}
	
	/**
	 * 
	 * Print a final report about the loading process
	 * 
	 * @param fileName is the name of the file that has been loaded
	 * @param nRows is the number of items (i.e., materials) that have been loaded succesfully
	 */
	private void printReport(String fileName, int nRows){
		System.out.println("---------------------------------------------------");
		System.out.println("The file "+fileName+" was successfully loaded.");
		System.out.println(nRows+" items loaded");
		System.out.println("---------------------------------------------------");
				
		l.info("---------------------------------------------------");
		l.info("The file "+fileName+" was successfully loaded.");
		l.info(nRows+" items loaded");
		l.info("---------------------------------------------------");
	}
}
