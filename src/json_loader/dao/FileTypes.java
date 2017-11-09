package json_loader.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;
import json_loader.utils.ConnectionPool;

public class FileTypes {
	private static Logger l = null;
	private static FileTypes m_fileTypes=null;
	
	TreeMap<String, FileType> m_listOfTypes;
	

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
	
	public static FileTypes getInstance(){
		if (m_fileTypes==null){
			m_fileTypes = new FileTypes();
		}
		return m_fileTypes;
	}
	
	private FileTypes(){
		l =	LoggerFactory.getLogger(FileTypes.class);
		
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
		} catch (SQLException | NamingException | IOException e) {
			p.undo(con);
			l.error(e.getMessage());
			
		} finally {			
			p.close(rs_sel_types);
			p.close(pstm_sel_types);
			p.close(con);
		}
	}	
	
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
	
	public boolean isText(String fileTypeName){
		
		FileType ft = m_listOfTypes.get(fileTypeName);
		return ft.getIsText();
	}

}
