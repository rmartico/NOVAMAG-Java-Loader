package json_loader.formulaparser;

import java.util.Set;
import java.util.TreeMap;
import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;

public class FormulaParser {
	
	static int NO_FORMULA=0;
	static int CHEMICAL_FORMULA=1;  //Indexes are integer or nothing (nothing means 1)
	static int STECHIOMETRY_FORMULA=2; //Stechiometric formula, all weights sum 1
	
	
	private static Logger l = null;	
	
	
	private ArrayFormula m_parsedFormula;

	private String m_chemicalFormula;
	private String m_stechiometryFormula;
			
	public FormulaParser(String formula) throws LoaderException{
				l =	LoggerFactory.getLogger(FormulaParser.class);
				
				m_parsedFormula=parseFormula(formula);
	}

	public static void main(String[] args) {
		//Test Case 1
				
		try{
			FormulaParser fp;
						
			String formula="Fe3Ni2LiHO35";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.out.println(fp.getChemicalFormula());
			System.out.println(fp.getStechiometryFormula());
			
			formula="Fe3Ni2LiHO356";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.err.println(fp.getChemicalFormula());
			System.err.println(fp.getStechiometryFormula());
			
			
			/*
			formula="Fe3Ni2LiHO356Fe2"; //jmaudes Test TO_FIX... m_NumAtoms is incremented wrongly if a repeated symbol is added.
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.out.println(fp.getFormula());
			
			
			formula="Fe3Ni2LiFe2HO356";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.out.println(fp.getChmemicalFormula());
			System.out.println(fp.getStechiometryFormula());
			*/		
			
			formula="Fe";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.out.println(fp.getChemicalFormula());
			
			formula="O2";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.out.println(fp.getChemicalFormula());
			
			formula="O";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.out.println(fp.getChemicalFormula());
			
			//formula="";
			/*
			formula="Fee3NO2";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			*/
			formula="H0.67O0.33";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			System.out.println(fp.getChemicalFormula());
			System.out.println(fp.getStechiometryFormula());
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	private ArrayFormula parseFormula( String formula ) throws LoaderException{
		if ( formula.equals("") ) throw new LoaderException(LoaderException.BAD_FORMULA);
		
		String chemicalFormulaPattern = "([A-Z][a-z]?)"
										+ "(([1-9][0-9]*)?)";
		
		String stechiometryFormulaPattern = "([A-Z][a-z]?)"
										+ "(0\\.[0-9]*)";
		
		int typeOfFormula = FormulaParser.NO_FORMULA;
		
		Pattern isItWellFormedChemical = Pattern.compile("(("
											+chemicalFormulaPattern
											+ ")+)");		
		
		Matcher testIfItIsWellFormedChemical = isItWellFormedChemical.matcher(formula);
		testIfItIsWellFormedChemical.find();
		
		int l1=-1;
		int l2 = formula.length();
		
		try{
			l1 = testIfItIsWellFormedChemical.group().length();
		} catch (IllegalStateException e){		
			l.error(e.getMessage());
		}
		        
        if ( l1 == l2 ){
        	typeOfFormula = FormulaParser.CHEMICAL_FORMULA;
        } else {
        	//try stechiometry formula
			
			Pattern isItWellFormedstechiometry = Pattern.compile("(("
					+stechiometryFormulaPattern
					+ ")+)");		
			
			Matcher testIfItIsWellFormedStechiometry = isItWellFormedstechiometry.matcher(formula);
			testIfItIsWellFormedStechiometry.find();
			
			try{ 
				l1 = testIfItIsWellFormedStechiometry.group().length();
			} catch (IllegalStateException e){
				//System.err.println(e);	
			}
			
			if ( l1 != l2 )
				throw new LoaderException(LoaderException.BAD_FORMULA);
			else
				typeOfFormula = FormulaParser.STECHIOMETRY_FORMULA;
        }        
        
    	Pattern elementsParseExp = null;
    	Matcher parseElements = null;
    	
		if (typeOfFormula==FormulaParser.CHEMICAL_FORMULA){        
			elementsParseExp = Pattern.compile( chemicalFormulaPattern );
		} else {
			elementsParseExp = Pattern.compile( stechiometryFormulaPattern );
		}
		
		parseElements = elementsParseExp.matcher(formula);
		//System.out.println(formula);
		m_parsedFormula = new ArrayFormula(typeOfFormula);
		
		
		while (parseElements.find()){
			
			String theElement = parseElements.group(1);
			String strNumElements = parseElements.group(2);
			
			m_parsedFormula.insertElement(theElement, strNumElements);
			
		}	
		
		m_parsedFormula.checkAndNormalize();
		m_chemicalFormula = m_parsedFormula.getFormula(FormulaParser.CHEMICAL_FORMULA);
		m_stechiometryFormula = m_parsedFormula.getFormula(FormulaParser.STECHIOMETRY_FORMULA); 
	
		return m_parsedFormula;
	}
	
	public BigDecimal getNumAtoms(){
		return m_parsedFormula.getNumAtoms();
	}
	
	public int getNumElements(){
		return m_parsedFormula.getNumElements();
	}
	
	public String getChemicalFormula(){
		return m_chemicalFormula;
	}
	
	public String getStechiometryFormula(){
		return m_stechiometryFormula;
	}
	
	public TreeMap<String,BigDecimal> getParsedChemicalFormula(){
		return m_parsedFormula.chemicalDict;
	}
	public TreeMap<String,BigDecimal> getParsedStechiometryFormula(){
		return m_parsedFormula.stechiometryDict;
	}
	
	public Set<Entry<String,BigDecimal>> entrySet(){
	    return m_parsedFormula.getEntrySetChemical();
	}
	
}
