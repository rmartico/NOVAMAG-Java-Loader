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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.JSONparser;
import json_loader.utils.Cleaner;
import json_loader.utils.Config;
import json_loader.utils.ConnectionPool;
import json_loader.utils.FileManager;

public class TestAttachedFiles {
	private static Logger l = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		l =	LoggerFactory.getLogger(TestAttachedFiles.class);
	}
	
	@Before
	public void setUp() throws Exception {
		
		//Clean temp folder
		Config.loadConfig();
		File file = new File(Config.TEMP_FOLDER);
		FileManager.purgeDirectory(file);
        
        //Copy the files into the temp folder
        FileManager.copy("data_for_tests/dao.attached_files/Fe12Ge6_#164_1.cif", Config.TEMP_FOLDER);
        FileManager.copy("data_for_tests/dao.attached_files/CONTCAR_Fe12Ge6_#164_1", Config.TEMP_FOLDER);
        FileManager.copy("data_for_tests/dao.attached_files/2-16031G20533419.jpg", Config.TEMP_FOLDER);
        FileManager.copy("data_for_tests/dao.attached_files/Fe12Ge6_#164_attach.json", Config.TEMP_FOLDER);
		
	}
	
	
	
	
	
	@Test
	public void testInsert() throws IOException, NamingException {		
			
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
		
		String m_clob=null;
		int wc=-1;
		int lc=-1;
		int ones=-1;
		
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
			int i=0;
			while (rs.next()){
				//System.out.println(rs.getString("mafId")+rs.getString("file_name")+rs.getString("file_type"));
				assertEquals(rs.getLong("mafId"),1);				
				byte[] fileBytes = rs.getBytes("blob_content");				
				
				
				String sft = rs.getString("file_Type");
				boolean isText = FileTypes.getInstance().isText(sft);
				
				if (isText){				
					m_clob = new String(fileBytes);
					//System.out.println(	"len="+m_clob.length());
					
					wc = m_clob.trim().split("\\s+").length;
					//System.out.println(	"wc="+ wc);
					
					lc = m_clob.split("\\n+").length;
					//System.out.println(	"lc="+ lc);
					
					ones =  m_clob.split("1").length;
					//System.out.println(	"ones="+ ones);
					
					//System.out.println(	m_clob);
				} else {
					
					/*
					InputStream in = new ByteArrayInputStream(fileBytes);
					BufferedImage bImageFromConvert = ImageIO.read(in);
					
					ImageIO.write(bImageFromConvert, "jpg", new File(
							"f:/new-darksouls.jpg"));
					*/
				}
				
				switch (i){
				case 0:
					assertEquals(rs.getString("file_name"),"Fe12Ge6_#164_1.cif");
					assertEquals(rs.getString("file_type"),"CIF");
					assertEquals(rs.getString("info"),"This is a cif file");
					assertEquals(m_clob.length(),1051);
					assertEquals(wc, 125);
					assertEquals(lc, 49);
					assertEquals(ones, 20);
					break;
				case 1:
					assertEquals(rs.getString("file_name"),"CONTCAR_Fe12Ge6_#164_1");
					assertEquals(rs.getString("file_type"),"CONTCAR");
					//assertEquals(rs.getString("info"),"This is a CONTCAR file");
					assertEquals(rs.getString("info"),null);
					assertEquals(m_clob.length(),2283);
					assertEquals(wc, 128);
					assertEquals(lc, 45);
					assertEquals(ones, 77);
					break;
				case 2:
					assertEquals(rs.getString("file_name"),"2-16031G20533419.jpg");
					assertEquals(rs.getString("file_type"),"JPG");
					assertEquals(rs.getString("info"),"This is a beautiful picture");
					assertEquals(fileBytes.length,29743);
					
					//System.out.println(fileBytes.length);					
					
					Checksum checksum = new CRC32();
					// update the current checksum with the specified array of bytes
					checksum.update(fileBytes, 0, fileBytes.length);
					// get the current checksum value
					long checksumValue = checksum.getValue();
					//System.out.println("CRC32 checksum for input string is: " + checksumValue);
					assertEquals(checksumValue,282947058);
					
					break;
				case 3:
					assertEquals(rs.getString("file_name"),"Fe12Ge6_#164_attach.json");
					assertEquals(rs.getString("file_type"),"JSON");
					assertEquals(rs.getString("info"),"An stupid json file");

					assertEquals(m_clob.length(),225);
					assertEquals(wc, 25);
					assertEquals(lc, 14);
					assertEquals(ones, 3);

					break;
				case 4:
					fail();
					break;
			}
			
			i++;
				
			}
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}
}
