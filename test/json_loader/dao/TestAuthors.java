package json_loader.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.utils.Cleaner;
import json_loader.utils.Comparators;
import json_loader.utils.ConnectionPool;

public class TestAuthors {
	private static Logger l = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		l =	LoggerFactory.getLogger(TestAuthors.class);
	}
	
	@Test
	public void testInsert() throws NamingException, IOException {
		String		js ="[\"Autor1\",\"Autor2\",\"Autor3\",\"Autor4\"]";
		JSONArray	ja = new JSONArray(js);
		
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
			
			Authors 	a  = new Authors(key);
			a.setAuthors(ja);
			a.insert(con, true);
			
			Comparators.assertEqualsResultSet("SELECT mafId||author FROM authoring order by 1", 770476957L);
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}

}
