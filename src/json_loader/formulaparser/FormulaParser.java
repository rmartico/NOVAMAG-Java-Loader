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

import java.util.Set;
import java.util.TreeMap;
import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;

/**
 * FormulaParser.java
 *  Class that parses a formula and stores it inside as an ArrayFormula
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class FormulaParser {
	
	static int NO_FORMULA=0;
	static int CHEMICAL_FORMULA=1;  //Indexes are integer or nothing (nothing means 1)
	static int STECHIOMETRY_FORMULA=2; //Stechiometric formula, all weights sum 1
	
	
	private static Logger l = LoggerFactory.getLogger(FormulaParser.class);	
	
	
	private ArrayFormula m_parsedFormula;

	private String m_chemicalFormula;
	private String m_stechiometryFormula;
	
	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 */
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

	/**
	 * Constructor of the class
	 * It parses a formula and stores it inside if correct as an ArrayFormula
	 * If the formula is not well formed it throws an exception
	 * 
	 * @param formula is the formula to be parsed
	 * @throws LoaderException
	 */
	public FormulaParser(String formula) throws LoaderException{				
				m_parsedFormula=parseFormula(formula);
	}

	/**
	 * It parses a formula and return the corresponding ArrayFormula
	 * if it's well formed.
	 * On the contrary it throws a LoaderException
	 * 
	 * @param formula
	 * @return
	 * @throws LoaderException
	 * 	if the formula is empty or
	 *  if its not a secuence of several groups of
	 *  	1 capital letter + 0 or 1 lower case letter + a number
	 *  	The number can be all integers (if it's a chemical formula) or
	 *   	some decimal numbers can appear (for stechiometric formula representation)
	 *  
	 *	If the formula turns out to be an stechiometric one, the chemical formula
	 *	is computed.
	 *	If the formula turns out to be a chemical one, the stechiometric formula
	 *	is computed.
	 */
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
	
	/**
	 * Getter for the number of atoms in the chemical formula
	 * Note: It must be an integer, but was implemented for convenience
	 * as BigDecimal to store it, because in an intermediate state
	 * it can contains a decimal (i.e., when it is a stechiometric formula
	 * and the chemical formula still wasn't computed) 
	 * @return
	 */
	public BigDecimal getNumAtoms(){
		return m_parsedFormula.getNumAtoms();
	}
	
	/**
	 * Getter that returns the number of differens species/atom symbols in the formula
	 * @return
	 */
	public int getNumElements(){
		return m_parsedFormula.getNumElements();
	}
	
	/**
	 * Getter that returns an String containing the chemical representation
	 * of the formula (i.e. all the atoms sub-indexes are integer
	 * @return
	 */
	public String getChemicalFormula(){
		return m_chemicalFormula;
	}
	
	/**
	 * Getter that returns an String containing the stoichiometric representation
	 * of the formula (i.e. all the atoms sub-indexes are decimal numbers
	 * @return
	 */
	public String getStechiometryFormula(){
		return m_stechiometryFormula;
	}
	
	/**
	 * It returns the chemical representation of the formula from the
	 * ArrayFormula as a TreeMap.
	 * Typically to use it for iteration
	 * Example:
	 * for (Map.Entry<String,BigDecimal> entry : 
	 *		m_formulaParser.getParsedChemicalFormula().entrySet()) {...
	 * 
	 * @return
	 */
	public TreeMap<String,BigDecimal> getParsedChemicalFormula(){
		return m_parsedFormula.chemicalDict;
	}
	
	/**
	 * It returns the stechiometric representation of the formula from the
	 * ArrayFormula as a TreeMap.
	 * Typically to use it for iteration
	 * Example:
	 * for (Map.Entry<String,BigDecimal> entry : 
	 *		m_formulaParser.getParsedStechiometryFormula().entrySet()) {...
	 * 
	 * @return
	 */
	public TreeMap<String,BigDecimal> getParsedStechiometryFormula(){
		return m_parsedFormula.stechiometryDict;
	}
	
	/**
	 * It returns the chemical representation of the formula from the
	 * ArrayFormula as a Set view
	 * Typically to use it for iteration
	 * fp = new FormulaParser(formulaIni);		
	 *    for (Map.Entry<String,BigDecimal> entry : fp.entrySet() ) {...
	 * 
	 * @return
	 */
	public Set<Entry<String,BigDecimal>> entrySet(){
	    return m_parsedFormula.getEntrySetChemical();
	}
	
}
