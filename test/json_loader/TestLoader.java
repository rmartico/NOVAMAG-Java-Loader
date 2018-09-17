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

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.utils.Cleaner;
import json_loader.utils.Comparators;
import json_loader.utils.Config;
import json_loader.utils.ConnectionPool;
import json_loader.utils.FileManager;

public class TestLoader {
	private static Logger l = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		l =	LoggerFactory.getLogger(TestLoader.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
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
		
		//Load configuration from res/config.json file
		Config.loadConfig();		
		FileManager.purgeDirectory(Config.TEMP_FOLDER);
	}

	@Test
	public void testParseJSONfile_FeNi_L10_v2() throws IOException, NamingException, SQLException {
		String file_name="data_for_tests/loader/FeNi_L10_v2.json";
		
		Loader loader=new Loader();
		int n = loader.parseFile(file_name);
		
		assertEquals(n,1);
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT author from authors order by 1", 2169542394L);
			Comparators.assertEqualsResultSet("SELECT author||mafId from authoring order by 1 ", 3728057379L);
			Comparators.assertEqualsResultSet("SELECT mafId||file_name||file_type||is_text||blob_content||info "
											+ "from attached_files order by 1", 634125391L);
			Comparators.assertEqualsResultSet("SELECT formula||stechiometry from molecules order by 1", 544171746L);
			Comparators.assertEqualsResultSet("SELECT mafId||type||name||"
					+ "coalesce(''||summary,'')"
					+ "||formula||"
					+ "coalesce(''||production_info,'') "
					+ "from items order by 1", 1782205737L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||compound_space_group,'')||"
					+ "coalesce(''||unit_cell_volume,'')||"
					+ "coalesce(''||lattice_parameters,'')||"
					+ "coalesce(''||lattice_angles,'')||"
					+ "coalesce(''||atomic_positions,'')||"
					+ "coalesce(''||crystal_info,'') "
					+ "from items order by 1", 2439965159L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||unit_cell_energy,'')||"
					+ "coalesce(''||unit_cell_formation_enthalpy,'')||"
					+ "coalesce(''||energy_info,'')||"
					+ "coalesce(''||interatomic_potentials_info,'')||"
					+ "coalesce(''||magnetic_free_energy,'')||"
					+ "coalesce(''||magnetic_free_energy_info,'')"
					+ "from items order by 1", 3603509917L);
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||unit_cell_spin_polarization,'')||"
					+ "coalesce(''||atomic_spin_specie,'')||"
					+ "coalesce(''||saturation_magnetization,'')||"
					+ "coalesce(''||magnetization_temperature,'')||"
					+ "coalesce(''||magnetization_info,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_energy,'')||"
					+ "coalesce(''||anisotropy_energy_type,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_constants,'')||"
					+ "coalesce(''||kind_of_anisotropy,'')||"
					+ "coalesce(''||anisotropy_info,'')||"
					+ "coalesce(''||exchange_integrals,'')||"
					+ "coalesce(''||exchange_info,'')||"
					+ "coalesce(''||magnetic_order,'')||"
					+ "coalesce(''||curie_temperature,'')||"
					+ "coalesce(''||curie_temperature_info,'')||"
					+ "coalesce(''||anisotropy_field,'')||"
					+ "coalesce(''||remanence,'')||"
					+ "coalesce(''||coercivity,'')||"
					+ "coalesce(''||energy_product,'')||"
					+ "coalesce(''||hysteresis_info,'')||"
					+ "coalesce(''||domain_wall_width,'')||"
					+ "coalesce(''||domain_wall_info,'')||"
					+ "coalesce(''||exchange_stiffness,'')||"
					+ "coalesce(''||exchange_stiffness_info,'')"
					+ "from items order by 1;",792082629L);
			
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||reference,'')||"
					+ "coalesce(''||comments,'')"
					+ "from items order by 1;",2212294583L);
			
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}

	
	@Test
	public void testParseJSONfile_examples_database_1() throws IOException, NamingException, SQLException {
		String file_name="data_for_tests/loader/examples_database_1.json";
		
		Loader loader=new Loader();
		int n = loader.parseFile(file_name);
		
		assertEquals(n,10);
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT author from authors order by 1", 2754730433L);
			Comparators.assertEqualsResultSet("SELECT author||mafId from authoring order by 1 ", 2933823148L);
			Comparators.assertEqualsResultSet("SELECT mafId||file_name||file_type||is_text||blob_content||info "
											+ "from attached_files order by 1", 1474648178L);
			Comparators.assertEqualsResultSet("SELECT formula||stechiometry from molecules order by 1", 2074024329L);
			Comparators.assertEqualsResultSet("SELECT mafId||type||name||"
					+ "coalesce(''||summary,'')"
					+ "||formula||"
					+ "coalesce(''||production_info,'') "
					+ "from items order by 1", 2239598508L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||compound_space_group,'')||"
					+ "coalesce(''||unit_cell_volume,'')||"
					+ "coalesce(''||lattice_parameters,'')||"
					+ "coalesce(''||lattice_angles,'')||"
					+ "coalesce(''||atomic_positions,'')||"
					+ "coalesce(''||crystal_info,'') "
					+ "from items order by 1", 3878567123L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||unit_cell_energy,'')||"
					+ "coalesce(''||unit_cell_formation_enthalpy,'')||"
					+ "coalesce(''||energy_info,'')||"
					+ "coalesce(''||interatomic_potentials_info,'')||"
					+ "coalesce(''||magnetic_free_energy,'')||"
					+ "coalesce(''||magnetic_free_energy_info,'')"
					+ "from items order by 1", 2221827069L);
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||unit_cell_spin_polarization,'')||"
					+ "coalesce(''||atomic_spin_specie,'')||"
					+ "coalesce(''||saturation_magnetization,'')||"
					+ "coalesce(''||magnetization_temperature,'')||"
					+ "coalesce(''||magnetization_info,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_energy,'')||"
					+ "coalesce(''||anisotropy_energy_type,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_constants,'')||"
					+ "coalesce(''||kind_of_anisotropy,'')||"
					+ "coalesce(''||anisotropy_info,'')||"
					+ "coalesce(''||exchange_integrals,'')||"
					+ "coalesce(''||exchange_info,'')||"
					+ "coalesce(''||magnetic_order,'')||"
					+ "coalesce(''||curie_temperature,'')||"
					+ "coalesce(''||curie_temperature_info,'')||"
					+ "coalesce(''||anisotropy_field,'')||"
					+ "coalesce(''||remanence,'')||"
					+ "coalesce(''||coercivity,'')||"
					+ "coalesce(''||energy_product,'')||"
					+ "coalesce(''||hysteresis_info,'')||"
					+ "coalesce(''||domain_wall_width,'')||"
					+ "coalesce(''||domain_wall_info,'')||"
					+ "coalesce(''||exchange_stiffness,'')||"
					+ "coalesce(''||exchange_stiffness_info,'')"
					+ "from items order by 1;",4127385181L);
			
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||reference,'')||"
					+ "coalesce(''||comments,'')"
					+ "from items order by 1;",1070921213L);
			
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}

	@Test
	public void testParseJSONfile_examples_database_2() throws IOException, NamingException, SQLException {
		String file_name="data_for_tests/loader/examples_database_2.json";
		
		Loader loader=new Loader();
		int n = loader.parseFile(file_name);
		
		assertEquals(n,12);
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT author from authors order by 1", 2169542394L);
			Comparators.assertEqualsResultSet("SELECT author||mafId from authoring order by 1 ", 2358028722L);
			Comparators.assertEqualsResultSet("SELECT mafId||file_name||file_type||is_text||blob_content||info "
											+ "from attached_files order by 1", 194858117L);
			Comparators.assertEqualsResultSet("SELECT formula||stechiometry from molecules order by 1", 181612618L);
			Comparators.assertEqualsResultSet("SELECT mafId||type||name||"
					+ "coalesce(''||summary,'')"
					+ "||formula||"
					+ "coalesce(''||production_info,'') "
					+ "from items order by 1", 3481940835L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||compound_space_group,'')||"
					+ "coalesce(''||unit_cell_volume,'')||"
					+ "coalesce(''||lattice_parameters,'')||"
					+ "coalesce(''||lattice_angles,'')||"
					+ "coalesce(''||atomic_positions,'')||"
					+ "coalesce(''||crystal_info,'') "
					+ "from items order by 1", 1263894005L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||unit_cell_energy,'')||"
					+ "coalesce(''||unit_cell_formation_enthalpy,'')||"
					+ "coalesce(''||energy_info,'')||"
					+ "coalesce(''||interatomic_potentials_info,'')||"
					+ "coalesce(''||magnetic_free_energy,'')||"
					+ "coalesce(''||magnetic_free_energy_info,'')"
					+ "from items order by 1", 2574669648L);
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||unit_cell_spin_polarization,'')||"
					+ "coalesce(''||atomic_spin_specie,'')||"
					+ "coalesce(''||saturation_magnetization,'')||"
					+ "coalesce(''||magnetization_temperature,'')||"
					+ "coalesce(''||magnetization_info,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_energy,'')||"
					+ "coalesce(''||anisotropy_energy_type,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_constants,'')||"
					+ "coalesce(''||kind_of_anisotropy,'')||"
					+ "coalesce(''||anisotropy_info,'')||"
					+ "coalesce(''||exchange_integrals,'')||"
					+ "coalesce(''||exchange_info,'')||"
					+ "coalesce(''||magnetic_order,'')||"
					+ "coalesce(''||curie_temperature,'')||"
					+ "coalesce(''||curie_temperature_info,'')||"
					+ "coalesce(''||anisotropy_field,'')||"
					+ "coalesce(''||remanence,'')||"
					+ "coalesce(''||coercivity,'')||"
					+ "coalesce(''||energy_product,'')||"
					+ "coalesce(''||hysteresis_info,'')||"
					+ "coalesce(''||domain_wall_width,'')||"
					+ "coalesce(''||domain_wall_info,'')||"
					+ "coalesce(''||exchange_stiffness,'')||"
					+ "coalesce(''||exchange_stiffness_info,'')"
					+ "from items order by 1;",166765711L);
			
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||reference,'')||"
					+ "coalesce(''||comments,'')"
					+ "from items order by 1;",1711679439L);
			
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}

	@Test
	public void testInsertIncludingAttachedFiles() throws IOException, NamingException, SQLException {
		String file_name="data_for_tests/loader/Fe12Ge6_#164_1.json";
		//Attached_files.setCurrPath("data_for_tests/loader/");
		
		Loader loader=new Loader();
		int n = loader.parseFile(file_name);
		
		assertEquals(n,1);

		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT mafId||file_name||file_type||is_text||blob_content||info "
											+ "from attached_files order by 1", 3296160627L);
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}

	@Test
	public void testInsertIncludingSeveralAuthors() throws IOException, NamingException, SQLException {
		String file_name="data_for_tests/loader/Fe12Ge6_#164_1.json";
		
		Loader loader=new Loader();
		int n = loader.parseFile(file_name);
		
		assertEquals(n,1);

		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT mafId||author "
											+ "from authors natural join authoring "
											+ "order by 1", 1614053957L);
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}

	@Test
	public void testInsertFeGeZip() throws IOException, NamingException, SQLException {
		String fileName = "data_for_tests/loader/FeGe.zip";		
		
		Loader loader = new Loader();							
		int n=loader.parseFile(fileName);
		assertEquals(n,5);
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT author from authors order by 1", 2169542394L);
			Comparators.assertEqualsResultSet("SELECT author||mafId from authoring order by 1 ", 234620115L);
			Comparators.assertEqualsResultSet("SELECT mafId||file_name||file_type||is_text||blob_content||coalesce(info,'') "
											+ "from attached_files order by 1", 2238034066L);
			Comparators.assertEqualsResultSet("SELECT formula||stechiometry from molecules order by 1", 3408102302L);
			Comparators.assertEqualsResultSet("SELECT mafId||type||name||"
					+ "coalesce(''||summary,'')"
					+ "||formula||"
					+ "coalesce(''||production_info,'') "
					+ "from items order by 1", 3850557665L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||compound_space_group,'')||"
					+ "coalesce(''||unit_cell_volume,'')||"
					+ "coalesce(''||lattice_parameters,'')||"
					+ "coalesce(''||lattice_angles,'')||"
					+ "coalesce(''||atomic_positions,'')||"
					+ "coalesce(''||crystal_info,'') "
					+ "from items order by 1", 2172444485L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||unit_cell_energy,'')||"
					+ "coalesce(''||unit_cell_formation_enthalpy,'')||"
					+ "coalesce(''||energy_info,'')||"
					+ "coalesce(''||interatomic_potentials_info,'')||"
					+ "coalesce(''||magnetic_free_energy,'')||"
					+ "coalesce(''||magnetic_free_energy_info,'')"
					+ "from items order by 1", 3145230331L);
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||unit_cell_spin_polarization,'')||"
					+ "coalesce(''||atomic_spin_specie,'')||"
					+ "coalesce(''||saturation_magnetization,'')||"
					+ "coalesce(''||magnetization_temperature,'')||"
					+ "coalesce(''||magnetization_info,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_energy,'')||"
					+ "coalesce(''||anisotropy_energy_type,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_constants,'')||"
					+ "coalesce(''||kind_of_anisotropy,'')||"
					+ "coalesce(''||anisotropy_info,'')||"
					+ "coalesce(''||exchange_integrals,'')||"
					+ "coalesce(''||exchange_info,'')||"
					+ "coalesce(''||magnetic_order,'')||"
					+ "coalesce(''||curie_temperature,'')||"
					+ "coalesce(''||curie_temperature_info,'')||"
					+ "coalesce(''||anisotropy_field,'')||"
					+ "coalesce(''||remanence,'')||"
					+ "coalesce(''||coercivity,'')||"
					+ "coalesce(''||energy_product,'')||"
					+ "coalesce(''||hysteresis_info,'')||"
					+ "coalesce(''||domain_wall_width,'')||"
					+ "coalesce(''||domain_wall_info,'')||"
					+ "coalesce(''||exchange_stiffness,'')||"
					+ "coalesce(''||exchange_stiffness_info,'')"
					+ "from items order by 1;",2618564273L);
			
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||reference,'')||"
					+ "coalesce(''||comments,'')"
					+ "from items order by 1;",3421846044L);			
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}		
	}

	@Test
	public void testInsertFeTaZip() throws IOException, NamingException, SQLException {
		String fileName = "data_for_tests/loader/FeTa.zip";		
		
		Loader loader = new Loader();							
		int n=loader.parseFile(fileName);
		assertEquals(n,7);
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT author from authors order by 1", 2169542394L);
			Comparators.assertEqualsResultSet("SELECT author||mafId from authoring order by 1 ", 2092041226L);
			Comparators.assertEqualsResultSet("SELECT mafId||file_name||file_type||is_text||blob_content||coalesce(''||info,'') "
											+ "from attached_files order by 1", 360256356L);
			Comparators.assertEqualsResultSet("SELECT formula||stechiometry from molecules order by 1", 2085786346L);
			Comparators.assertEqualsResultSet("SELECT mafId||type||name||"
					+ "coalesce(''||summary,'')"
					+ "||formula||"
					+ "coalesce(''||production_info,'') "
					+ "from items order by 1", 1617693418L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||compound_space_group,'')||"
					+ "coalesce(''||unit_cell_volume,'')||"
					+ "coalesce(''||lattice_parameters,'')||"
					+ "coalesce(''||lattice_angles,'')||"
					+ "coalesce(''||atomic_positions,'')||"
					+ "coalesce(''||crystal_info,'') "
					+ "from items order by 1", 2869733237L);
			Comparators.assertEqualsResultSet("SELECT ''||mafId||"
					+ "coalesce(''||unit_cell_energy,'')||"
					+ "coalesce(''||unit_cell_formation_enthalpy,'')||"
					+ "coalesce(''||energy_info,'')||"
					+ "coalesce(''||interatomic_potentials_info,'')||"
					+ "coalesce(''||magnetic_free_energy,'')||"
					+ "coalesce(''||magnetic_free_energy_info,'')"
					+ "from items order by 1", 3285782741L);
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||unit_cell_spin_polarization,'')||"
					+ "coalesce(''||atomic_spin_specie,'')||"
					+ "coalesce(''||saturation_magnetization,'')||"
					+ "coalesce(''||magnetization_temperature,'')||"
					+ "coalesce(''||magnetization_info,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_energy,'')||"
					+ "coalesce(''||anisotropy_energy_type,'')||"
					+ "coalesce(''||magnetocrystalline_anisotropy_constants,'')||"
					+ "coalesce(''||kind_of_anisotropy,'')||"
					+ "coalesce(''||anisotropy_info,'')||"
					+ "coalesce(''||exchange_integrals,'')||"
					+ "coalesce(''||exchange_info,'')||"
					+ "coalesce(''||magnetic_order,'')||"
					+ "coalesce(''||curie_temperature,'')||"
					+ "coalesce(''||curie_temperature_info,'')||"
					+ "coalesce(''||anisotropy_field,'')||"
					+ "coalesce(''||remanence,'')||"
					+ "coalesce(''||coercivity,'')||"
					+ "coalesce(''||energy_product,'')||"
					+ "coalesce(''||hysteresis_info,'')||"
					+ "coalesce(''||domain_wall_width,'')||"
					+ "coalesce(''||domain_wall_info,'')||"
					+ "coalesce(''||exchange_stiffness,'')||"
					+ "coalesce(''||exchange_stiffness_info,'')"
					+ "from items order by 1;",3093888691L);
			
			Comparators.assertEqualsResultSet("select ''||mafId||"
					+ "coalesce(''||reference,'')||"
					+ "coalesce(''||comments,'')"
					+ "from items order by 1;",1342400927L);			
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}		
	}

	@Test
	public void testParseJSONfile_confidential() throws IOException, NamingException, SQLException {
		String file_name="data_for_tests/loader/examples_database_1confidential.json";
		
		Loader loader=new Loader();
		int n = loader.parseFile(file_name);
		
		assertEquals(n,10);
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Comparators.assertEqualsResultSet("SELECT cast(mafId as varchar(10))||confidential||name"
					+ " from items where confidential", 642124489L);
						
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}
	}

	@Test
	public void testLoadedJSONisValid() throws Exception{
		String file_name="data_for_tests/loader/examples_database_1confidential.json";
		
		Loader loader=new Loader();
		int n = loader.parseFile(file_name);
				
		//Loop recover items one by one and concatenating them into an String
		ResultSet 			rs=null;
		PreparedStatement	pst_select=null;
		Connection			con=null;
		ConnectionPool 		p=null;
		
		//Create a json file simultaneusly
		file_name = "data_for_tests/loader/delete_me.json";
		File fout = new File(file_name);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("UTF-8")));
		
		String				firstRead="";
		String				secondRead="";
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			pst_select = con.prepareStatement(
					"SELECT  mafid, blob_content "+
					"FROM attached_files "+
					"ORDER BY mafId");
			
			rs = pst_select.executeQuery();
			bw.write("[\n");
			boolean writeComma = false;
			while (rs.next()){				
				
				firstRead+="MafId="+rs.getString(1)+"\n";
				String theJSON = new String(rs.getBytes(2), Charset.forName("UTF-8"));
				firstRead+=theJSON+"\n";
				firstRead+="=====================================";
				
				if (writeComma){
					bw.write(",\n");					
				}
				bw.write(theJSON);
				writeComma = true;
			}
			bw.write("\n]");
			bw.close();
		
		
			//Load the file
			rs.close();
			con.commit();
			setUp();	//CleanDB		
			n = loader.parseFile(file_name);
			
			//Loop to compare results
			
			rs = pst_select.executeQuery();
			while (rs.next()){				
				
				secondRead+="MafId="+rs.getString(1)+"\n";
				String theJSON = new String(rs.getBytes(2), Charset.forName("UTF-8"));				
				secondRead+=""+theJSON+"\n";
				secondRead+="=====================================";
			}
			
			assertEquals(firstRead.equals(secondRead), true);
			
		} catch (SQLException|IOException e){			
			throw e;
		} finally {
			con.commit();
			p.close(rs);
			p.close(pst_select);
			p.close(con);
			
			fout.delete();
		}
			
	}
	
}
