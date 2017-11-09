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

import json_loader.JSONparser;
import json_loader.error_handling.LoaderException;
import json_loader.utils.Cleaner;
import json_loader.utils.Comparators;
import json_loader.utils.ConnectionPool;

public class DBitem {	
	 
	private static Logger l = null;	
	
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
	
	public void insert(Connection con, boolean doCommit){
		
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
							"type, name, summary, formula,"+ 
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
							+ "?,?,?,?,"
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
			
			ins_item.setString(1, getType());
			ins_item.setString(2, getName());
			ins_item.setString(3, getSummary());
			ins_item.setString(4, getFormula());
			
			ins_item.setString(5, getProduction_info());
			
			ins_item.setInt(6, getCompound_space_group());
			ins_item.setBigDecimal(7, getUnit_cell_volume());
			setJSONArray(ins_item, 8, getLattice_parameters());
			setJSONArray(ins_item, 9, getLattice_angles());
			
			setJSONArray(ins_item, 10, getAtomic_positions());
			ins_item.setString(11, getCrystal_info());
			
			ins_item.setBigDecimal(12, getUnitCellEnergy());
			ins_item.setBigDecimal(13, getUnit_cell_formation_enthalpy());
			ins_item.setString(14, getEnergy_info());

			ins_item.setBigDecimal(15, getUnit_cell_spin_polarization());
			setJSONArray(ins_item, 16, getAtomic_spin_specie());
			ins_item.setBigDecimal(17, getSaturation_magnetization());
			ins_item.setBigDecimal(18, getMagnetization_temperature());
			ins_item.setString(19, getMagnetization_info());
			
			setJSONArray(ins_item, 20, getMagnetocrystalline_anisotropy_energy());
			ins_item.setString(21, getAnisotropy_energy_type());
			setJSONArray(ins_item, 22, getMagnetocrystalline_anisotropy_constants());
			ins_item.setString(23, getKind_of_anisotropy());
			ins_item.setBigDecimal(24, getAnisotropy_field());
			ins_item.setString(25, getAnisotropy_info());
			
			setJSONArray(ins_item, 26, getExchange_integrals());
			
			ins_item.setString(27, getExchange_info());
			ins_item.setString(28, getMagnetic_order());
			ins_item.setBigDecimal(29, getCurie_temperature());
			ins_item.setString(30, getCurie_temperature_info());
			ins_item.setBigDecimal(31, getRemanence());
			ins_item.setBigDecimal(32, getCoercivity());
			ins_item.setBigDecimal(33, getEnergy_product());
			
			ins_item.setString(34, getHysteresis_info());
			ins_item.setBigDecimal(35, getDomain_wall_width());
			ins_item.setString(36, getDomain_wall_info());
			ins_item.setBigDecimal(37, getExchange_stiffness());
			ins_item.setString(38, getExchange_stiffness_info());
			
			ins_item.setString(39, getReference());
			ins_item.setString(40, getComments());

			
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
			
		} catch (NamingException | SQLException | IOException e) {
			l.error(e.getMessage());
			//e.printStackTrace();
		} finally {
			p.close(rs_lastMafId);
			p.close(ins_item);
			if (closeConnection) p.close(con);
		}
		
		
	}
	
	public String toString(){
		String s="";
		
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
	
	public String getName(){
		return m_name;
	}
	
	public void setName(String name){
		m_name=name;
	}
	
	public String getType(){
		return m_type;
	}
	
	public void setSummary(String  summary){
		m_summary=summary;
	}
	
	public String getSummary(){
		return m_summary;
	}	
	
	public void setFormula(String formula){
		m_formula = formula;
	};
	
	public String getFormula(){
		return m_formula;
	}
	
	public void setProduction_info(String production_info){
		m_production_info = production_info;
	}	
	
	public String getProduction_info(){
		return m_production_info;
	}
	
	public void setCompound_space_group( int compound_space_group){
		if (compound_space_group<0)
			m_compound_space_group=null;
		else
			m_compound_space_group = compound_space_group;
	}
	
	public Integer getCompound_space_group(){
		return m_compound_space_group;
	}
	
	public void setUnit_cell_volume( BigDecimal unit_cell_volume){
		m_unit_cell_volume = unit_cell_volume;
	}
	
	public BigDecimal getUnit_cell_volume(){
		return m_unit_cell_volume;
	}
		
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
		
		/*
		PGobject lattice_parameters = new PGobject();
		lattice_parameters.setType("json");
		lattice_parameters.setValue(lp.toString());
		item.setLattice_parameters(lattice_parameters);
		*/
		
		m_lattice_parameters = lattice_parameters;
	}
	
	public JSONArray getLattice_parameters(){
		return m_lattice_parameters;
	}
		
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
	
	public JSONArray getLattice_angles(){
		return m_lattice_angles;
	}
	
	public void setAtomic_positions( JSONArray atomic_positions) throws LoaderException{
		
		if (atomic_positions==null){
			m_atomic_positions=null;
			return;
		}
		
		//TODO
		//Check it is OK --> These checks must be done after parsing the formula
		/*
		if (lp.length()==0)
			throw new LoaderException(LoaderException.ATOMIC_POSITIONS_EMPTY);
		if (lp.length()>m_numAtoms*4)
			throw new LoaderException(LoaderException.MORE_ATOMIC_POSITIONS_THAN_ATOMS);				
		*/
		
		//BigDecimal jo = null;
		for ( int i=0; i<atomic_positions.length(); i++){
			if ( i % 4 == 0){
				//String atom = lp.getString(i);
		
				//TODO
				//Check it is OK --> These checks must be done after parsing the formula
				/*
				boolean found = allAtoms.stream().filter( item -> item.equals(atom)).findAny().isPresent();
				
				if (!found)
					throw new LoaderException() el atomo no esta en la tabla de atomos
					pero otra opción es lanzar el atomo no esta en la formula y dejar que la opcion anterior salte al parsear la formula
					
				coordinates = new JSONArray();
				¿? coordinates.getJSONArray(0);
				*/
				
			} else {
				try {
				
				BigDecimal occurrences = atomic_positions.getBigDecimal(i);
				
				//if (occurrences<0 || occurrences>1)
				//if (occurrences.compareTo(BigDecimal.ZERO)<0 || occurrences.compareTo(new BigDecimal(1.0))>0)
				if (occurrences.abs().compareTo(BigDecimal.ONE)>0)
					throw new LoaderException(LoaderException.ATOMIC_POSITIONS_OUT_OF_RANGE);
					
				} catch (ClassCastException e){
					throw new LoaderException(LoaderException.AN_ATOMIC_POSITION_IS_NOT_NUMERIC);
				}
				
			}
		}
		
		m_atomic_positions = atomic_positions;
	}
	
	public JSONArray getAtomic_positions(){
		return m_atomic_positions;
	}
	
	public void setCrystal_info( String crystal_info){
		m_crystal_info = crystal_info;
	}
	
	public String getCrystal_info(){
		return m_crystal_info;
	}
	
	public void setUnitCellEnergy( BigDecimal unit_cell_energy){
		m_unit_cell_energy = unit_cell_energy;
	}
	
	public BigDecimal getUnitCellEnergy(){
		return m_unit_cell_energy;
	}
	
	public void setUnit_cell_formation_enthalpy( BigDecimal unit_cell_formation_enthalpy){
		m_unit_cell_formation_enthalpy = unit_cell_formation_enthalpy;
	}
	
	public BigDecimal getUnit_cell_formation_enthalpy(){
		return m_unit_cell_formation_enthalpy;
	}
	
	public void setEnergy_info( String energy_info){
		m_energy_info = energy_info;
	}
	
	public String getEnergy_info(){
		return m_energy_info;
	}
	
	public void setInteratomic_potentials_info( String interatomic_potentials_info){
		m_interatomic_potentials_info=interatomic_potentials_info;
	}
	
	public String getInteratomic_potentials_info(){
		return m_interatomic_potentials_info;
	}
	
	public void setMagnetic_free_energy( BigDecimal magnetic_free_energy ){
		m_magnetic_free_energy = magnetic_free_energy;
	}
	
	public BigDecimal getMagnetic_free_energy(){
		return m_magnetic_free_energy;
	}
	
	public void setMagnetic_free_energy_info( String magnetic_free_energy_info){
		m_magnetic_free_energy_info = magnetic_free_energy_info;
	}
	
	public String getMagnetic_free_energy_info(){
		return m_magnetic_free_energy_info;
	}

	public BigDecimal getUnit_cell_spin_polarization() {
		return m_unit_cell_spin_polarization;
	}

	public void setUnit_cell_spin_polarization(BigDecimal m_unit_cell_spin_polarization) {
		this.m_unit_cell_spin_polarization = m_unit_cell_spin_polarization;
	}

	public JSONArray getAtomic_spin_specie() {
		return m_atomic_spin_specie;
	}

	public void setAtomic_spin_specie(JSONArray m_atomic_spin_specie) {
		this.m_atomic_spin_specie = m_atomic_spin_specie;
	}

	public BigDecimal getSaturation_magnetization() {
		return m_saturation_magnetization;
	}

	public void setSaturation_magnetization(BigDecimal m_saturation_magnetization) {
		this.m_saturation_magnetization = m_saturation_magnetization;
	}

	public BigDecimal getMagnetization_temperature() {
		return m_magnetization_temperature;
	}

	public void setMagnetization_temperature(BigDecimal m_magnetization_temperature) {
		this.m_magnetization_temperature = m_magnetization_temperature;
	}

	public String getMagnetization_info() {
		return m_magnetization_info;
	}

	public void setMagnetization_info(String m_magnetization_info) {
		this.m_magnetization_info = m_magnetization_info;
	}

	public JSONArray getMagnetocrystalline_anisotropy_energy() {
		return m_magnetocrystalline_anisotropy_energy;
	}

	public void setMagnetocrystalline_anisotropy_energy(JSONArray m_magnetocrystalline_anisotropy_energy) {
		this.m_magnetocrystalline_anisotropy_energy = m_magnetocrystalline_anisotropy_energy;
	}

	public String getAnisotropy_energy_type() {
			return m_anisotropy_energy_type;
	}
			
	public String decodeAnisotropy_energy_type() {		
		
		switch (m_anisotropy_energy_type){
			case "U":
				return "uniaxial";
			case "C":
				return "cubic";
			case "P":
				return "planar";
		}
		return null;		
	}

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
	
	public JSONArray getMagnetocrystalline_anisotropy_constants() {
		return m_magnetocrystalline_anisotropy_constants;
	}

	public void setMagnetocrystalline_anisotropy_constants(JSONArray m_magnetocrystalline_anisotropy_constants) {
		this.m_magnetocrystalline_anisotropy_constants = m_magnetocrystalline_anisotropy_constants;
	}

	
	public String getKind_of_anisotropy() {
		return m_kind_of_anisotropy;
	}
	
	public String decodeKind_of_anisotropy() {
		switch (m_kind_of_anisotropy){
		case "A":
			return "easy axis";
		case "P":
			return "planar easy axis";
		case "C":
			return "easy cone";
		}
		return null;
		
	}

	public void setKind_of_anisotropy(String m_kind_of_anisotropy) throws LoaderException {
		
		if (m_kind_of_anisotropy==null){
			this.m_kind_of_anisotropy = null;
			return;
		}
		
		switch (m_kind_of_anisotropy){
			case "easy axis":
				this.m_kind_of_anisotropy = "A";
				break;
			case "planar easy axis":
				this.m_kind_of_anisotropy = "P";
				break;
			case "easy cone":
				this.m_kind_of_anisotropy = "C";				
			default:
				throw new LoaderException(LoaderException.KIND_OF_ANISOTROPY_INCORRECT);
		}		
	}

	public String getAnisotropy_info() {
		return m_anisotropy_info;
	}

	public void setAnisotropy_info(String m_anisotropy_info) {
		this.m_anisotropy_info = m_anisotropy_info;
	}

	public JSONArray getExchange_integrals() {
		return m_exchange_integrals;
	}

	public void setExchange_integrals(JSONArray m_exchange_integrals) {
		this.m_exchange_integrals = m_exchange_integrals;
	}

	public String getExchange_info() {
		return m_exchange_info;
	}

	public void setExchange_info(String m_exchange_info) {
		this.m_exchange_info = m_exchange_info;
	}

	public String getMagnetic_order() {
		return m_magnetic_order;
	}

	public void setMagnetic_order(String m_magnetic_order) {
		this.m_magnetic_order = m_magnetic_order;
	}

	public BigDecimal getCurie_temperature() {
		return m_curie_temperature;
	}

	public void setCurie_temperature(BigDecimal m_curie_temperature) {
		this.m_curie_temperature = m_curie_temperature;
	}

	public String getCurie_temperature_info() {
		return m_curie_temperature_info;
	}

	public void setCurie_temperature_info(String m_curie_temperature_info) {
		this.m_curie_temperature_info = m_curie_temperature_info;
	}

	public BigDecimal getAnisotropy_field() {
		return m_anisotropy_field;
	}

	public void setAnisotropy_field(BigDecimal m_anisotropy_field) {
		this.m_anisotropy_field = m_anisotropy_field;
	}

	public BigDecimal getRemanence() {
		return m_remanence;
	}

	public void setRemanence(BigDecimal m_remanence) {
		this.m_remanence = m_remanence;
	}

	public BigDecimal getCoercivity() {
		return m_coercivity;
	}

	public void setCoercivity(BigDecimal m_coercivity) {
		this.m_coercivity = m_coercivity;
	}

	public BigDecimal getEnergy_product() {
		return m_energy_product;
	}

	public void setEnergy_product(BigDecimal m_energy_product) {
		this.m_energy_product = m_energy_product;
	}

	public String getHysteresis_info() {
		return m_hysteresis_info;
	}

	public void setHysteresis_info(String m_hysteresis_info) {
		this.m_hysteresis_info = m_hysteresis_info;
	}

	public BigDecimal getDomain_wall_width() {
		return m_domain_wall_width;
	}

	public void setDomain_wall_width(BigDecimal m_domain_wall_width) {
		this.m_domain_wall_width = m_domain_wall_width;
	}

	public String getDomain_wall_info() {
		return m_domain_wall_info;
	}

	public void setDomain_wall_info(String m_domain_wall_info) {
		this.m_domain_wall_info = m_domain_wall_info;
	}

	public BigDecimal getExchange_stiffness() {
		return m_exchange_stiffness;
	}

	public void setExchange_stiffness(BigDecimal m_exchange_stiffness) {
		this.m_exchange_stiffness = m_exchange_stiffness;
	}

	public String getExchange_stiffness_info() {
		return m_exchange_stiffness_info;
	}

	public void setExchange_stiffness_info(String m_exchange_stiffness_info) {
		this.m_exchange_stiffness_info = m_exchange_stiffness_info;
	}

	public JSONArray getAuthors() {
		return m_authors;
	}

	public void setAuthors(JSONArray m_authors) {
		this.m_authors = m_authors;
	}

	public String getReference() {
		return m_reference;
	}

	public void setReference(String m_reference) {
		this.m_reference = m_reference;
	}

	public String getComments() {
		return m_comments;
	}

	public void setComments(String m_comments) {
		this.m_comments = m_comments;
	}

	public JSONArray getAttached_files() {
		return m_attached_files;
	}

	public void setAttached_files(JSONArray m_attached_files) {
		this.m_attached_files = m_attached_files;
	}

	public JSONArray getAttached_files_info() {
		return m_attached_files_info;
	}

	public void setAttached_files_info(JSONArray m_attached_files_info) {
		this.m_attached_files_info = m_attached_files_info;
	}
	
	private void setJSONArray(
			PreparedStatement pstm,
			int position, JSONArray val ) throws SQLException{
		if (val==null){
			pstm.setNull(position, Types.VARCHAR);
		} else{
			pstm.setString(position, val.toString());
		}
			
	}
}
