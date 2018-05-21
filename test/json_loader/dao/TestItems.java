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

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;

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
import json_loader.utils.Comparators;
import json_loader.utils.ConnectionPool;

public class TestItems {
	private static Logger l = null;
	static private ConnectionPool p;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		l =	LoggerFactory.getLogger(TestItems.class);
	}
	
	@Before
	public void setUp() throws Exception {
		p = ConnectionPool.getInstance();
		Cleaner.CleanDB();
	}


	@Test
	public void testInsertOK() throws Exception {//Test without using DBItem.insert
		Connection con=null;
		Molecule m_molecule=null;
		
		Items m_items=null;
		
		PreparedStatement pst_INS_atoms=null; 
		String formula="Fe1Ni1";
		String name="Item of novamag database";
		
		try{
			Cleaner.insertAtom("Fe");
			Cleaner.insertAtom("Ni");
			con = p.getConnection();
		
			m_molecule=new Molecule();
			m_molecule.setFormula(formula);
			m_molecule.insert(con, true);
						
			int n=0;
			//JSONparser jp = new JSONparser();
			
			String fileName = "data_for_tests/dao.items/FeNi_L10_v2.json";
			InputStream is = new FileInputStream(fileName);
			String jsonTxt = IOUtils.toString(is, "UTF-8");	
			
			JSONparser jp = new JSONparser();
						
			Iterator<Object> iter = new JSONArray(jsonTxt).iterator();
			while (iter.hasNext()){
				if (n>1) fail(); //Test file must have an only material
				jp.parseJSON((JSONObject) iter.next());				
				n++;
			}			

			DBitem m_DBitem = jp.getItem();
			String m_formula=m_molecule.getFormula();
			m_DBitem.setFormula(m_formula);
						
			m_items = new Items();
			m_items.setDBItem(m_DBitem);
			
			m_items.insert(con, true);	
			
			String query="SELECT type, name, summary, production_info, formula,"
					+ " compound_space_group, unit_cell_volume, " //Chemistry
					+ "lattice_parameters, lattice_angles, atomic_positions, crystal_info, "
					+ "unit_cell_energy, unit_cell_formation_enthalpy, "	//Thermodynamics
					+ "energy_info, interatomic_potentials_info, "
					+ "magnetic_free_energy, magnetic_free_energy_info, "
					+ "unit_cell_spin_polarization, atomic_spin_specie, " //Magnetics
					+ "saturation_magnetization, magnetization_temperature, "
					+ "magnetization_info, magnetocrystalline_anisotropy_energy, "
					+ "anisotropy_energy_type, magnetocrystalline_anisotropy_constants, "
					+ "kind_of_anisotropy, anisotropy_info, exchange_integrals, exchange_info,"
					+ "magnetic_order, curie_temperature, curie_temperature_info, "
					+ "anisotropy_field, remanence, coercivity, energy_product, "
					+ "hysteresis_info, domain_wall_width, domain_wall_info, "
					+ "exchange_stiffness, exchange_stiffness_info, "
					+ "reference, comments" //Additional information
					//+ "authors, reference, comments" //Additional information
					+ " FROM items "
					+ " WHERE name='"+name+"'";
			Comparators.assertEqualsResultSet(query, 3187964512L);
			
		} catch (Exception e){
			p.undo(con);
			l.error(e.getMessage());
			throw e;
		} finally {
			p.close(pst_INS_atoms);
			p.close(con);
		}
		
	}			
}
