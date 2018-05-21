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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.dao.DBitem;

public class TestJSONparser {
	static private File f=null;
	static private JSONArray obj = null;
	static private String jsonTxt=null;
	
	static private String path="data_for_tests/jsonparser/";
	
	private static Logger l = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		l =	LoggerFactory.getLogger(TestJSONparser.class);
		String filename=path+"FeNi_L10_v2.json";
		
		try{
			f = new File(filename);
			
			if (f.exists()){
	            InputStream is = new FileInputStream(filename);
	            
	            jsonTxt = IOUtils.toString(is, "UTF-8");
	            //System.err.println(jsonTxt);            
	            //obj = new JSONArray(jsonTxt);
	            
			}
		} catch (Exception e){
			l.error(e.getMessage());
			throw e;
		}
	}

	/*
	 * Test for parsing a file containing one single object
	 * The database content is not tested. Only is tested the resulting DBItem
	 */
	@Test
	public void testParseJSONdbItem() {
		JSONparser jp = new JSONparser();
		obj = new JSONArray(jsonTxt);
		
		Iterator<Object> iter = obj.iterator();
		int n=0;
		while (iter.hasNext()){
			if (n>1) fail(); //Test file must have an only material
			jp.parseJSON((JSONObject) iter.next());				
			n++;
		}		
        //jp.parseJSON(obj);
        
        DBitem item = jp.getItem();
        
        assertEquals( "T", item.getType() );
        assertEquals( "AGA, crystal, energy, magnetization, anisotropy, exchange, Tc, domain wall width, exchange stiffness",
        		item.getSummary());
        
        assertEquals( "Fe1Ni1", item.getFormula());
        assertEquals("obtained by Adaptive Genetic Algorithm,  software USPEX+VASP",
        		item.getProduction_info());
        
        assertEquals( item.getCompound_space_group(), new Integer(123));
        assertEquals( item.getUnit_cell_volume().toString(), "22.7138");        
        assertEquals( item.getLattice_parameters().toString(), new JSONArray("[ 2.518, 2.518, 3.582 ]").toString());
        assertEquals( item.getLattice_angles().toString(), new JSONArray("[90.000, 90.000, 90.000]").toString());
        assertEquals( item.getAtomic_positions().toString(), new JSONArray("[\"Fe1\", 0.0, 0.0, 0.0, \"Ni1\",  0.5, 0.5, 0.5 ]").toString());
        assertEquals( item.getCrystal_info(), "software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000,Pressure=0.0 kbar,temperature=0K");
        
        assertEquals( item.getUnitCellEnergy().toString(), "-13.892258");        
        assertEquals( item.getUnit_cell_formation_enthalpy().toString(), "-0.135758");
        assertEquals( item.getEnergy_info(), "software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000, Pressure=0.0 kbar,temperature=0K");
        assertEquals( item.getInteratomic_potentials_info(), "https://www.ctcms.nist.gov/potentials/Fe-Ni.html");
        assertEquals( item.getMagnetic_free_energy().toString(), "-13.892258");
        assertEquals( item.getMagnetic_free_energy_info(), "software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000, Pressure=0.0 kbar, temperature=0K");
        assertEquals( item.getUnit_cell_spin_polarization().toString(),"3.33");
        assertEquals( item.getAtomic_spin_specie().toString(), "[\"Fe1\",2.659,\"Ni1\",0.671]");
        assertEquals( item.getSaturation_magnetization().toString(), "1.71");
        assertEquals( item.getMagnetization_temperature().toString(),"0.0");
        assertEquals( item.getMagnetization_info(),"software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000, Pressure=0.0 kbar");
        assertEquals( item.getMagnetocrystalline_anisotropy_energy().toString(),
        		"[0,0,1,-13.9401908,1,0,1,-13.9401299,1,0,0,-13.94008791,0,1,0,-13.94008803]");
        assertEquals( item.getAnisotropy_energy_type(), "U");
        assertEquals( item.getMagnetocrystalline_anisotropy_constants().toString(),"[0.73,0]");
        assertEquals( item.getKind_of_anisotropy(),"A");
        assertEquals( item.getAnisotropy_info(),
        		"software VASP, including spin-orbit coupling, 2816 k-points in IBZ, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000, Pressure=0.0 kbar");
        assertEquals( item.getExchange_integrals().toString(),
        		"[\"Fe-Fe\",2.22119,1.35194,0.38785,0.50683,-0.67064,-0.4195,"
        		+ "\"Ni-Ni\",0.29772,-0.04463,0.04442,0.04059,-0.01808,-0.00461,"
        		+ "\"Fe-Ni\",1.27737,0.08671,0.03079,-0.02957,0.01218,0.0091]");
        assertEquals( item.getExchange_info(),
        		"software Fleur, spin spirals (Fourier transform), kmax=4.1 a.u., 1960 k-points and 463 q-points, PBE, Pressure=0.0 kbar");
        assertEquals( item.getMagnetic_order(),"ferromagnet");
        assertEquals( item.getCurie_temperature().toString(), "800.0" );
        assertEquals( item.getCurie_temperature_info(), 
        		"software UppASD, Atomistic Spin Dynamics, system size 40x40x40 spins, periodic boundary conditions");
        assertEquals( item.getAnisotropy_field().toString(),"1.07");
        assertEquals( item.getRemanence().toString(), "1.42");
        assertEquals( item.getCoercivity().toString(), "1.75");
        assertEquals( item.getEnergy_product().toString(), "352.23");
        assertEquals( item.getHysteresis_info(), "software OOMMF, Micromagnetics, bulk");
        assertEquals( item.getDomain_wall_width().toString(),"14.4");
        assertEquals( item.getDomain_wall_info(),
        		"Atomistic Spin Dynamics, system size 300x40x40 spins, temperature=5K");
        assertEquals( item.getExchange_stiffness().toString(), "15.3");
        assertEquals( item.getExchange_stiffness_info(),
        		 "It was calculated using well-known formula where A is the exchange stiffness");
        
        assertEquals( item.getAuthors().toString(),"[\"ICCRAM\"]");
        assertEquals( item.getReference(), null);
        assertEquals( item.getComments(), null);
        assertEquals( item.getAttached_files(), null);
        assertEquals( item.getAttached_files_info(), null);
        
     }
	
}
