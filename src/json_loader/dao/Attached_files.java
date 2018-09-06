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
package json_loader.dao;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.JSONparser;
import json_loader.error_handling.LoaderException;
import json_loader.utils.Cleaner;
import json_loader.utils.Config;
import json_loader.utils.ConnectionPool;

/**
 * Attached_files.java
 *  Class to represent a java object containing the list of files a material
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Attached_files {
	
	private static String curr_path=Config.TEMP_FOLDER;
	
	private long lastMafId;
	private static Logger l = LoggerFactory.getLogger(Attached_files.class);
	
	private String	 m_file_type;
	private boolean  m_isText;
	private JSONArray	 m_info;
	private JSONArray m_files;
	
	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 * @throws IOException
	 * @throws NamingException
	 */
	public static void main(String[] args) throws IOException, NamingException {
		
		 String fileName="data_for_tests/dao.attached_files/Fe12Ge6_#164_1.json";
		
		 InputStream is = new FileInputStream(fileName);         
         String jsonTxt = IOUtils.toString(is, "UTF-8");
         //System.out.println(jsonTxt);
         
         JSONObject obj = new JSONObject(jsonTxt);          
         JSONparser jp = new JSONparser();
         jp.parseJSON(obj);
		
         DBitem item = jp.getItem();
		
		JSONArray allFiles = item.getAttached_files();
		JSONArray allFilesInfo = item.getAttached_files_info();
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		ResultSet rs=null;		
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Cleaner.CleanDB();
			
			st.executeUpdate("INSERT INTO molecules (formula,stechiometry) VALUES ('F3Sn','F0.75Sn0.25')");			
			
			st.executeUpdate(
					  "INSERT INTO items"
					  + "(type, name, summary, formula, production_info,compound_space_group,"
					  + " unit_cell_volume, lattice_parameters, lattice_angles,	atomic_positions,"
					  + " crystal_info, unit_cell_energy, unit_cell_formation_enthalpy, energy_info,"
					  + "unit_cell_spin_polarization, atomic_spin_specie, saturation_magnetization,"
					  + "magnetization_temperature, magnetization_info,	magnetocrystalline_anisotropy_energy,"
					  + "anisotropy_energy_type, magnetocrystalline_anisotropy_constants, kind_of_anisotropy,"
					  + "anisotropy_field, anisotropy_info,exchange_integrals,exchange_info,magnetic_order,"
					  + "curie_temperature, curie_temperature_info, remanence, coercivity, energy_product,"
					  + "hysteresis_info, domain_wall_width, domain_wall_info, exchange_stiffness,"
					  + "exchange_stiffness_info,reference, comments)"
					  + " VALUES ( "
					  + " 'E', 'Fe3Sn', 'Solid state reaction, crystal, anisotropy, magnetization, Tc', 'F3Sn',"
					  + " '2 Solid State Reactions at 800ºC, for 48h',"
					  + "  194, null, '[5.4621, 5.4621,4.3490]', null,"
					  + " '[{\"atom\":\"Fe1\",\"vals\":[0.8442,0.6912,\"1/4\"]},"
					  + "	{\"atom\":\"Sn1\",\"vals\":[\"1/3\",\"2/3\",\"1/4\"]}]',"
					  + "'Cu-Kα radiation, room temperature, atmospheric pressure',"
					  + " null, null, null,  null, null, 1.2, 300, null,"
					  + " null, 'U', null, 'P', 2.5, null, null, null,"
					  + " 'ferromagnet', 747, null, null, null, null,"
					  + " null, null, null, null, null, null, null  )"
					  , Statement.RETURN_GENERATED_KEYS);
									
			rs = st.getGeneratedKeys();
			long key=-1;
			if(rs.next())
				key = rs.getLong(1);
			con.commit();
			
			Attached_files 	af  = new Attached_files(key);
			af.setAttached_files(allFiles);
			af.setAttached_files_info(allFilesInfo);
			af.insert(con, true);
			
			rs = st.executeQuery("SELECT mafId, file_name, file_type, blob_content, info FROM attached_files");
			while (rs.next()){
				System.out.println(rs.getString("mafId")+rs.getString("file_name")+rs.getString("file_type")+rs.getString("info"));
				
				byte[] fileBytes = rs.getBytes("blob_content");
				
				String sft = rs.getString("file_Type");
				boolean isText = FileTypes.getInstance().isText(sft);
				
				if (isText){				
					String m_clob = new String(fileBytes);
					System.out.println(	m_clob);
				} else {
					/*
					InputStream in = new ByteArrayInputStream(fileBytes);
					BufferedImage bImageFromConvert = ImageIO.read(in);
					
					ImageIO.write(bImageFromConvert, "jpg", new File(
							"f:/new-darksouls.jpg"));
					*/
					System.out.println("JPG OK!");
				}
				
			}
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}

	}
	
	/**
	 * Setter for the path where the attached files are
	 * 
	 * @param thePath
	 */
	public static void setCurrPath(String thePath){
		curr_path=thePath;
	}
	
	/**
	 * Setter for the crystallographic data (metadata format CIF, CONTCAR.vasp, …), 
	 * property values versus temperature, property values versus external magnetic field,
	 *  interatomic potentials, figures,
	 *  Example: ["Fe10Ta2_#129_1.cif", "CONTCAR_Fe10Ta2_#129_1"]
	 *  
	 * @param ja is a a json array with a list of file names (the path is assumed that is the
	 * same path than the json file that references them)
	 */
	public void setAttached_files(JSONArray ja){
		m_files=ja;
	}
	
	/**
	 * Setter for the attached files info providing a short description 
	 * of the attached files, that is, which information contain and how it was obtained. 
	 *
	 * @param ja is a json array with the same number of elements than the attached files json array
	 */	
	public void setAttached_files_info(JSONArray ja){
		m_info=ja;
	}
	
	/**
	 * Class constructor
	 * It sets the foreign key that references items table
	 * 
	 * @param itemKey is the items.mafId value of the material related
	 * with the list of files in the Attached_files object
	 */
	public Attached_files(long itemKey){
			lastMafId = itemKey;			
	}	
	
	/**
	 * Makes the necessary SQL insertions in the attached_files table
	 * to associate the list of files in the Attached_files object to its
	 * item
	 * 
	 * @param con is the database connection. If it's null a new connection is
	 * 	created and also released at the end of the method
	 *  Usually this param is not null, as this insertion is part of the item insertion
	 *  in DBitem class, and both share the same transaction, so both share the same
	 *  connection as well. However you can set it to true for debugging and testing
	 *  purposes.
	 * @param doCommit is true if the insertions must be committed at the end
	 *  of method execution, and it's false if not
	 *  Usually this param is false, as this insertion is part of the item insertion
	 *  in DBitem class. However you can set it to true for debugging and testing
	 *  purposes.
	 *  
	 *  @throws
	 *  	IOException  usually when a referenced file is not found in the expected path
	 *  	SQLException debt to any database violation
	 */
	public void insert( Connection con, boolean doCommit)
			throws IOException, SQLException{
		
		ConnectionPool p = null;
		boolean closeConnection=false;
		
		PreparedStatement pst_ins_file=null;
		
		FileInputStream fis = null;
		
		try{
			p = ConnectionPool.getInstance();
			if (con==null){
				con = p.getConnection();
				closeConnection = true;
			}
			
			if (m_files!=null){
			
				Object js_file = null;
				Object js_info = null;
				String theFile = null;  
				int infolen = 0;
				if (m_info!=null) infolen=m_info.length();
				String theInfo = null;
				for (int i = 0, size = m_files.length(); i < size; i++){
					js_file = m_files.get(i);
					theFile = js_file.toString();
					
					if (i<infolen){
						js_info = m_info.get(i);
						if (js_info.equals(null))
							theInfo=null;
						else
							theInfo = js_info.toString();
					} else {
						theInfo = null;
					}
						
					File f = new File(curr_path+theFile);
					if (!f.exists()||!f.isFile()){
						throw new LoaderException(LoaderException.MISSING_ATTACHED_FILE);
					}
					
					deduceFileType(f.getName());
					
					fis = new FileInputStream(f);
					pst_ins_file = con.prepareStatement("INSERT INTO attached_files"
							+ "(mafId, file_name, file_type, is_text, blob_content, info) "
							+ "VALUES (?, ?, ?, ?, ?, ?)");
					pst_ins_file.setLong(1, lastMafId);
					pst_ins_file.setString(2, theFile);
					pst_ins_file.setString(3, m_file_type);
					pst_ins_file.setBoolean(4, m_isText);
					pst_ins_file.setBinaryStream(5, fis, (int)f.length());
					pst_ins_file.setString( 6, theInfo);
					pst_ins_file.executeUpdate();
					
					fis.close();
					
				}
			}
			
			if (doCommit)
				con.commit();
		
		} catch (SQLException| IOException e) {
			p.undo(con);
			l.error(e.getMessage());
			throw e;
			
		} finally {			
			p.close(pst_ins_file);
			if (closeConnection) p.close(con);
			if (fis!=null)
				try{
					fis.close();
				} catch (IOException e1){
					l.error(e1.getMessage());
				}
		}		
	}
	
	/**
	 * 
	 * Setter for m_file_type & m_isText
	 * 	m_file_type is the type of an attached file.
	 * 		Usually it matches with its extension.
	 * 		However there're files without extension like CONTCAR
	 *  m_isText = true when the file is a text file, and false otherwise
	 * 
	 * @param f is the String containing the file name
	 * @throws LoaderException if the file type is not recognized by the application
	 */
	private void deduceFileType(String f) throws LoaderException{
				
		FileTypes fp = FileTypes.getInstance();
		FileType ft = fp.getFileType(f);
		m_file_type = ft.getType();
		m_isText = ft.getIsText();
		
	}

}
