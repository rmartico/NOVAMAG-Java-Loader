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

package json_loader.formulaparser;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

public class TestFractFormula {
	FractFormula ff;
	TreeMap<String, Fraction> comp;

	
	@Before
	public void setUp() throws Exception {
		comp = new TreeMap<String, Fraction>();
		
		comp.put("X", new Fraction(1,6));
		comp.put("Y", new Fraction(1,9));
		comp.put("Z", new Fraction(1,8));
		
		ff = new FractFormula( "XYZ", comp);
		
	}


	@Test
	public void testGetCommonDen() {
		assertEquals(ff.getCommonDen(),72);		
	}

	@Test
	public void testGetComponents() {
		
		int i = 0;
		for(Map.Entry<String, Fraction> entry: ff.getComponents().entrySet()){
			String key = entry.getKey();
			Fraction f = entry.getValue();
			
			switch (i){
				case 0:
					assertEquals("X",key);
					assertEquals("12/72",f.toString());
					break;
				case 1:		
					assertEquals("8/72",f.toString());
					break;
				case 2:
					assertEquals("9/72",f.toString());
					break;
				case 4:
					fail();
			}
			i++;
		}
	}

}
