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
import java.math.BigDecimal;
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
import json_loader.utils.JSONformatter;

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
		JSONformatter	jsonRoot = new JSONformatter(null);		
		
		try {			
			
			boolean confidential = false;
			try{
				confidential = obj.getJSONObject("confidential").getBoolean("value");
				item.setConfidential(confidential);
			} catch (JSONException e){ //Confidential attribute is not there
				item.setConfidential(false);//default value
				l.info(e.getMessage());
			} finally {
				jsonRoot.addItem("confidential", ""+confidential);
			}
			
			String name = obj.getString("name");
			item.setName(name);
			jsonRoot.addValue("name", "\""+name+"\"");
			
			allProps = obj.getJSONObject("properties");
			jsonContext = allProps;
			JSONformatter jsonProps = new JSONformatter("properties");	
			
			String approach = jsonContext.getJSONObject("approach").getString("value");//JSON approach
			item.setType(approach);
			jsonProps.addStringItem("approach", approach);
			
			String summary = jsonContext.getJSONObject("summary").getString("value");
			item.setSummary(summary);
			jsonProps.addStringItem("summary", summary);
			
			//Chemistry
			jsonContext = allProps.getJSONObject("chemistry");
			JSONformatter jsonChemistry = new JSONformatter("chemistry");
			
			String chemicalFormula = jsonContext.getJSONObject("chemical formula").getString("value");
			item.setFormula(chemicalFormula);
			jsonChemistry.addStringItem("chemical formula", chemicalFormula);
			
			String productionInfo = jsonContext.getJSONObject("production info").optString("value");
			item.setProduction_info(productionInfo);
			jsonChemistry.addStringItem("production info", productionInfo);
			
			jsonProps.addSection(jsonChemistry);
			
			//Crystal
			jsonContext = allProps.getJSONObject("crystal");
			JSONformatter jsonCrystal = new JSONformatter("crystal");
			
			int compoundSpaceGroup = jsonContext.getJSONObject("compound space group").optInt("value", -1);			
			item.setCompound_space_group(compoundSpaceGroup);
			jsonCrystal.addItem("compound space group", ""+compoundSpaceGroup);
			
			BigDecimal unitCellVolume = jsonContext.getJSONObject("unit cell volume").optBigDecimal("value",null); 
			item.setUnit_cell_volume(unitCellVolume);
			jsonCrystal.addItem("unit cell volume", ""+unitCellVolume);
			
			JSONArray latticeParameters = jsonContext.getJSONObject("lattice parameters").optJSONArray("value"); 
			item.setLattice_parameters(latticeParameters);
			jsonCrystal.addItem("lattice parameters", ""+latticeParameters);			
			
			JSONArray latticeAngles = jsonContext.getJSONObject("lattice angles").optJSONArray("value");
			item.setLattice_angles(latticeAngles);
			jsonCrystal.addItem("lattice angles", ""+latticeAngles);
			
			JSONArray atomicPositions = jsonContext.getJSONObject("atomic positions").optJSONArray("value");
			item.setAtomic_positions(atomicPositions);
			jsonCrystal.addItem("atomic positions", ""+atomicPositions);
			
			String crystalInfo = jsonContext.getJSONObject("crystal info").optString("value");
			item.setCrystal_info(crystalInfo);
			jsonCrystal.addStringItem("crystal info", crystalInfo);
			
			jsonProps.addSection(jsonCrystal);
			
			//thermodynamics
			jsonContext = allProps.getJSONObject("thermodynamics");
			JSONformatter jsonThermodynamics = new JSONformatter("thermodynamics");
			
			BigDecimal unitCellEnergy = jsonContext.getJSONObject("unit cell energy").optBigDecimal("value",null);
			item.setUnitCellEnergy(unitCellEnergy);
			jsonThermodynamics.addItem("unit cell energy", ""+unitCellEnergy);
			
			BigDecimal unitCellFormationEnthalpy = 
					jsonContext.getJSONObject("unit cell formation enthalpy").optBigDecimal("value",null);
			item.setUnit_cell_formation_enthalpy(unitCellFormationEnthalpy);
			jsonThermodynamics.addItem("unit cell formation enthalpy", ""+unitCellFormationEnthalpy);
			
			String energyInfo = jsonContext.getJSONObject("energy info").optString("value", null);
			item.setEnergy_info(energyInfo);
			jsonThermodynamics.addStringItem("energy info", energyInfo);
			
			String interatomicPotentialsInfo = jsonContext.getJSONObject("interatomic potentials info").optString("value", null);
			item.setInteratomic_potentials_info(interatomicPotentialsInfo);
			jsonThermodynamics.addStringItem("interatomic potentials info", interatomicPotentialsInfo);
			
			BigDecimal magneticFreeEnergy = jsonContext.getJSONObject("magnetic free energy").optBigDecimal("value",null);			
			item.setMagnetic_free_energy(magneticFreeEnergy);
			jsonThermodynamics.addItem("magnetic free energy", ""+magneticFreeEnergy);
			
			String magneticFreeEnergyInfo = jsonContext.getJSONObject("magnetic free energy info").optString("value", null);			
			item.setMagnetic_free_energy_info(magneticFreeEnergyInfo);
			jsonThermodynamics.addStringItem("magnetic free energy info", magneticFreeEnergyInfo);
			
			jsonProps.addSection(jsonThermodynamics);
			
			//magnetics
			jsonContext = allProps.getJSONObject("magnetics");
			JSONformatter jsonMagnetics = new JSONformatter("magnetics");
			
			BigDecimal unitCellSpinPolarization = jsonContext.getJSONObject(
					"unit cell spin polarization").optBigDecimal("value",null);			
			item.setUnit_cell_spin_polarization(unitCellSpinPolarization);
			jsonMagnetics.addItem("unit cell spin polarization", ""+unitCellSpinPolarization);
			
			JSONArray atomicSpinSpecie = jsonContext.getJSONObject("atomic spin specie").optJSONArray("value");
			item.setAtomic_spin_specie(atomicSpinSpecie);
			jsonMagnetics.addItem("atomic spin specie", ""+atomicSpinSpecie);
			
			BigDecimal saturationMagnetization = jsonContext.getJSONObject("saturation magnetization").optBigDecimal("value",null);
			item.setSaturation_magnetization(saturationMagnetization);
			jsonMagnetics.addItem("saturation magnetization", ""+saturationMagnetization);
			
			BigDecimal magnetizationTemperature = jsonContext.getJSONObject("magnetization temperature").optBigDecimal("value",null);
			item.setMagnetization_temperature(magnetizationTemperature);
			jsonMagnetics.addItem("magnetization temperature", ""+magnetizationTemperature);
			
			String magnetizationInfo = jsonContext.getJSONObject("magnetization info").optString("value", null);
			item.setMagnetization_info(magnetizationInfo);
			jsonMagnetics.addStringItem("magnetization info", magnetizationInfo);
			
			JSONArray magnetocrystallineAnisotropyEnergy = jsonContext.getJSONObject("magnetocrystalline anisotropy energy").optJSONArray("value");			
			item.setMagnetocrystalline_anisotropy_energy(magnetocrystallineAnisotropyEnergy);
			jsonMagnetics.addItem("magnetocrystalline anisotropy energy", ""+magnetocrystallineAnisotropyEnergy);
			
			String anisotropyEnergyType = jsonContext.getJSONObject("anisotropy energy type").optString("value", null);
			item.setAnisotropy_energy_type(anisotropyEnergyType);
			jsonMagnetics.addStringItem("anisotropy energy type", anisotropyEnergyType);
			
			JSONArray magnetocrystallineAnisotropyConstants = 
					jsonContext.getJSONObject("magnetocrystalline anisotropy constants").optJSONArray("value");
			item.setMagnetocrystalline_anisotropy_constants(magnetocrystallineAnisotropyConstants);
			jsonMagnetics.addItem("magnetocrystalline anisotropy constants", ""+magnetocrystallineAnisotropyConstants);
			
			String kindOfAnisotropy = jsonContext.getJSONObject("kind of anisotropy").optString("value", null);
			item.setKind_of_anisotropy(kindOfAnisotropy);
			jsonMagnetics.addStringItem("kind of anisotropy", kindOfAnisotropy);
			
			String anisotropyInfo = jsonContext.getJSONObject("anisotropy info").optString("value", null);
			item.setAnisotropy_info(anisotropyInfo);
			jsonMagnetics.addStringItem("anisotropy info", anisotropyInfo);
			
			JSONArray exchangeIntegrals = jsonContext.getJSONObject("exchange integrals").optJSONArray("value");
			item.setExchange_integrals(exchangeIntegrals);
			jsonMagnetics.addItem("exchange integrals", ""+exchangeIntegrals);
			
			String exchangeInfo = jsonContext.getJSONObject("exchange info").optString("value", null);
			item.setExchange_info(exchangeInfo);
			jsonMagnetics.addStringItem("exchange info", exchangeInfo);
			
			String magneticOrder = jsonContext.getJSONObject("magnetic order").optString("value", null);
			item.setMagnetic_order(magneticOrder);
			jsonMagnetics.addStringItem("magnetic order", magneticOrder);
			
			BigDecimal curieTemperature = jsonContext.getJSONObject("curie temperature").optBigDecimal("value",null);
			item.setCurie_temperature(curieTemperature);
			jsonMagnetics.addItem("curie temperature", ""+curieTemperature);
			
			String curieTemperatureInfo = jsonContext.getJSONObject("curie temperature info").optString("value", null);
			item.setCurie_temperature_info(curieTemperatureInfo);
			jsonMagnetics.addStringItem("curie temperature info", curieTemperatureInfo);
			
			BigDecimal anisotropyField = jsonContext.getJSONObject("anisotropy field").optBigDecimal("value",null);
			item.setAnisotropy_field(anisotropyField);
			jsonMagnetics.addItem("anisotropy field", ""+anisotropyField);
			
			BigDecimal remanence = jsonContext.getJSONObject("remanence").optBigDecimal("value",null);
			item.setRemanence(remanence);
			jsonMagnetics.addItem("remanence", ""+remanence);
			
			BigDecimal coercivity = jsonContext.getJSONObject("coercivity").optBigDecimal("value",null);
			item.setCoercivity(coercivity);
			jsonMagnetics.addItem("coercivity", ""+coercivity);
			
			BigDecimal energyProduct = jsonContext.getJSONObject("energy product").optBigDecimal("value",null);
			item.setEnergy_product(energyProduct);
			jsonMagnetics.addItem("energy product", ""+energyProduct);
			
			String hysteresisInfo = jsonContext.getJSONObject("hysteresis info").optString("value", null);
			item.setHysteresis_info(hysteresisInfo);
			jsonMagnetics.addStringItem("hysteresis info", hysteresisInfo);
			
			BigDecimal domainWallWidth = jsonContext.getJSONObject("domain wall width").optBigDecimal("value",null);
			item.setDomain_wall_width(domainWallWidth);
			jsonMagnetics.addItem("domain wall width", ""+domainWallWidth);
			
			String domainWallInfo = jsonContext.getJSONObject("domain wall info").optString("value", null);
			item.setDomain_wall_info(domainWallInfo);
			jsonMagnetics.addStringItem("domain wall info", domainWallInfo);
			
			BigDecimal exchangeStiffness = jsonContext.getJSONObject("exchange stiffness").optBigDecimal("value",null);
			item.setExchange_stiffness(exchangeStiffness);
			jsonMagnetics.addItem("exchange stiffness", ""+exchangeStiffness);
			
			String exchangeStiffnessInfo = jsonContext.getJSONObject("exchange stiffness info").optString("value", null);
			item.setExchange_stiffness_info(exchangeStiffnessInfo);
			jsonMagnetics.addStringItem("exchange stiffness info", exchangeStiffnessInfo);
			
			jsonProps.addSection(jsonMagnetics);
			
			//additional information
			jsonContext = allProps.getJSONObject("additional information");
			JSONformatter jsonAdditionalInfo = new JSONformatter("additional information");
			
			JSONArray authors = jsonContext.getJSONObject("authors").optJSONArray("value");
			item.setAuthors(authors);
			jsonAdditionalInfo.addItem("authors", ""+authors);
			
			String reference = jsonContext.getJSONObject("reference").optString("value", null);
			item.setReference(reference);
			jsonAdditionalInfo.addStringItem("reference", reference);
			
			String comments = jsonContext.getJSONObject("comments").optString("value", null);
			item.setComments(comments);
			jsonAdditionalInfo.addStringItem("comments", comments);
			
			JSONArray attachedFiles = jsonContext.getJSONObject("attached files").optJSONArray("value");
			item.setAttached_files(attachedFiles);
			jsonAdditionalInfo.addItem("attached files", ""+attachedFiles);
			
			JSONArray attachedFilesInfo = jsonContext.getJSONObject("attached files info").optJSONArray("value");
			item.setAttached_files_info(attachedFilesInfo);
			jsonAdditionalInfo.addItem("attached files info", ""+attachedFilesInfo);
			
			jsonProps.addSection(jsonAdditionalInfo);
			
			jsonRoot.addSection(jsonProps);
			
			jsonRoot.closeDocument();			
			item.setJsonObject(jsonRoot.getJSONformatted());
			
		} catch (Exception e) {
			l.error(e.getMessage());
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			l.error(sw.toString());
		}
	}
}