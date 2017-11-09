package json_loader.oldies;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;

public class FormulaParser implements Iterator {
	private static Logger l = null;	
	
	
	private TreeSet<ElementInFormula> m_parsedFormula;
	private Iterator<ElementInFormula> m_iterator;
	private Comparator<ElementInFormula> comp =
			(ElementInFormula e1, ElementInFormula e2) -> (e1.m_element.compareTo(e2.m_element));
			
	private int m_numAtoms;
	private int m_numElements;
	private String m_formula;
			
	public FormulaParser(String formula) throws LoaderException{
				l =	LoggerFactory.getLogger(FormulaParser.class);
				
				parseFormula(formula);
				m_iterator = m_parsedFormula.iterator();
	}

	public static void main(String[] args) {
		//Test Case 1
				
		try{
			FormulaParser fp;
			
			
			String formula="Fe3Ni2LiHO356";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			//System.out.println();
			
			formula="Fe3Ni2LiHO356Fe2"; //jmaudes Test TO_FIX... m_NumAtoms is incremented wrongly if a repeated symbol is added.
			System.out.println(formula);
			fp = new FormulaParser(formula);
			
			formula="Fe3Ni2LiFe2HO356";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			
			formula="Fe";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			
			formula="O2";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			
			formula="O";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			
			//formula="";
			formula="Fee3NO2";
			System.out.println(formula);
			fp = new FormulaParser(formula);
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
			

	}

	private TreeSet<ElementInFormula> parseFormula( String formula ) throws LoaderException{
		
		Pattern isItWellFormed = Pattern.compile("(("
								+ "([A-Z][a-z]?)"
								+ "(([1-9][0-9]*)?)" 
								+ ")+)");
		
		Matcher testIfItIsWellFormed = isItWellFormed.matcher(formula);
		testIfItIsWellFormed.find();
		
		int l1 = testIfItIsWellFormed.group().length(); 
        int l2 = formula.length();
        if ( l1 != l2 ) throw new LoaderException(LoaderException.BAD_FORMULA);
		
		Pattern elementsParseExp = Pattern.compile(
							  "([A-Z][a-z]?)"
							+ "(([1-9][0-9]*)?)" 
								);
		Matcher parseElements = elementsParseExp.matcher(formula);
		
		
		//System.out.println(formula);
		m_parsedFormula = new TreeSet<ElementInFormula>(comp);
		
		
		while (parseElements.find()){
			
			
			ElementInFormula e = new ElementInFormula();
			e.m_element = parseElements.group(1);
			String strNumElements = parseElements.group(2);
			
			int numElements;
			if (strNumElements.length()==0){
				numElements = 1;
			} else {
				numElements = (int) Integer.parseInt(strNumElements);
			}
			
			e.m_cardinality = numElements;							
			System.out.println(e.m_element+"_"+e.m_cardinality);
			
			m_parsedFormula.add(e);
			
			m_numAtoms+=numElements;
			m_numElements++;
		}	
						
		/*
		m_parsedFormula.stream().forEach(element -> System.out.print(element));
		System.out.println();
		System.out.println("TOTAL Elementos="+elementsCounter);
		System.out.println("TOTAL Atomos="+m_numAtoms);
		System.out.println("m_formula="+m_formula);
		*/
		
		//Compute m_formula
		m_formula ="";
		m_parsedFormula.stream().forEach(element -> m_formula+=element);
		
		return m_parsedFormula;
	}
	
	public int getNumAtoms(){
		return m_numAtoms;
	}
	
	public int getNumElements(){
		return m_numElements;
	}
	
	public String getFormula(){
		return m_formula;
	}

	@Override
	public boolean hasNext() {
		return m_iterator.hasNext();
	}

	@Override
	public ElementInFormula next() {
		return m_iterator.next();
	}
	
	public Iterator iterator(){
		return m_iterator;
	}
	
}
