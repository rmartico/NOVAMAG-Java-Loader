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
