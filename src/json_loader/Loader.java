package json_loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.dao.Attached_files;
import json_loader.dao.DBitem;
import json_loader.error_handling.LoaderException;
import json_loader.utils.Cleaner;
import json_loader.utils.ConnectionPool;
import json_loader.utils.FileManager;
import json_loader.utils.Unzipper;

public class Loader {
	
	private static Logger l = null;	
	
	public static void main(String[] args) throws Exception{
		
		
		try{
			
			if (args==null||args.length==0)
				throw new LoaderException(LoaderException.MISSING_ARG);
			
			String fileName = args[0]; 
			
			Loader l = new Loader();							
			l.parseFile(fileName);
			
			
		} catch ( Exception e ){
			l.error(e.getMessage());			
		} 
				
	}
	
	public Loader(){
		l =	LoggerFactory.getLogger(Loader.class);
	}
	
	//Return the number of materials succesfully loaded
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
	
	public int  processUnizipped(String filename) throws IOException{
		return processUnizipped(filename,0);
	}
	
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
	
	//It returns the number of materials succesfully loaded
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
        		
    	} catch (NamingException | SQLException | IOException e) {
			l.error(e.getMessage());
			e.printStackTrace();
		} finally {
			p.close(con);
			is.close(); 
		}
         
		return n;
	
	}
	
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
