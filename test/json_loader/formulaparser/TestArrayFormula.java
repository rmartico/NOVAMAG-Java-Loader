package json_loader.formulaparser;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import json_loader.error_handling.LoaderException;
import json_loader.formulaparser.ArrayFormula;
import json_loader.utils.Comparators;

import java.math.BigDecimal;

public class TestArrayFormula {
	private ArrayFormula water;
	private ArrayFormula sulfuric;
	private ArrayFormula xyz;
	

	
	@Before
	public void setUp() throws Exception {
		water = new ArrayFormula(FormulaParser.CHEMICAL_FORMULA);
		water.insertElement("O", "");
		water.insertElement("H", "2");
		
		sulfuric = new ArrayFormula(FormulaParser.STECHIOMETRY_FORMULA);
		sulfuric.insertElement("S", "0.143");
		sulfuric.insertElement("O", "0.571");
		sulfuric.insertElement("H", "0.286");
		
		xyz = new ArrayFormula(FormulaParser.STECHIOMETRY_FORMULA);
		//unordered
		xyz.insertElement("Z", "0.35");
		xyz.insertElement("Y", "0.45");
		xyz.insertElement("X", "0.2");		
		
	}

	@Test
	public void testArrayXYZ() throws LoaderException{
		xyz.checkAndNormalize();
		//System.err.println(xyz);
		
		int i=0;
		for (Map.Entry<String,BigDecimal> entry : xyz.getParsedChemicalFormula().entrySet()){
			String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            switch (i){
            	case 0:
	            	assertEquals(key,"X");
	            	Comparators.assertEqualsBD(val, 4);
	            	//assertTrue(val.compareTo(new BigDecimal("0.2"))==0);
	            	break;
	            	
            	case 1:
	            	assertEquals(key,"Y");
	            	Comparators.assertEqualsBD(val, 9);
	            	//assertTrue(val.compareTo(new BigDecimal("0.35"))==0);
	            	break;
	            	
            	case 2:
	            	assertEquals(key,"Z");
	            	Comparators.assertEqualsBD(val, 7);
	            	//assertTrue(val.compareTo(new BigDecimal("0.45"))==0);
	            	break;
	            	
	            default:
	            	fail();
            }	
            i++;
		}
	}
	
	
	@Test
	public void testArrayWater() throws LoaderException{
		water.checkAndNormalize();
		
		int i=0;
		for (Map.Entry<String,BigDecimal> entry : water.getParsedStechiometryFormula().entrySet()){
			String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            switch (i){
            	case 0:
	            	assertEquals(key,"H");
	            	Comparators.assertEqualsBD(val, 0.667);
	            	//assertTrue(val.compareTo(new BigDecimal("0.667"))==0);
	            	break;
	            	
            	case 1:
	            	assertEquals(key,"O");
	            	Comparators.assertEqualsBD(val, 0.333);
	            	//assertTrue(val.compareTo(new BigDecimal("0.333"))==0);
	            	break;
	            	
	            default:
	            	fail();
            }	
            i++;
		}
	}
	
	@Test
	public void testInsertElement() throws LoaderException {
		water.insertElement("Na", "2");
		//water.addTo("Na", "3");
		water.insertElement("Li", "");
		
		//System.err.println(water.getFormula());
		//assertTrue(water.getFormula().equals("H2LiNa5O"));
		assertTrue(water.getFormula(FormulaParser.CHEMICAL_FORMULA).equals("H2LiNa2O"));
		
		int i=0;
		for (Map.Entry<String,BigDecimal> entry : water.getParsedChemicalFormula().entrySet()){
			String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            switch (i){
            	case 0:
	            	assertEquals(key,"H");
	            	assertTrue(val.compareTo(new BigDecimal("2"))==0);
	            	break;
	            	
            	case 1:
	            	assertEquals(key,"Li");
	            	assertTrue(val.compareTo(BigDecimal.ONE)==0);
	            	break;
	            	
            	case 2:
	            	assertEquals(key,"Na");
	            	assertTrue(val.compareTo(new BigDecimal("2"))==0);
	            	break;
	            	
            	case 3:
	            	assertEquals(key,"O");
	            	assertTrue(val.compareTo(BigDecimal.ONE)==0);
	            	break;
	            	
	            default:
	            	fail();
            }	
            i++;
		}
	}
	
	@Test		//(expected = LoaderException.class )
	public void testInsertRepeatedelement() throws LoaderException {
		water.insertElement("Na", "2");
		
		try {
			water.insertElement("Na", "3");
		} catch (LoaderException e){
			if ( (e instanceof LoaderException) &&
				 ((LoaderException) e).getErrorCode()==LoaderException.REPEATED_ELEMENT_IN_FORMULA
				){
				assertTrue(true);
				return;
			} else
				fail("A repeated element was not detected");
		}
		
		fail("A repeated element was not detected");
	}
	
	@Test
	public void testToString() {
		//System.err.println(water.toString());		
		assertTrue(water.toString().equals("{H=2, O=1}"));
	}
	
	@Test
	public void testGetFormula() throws LoaderException {
		//System.err.println(water.getFormula(FormulaParser.CHEMICAL_FORMULA));
		assertTrue(water.getFormula(FormulaParser.CHEMICAL_FORMULA).equals("H2O"));		
		water.checkAndNormalize();
		
		//System.err.println(water.getFormula(FormulaParser.STECHIOMETRY_FORMULA));
		assertTrue(water.getFormula(FormulaParser.STECHIOMETRY_FORMULA).equals("H0.667O0.333"));
	}
	
	@Test
	public void testEntrySetChemical() {
		
		String formula="";
		for (Map.Entry<String,BigDecimal> entry : water.getEntrySetChemical() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            formula+=key+"_"+val+"\t";
            //System.out.println(key+"=>"+val);
        }
		//System.out.println(formula);
		assertTrue( formula.equals("H_2\tO_1\t"));
	}	
	
	@Test
	public void testGetNumAtoms() {
		assertTrue(
				water.getNumAtoms().compareTo(new BigDecimal(3))==0
		);
	}
	
	@Test
	public void testGetNumElements() {
		assertEquals(water.getNumElements(),2);
	}
	
	@Test
	public void testWaterNormalized() throws LoaderException {
		water.checkAndNormalize();
		
		for (Map.Entry<String,BigDecimal> entry : water.getEntrySetStechiometry() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            if (key=="H")
            	assertTrue( val.compareTo(new BigDecimal("0.667"))==0);
            else if (key=="O")
            	assertTrue( val.compareTo(new BigDecimal("0.333"))==0);
            else 
            	fail();
        }		
	}
	
	@Test
	public void testSulfuric(){
		//System.err.println(sulfuric.getFormula(FormulaParser.STECHIOMETRY_FORMULA));
		try{
			sulfuric.checkAndNormalize();
		} catch (LoaderException e){
			if (e.getErrorCode()==LoaderException.CENTESIMAL_FORMULA_DOES_NOT_SUM_ONE)
				fail();
		}
		
		
		int i=0;
		for (Map.Entry<String,BigDecimal> entry : sulfuric.getParsedChemicalFormula().entrySet()){
			String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            switch (i){
            	case 0:
	            	assertEquals(key,"H");
	            	Comparators.assertEqualsBD( val, 2);
	            	//assertTrue(val.compareTo(new BigDecimal("0.286"))==0);
	            	break;
	            	
            	case 1:
	            	assertEquals(key,"O");
	            	Comparators.assertEqualsBD( val, 4);
	            	//assertTrue(val.compareTo(new BigDecimal("0.571"))==0);
	            	break;
	            	
            	case 2:
	            	assertEquals(key,"S");
	            	Comparators.assertEqualsBD( val, 1);
	            	//assertTrue(val.compareTo(new BigDecimal("0.143"))==0);
	            	break;
	            	
	            default:
	            	fail();
            }	
            i++;
		}
		
		try{
			sulfuric.insertElement("K", new BigDecimal("0.2"));
			sulfuric.checkAndNormalize();
			fail();
		} catch (LoaderException e){
			if (e.getErrorCode()!=LoaderException.CENTESIMAL_FORMULA_DOES_NOT_SUM_ONE)
				fail();
		}
	}
		
	@Test
	public void testComputeChemical() throws LoaderException{
		sulfuric = new ArrayFormula(FormulaParser.STECHIOMETRY_FORMULA);
		sulfuric.insertElement("S", new BigDecimal(0.143+0.005));
		sulfuric.insertElement("O", new BigDecimal(0.571-0.006));
		sulfuric.insertElement("H", new BigDecimal(0.286+0.001));
		
		sulfuric.computeChemical();
		//System.out.println(sulfuric);
		for (Map.Entry<String,BigDecimal> entry : sulfuric.getParsedChemicalFormula().entrySet() ) {
			String m_key = entry.getKey();
			switch (m_key) {
				case "H":
					Comparators.assertEqualsBD(entry.getValue(),2);
					break;
				case "O":
					Comparators.assertEqualsBD(entry.getValue(),4);
					break;
				case "S":
					Comparators.assertEqualsBD(entry.getValue(),1);
					break;
			}
			
		}
		
		sulfuric = new ArrayFormula(FormulaParser.STECHIOMETRY_FORMULA);
		sulfuric.insertElement("S", new BigDecimal(0.143+0.005));
		sulfuric.insertElement("O", new BigDecimal(0.571-0.009));
		sulfuric.insertElement("H", new BigDecimal(0.286+0.004));
		sulfuric.computeChemical();
		
		//System.out.println(sulfuric);
		for (Map.Entry<String,BigDecimal> entry : sulfuric.getParsedChemicalFormula().entrySet() ) {
			String m_key = entry.getKey();
			switch (m_key) {
				case "H":
					Comparators.assertEqualsBD(entry.getValue(),2);
					break;
				case "O":
					Comparators.assertEqualsBD(entry.getValue(),4);
					break;
				case "S":
					Comparators.assertEqualsBD(entry.getValue(),1);
					break;
			}
			
		}
		
		
		//assert(sulfuric.toString(),)
		/*
		h=h.add(new BigDecimal("0.004"));
		s=s.add(new BigDecimal("0.005"));
		o=o.subtract(new BigDecimal("0.009"));
		
		m_gcd.computeChemical();
		
		System.out.println(m_gcd);
		*/
	}
}
