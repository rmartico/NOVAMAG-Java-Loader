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

import json_loader.Config;
import json_loader.JSONparser;
import json_loader.error_handling.LoaderException;
import json_loader.utils.Cleaner;
import json_loader.utils.ConnectionPool;

public class Attached_files {
	
	private static String curr_path=Config.TEMP_FOLDER;
	
	private long lastMafId;
	private static Logger l = null;
	
	private String	 m_file_type;
	private boolean  m_isText;
	private JSONArray	 m_info;
	private JSONArray m_files;
		
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
	
	public static void setCurrPath(String thePath){
		curr_path=thePath;
	}
	
	public void setInfo(JSONArray theInfo){
		m_info=theInfo;
	}
	
	public void setAttached_files(JSONArray ja){
		m_files=ja;
	}
	
	public void setAttached_files_info(JSONArray ja){
		m_info=ja;
	}
	
	
	public Attached_files(long itemKey){
			l =	LoggerFactory.getLogger(Attached_files.class);
			lastMafId = itemKey;			
	}	
	
	public void insert( Connection con, boolean doCommit)
			throws LoaderException, IOException, NamingException{
		
		
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
		
		} catch (SQLException e) {
			//TODO rename in case duplicate name => unq in table
			
			p.undo(con);
			l.error(e.getMessage());
			
		} finally {			
			p.close(pst_ins_file);
			if (closeConnection) p.close(con);
			if (fis!=null) fis.close();
		}
		
	}
	
	private void deduceFileType(String f) throws LoaderException{
				
		FileTypes fp = FileTypes.getInstance();
		FileType ft = fp.getFileType(f);
		m_file_type = ft.getType();
		m_isText = ft.getIsText();
		
	}

}
