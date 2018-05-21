package json_loader.utils;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.Loader;
import json_loader.error_handling.LoaderException;

/**
 * 
 * Backup.java
 * 
 * Class that performs backups by invoking Postgres pg_dump backup utility
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Backup {
	
	/*pg_dump invocation
	 * -U postgres = user is postgres
	 * -d postgres = database is postgres
	 * -n public = schema is publci
	 * -a = export only data wihtout schema, triggers and procedures
	 * -E utf8 = use utf8 encoding
	 * -b = force blobs exportation (for the attached files: png, concat, cif ... )
	 * --inserts = use inserts intead of copy command for the sake of SQL compatibility if case of migration to another DBMS in the future
	 * --disable-triggers = the generated backup sctipt will deactivate triggers before insertions and reactivate them after insertion
	 * 		Explanation: some of the triggers forbid updates in calculated fields (i.e., materials auxiliary properties).
	 * 		If these triggers are no deactivated temporally during the loading process, we'll get SQL errors because the triggers will detect
	 * 		the script trying to update these fields.    
	 * -f <name of the backup file> = the name of the backup file that will be created 
	 * 
	 */
	
	private static Logger l = LoggerFactory.getLogger(Backup.class);;
	
	/**
	 * Backup.java
	 * 
	 * Main class that can be used to make eventual backups
	 * 
	 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
	 * @version 1.0
	 * @since 1.0 
	 */
	public static void main(String[] args) {
		Config.loadConfig();
		doBackup();

	}
	
	/**	
	 * It performs a backup copy of the PostgreSQL public schema
	 * invoking pg_dump
	 * 
	 * It checks in which OS the app is running and then it picks
	 * the corresponding backup script
	 * 
	 * The backups are allocated in the backups folder (see Config.java)
	 * The backup file names are in the form:
	 * 
	 * backup_<time in millisecs><date>#<24h time>#<minutes>_CEST_<year>
	 * 
	 * For example
	 * 
	 * backup_1526742755077Sat_May_19_17#12#35_CEST_2018
	 */	
	public static void doBackup(){
		try{
		ConnectionPool p = ConnectionPool.getInstance();
		String password = p.getPassword();
		
		List<String> command = new ArrayList<String>();
		
		String OS = System.getProperty("os.name").toLowerCase();
		
		if (OS.indexOf("win") >= 0)		//Windows		
			command.add("res\\doBackUp.bat");
		else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 || OS.indexOf("sunos") >=0 ) //Unix
			command.add("./res/doBackUp.sh");
		else //Mac?? 
			throw new LoaderException(LoaderException.UNSUPPORTED_OS);
		
		Long now=System.currentTimeMillis();
		String today=new Date().toString();
		today=today.replaceAll(" ", "_");
		today=today.replaceAll(":", "#");
		
		command.add(Config.BACKUP_FOLDER+"backup_"+now+today+".sql");		
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Map<String, String> environ = builder.environment();
	    environ.put("PGPASSWORD", password);
	    
	    String myPgHome = Config.PG_HOME;
	    if (myPgHome.indexOf(" ") >= 0)	
	    	myPgHome="\""+myPgHome+"\"";
			
	    environ.put("PG_HOME", myPgHome);	    
		
	    builder.redirectErrorStream(true);	    
	    
	    try {
			final Process process = builder.start();
			
			InputStream is = process.getInputStream();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
		    String line;
		    while ((line = br.readLine()) != null) {
		      l.info(line);
		    }
		    l.info("Backup at time in milli="+now+" finished succesfully");
		} catch (IOException e) {
			throw new LoaderException(LoaderException.INTERRUPTED_BACK_UP);
		}
	    
		} catch (LoaderException e) {
        	throw new RuntimeException();
		}	
	}

}
