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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.dao.DBitem;
import json_loader.utils.Cleaner;
import json_loader.utils.ConnectionPool;

/**
 * 
 * JSONparser.java
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class JSONparser {
	
	private static Logger l = LoggerFactory.getLogger(JSONparser.class);
	
	/** The java object containing the item (i.e., material) that is being parsed at this moment*/
	private DBitem item = null;
	
	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		/* 
		File f = new File("data/FeNi_L10_v2.json");
		if (f.exists()){
            InputStream is = new FileInputStream("data/FeNi_L10_v2.json");
            
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            //System.out.println(jsonTxt);
            
            JSONObject obj = new JSONObject(jsonTxt); 
            
            JSONparser jp = new JSONparser("data/");
            jp.parseJSON(obj);
            System.out.println(jp.getItem());
            System.out.println("FIN");
		}
		*/
		
		Cleaner.CleanDB();
		Cleaner.insertAtom("Al");
		Cleaner.insertAtom("Co");
		Cleaner.insertAtom("Fe");
		Cleaner.insertAtom("Ge");
		Cleaner.insertAtom("Ni");
		Cleaner.insertAtom("Mn");
		Cleaner.insertAtom("Sb");
		Cleaner.insertAtom("Sn");
		Cleaner.insertAtom("Ta");
		
		
		//String file_name="data/examples_database_1.json";
		String file_name="data/examples_database_2.json";
		File f = new File(file_name);

		JSONObject obj=null;
		JSONArray  a_obj=null;
		
		if (f.exists()){
            InputStream is = new FileInputStream(file_name);
            
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            //System.out.println(jsonTxt);
            String firstChar = String.valueOf(jsonTxt.charAt(0));
            if (firstChar.equalsIgnoreCase("{")) {
            	obj = new JSONObject(jsonTxt);
            	
                JSONparser jp = new JSONparser();
                jp.parseJSON(obj);
                
                DBitem item = jp.getItem();
                item.insert(null, true); 
            } else {
            	a_obj=new JSONArray(jsonTxt);
            	
            	ConnectionPool p=ConnectionPool.getInstance();
                Connection con = p.getConnection();
                            	
            	for (int i = 0, size = a_obj.length(); i < size; i++)
                {
                  obj = a_obj.getJSONObject(i);
            	
                  JSONparser jp = new JSONparser();
                  jp.parseJSON(obj);
                  //System.out.println(jp.getItem());
                  DBitem item = jp.getItem();
                  
                  
                  item.insert(con, false);
                  
                  
                  System.out.println("FIN-------------------------------------"+i);
                }
            	con.commit();
            }
            
		}
		
		System.out.println("OK---------");
	}
	
	/**
	 * Constructor for a parser of a json object
	 */
	public JSONparser() {		
		item = new DBitem();
	}	
	
	/**
	 * Getter for the java object containing the item (i.e., material)
	 *  that is being parsed at this moment
	 * 
	 * @return such java object containing the item, typically when the json object
	 * has been already parsed
	 */
	public DBitem getItem(){
		return item;
	}
	
	/**
	 * 
	 * It parses a json object containing the info corresponding to
	 * one item (i.e., material).
	 * It stores that info into a DBitem object
	 * Therefore, it makes a transaltion of the material info from json to java object
	 * 
	 * @param obj the json object to be parsed
	 */
	public void parseJSON( JSONObject obj ){
		
		JSONObject jsonContext, allProps = null;
		
		try {
			
			try{
				item.setConfidential(obj.getJSONObject("confidential").getBoolean("value"));				
			} catch (JSONException e){ //Confidential attribute is not there
				l.info(e.getMessage());
				item.setConfidential(false);
			}
			
			item.setName(obj.getString("name"));
			
			allProps = obj.getJSONObject("properties");
			jsonContext = allProps;
			
			item.setType(jsonContext.getJSONObject("approach").getString("value"));//JSON approach
			item.setSummary(jsonContext.getJSONObject("summary").getString("value"));
			
			//Chemistry
			jsonContext = allProps.getJSONObject("chemistry");
			item.setFormula(jsonContext.getJSONObject("chemical formula").getString("value"));
			item.setProduction_info(jsonContext.getJSONObject("production info").optString("value"));
			
			//Crystal
			jsonContext = allProps.getJSONObject("crystal");
			item.setCompound_space_group(jsonContext.getJSONObject("compound space group").optInt("value", -1));
			item.setUnit_cell_volume(jsonContext.getJSONObject("unit cell volume").optBigDecimal("value",null));
			
			item.setLattice_parameters(jsonContext.getJSONObject("lattice parameters").optJSONArray("value"));
			item.setLattice_angles(jsonContext.getJSONObject("lattice angles").optJSONArray("value"));
			item.setAtomic_positions(jsonContext.getJSONObject("atomic positions").optJSONArray("value"));
			item.setCrystal_info(jsonContext.getJSONObject("crystal info").optString("value"));
			
			//thermodynamics
			jsonContext = allProps.getJSONObject("thermodynamics");
			item.setUnitCellEnergy(jsonContext.getJSONObject("unit cell energy").optBigDecimal("value",null));;
			item.setUnit_cell_formation_enthalpy(jsonContext.getJSONObject("unit cell formation enthalpy").optBigDecimal("value",null));
			item.setEnergy_info(jsonContext.getJSONObject("energy info").optString("value", null));
			item.setInteratomic_potentials_info(jsonContext.getJSONObject("interatomic potentials info").optString("value", null));
			item.setMagnetic_free_energy(jsonContext.getJSONObject("magnetic free energy").optBigDecimal("value",null));
			item.setMagnetic_free_energy_info(jsonContext.getJSONObject("magnetic free energy info").optString("value", null));
			
			//magnetics
			jsonContext = allProps.getJSONObject("magnetics");
			item.setUnit_cell_spin_polarization(jsonContext.getJSONObject(
					"unit cell spin polarization").optBigDecimal("value",null));
			item.setAtomic_spin_specie(jsonContext.getJSONObject("atomic spin specie").optJSONArray("value"));
			item.setSaturation_magnetization(jsonContext.getJSONObject("saturation magnetization").optBigDecimal("value",null));
			item.setMagnetization_temperature(jsonContext.getJSONObject("magnetization temperature").optBigDecimal("value",null));
			item.setMagnetization_info(jsonContext.getJSONObject("magnetization info").optString("value", null));
			item.setMagnetocrystalline_anisotropy_energy(jsonContext.getJSONObject("magnetocrystalline anisotropy energy").optJSONArray("value"));
			item.setAnisotropy_energy_type(jsonContext.getJSONObject("anisotropy energy type").optString("value", null));			
			item.setMagnetocrystalline_anisotropy_constants(jsonContext.getJSONObject("magnetocrystalline anisotropy constants").optJSONArray("value"));
			item.setKind_of_anisotropy(jsonContext.getJSONObject("kind of anisotropy").optString("value", null));
			item.setAnisotropy_info(jsonContext.getJSONObject("anisotropy info").optString("value", null));
			item.setExchange_integrals(jsonContext.getJSONObject("exchange integrals").optJSONArray("value"));
			item.setExchange_info(jsonContext.getJSONObject("exchange info").optString("value", null));
			item.setMagnetic_order(jsonContext.getJSONObject("magnetic order").optString("value", null));
			item.setCurie_temperature(jsonContext.getJSONObject("curie temperature").optBigDecimal("value",null));
			item.setCurie_temperature_info(jsonContext.getJSONObject("curie temperature info").optString("value", null));
			item.setAnisotropy_field(jsonContext.getJSONObject("anisotropy field").optBigDecimal("value",null));
			item.setRemanence(jsonContext.getJSONObject("remanence").optBigDecimal("value",null));
			item.setCoercivity(jsonContext.getJSONObject("coercivity").optBigDecimal("value",null));
			item.setEnergy_product(jsonContext.getJSONObject("energy product").optBigDecimal("value",null));
			item.setHysteresis_info(jsonContext.getJSONObject("hysteresis info").optString("value", null));
			item.setDomain_wall_width(jsonContext.getJSONObject("domain wall width").optBigDecimal("value",null));
			item.setDomain_wall_info(jsonContext.getJSONObject("domain wall info").optString("value", null));
			item.setExchange_stiffness(jsonContext.getJSONObject("exchange stiffness").optBigDecimal("value",null));
			item.setExchange_stiffness_info(jsonContext.getJSONObject("exchange stiffness info").optString("value", null));
			
			//additional information
			jsonContext = allProps.getJSONObject("additional information");
			item.setAuthors(jsonContext.getJSONObject("authors").optJSONArray("value"));
			item.setReference(jsonContext.getJSONObject("reference").optString("value", null));
			item.setComments(jsonContext.getJSONObject("comments").optString("value", null));
			item.setAttached_files(jsonContext.getJSONObject("attached files").optJSONArray("value"));
			item.setAttached_files_info(jsonContext.getJSONObject("attached files info").optJSONArray("value"));
			
		} catch (Exception e) {
			l.error(e.getMessage());
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			l.error(sw.toString());
		}
	}
}