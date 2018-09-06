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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.JSONparser;
import json_loader.error_handling.LoaderException;
import json_loader.utils.Cleaner;
import json_loader.utils.Comparators;
import json_loader.utils.ConnectionPool;

/**
 * 
 * DBitem.java
 * Class to represent a java object containing the information
 * from an item (i.e., material)
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class DBitem {	
	 
	private static Logger l = LoggerFactory.getLogger(DBitem.class);	
	
	private boolean m_confidential;
	private String  m_name;
	
	private String 	m_type;	
	private String  m_summary;
	
	//Chemistry
	private String  			m_production_info;
	private String  			m_formula;
	
	//Crystal
	private Integer				m_compound_space_group;
	private BigDecimal			m_unit_cell_volume;
	private JSONArray			m_lattice_parameters;//json
	private JSONArray			m_lattice_angles;//json
	private JSONArray			m_atomic_positions;//json
	private String				m_crystal_info;
	
	//Thermodynamics
	private BigDecimal			m_unit_cell_energy;
	private BigDecimal			m_unit_cell_formation_enthalpy;
	private String				m_energy_info;
	private String				m_interatomic_potentials_info;
	private	BigDecimal			m_magnetic_free_energy;
	private String				m_magnetic_free_energy_info;
	
	//Magnetics
	private BigDecimal			m_unit_cell_spin_polarization;
	private JSONArray			m_atomic_spin_specie;//json	
	private BigDecimal			m_saturation_magnetization;	
	private BigDecimal			m_magnetization_temperature;	
	private String				m_magnetization_info;
	private JSONArray			m_magnetocrystalline_anisotropy_energy;//json	
	private String				m_anisotropy_energy_type;
	private JSONArray			m_magnetocrystalline_anisotropy_constants;//json
	private String				m_kind_of_anisotropy;
	private String				m_anisotropy_info;
	private JSONArray			m_exchange_integrals;//json
	private String				m_exchange_info;
	private String				m_magnetic_order;
	private BigDecimal			m_curie_temperature;
	private String				m_curie_temperature_info;
	private BigDecimal			m_anisotropy_field;
	private BigDecimal			m_remanence;
	private BigDecimal			m_coercivity;
	private BigDecimal			m_energy_product;
	private String				m_hysteresis_info;
	private BigDecimal			m_domain_wall_width;
	private String				m_domain_wall_info;
	private BigDecimal			m_exchange_stiffness;
	private String				m_exchange_stiffness_info;
	
	//Additional information
	private JSONArray			m_authors;
	private String				m_reference;
	private String				m_comments;
	private JSONArray			m_attached_files;
	private JSONArray			m_attached_files_info;
	
	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 * @throws IOException
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws IOException, NamingException, SQLException {
		 String fileName="data_for_tests/dao.dbitem/Fe12Ge6_#164_1.json";		
		
		 Cleaner.CleanDB();
		 Cleaner.insertAtom("Fe");
		 Cleaner.insertAtom("Ge");
		
		 InputStream is = new FileInputStream(fileName);         
         String jsonTxt = IOUtils.toString(is, "UTF-8");
         //System.out.println(jsonTxt);
         
         JSONObject obj = new JSONObject(jsonTxt);          
         JSONparser jp = new JSONparser();
         jp.parseJSON(obj);
		
         DBitem item = jp.getItem();
         item.insert(null, true);         
         
         String query = "select formula||mafid||type||name||summary||production_info||stechiometry"
         		+ " from items natural join molecules;";
         Comparators.assertEqualsResultSet(query, 4205810780L);
         
         System.out.println("OK!!");
	}
	
	/**
	 * Prepares a a jsonarray to be set as one of the parameters
	 * of a PreparedStatement.
	 * The jsonArray is trated as an String
	 * It takes into account the jsonArray could be null
	 * 
	 * @param pstm is the PreparedStatement
	 * @param position is the index of the "?" symbol corresponding to the jsonArray in
	 *    the preparedStatement
	 * @param val is the jsonArray
	 * @throws SQLException
	 */
	private void setJSONArray(
			PreparedStatement pstm,
			int position, JSONArray val ) throws SQLException{
		if (val==null){
			pstm.setNull(position, Types.VARCHAR);
		} else{
			pstm.setString(position, val.toString());
		}
			
	}

	/**
	 * Given a database connection it uses it to insert a row
	 * into the items table. This row contains the info in the DBitem object
	 * It also inserts information in the associated tables
	 * (i.e., molecules, authors & authoring, attachedFiles)
	 * 
	 * Typical usage: insert( null, true)
	 * 
	 * @param con the database connection, if null then a new one is created and also released
	 * at the end of the method
	 * @param doCommit if true commit is made at the end of the method
	 * @throws  SQLException when database violation
	 * 			IOException if some attached file is not found 
	 * 
	 */
	public void insert(Connection con, boolean doCommit) throws SQLException, IOException{
		
		ConnectionPool p = null;
		boolean closeConnection=false;
		
		Molecule m_molecule=null;
		
		PreparedStatement ins_item=null;
		ResultSet rs_lastMafId=null;		
		
		try {
			p = ConnectionPool.getInstance();
			if (con==null){
				con = p.getConnection();
				closeConnection = true;
			}
			
			m_molecule = new Molecule();
			m_molecule.setFormula(m_formula);
			m_molecule.insert(con, false);
			m_formula=m_molecule.getFormula();//Get reordered formula
			
			ins_item = con.prepareStatement(
					"INSERT INTO items ("+	
							"confidential, type, name, summary, formula,"+ 
							"production_info,"+
							"compound_space_group, unit_cell_volume, lattice_parameters, lattice_angles,"+ 
								"atomic_positions,"+ 
								"crystal_info,"+
							"unit_cell_energy, unit_cell_formation_enthalpy, energy_info,"+
							"unit_cell_spin_polarization, atomic_spin_specie, saturation_magnetization, magnetization_temperature, magnetization_info,"+
								"magnetocrystalline_anisotropy_energy, anisotropy_energy_type, magnetocrystalline_anisotropy_constants, kind_of_anisotropy, anisotropy_field, anisotropy_info,"+
								"exchange_integrals,"+
								"exchange_info, magnetic_order, 	curie_temperature, curie_temperature_info, remanence, coercivity, energy_product,"+
								"hysteresis_info, domain_wall_width, domain_wall_info, exchange_stiffness, exchange_stiffness_info,"+
							"reference, comments"+
							") values ("
							+ "?,?,?,?,?,"
							+ "?,"
							+ "?,?,cast( ? as json),cast (? as json),"
							+ "cast(? as json),"
							+ "?,"
							+ "?,?,?,"
							+ "?,cast(? as json),?,?,?,"
							+ "cast(? as json),?,cast(? as json),?,?,?,"
							+ "cast(? as json),"
							+ "?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,"
							+ "?,?"
							+ ");"
							, Statement.RETURN_GENERATED_KEYS);
			
			int idx=1;
			ins_item.setBoolean(idx++, getConfidential());
			ins_item.setString(idx++, getType());
			ins_item.setString(idx++, getName());
			ins_item.setString(idx++, getSummary());
			ins_item.setString(idx++, getFormula());
			
			ins_item.setString(idx++, getProduction_info());
			
			ins_item.setInt(idx++, getCompound_space_group());
			ins_item.setBigDecimal(idx++, getUnit_cell_volume());
			setJSONArray(ins_item, idx++, getLattice_parameters());
			setJSONArray(ins_item, idx++, getLattice_angles());
			
			setJSONArray(ins_item, idx++, getAtomic_positions());
			ins_item.setString(idx++, getCrystal_info());
			
			ins_item.setBigDecimal(idx++, getUnitCellEnergy());
			ins_item.setBigDecimal(idx++, getUnit_cell_formation_enthalpy());
			ins_item.setString(idx++, getEnergy_info());

			ins_item.setBigDecimal(idx++, getUnit_cell_spin_polarization());
			setJSONArray(ins_item, idx++, getAtomic_spin_specie());
			ins_item.setBigDecimal(idx++, getSaturation_magnetization());
			ins_item.setBigDecimal(idx++, getMagnetization_temperature());
			ins_item.setString(idx++, getMagnetization_info());
			
			setJSONArray(ins_item, idx++, getMagnetocrystalline_anisotropy_energy());
			ins_item.setString(idx++, getAnisotropy_energy_type());
			setJSONArray(ins_item, idx++, getMagnetocrystalline_anisotropy_constants());
			ins_item.setString(idx++, getKind_of_anisotropy());
			ins_item.setBigDecimal(idx++, getAnisotropy_field());
			ins_item.setString(idx++, getAnisotropy_info());
			
			setJSONArray(ins_item, idx++, getExchange_integrals());
			
			ins_item.setString(idx++, getExchange_info());
			ins_item.setString(idx++, getMagnetic_order());
			ins_item.setBigDecimal(idx++, getCurie_temperature());
			ins_item.setString(idx++, getCurie_temperature_info());
			ins_item.setBigDecimal(idx++, getRemanence());
			ins_item.setBigDecimal(idx++, getCoercivity());
			ins_item.setBigDecimal(idx++, getEnergy_product());
			
			ins_item.setString(idx++, getHysteresis_info());
			ins_item.setBigDecimal(idx++, getDomain_wall_width());
			ins_item.setString(idx++, getDomain_wall_info());
			ins_item.setBigDecimal(idx++, getExchange_stiffness());
			ins_item.setString(idx++, getExchange_stiffness_info());
			
			ins_item.setString(idx++, getReference());
			ins_item.setString(idx++, getComments());

			
			ins_item.executeUpdate();
			rs_lastMafId = ins_item.getGeneratedKeys();
			
			long lastMafId=-1;
			if(rs_lastMafId.next())
				lastMafId = rs_lastMafId.getLong(1);

			Authors authors = new Authors(lastMafId);
			authors.setAuthors(m_authors);
			authors.insert(con, false);
			
			Attached_files files = new Attached_files(lastMafId);
			files.setAttached_files(m_attached_files);
			files.setAttached_files_info(m_attached_files_info);
			files.insert(con, false);
			
			if (doCommit)
				con.commit();			
			
		} catch ( SQLException|IOException e) {
			l.error(e.getMessage());
			throw e;
			//e.printStackTrace();
		} finally {
			p.close(rs_lastMafId);
			p.close(ins_item);
			if (closeConnection) p.close(con);
		}
		
		
	}
	
	/**
	 * String representation of DBitem
	 * (for debugging purposes)
	 */
	public String toString(){
		String s="";
		
		s+="m_confidential="+m_confidential+"\n";
		s+="m_type="+m_type+"\n";
		s+="m_name="+m_name+"\n";
		s+="m_summary="+m_summary+"\n";
		
		s+="\nChemistry:\n";
		s+="m_production_info="+m_production_info+"\n";
		s+="m_formula="+m_formula+"\n";
		
		s+="\nCrystal:\n";
		s+="m_compound_space_group="+m_compound_space_group+"\n";
		s+="m_unit_cell_volume="+m_unit_cell_volume+"\n";
		s+="m_lattice_parameters="+m_lattice_parameters+"\n";
		s+="m_lattice_angles="+m_lattice_angles+"\n";
		s+="m_atomic_positions="+m_atomic_positions+"\n";
		
		s+="\nThermodynamics:\n";
		s+="unit cell energy="+m_unit_cell_energy+"\n";
		s+="unit cell formation enthalpy="+m_unit_cell_formation_enthalpy+"\n";
		s+="energy info="+m_energy_info+"\n";
		s+="interatomic potentials info="+m_interatomic_potentials_info+"\n";
		s+="magnetic free energy="+m_magnetic_free_energy+"\n";
		s+="magnetic free energy info="+m_magnetic_free_energy_info+"\n";
		
		s+="\nMagnetics:\n";
		s+="unit cell spin polarization="+m_unit_cell_spin_polarization+"\n";
	    s+="atomic spin specie="+m_atomic_spin_specie+"\n";
	    s+="saturation magnetization="+m_saturation_magnetization+"\n";
	    s+="magnetization temperature="+m_magnetization_temperature+"\n";
	    s+="magnetization info="+m_magnetization_info+"\n";
	    s+="magnetocrystalline anisotropy energy="+m_magnetocrystalline_anisotropy_energy+"\n";
	    s+="anisotropy energy type="+m_anisotropy_energy_type+"\n";
	    s+="magnetocrystalline anisotropy constants="+m_magnetocrystalline_anisotropy_constants+"\n";
	    s+="kind of anisotropy="+m_kind_of_anisotropy+"\n";
	    s+="anisotropy info="+m_anisotropy_info+"\n";
	    s+="exchange integrals="+m_exchange_integrals+"\n";
	    s+="exchange info="+m_exchange_info+"\n";
	    s+="magnetic order="+m_magnetic_order+"\n";
	    s+="curie temperature="+m_curie_temperature+"\n";
	    s+="curie temperature info="+m_curie_temperature_info+"\n";
	    s+="anisotropy field="+m_anisotropy_field+"\n";
	    s+="remanence="+m_remanence+"\n";
	    s+="coercivity="+m_coercivity+"\n";
	    s+="energy product="+m_energy_product+"\n";
	    s+="hysteresis info="+m_hysteresis_info+"\n";
	    s+="domain wall width="+m_domain_wall_width+"\n";
	    s+="domain wall info="+m_domain_wall_info+"\n";
	    s+="exchange stiffness="+m_exchange_stiffness+"\n";
	    s+="exchange stiffness info="+m_exchange_stiffness_info+"\n";
	    
	    s+="\nAdditional information:\n";
	    s+="authors="+m_authors+"\n";
	    s+="reference="+m_reference+"\n";
	    s+="comments="+m_comments+"\n";
	    s+="attached files="+m_attached_files+"\n";
	    s+="attached files info="+m_attached_files_info+"\n";				
				
		return s;
	}
	
	/**
	 * getter for the boolean variable indicating if the item is 
	 * confidential or public
	 * 
	 * @return true=>confidential, or false=>public	 * 
	 */
	public boolean getConfidential(){
		return m_confidential;
	}

	/**
	 * setter for the boolean variable indicating if the item is 
	 * confidential or public
	 * 
	 * @param isConfidential true=>confidential, false=>public
	 */
	public void setConfidential(boolean isConfidential){
		m_confidential=isConfidential;
	}	
	
	/**
	 * Getter for the variable indicating if the item is theoretical or experimental
	 * @return E when experimental, T when theoretical
	 */	
	public String getType(){
		return m_type;
	}
	
	/**
	 * 
	 * Setter for the variable indicating if the item is theoretical or experimental
	 * 
	 * @param type = E when experimental, = T when theoretical
	 * @throws LoaderException when other value different than E or T is tried to be set
	 */
	public void setType(String type) throws LoaderException{
		switch (type){
			case "theory":
				type="T";
				break;
			case "experiment":
				type="E";
				break;
			default:
				throw new LoaderException(LoaderException.INCORRECT_TYPE);				
		}
		
		m_type=type;
	}
	
	/**
	 * Getter for the name of the item (i.e., material)
	 * @return the name of the material
	 */
	public String getName(){
		return m_name;
	}
	
	/**
	 * Setter for the name of the item (i.e., material)
	 * @param name is the name of the material
	 */
	public void setName(String name){
		m_name=name;
	}
	
	/**
	 * Getter for the summary which is a text entry,
	 *  it provides a short list of main calculated or measured
	 *  properties and production method. This is like a keyword entry. 
	 * 
	 * @return the summary
	 */
	public String getSummary(){
		return m_summary;
	}

	/**
	 * Setter for the summary which is a text entry,
	 *  it provides a short list of main calculated or measured
	 *  properties and production method. This is like a keyword entry. 
	 * 
	 * @param summary
	 */
	public void setSummary(String  summary){
		m_summary=summary;
	}
	
	/**
	 * Getter for the chemical formula
	 * 
	 * composition description of the compound in the calculated or measured unit cell.
	 * Important to note:
	 *  -For theoretical approach: only integer numbers after the name of the elements. Example: N1Fe8
     *  -For experimental approach: it could be integer or decimal numbers after the name of the elements. Examples: Fe3Sn1 or Fe0.75Sn0.25, Fe15Mn15Sn9Sb1 or Fe1.5Mn1.5Sn0.9Sb0.1, Fe200Mn100Sn75Sb25 or Fe2Mn1Sn0.75Sb0.25 
	 * 
	 * @return the chemical formula
	 */
	public String getFormula(){
		return m_formula;
	}

	/**
	 *
	 * Setter for the chemical formula
	 * 
	 * composition description of the compound in the calculated or measured unit cell.
	 * Important to note:
	 *  -For theoretical approach: only integer numbers after the name of the elements. Example: N1Fe8
     *  -For experimental approach: it could be integer or decimal numbers after the name of the elements. Examples: Fe3Sn1 or Fe0.75Sn0.25, Fe15Mn15Sn9Sb1 or Fe1.5Mn1.5Sn0.9Sb0.1, Fe200Mn100Sn75Sb25 or Fe2Mn1Sn0.75Sb0.25 
	 * 
	 * @param formula is the chemical formula
	 */
	public void setFormula(String formula){
		m_formula = formula;
	};
	
	/**
	* Getter for the production information
	* that provides information about how the material was made 
	* (synthesis method, temperature, time, …) 
	* or theoretically obtained/predicted (software, method, …).
	* 
	* @return the production information
	*/
	public String getProduction_info(){
		return m_production_info;
	}

	/**
	 * Setter for the production information
	 * that provides information about how the material was made 
	 * (synthesis method, temperature, time, …) 
	 * or theoretically obtained/predicted (software, method, …).
	 * 
	 * @param production_info
	 */
	public void setProduction_info(String production_info){
		m_production_info = production_info;
	}	
	
	/**
	 * Getter for the compound space group which ranges in [1-230]
	 * @return the compound space group
	 */
	public Integer getCompound_space_group(){
		return m_compound_space_group;
	}

	/**
	 * Setter for the compound space group which ranges in [1-230] 
	 * @param compound_space_group
	 */
	public void setCompound_space_group( int compound_space_group){
		if (compound_space_group<0)
			m_compound_space_group=null;
		else
			m_compound_space_group = compound_space_group;
	}
	
	/**
	 *  Getter for the unit cell volume in units of Å3,
	 *  [min,max]=[0.0000,100000.0000]
	 * @return the unit cell volume
	 */
	public BigDecimal getUnit_cell_volume(){
		return m_unit_cell_volume;
	}

	/**
	 *  Setter for the unit cell volume in units of Å3,
	 *  [min,max]=[0.0000,100000.0000]
	 * 
	 * @param unit_cell_volume
	 */
	public void setUnit_cell_volume( BigDecimal unit_cell_volume){
		m_unit_cell_volume = unit_cell_volume;
	}
	
	/**
	 * Getter for the lattice parameters: (a,b,c) in units of Å,
	 * [min,max]=[0.000000,100000.000000]
	 * Example: [ 2.518, 2.518, 3.582]
	 * 
	 * @return a json array containing the lattice parameters
	 */
	public JSONArray getLattice_parameters(){
		return m_lattice_parameters;
	}

	/**
	 * Setter for the lattice parameters: (a,b,c) in units of Å,
	 * [min,max]=[0.000000,100000.000000]
	 * Example: [ 2.518, 2.518, 3.582]
	 * 
	 * @param lattice_parameters is a json array containing the lattice parameters
	 * @throws LoaderException if the number of lattice parameters is other than 3
	 * 	or any of them is not numeric or is out of the [0.000000,100000.000000] range
	 */
	public void setLattice_parameters( JSONArray lattice_parameters ) throws LoaderException{
		
		//Check it is OK
		if (!(lattice_parameters.length()==3 || lattice_parameters.length()==0))
			throw new LoaderException(LoaderException.LATTICE_PARAMETERS_DISTINCT_THAN_3);
		
		BigDecimal jo=null;
		for ( int i=0; i<lattice_parameters.length(); i++){
			try {
				jo = lattice_parameters.getBigDecimal(i);
			} catch (ClassCastException e){
				throw new LoaderException(LoaderException.A_LATTICE_PARAMETER_IS_NOT_NUMERIC);
			}			
				
			if (jo.compareTo(BigDecimal.ZERO) < 0 ||
				jo.compareTo(new BigDecimal("100000")) > 0)
				throw new LoaderException(LoaderException.LATTICE_PARAMETER_OUT_OF_RANGE);			
		}		
	
		m_lattice_parameters = lattice_parameters;
	}
	
	/**
	 * Getter for the lattice angles: (alpha, beta, gamma) in units of º,
	 * [min,max]=[0.000,360.000]
	 * Example: [ 90.000, 90.000, 90.000]
	 * 
	 * @return a json array containing the lattice angles
	 */
	public JSONArray getLattice_angles(){
		return m_lattice_angles;
	}

	/**
	 * 
	 * Setter for the lattice angles: (alpha, beta, gamma) in units of º,
	 * [min,max]=[0.000,360.000]
	 * Example: [ 90.000, 90.000, 90.000]
	 *
	 * @param lattice_angles is json array containing the lattice angles
	 * @throws LoaderException if the number of lattice angles is other than 3
	 * 	or any of them is not numeric or is out of the [0.000,360.000] range
	 */
	public void setLattice_angles( JSONArray lattice_angles) throws LoaderException{
		//Check it is OK
		if (!(lattice_angles.length()==3 || lattice_angles.length()==0))
			throw new LoaderException(LoaderException.LATTICE_ANGLES_DISTINCT_THAN_3);
		
		BigDecimal jo = null;
		for ( int i=0; i<lattice_angles.length(); i++){
			try {
				jo = lattice_angles.getBigDecimal(i);
			} catch (ClassCastException e){
				throw new LoaderException(LoaderException.A_LATTICE_PARAMETER_IS_NOT_NUMERIC);
			}
			
			if (jo.compareTo(BigDecimal.ZERO) < 0 ||
				jo.compareTo(new BigDecimal("360")) > 0)
				throw new LoaderException(LoaderException.LATTICE_ANGLE_OUT_OF_RANGE);			
		}
		
		/*
		PGobject lattice_angles = new PGobject();
		lattice_angles.setType("json");
		lattice_angles.setValue(lp.toString());		
		item.setLattice_angles(lattice_angles);
		*/
				
		m_lattice_angles = lattice_angles;
	}
	
	/**
	 * Setter for the atomic positions
	 *  Example: [ "Fe1(1a)", 0.0, 0.0, 0.0,
     *     		   "Ni1(1d)", 0.5, 0.5, 0.5]
	 * @return a json array containing the atomic positions
	 */
	public JSONArray getAtomic_positions(){
		return m_atomic_positions;
	}

	/**
	 * Getter for the atomic positions
	 *  Example: [ "Fe1(1a)", 0.0, 0.0, 0.0,
     *     		   "Ni1(1d)", 0.5, 0.5, 0.5]
     *     		   
	 * @param atomic_positions is a json array containing the atomic positions
	 * @throws LoaderException if one of the atomic positions is bigger than one
	 * or is not numeric
	 */
	public void setAtomic_positions( JSONArray atomic_positions) throws LoaderException{
		
		if (atomic_positions==null){
			m_atomic_positions=null;
			return;
		}
	
		for ( int i=0; i<atomic_positions.length(); i++){
			if ( i % 4 == 0){
					
			} else {
				try {
				
				BigDecimal occurrences = atomic_positions.getBigDecimal(i);
				
				if (occurrences.abs().compareTo(BigDecimal.ONE)>0)
					throw new LoaderException(LoaderException.ATOMIC_POSITIONS_OUT_OF_RANGE);
					
				} catch (ClassCastException e){
					throw new LoaderException(LoaderException.AN_ATOMIC_POSITION_IS_NOT_NUMERIC);
				}
				
			}
		}
		
		m_atomic_positions = atomic_positions;
	}

	/**
	 * Getter for the crystal information, which provides information 
	 * about how the crystal structure was calculated (software, settings, …)
	 *  or measured (temperature, pressure, method, …).
	 *  
	 * @return the crystal information
	 */
	public String getCrystal_info(){
		return m_crystal_info;
	}

	/**
	 * Setter for the crystal information, which provides information 
	 * about how the crystal structure was calculated (software, settings, …)
	 *  or measured (temperature, pressure, method, …).
	 * 
	 * @param crystal_info
	 */
	public void setCrystal_info( String crystal_info){
		m_crystal_info = crystal_info;
	}
	
	/**
	 * Getter for the unit cell energy, that is total ab initio energy of the unit cell,
	 *  E. At T=0K and p=0, this is the internal energy of the system (per unit cell).
	 *  In units of eV., [min,max]=[-100000.0000000,100000.0000000]
	 * 
	 * @return the unit cell energy
	 */
	public BigDecimal getUnitCellEnergy(){
		return m_unit_cell_energy;
	}

	/**
	 * Setter for the unit cell energy, that is total ab initio energy of the unit cell,
	 *  E. At T=0K and p=0, this is the internal energy of the system (per unit cell).
	 *  In units of eV., [min,max]=[-100000.0000000,100000.0000000] 
	 * 
	 * @param unit_cell_energy
	 */
	public void setUnitCellEnergy( BigDecimal unit_cell_energy){
		m_unit_cell_energy = unit_cell_energy;
	}
	
	/**
	 * Getter for the unit cell formation enthalpy: 
	 * formation enthalpy ∆HF per unit cell. In units of eV., 
	 * [min,max]=[-1000.000000,1000.000000]
	 * 
	 * @return the unit cell formation enthalpy
	 */
	public BigDecimal getUnit_cell_formation_enthalpy(){
		return m_unit_cell_formation_enthalpy;
	}

	/**
	 * Setter for the unit cell formation enthalpy: 
	 * formation enthalpy ∆HF per unit cell. In units of eV., 
	 * [min,max]=[-1000.000000,1000.000000]
	 * 
	 * @param unit_cell_formation_enthalpy
	 */
	public void setUnit_cell_formation_enthalpy( BigDecimal unit_cell_formation_enthalpy){
		m_unit_cell_formation_enthalpy = unit_cell_formation_enthalpy;
	}
	
	/**
	 * Getter for the energy info which provides information about how the energy
	 *  and enthalpy of formation were calculated or meassured.
	 * @return the energy info
	 */
	public String getEnergy_info(){
		return m_energy_info;
	}

	/**
	 * Setter for the energy info which provides information about how the energy
	 *  and enthalpy of formation were calculated or meassured.
	 *  
	 * @param energy_info
	 */
	public void setEnergy_info( String energy_info){
		m_energy_info = energy_info;
	}
	
	/**
	 * Getter for the interatomic potentials info which provides information about 
	 * how the interatomic potentials were developed (software, settings, …). 
	 * Potentials should be uploaded as an attached file.
	 * @return
	 */
	public String getInteratomic_potentials_info(){
		return m_interatomic_potentials_info;
	}

	/**
	 * Setter for the interatomic potentials info which provides information about 
	 * how the interatomic potentials were developed (software, settings, …). 
	 * Potentials should be uploaded as an attached file.
	 * @param interatomic_potentials_info
	 */
	public void setInteratomic_potentials_info( String interatomic_potentials_info){
		m_interatomic_potentials_info=interatomic_potentials_info;
	}
	
	/**
	 * Getter for the magnetic free energy in units of eV., 
	 * [min,max]=[-100000.000000,100000.000000]
	 * @return
	 */
	public BigDecimal getMagnetic_free_energy(){
		return m_magnetic_free_energy;
	}

	/**
	 * Setter for the magnetic free energy in units of eV., 
	 * [min,max]=[-100000.000000,100000.000000]

	 * @param magnetic_free_energy
	 */
	public void setMagnetic_free_energy( BigDecimal magnetic_free_energy ){
		m_magnetic_free_energy = magnetic_free_energy;
	}
	
	/**
	 * Getter for the magnetic free energy info, it provides information
	 *  about how the magnetic free energy was calculated
	 *   (software, thermodynamic model, temperature, …).
	 * @return
	 */
	public String getMagnetic_free_energy_info(){
		return m_magnetic_free_energy_info;
	}

	/**
	 * Setter for the magnetic free energy info, it provides information
	 *  about how the magnetic free energy was calculated
	 *   (software, thermodynamic model, temperature, …).

	 * @param magnetic_free_energy_info
	 */
	public void setMagnetic_free_energy_info( String magnetic_free_energy_info){
		m_magnetic_free_energy_info = magnetic_free_energy_info;
	}
	
	/**
	 * Getter for the unit cell spin polarization, which is the total magnetization 
	 * of the cell (in units of µB) 
	 * [min,max]=[0.000000,10000.000000]
	 * @return
	 */
	public BigDecimal getUnit_cell_spin_polarization() {
		return m_unit_cell_spin_polarization;
	}

	/**
	 * Setter for the unit cell spin polarization, which is the total magnetization 
	 * of the cell (in units of µB) 
	 * [min,max]=[0.000000,10000.000000]

	 * @param m_unit_cell_spin_polarization
	 */
	public void setUnit_cell_spin_polarization(BigDecimal m_unit_cell_spin_polarization) {
		this.m_unit_cell_spin_polarization = m_unit_cell_spin_polarization;
	}

	/**
	 * Getter for the atomic spin specie, which represents the magnetization per atom
	 *  of each specie (in units of µB/atom). 
	 *  Example: ["1 Fe",2.659,"2 Ni",0.671]
	 *  
	 * @return a json array containing the atomic spin specie
	 */
	public JSONArray getAtomic_spin_specie() {
		return m_atomic_spin_specie;
	}

	/**
	 * Setter for the atomic spin specie, which represents the magnetization per atom
	 *  of each specie (in units of µB/atom). 
	 *  Example: ["1 Fe",2.659,"2 Ni",0.671]
	 *  
	 * @param m_atomic_spin_specie is a json array containing the atomic spin specie
	 */
	public void setAtomic_spin_specie(JSONArray m_atomic_spin_specie) {
		this.m_atomic_spin_specie = m_atomic_spin_specie;
	}
	
	/**
	 * Getter for the saturation magnetization that is computed as
	 *  unit cell spin polarization/ unit cell volume (in units of Tesla)
	 * [min,max]=[0.000,100.000]
	 * @return
	 */
	public BigDecimal getSaturation_magnetization() {
		return m_saturation_magnetization;
	}

	/**
	 * Setter for the saturation magnetization that is computed as
	 *  unit cell spin polarization/ unit cell volume (in units of Tesla)
	 * [min,max]=[0.000,100.000]
	 * 
	 * @param m_saturation_magnetization
	 */
	public void setSaturation_magnetization(BigDecimal m_saturation_magnetization) {
		this.m_saturation_magnetization = m_saturation_magnetization;
	}
	
	/**
	 * Getter for the magnetization temperature, that is the temperature at which 
	 * the magnetization is calculated or measured. 
	 * In units of Kelvin [min,max]=[0.000,10000.000]
	 * @return
	 */
	public BigDecimal getMagnetization_temperature() {
		return m_magnetization_temperature;
	}

	/**
	 * Setter for the magnetization temperature, that is the temperature at which 
	 * the magnetization is calculated or measured. 
	 * In units of Kelvin [min,max]=[0.000,10000.000]

	 * @param m_magnetization_temperature
	 */
	public void setMagnetization_temperature(BigDecimal m_magnetization_temperature) {
		this.m_magnetization_temperature = m_magnetization_temperature;
	}

	/**
	 * Getter for the magnetization info, that provides information about
	 *  how the magnetization was calculated (software, settings, …) or measured. 
	 * @return
	 */
	public String getMagnetization_info() {
		return m_magnetization_info;
	}

	/**
	 * 	Setter for the magnetization info, that provides information about
	 *  how the magnetization was calculated (software, settings, …) or measured. 

	 * @param m_magnetization_info
	 */
	public void setMagnetization_info(String m_magnetization_info) {
		this.m_magnetization_info = m_magnetization_info;
	}

	/**
	 * Getter for the magnetocrystalline anisotropy energy, which is the energy
	 *  of unit cell when magnetization is constraint in some particular directions.
	 *  In units of eV.
	 *  Format= direction of magnetization (mx,my,mz), energy; …
	 *  Example: [	0,0,1,-13.94019080,
	 *  			1,0,1,-13.94012990,
	 *  			1,0,0,-13.94008791,
	 *  			0,1,0,-13.94008803]
	 * @return a json array containng the magnetocrystalline anisotropy energy
	 */
	public JSONArray getMagnetocrystalline_anisotropy_energy() {
		return m_magnetocrystalline_anisotropy_energy;
	}

	/**
	 * Setter for the magnetocrystalline anisotropy energy, which is the energy
	 *  of unit cell when magnetization is constraint in some particular directions.
	 *  In units of eV.
	 *  Format= direction of magnetization (mx,my,mz), energy; …
	 *  Example: [	0,0,1,-13.94019080,
	 *  			1,0,1,-13.94012990,
	 *  			1,0,0,-13.94008791,
	 *  			0,1,0,-13.94008803]
 
	 * @param m_magnetocrystalline_anisotropy_energy is a json array containng the magnetocrystalline anisotropy energy
	 */
	public void setMagnetocrystalline_anisotropy_energy(JSONArray m_magnetocrystalline_anisotropy_energy) {
		this.m_magnetocrystalline_anisotropy_energy = m_magnetocrystalline_anisotropy_energy;
	}
	
	/**
	 * Setter for the anisotropy energy type, that can be uniaxial or cubic or planar.
	 *   
	 * @return U when uniaxial, C when cubic, P when planar
	 */
	public String getAnisotropy_energy_type() {
			return m_anisotropy_energy_type;
	}

	/**
	 * Setter for the anisotropy energy type, that can be uniaxial or cubic or planar.
	 *   
	 * @param m_anisotropy_type must be U when uniaxial, C when cubic, P when planar
	 * @throws LoaderException when none of the values avobe is used as argument
	 */
	public void setAnisotropy_energy_type(String m_anisotropy_type) throws LoaderException {
		
		if(m_anisotropy_type==null){
			this.m_anisotropy_energy_type=null;
			return;
		}
		
		switch (m_anisotropy_type){
			case "uniaxial":
				this.m_anisotropy_energy_type = "U";
				break;
			case "cubic":
				this.m_anisotropy_energy_type = "C";
				break;
			case "planar":
				this.m_anisotropy_energy_type = "P";
				break;
				
			default:
				throw new LoaderException(LoaderException.ANISOTROPY_ENENRGY_TYPE_INCORRECT);
		}
	}

	/**
	 * Getter for the magnetocrystalline anisotropy constants
	 *  K1 and K2 in units of MJ/m3, separeted by “,”. 
	 *  An uniaxial anisotropy energy type with K1<0 means a planar anisotropy, 
	 *  while K1>0 implies an easy axis (uniaxial).
	 *  [min,max]=[-100.000,100.000]
	 *  Example: [0.73,0.00]
	 *  
	 * @return a json array containing the magnetocrystalline anisotropy constants
	 */
	public JSONArray getMagnetocrystalline_anisotropy_constants() {
		return m_magnetocrystalline_anisotropy_constants;
	}

	/**
	 * Setter for the magnetocrystalline anisotropy constants
	 *  K1 and K2 in units of MJ/m3, separeted by “,”. 
	 *  An uniaxial anisotropy energy type with K1<0 means a planar anisotropy, 
	 *  while K1>0 implies an easy axis (uniaxial).
	 *  [min,max]=[-100.000,100.000]
	 *  Example: [0.73,0.00]

	 * 
	 * @param m_magnetocrystalline_anisotropy_constants is a a json array containing the magnetocrystalline anisotropy constants
	 */
	public void setMagnetocrystalline_anisotropy_constants(JSONArray m_magnetocrystalline_anisotropy_constants) {
		this.m_magnetocrystalline_anisotropy_constants = m_magnetocrystalline_anisotropy_constants;
	}

	/**
	 * Getter for the kind of anisotropy
	 * (i.e., easy axis, easy plane, easy cone)
	 *  
	 * @return A when easy axis, P when planar easy axis, C when easy cone
	 */
	public String getKind_of_anisotropy() {
		return m_kind_of_anisotropy;
	}
	
	/**
	 * 	Setter for the kind of anisotropy
	 * (i.e., easy axis, easy plane, easy cone)

	 * 
	 * @param m_kind_of_anisotropy can take the values
	 * A when easy axis, P when easy plane, C when easy cone
	 * @throws LoaderException when none of the values above is taken by the argument
	 */
	public void setKind_of_anisotropy(String m_kind_of_anisotropy) throws LoaderException {
		
		if (m_kind_of_anisotropy==null){
			this.m_kind_of_anisotropy = null;
			return;
		}
		
		switch (m_kind_of_anisotropy){
			case "easy axis":
				this.m_kind_of_anisotropy = "A";
				break;
			case "easy plane":
				this.m_kind_of_anisotropy = "P";
				break;
			case "easy cone":
				this.m_kind_of_anisotropy = "C";
				break;
			default:
				throw new LoaderException(LoaderException.KIND_OF_ANISOTROPY_INCORRECT);
		}		
	}

	/**
	 * Getter for the anisotropy info, that provides information about 
	 * how the magnetocrystalline anisotropy was calculated (software, settings, …) 
	 * or measured (method, conditions, …).
	 * @return
	 */
	public String getAnisotropy_info() {
		return m_anisotropy_info;
	}

	/**
	 * Setter for the anisotropy info, that provides information about 
	 * how the magnetocrystalline anisotropy was calculated (software, settings, …) 
	 * or measured (method, conditions, …).

	 * @param m_anisotropy_info
	 */
	public void setAnisotropy_info(String m_anisotropy_info) {
		this.m_anisotropy_info = m_anisotropy_info;
	}

	/**
	 * Getter for the exchange integrals in units of mRy, sorted by interatomic distance.
	 *  Exchange energy is written as E_ex=-1/2 ∑▒J_ij  s_i∙s_j, 
	 *  where J_ij is the exchange integral and
	 *   s_i is the unit vector along the i-th atomic magnetic moment.
	 *  Example: [	"Fe-Fe",2.22119,1.35194,0.38785,0.50683,-0.67064,-0.4195,
	 *  			"Ni-Ni",0.29772,-0.04463,0.04442,0.04059,-0.01808,-0.00461,
	 *  			"Fe-Ni",1.27737,0.08671,0.03079,-0.02957,0.01218,0.0091] 
	 *  
	 * @return a json array containing the exchange integrals
	 */
	public JSONArray getExchange_integrals() {
		return m_exchange_integrals;
	}

	/**
	 * Setter for the exchange integrals in units of mRy, sorted by interatomic distance.
	 *  Exchange energy is written as E_ex=-1/2 ∑▒J_ij  s_i∙s_j, 
	 *  where J_ij is the exchange integral and
	 *   s_i is the unit vector along the i-th atomic magnetic moment.
	 *  Example: [	"Fe-Fe",2.22119,1.35194,0.38785,0.50683,-0.67064,-0.4195,
	 *  			"Ni-Ni",0.29772,-0.04463,0.04442,0.04059,-0.01808,-0.00461,
	 *  			"Fe-Ni",1.27737,0.08671,0.03079,-0.02957,0.01218,0.0091] 
	
	 * 
	 * @param m_exchange_integrals is a json array containing the exchange integrals
	 */
	public void setExchange_integrals(JSONArray m_exchange_integrals) {
		this.m_exchange_integrals = m_exchange_integrals;
	}

	/**
	 * Getter for the exchange info, which returns information about 
	 * how exchange integrals were calculated (software, settings, …).
	 * @return
	 */
	public String getExchange_info() {
		return m_exchange_info;
	}

	/**
	 * Setter for the exchange info, which returns information about 
	 * how exchange integrals were calculated (software, settings, …).

	 * @param m_exchange_info
	 */
	public void setExchange_info(String m_exchange_info) {
		this.m_exchange_info = m_exchange_info;
	}

	/**
	 * Getter for the magnetic order:
	 *  Ferromagnet, Antiferromagnet, Ferrimagnet, Paramagnet, Diamagnet, …
	 * @return
	 */
	public String getMagnetic_order() {
		return m_magnetic_order;
	}

	/**
	 * 	Setter for the magnetic order:
	 *  Ferromagnet, Antiferromagnet, Ferrimagnet, Paramagnet, Diamagnet, …

	 * @param m_magnetic_order
	 */
	public void setMagnetic_order(String m_magnetic_order) {
		this.m_magnetic_order = m_magnetic_order;
	}

	/**
	 * Getter for the Curie temperature in units of Kelvin.
	 * [min,max]=[0.000,10000.000]
	 * @return
	 */
	public BigDecimal getCurie_temperature() {
		return m_curie_temperature;
	}

	/**
	 * Setter for the Curie temperature in units of Kelvin.
	 * [min,max]=[0.000,10000.000]

	 * @param m_curie_temperature
	 */
	public void setCurie_temperature(BigDecimal m_curie_temperature) {
		this.m_curie_temperature = m_curie_temperature;
	}
	
	/**
	 * Getter for the Curie temperature info, that provides information about 
	 * how Curie temperature was calculated (MFA, RPA, ASD, 
	 * longitudinal susceptibility peak, fourth-order Binder cumulant) 
	 * or measured (method, …).
	 * @return
	 */
	public String getCurie_temperature_info() {
		return m_curie_temperature_info;
	}

	/**
	 * Setter for the Curie temperature info, that provides information about 
	 * how Curie temperature was calculated (MFA, RPA, ASD, 
	 * longitudinal susceptibility peak, fourth-order Binder cumulant) 
	 * or measured (method, …).

	 * @param m_curie_temperature_info
	 */
	public void setCurie_temperature_info(String m_curie_temperature_info) {
		this.m_curie_temperature_info = m_curie_temperature_info;
	}

	/**
	 * Getter for the anisotropy field in units of Tesla.
	 * [min,max]=[0.000,100.000]
	 * @return
	 */
	public BigDecimal getAnisotropy_field() {
		return m_anisotropy_field;
	}

	/**
	 * Setter for the anisotropy field in units of Tesla.
	 * @param m_anisotropy_field
	 */
	public void setAnisotropy_field(BigDecimal m_anisotropy_field) {
		this.m_anisotropy_field = m_anisotropy_field;
	}

	/**
	 * Getter for the remanence in units of Tesla.
	 * [min,max]=[0.000,100.000
	 * @return
	 */
	public BigDecimal getRemanence() {
		return m_remanence;
	}

	/**
	 * Setter for the remanence in units of Tesla.
	 * [min,max]=[0.000,100.000

	 * @param m_remanence
	 */
	public void setRemanence(BigDecimal m_remanence) {
		this.m_remanence = m_remanence;
	}

	/**
	 * Getter for the coercivity in units of Tesla.
	 * [min,max]=[0.000,100.000]
	 * @return
	 */
	public BigDecimal getCoercivity() {
		return m_coercivity;
	}

	/**
	 * Setter for the coercivity in units of Tesla.
	 * [min,max]=[0.000,100.000]
	 * @param m_coercivity
	 */
	public void setCoercivity(BigDecimal m_coercivity) {
		this.m_coercivity = m_coercivity;
	}

	/**
	 * Getter for the energy product in units of kJ/m3.
	 * [min,max]=[0.000,10000.000]
	 * @return
	 */
	public BigDecimal getEnergy_product() {
		return m_energy_product;
	}

	/**
	 * Setter for the energy product in units of kJ/m3.
	 * [min,max]=[0.000,10000.000]

	 * @param m_energy_product
	 */
	public void setEnergy_product(BigDecimal m_energy_product) {
		this.m_energy_product = m_energy_product;
	}

	/**
	 * Getter for the hysteresis info, that provides information about
	 *  how hysterisis properties (anisotropy field, remanence, coercivity and 
	 *  energy product) were calculated (ASD, micromagnetics, temperature, …) 
	 *  or measured (method, conditions, …).. 
	 * @return
	 */
	public String getHysteresis_info() {
		return m_hysteresis_info;
	}

	/**
	 * 	Setter for the hysteresis info, that provides information about
	 *  how hysterisis properties (anisotropy field, remanence, coercivity and 
	 *  energy product) were calculated (ASD, micromagnetics, temperature, …) 
	 *  or measured (method, conditions, …).. 

	 * @param m_hysteresis_info
	 */
	public void setHysteresis_info(String m_hysteresis_info) {
		this.m_hysteresis_info = m_hysteresis_info;
	}

	/**
	 * Getter for the domain wall width in units of nm. 
	 * [min,max]=[0.000,1000.000]
	 * @return
	 */
	public BigDecimal getDomain_wall_width() {
		return m_domain_wall_width;
	}

	/**
	 * Setter for the domain wall width in units of nm. 
	 * [min,max]=[0.000,1000.000]

	 * @param m_domain_wall_width
	 */
	public void setDomain_wall_width(BigDecimal m_domain_wall_width) {
		this.m_domain_wall_width = m_domain_wall_width;
	}
	
	/**
	 * Getter for the domain wall info, that provides information about
	 *  how domain wall width was calculated (model, ASD, temperature, …) 
	 *  or measured (method, conditions, …).
	 * @return
	 */
	public String getDomain_wall_info() {
		return m_domain_wall_info;
	}

	/**
	 * 	Setter for the domain wall info, that provides information about
	 *  how domain wall width was calculated (model, ASD, temperature, …) 
	 *  or measured (method, conditions, …).
	 * @param m_domain_wall_info
	 */
	public void setDomain_wall_info(String m_domain_wall_info) {
		this.m_domain_wall_info = m_domain_wall_info;
	}

	/**
	 * Getter for the exchange stiffness in units of pJ/m.
	 * [min,max]=[0.000,1000.000]
	 * @return
	 */
	public BigDecimal getExchange_stiffness() {
		return m_exchange_stiffness;
	}

	/**
	 * Setter for the exchange stiffness in units of pJ/m.
	 * [min,max]=[0.000,1000.000]
	 * @param m_exchange_stiffness
	 */
	public void setExchange_stiffness(BigDecimal m_exchange_stiffness) {
		this.m_exchange_stiffness = m_exchange_stiffness;
	}

	/**
	 * Getter for the exchange stiffness info, that provides information about 
	 * how exchange stiffness was calculated (model, ASD, temperature, …) 
	 * or measured (method, conditions, …). 
	 * @return
	 */
	public String getExchange_stiffness_info() {
		return m_exchange_stiffness_info;
	}
	
	/**
	 * Setter for the exchange stiffness info, that provides information about 
	 * how exchange stiffness was calculated (model, ASD, temperature, …) 
	 * or measured (method, conditions, …). 
	 * @param m_exchange_stiffness_info
	 */
	public void setExchange_stiffness_info(String m_exchange_stiffness_info) {
		this.m_exchange_stiffness_info = m_exchange_stiffness_info;
	}

	/**
	 * Getter for the publications or links where these data can be found
	 * @return
	 */
	public String getReference() {
		return m_reference;
	}

	/**
	 * Setter for the publications or links where these data can be found
	 * @param m_reference
	 */
	public void setReference(String m_reference) {
		this.m_reference = m_reference;
	}

	/**
	 * Getter for the additional information about these data,
	 *  which was not mentioned above.
	 * @return
	 */
	public String getComments() {
		return m_comments;
	}

	/**
	 *  Setter for the additional information about these data,
	 *  which was not mentioned above.

	 * @param m_comments
	 */
	public void setComments(String m_comments) {
		this.m_comments = m_comments;
	}

	/**
	 * Getter for the names or/and institutions which are the authors of these data.
	 *  Example ["One author", "Other author"]
	 * @return a json array with the list of authors
	 */
	public JSONArray getAuthors() {
		return m_authors;
	}

	/**
	 * 	Setter for the names or/and institutions which are the authors of these data.
	 *  Example ["One author", "Other author"]

	 * @param m_authors is a json array with the list of authors
	 */
	public void setAuthors(JSONArray m_authors) {
		this.m_authors = m_authors;
	}

	/**
	 * Getter for the crystallographic data (metadata format CIF, CONTCAR.vasp, …), 
	 * property values versus temperature, property values versus external magnetic field,
	 *  interatomic potentials, figures,
	 *  Example: ["Fe10Ta2_#129_1.cif", "CONTCAR_Fe10Ta2_#129_1"]
	 *  
	 * @return a json array with a list of file names (the path is assumed that is the
	 * same path than the json file that references them)
	 */
	public JSONArray getAttached_files() {
		return m_attached_files;
	}

	/**
	 * Setter for the crystallographic data (metadata format CIF, CONTCAR.vasp, …), 
	 * property values versus temperature, property values versus external magnetic field,
	 *  interatomic potentials, figures,
	 *  Example: ["Fe10Ta2_#129_1.cif", "CONTCAR_Fe10Ta2_#129_1"]
	 *  
	 * @param m_attached_files is a a json array with a list of file names (the path is assumed that is the
	 * same path than the json file that references them)
	 */
	public void setAttached_files(JSONArray m_attached_files) {
		this.m_attached_files = m_attached_files;
	}

	/**
	 * Getter for the attached files info providing a short description 
	 * of the attached files, that is, which information contain and how it was obtained. 
	 *  
	 * @return a json array with the same number of elements than the attached files json array
	 */
	public JSONArray getAttached_files_info() {
		return m_attached_files_info;
	}

	/**
	 * Setter for the attached files info providing a short description 
	 * of the attached files, that is, which information contain and how it was obtained. 
	 *
	 * @param m_attached_files_info is a json array with the same number of elements than the attached files json array
	 */
	public void setAttached_files_info(JSONArray m_attached_files_info) {
		this.m_attached_files_info = m_attached_files_info;
	}
}
