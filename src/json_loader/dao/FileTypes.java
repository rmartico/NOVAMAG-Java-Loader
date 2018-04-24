package json_loader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;
import json_loader.utils.ConnectionPool;

/**
 * FileTypes.java
 *  Class to represent a java object containing the list of allowed file types
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class FileTypes {
	private static Logger l = LoggerFactory.getLogger(FileTypes.class);
	private static FileTypes m_fileTypes=null;
	
	TreeMap<String, FileType> m_listOfTypes;
	
	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 * @throws LoaderException
	 */
	public static void main(String[] args) throws LoaderException {
		FileTypes fp = FileTypes.getInstance();
		
		String type;
		String fileName;
		
		FileType ft;		
		
		fileName="prueba.jpg";
		ft = fp.getFileType(fileName);
		type = ft.getType();
		System.out.println(fileName+": "+type);
		

		fileName="prueba.CIF";
		ft = fp.getFileType(fileName);
		type = ft.getType();
		System.out.println(fileName+": "+type);
		
		fileName="CONTCAR_prueba";
		ft = fp.getFileType(fileName);
		type = ft.getType();
		System.out.println(fileName+": "+type);

	}
	
	/**
	 * Factory of the class implementing the Singleton design pattern
	 * 
	 * @return the instance of FileTypes.
	 * 		If there isn't still an instance, a new one is created
	 */
	public static FileTypes getInstance(){
		if (m_fileTypes==null){
			m_fileTypes = new FileTypes();
		}
		return m_fileTypes;
	}
	
	/**
	 * Constructor of the class. Is queries the database getting the list
	 * of allowed file types.
	 */
	private FileTypes(){		
		
		m_listOfTypes = new TreeMap<String, FileType>();
		
		ConnectionPool p = null;
		Connection con=null;
		PreparedStatement pstm_sel_types=null;
		ResultSet rs_sel_types=null;
		
		try{
			p=ConnectionPool.getInstance();
			con=p.getConnection();
			
			pstm_sel_types=con.prepareStatement("SELECT file_type, regExp, is_text FROM file_types");
			rs_sel_types=pstm_sel_types.executeQuery();
			
			while (rs_sel_types.next()){
				String key=rs_sel_types.getString("file_type");
				FileType value= new FileType();
				value.setType(key);
				value.setExpReg(rs_sel_types.getString("regExp"));
				value.setIsText(rs_sel_types.getBoolean("is_text"));
				
				m_listOfTypes.put(key, value);
			}
			
			con.commit();
		} catch (SQLException e) {
			p.undo(con);
			l.error(e.getMessage());
			
		} finally {			
			p.close(rs_sel_types);
			p.close(pstm_sel_types);
			p.close(con);
		}
	}	
	
	/**
	 * 
	 * It returns the FileType object that matches with the string in the argument
	 * 
	 * @param fileName is an String representing a file type
	 * @return a FileType object representing this file type
	 * @throws LoaderException when the file type is a not allowed one
	 */
	public FileType getFileType(String fileName) throws LoaderException{
		FileType toReturn=null;
		
		for (Map.Entry<String, FileType> entry: m_listOfTypes.entrySet()){
			//String type=entry.getKey();
			FileType value=entry.getValue(); 
			String regExp = value.getExpReg();
			//boolean is_text = value.getIsText();
			
			Pattern thePattern = Pattern.compile(regExp);
			Matcher doesItMatches = thePattern.matcher(fileName);
			doesItMatches.find();
			
			try{
				doesItMatches.group().length();
				toReturn=value;
				break;
			} catch (IllegalStateException e){		
				//l.error(e.getMessage());
			}
		}
		
		if (toReturn==null)
			throw new LoaderException(LoaderException.NOT_ALLOWED_FILE_TYPE);
		
		return toReturn;
	}
	
	/**
	 * 
	 * Getter to know if the String representing a file type in the argument is
	 *  a text file (e.g., cif, contcar) or not (e.g., png, jpeg)
	 * 
	 * @param fileTypeName
	 * @return
	 */
	public boolean isText(String fileTypeName){
		
		FileType ft = m_listOfTypes.get(fileTypeName);
		return ft.getIsText();
	}

}
