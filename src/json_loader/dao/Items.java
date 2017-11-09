package json_loader.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;
import json_loader.utils.ConnectionPool;

public class Items {
	
	private static Logger l = null;
	
	private DBitem m_DBitem;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public Items(){
		l =	LoggerFactory.getLogger(Items.class);
	}
	
	public void setDBItem( DBitem arg_DBitem ){
		m_DBitem = arg_DBitem;
	}
	
	public void insert( Connection con, boolean doCommit)
			throws LoaderException, IOException, NamingException{
		
		ConnectionPool p = null;		
		boolean closeConnection=false;
		
		PreparedStatement ins_materialFeatures=null;
		
		try{
			p = ConnectionPool.getInstance();
			if (con==null){
				con = p.getConnection();
				closeConnection=true;
			}
			
			ins_materialFeatures = con.prepareStatement(
					"INSERT INTO items "
					+ "( type, name, summary, production_info,formula,"
					+ " compound_space_group, unit_cell_volume, lattice_parameters, " //Crystal
					+ " lattice_angles, atomic_positions, crystal_info, "
					+ " unit_cell_energy, unit_cell_formation_enthalpy," //Thermodynamics
					+ " energy_info, interatomic_potentials_info, "
					+ " magnetic_free_energy, magnetic_free_energy_info, "
					+ " unit_cell_spin_polarization, atomic_spin_specie, " //Magnetics
					+ " saturation_magnetization, magnetization_temperature, "
					+ " magnetization_info, magnetocrystalline_anisotropy_energy, "
					+ " anisotropy_energy_type, magnetocrystalline_anisotropy_constants, "
					+ " kind_of_anisotropy, anisotropy_info, "
					+ " exchange_integrals, exchange_info, magnetic_order, "
					+ " curie_temperature, curie_temperature_info,"
					+ " anisotropy_field, remanence, coercivity, energy_product, "
					+ " hysteresis_info, domain_wall_width, domain_wall_info, "
					+ " exchange_stiffness, exchange_stiffness_info, "
					+ " reference, comments" //Additional information
					//+ " authors, reference, comments" //Additional information
					+ ")"
					+ "VALUES(?,?,?,?,?"
					+ ",?,?, cast(? as json), cast(? as json), cast(? as json), ?," //Crystal
					+ "?,?,?,?,?,?," //Thermodynamics
					+ "?,cast(? as json),?,?,?,cast(? as json),?,cast(? as json),?,?,"//Magnetics
					+ "cast(? as json),?,?,?,?,?,?,?,?,?,?,?,?,?,"
					+ "?,?" //Additional information
					+ ")"
					);
			
			int pos=0;
			ins_materialFeatures.setString(++pos, m_DBitem.getType());
			ins_materialFeatures.setString(++pos, m_DBitem.getName());
			ins_materialFeatures.setString(++pos, m_DBitem.getSummary());
			ins_materialFeatures.setString(++pos, m_DBitem.getProduction_info());
			ins_materialFeatures.setString(++pos, m_DBitem.getFormula());
			
			//Crystal
			ins_materialFeatures.setInt(++pos, m_DBitem.getCompound_space_group());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getUnit_cell_volume());
			ins_materialFeatures.setString(++pos, m_DBitem.getLattice_parameters().toString());
			ins_materialFeatures.setString(++pos, m_DBitem.getLattice_angles().toString());
			ins_materialFeatures.setString(++pos, m_DBitem.getAtomic_positions().toString());
			ins_materialFeatures.setString(++pos, m_DBitem.getCrystal_info());
			
			//Thermodynamics
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getUnitCellEnergy());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getUnit_cell_formation_enthalpy());
			ins_materialFeatures.setString(++pos, m_DBitem.getEnergy_info());
			ins_materialFeatures.setString(++pos, m_DBitem.getInteratomic_potentials_info());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getMagnetic_free_energy());
			ins_materialFeatures.setString(++pos, m_DBitem.getMagnetic_free_energy_info());
			
			//Magnetics
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getUnit_cell_spin_polarization());
			ins_materialFeatures.setString(++pos, m_DBitem.getAtomic_spin_specie().toString());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getSaturation_magnetization());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getMagnetization_temperature());
			ins_materialFeatures.setString(++pos, m_DBitem.getMagnetization_info());
			ins_materialFeatures.setString(++pos, m_DBitem.getMagnetocrystalline_anisotropy_energy().toString());			
			ins_materialFeatures.setString(++pos, m_DBitem.getAnisotropy_energy_type());			
			ins_materialFeatures.setString(++pos, m_DBitem.getMagnetocrystalline_anisotropy_constants().toString());
			ins_materialFeatures.setString(++pos, m_DBitem.getKind_of_anisotropy());
			ins_materialFeatures.setString(++pos, m_DBitem.getAnisotropy_info());
			ins_materialFeatures.setString(++pos, m_DBitem.getExchange_integrals().toString());
			ins_materialFeatures.setString(++pos, m_DBitem.getExchange_info());
			ins_materialFeatures.setString(++pos, m_DBitem.getMagnetic_order());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getCurie_temperature());
			ins_materialFeatures.setString(++pos, m_DBitem.getCurie_temperature_info());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getAnisotropy_field());				
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getRemanence());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getCoercivity());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getEnergy_product());
			ins_materialFeatures.setString(++pos, m_DBitem.getHysteresis_info());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getDomain_wall_width());
			ins_materialFeatures.setString(++pos, m_DBitem.getDomain_wall_info());
			ins_materialFeatures.setBigDecimal(++pos, m_DBitem.getExchange_stiffness());
			ins_materialFeatures.setString(++pos, m_DBitem.getExchange_stiffness_info());
			
			//Additional information
			//ins_materialFeatures.setString(41, m_DBitem.getAuthors().toString());
			ins_materialFeatures.setString(++pos, m_DBitem.getReference());
			ins_materialFeatures.setString(++pos, m_DBitem.getComments());
			ins_materialFeatures.executeUpdate();
			
			if (doCommit)
				con.commit();
			
		} catch (SQLException e){
			l.error(e.getMessage());
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			l.error(sw.toString());			
			
		} finally{
			p.close(ins_materialFeatures);
			if (closeConnection) p.close(con);
		}
	}

}
