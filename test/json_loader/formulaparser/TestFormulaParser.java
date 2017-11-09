package json_loader.formulaparser;

import static org.junit.Assert.*;

import java.util.Map;
import java.math.BigDecimal;

import org.junit.Test;

import json_loader.error_handling.LoaderException;
import json_loader.utils.Comparators;


public class TestFormulaParser {
	FormulaParser fp;	

	@Test
	public void	Fe3Ni2LiHO35()throws Exception{
		String formula="Fe3Ni2LiHO35";
		//System.out.println(formula);
		fp = new FormulaParser(formula);
		//System.out.println(fp.getFormula());
		
		assertEquals(fp.getChemicalFormula(),"Fe3HLiNi2O35");
		assertEquals(fp.getNumElements(),5);
		assertTrue(fp.getNumAtoms().compareTo(
				new BigDecimal(3+1+1+2+35))==0);		
		
		//System.err.println(fp.getStechiometryFormula());
		assertEquals(fp.getStechiometryFormula(), 
				"Fe0.071H0.024Li0.024Ni0.048O0.833");
		         
	}
	
	
	@Test
	public void	Fe3Ni2LiHO356()throws Exception{
		String formula="Fe3Ni2LiHO356";
		//System.out.println(formula);
		fp = new FormulaParser(formula);
		//System.out.println(fp.getFormula());
		
		assertEquals(fp.getChemicalFormula(),"Fe3HLiNi2O356");
		assertEquals(fp.getNumElements(),5);
		assertTrue(fp.getNumAtoms().compareTo(
				new BigDecimal(3+1+1+2+356))==0);		
		
		//System.out.println(fp.getCentesimalFormula());
		assertEquals(fp.getStechiometryFormula(), 
				"Fe0.008H0.003Li0.003Ni0.006O0.981");
	}

	@Test
	public void H2Ocentesimal() throws Exception{
		String formula="H0.67O0.33";
		fp = new FormulaParser(formula);
		
		//System.out.println(fp.getChemicalFormula());
		assertEquals(fp.getChemicalFormula(),"H2O");
		
		//System.out.println(fp.getStechiometryFormula());
		assertEquals(fp.getStechiometryFormula(),formula);
		
		//System.out.println(fp.getNumElements());
		assertEquals(fp.getNumElements(),2);
		
		System.out.println(fp.getNumAtoms());
		Comparators.assertEqualsBD(fp.getNumAtoms(), 3);
		
	}
	
	@Test	//(expected = LoaderException.class )
	public void	Fe3Ni2LiHO356Fe2()throws Exception{
		String formula="Fe3Ni2LiHO356Fe2";
		//System.out.println(formula);
		try{
			fp = new FormulaParser(formula);
			//fp.getFormula();
		} catch (LoaderException e){
			if (	(e instanceof LoaderException) &&
					((LoaderException) e).getErrorCode()==
						LoaderException.REPEATED_ELEMENT_IN_FORMULA
				){
				assertTrue(true);
				return;
			} else
				fail("A repeated element was not detected");
		}
		
		fail("A repeated element was not detected");
		
		/*		
		assertEquals(fp.getFormula(),"Fe5HLiNi2O356");
		assertEquals(fp.getNumElements(),5);
		assertEquals(fp.getNumAtoms(),5+1+1+2+356);
		*/		
	}
	
	@Test		//(expected = LoaderException.class )
	public void	Fe3Ni2LiFe2HO356()throws LoaderException{
		String formula="Fe3Ni2LiFe2HO356";
		//System.out.println(formula);		
		//System.out.println(fp.getFormula());
		
		try{
			fp = new FormulaParser(formula);
			//fp.getFormula();
		} catch (LoaderException e){
			if ( (e instanceof LoaderException) &&
				 ((LoaderException)e).getErrorCode()==
					LoaderException.REPEATED_ELEMENT_IN_FORMULA
				){
				assertTrue(true);
				return;
			} else
				fail("A repeated element was not detected");
		}
		
		fail("A repeated element was not detected");
		
		/*
		assertEquals(fp.getFormula(),"Fe5HLiNi2O356");
		assertEquals(fp.getNumElements(),5);
		assertEquals(fp.getNumAtoms(),5+1+1+2+356);
		*/		
	}
	
	@Test
	public void	Fe()throws Exception{
		String formula="Fe";
		//System.out.println(formula);
		fp = new FormulaParser(formula);
		//System.out.println(fp.getChmemicalFormula()+"=Fe");
		
		assertEquals(fp.getChemicalFormula(),"Fe");
		assertEquals(fp.getStechiometryFormula(),"Fe1.000");
		
		assertEquals(fp.getNumElements(),1);
		//System.out.println(fp.getNumAtoms());
		assertTrue(fp.getNumAtoms().compareTo(BigDecimal.ONE)==0);		
	}
	
	@Test
	public void	O2()throws Exception{
		String formula="O2";
		//System.out.println(formula);
		fp = new FormulaParser(formula);
		//System.out.println(fp.getFormula());
		
		assertEquals(fp.getChemicalFormula(),"O2");
		assertEquals(fp.getStechiometryFormula(),"O1.000");
		
		assertEquals(fp.getNumElements(),1);
		assertTrue(fp.getNumAtoms().compareTo(new BigDecimal("2"))==0);			
	}
	
	@Test(expected = LoaderException.class )
	public void	Fee3NO2()throws Exception{
				
		String formula="Fee3NO2";
		//System.out.println(formula);
		
		try{
			fp = new FormulaParser(formula);
		} catch (LoaderException e){
			assertEquals(e.getMessage(), "The formula is not well formed" );
			throw e;
		}
			
		fail("LoaderException was not thrown");
				
	}
	
	
	private void badFormula(String formula) throws Exception{
		try{
			fp = new FormulaParser(formula);
		} catch (LoaderException e){
			assertEquals(e.getMessage(), "The formula is not well formed" );
			throw e;
		}
		
		fail("LoaderException was not thrown");
	}
	
	@Test(expected = LoaderException.class )
	public void	emptyFormula()throws Exception{
		badFormula("");		
	} 
	
	@Test			//(expected = LoaderException.class )
	public void	notAllowedCharInFormula()throws Exception{
		String badFormulas[]={ "F_", "F$", "F ", "F\t", "F\n", "F%" };
		int i=0;
		boolean flag=true;
		
		while (flag){
			//System.out.println(badFormulas[i]);
			try {
				badFormula(badFormulas[i]);
				flag=false;
			} catch (Exception e){
				if (e instanceof LoaderException){
					if (i<badFormulas.length){
						i++;
						flag = i<badFormulas.length;
					} else throw e;
				} else throw e;
			}
		}
	}
		
	@Test(expected = LoaderException.class )
	public void	firstCharCapital()throws Exception{
		badFormula("f");		
	}
	
	@Test(expected = LoaderException.class )
	public void	numberFollowedByLower()throws Exception{
		badFormula("Fe3a4");		
	}
	
	//For testing the set for ordered iteration
	@Test
	public void testEntrySet() throws Exception {
		String formulaIni="Fe3Ni2LiHO356";
		fp = new FormulaParser(formulaIni);
		
		String formula="";
		for (Map.Entry<String,BigDecimal> entry : fp.entrySet() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            formula+=key+"_"+val+"\t";
            //System.out.println(key+"=>"+val);
        }
		//System.out.println(formula);
		assertEquals( formula.toString(), "Fe_3\tH_1\tLi_1\tNi_2\tO_356\t");
	}

}
