package json_loader.oldies;

import java.util.Comparator;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Moleculesv2 {
	private class ElementInFormula{
		public String m_element;
		public int    m_cardinality;		
		
		public String toString(){
			return m_element+"_"+m_cardinality;
		}
		
	}
	
	//Stetes for parsing formulas
	private enum State{
		INI, MAY, MIN, NUM
	}
	
	private static Logger l = null;	
	
	private String m_formula;
	private int m_numAtoms;
	
	private TreeSet<ElementInFormula> m_parsedFormula;
	private Comparator<ElementInFormula> comp =
			(ElementInFormula e1, ElementInFormula e2) -> (e1.m_element.compareTo(e2.m_element));

	public static void main(String[] args) {
		
		Moleculesv2 m = new Moleculesv2();
		try{
			m.parseFormula("Fe3Ni2LiHO356");
			//System.out.println();
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}
	
	public Moleculesv2(){
		l =	LoggerFactory.getLogger(Moleculesv2.class);
	}
	
	
	public String toString(){
		return "Formula:"+m_formula;		
	}
	
	public int getNumAtoms(){
		return m_numAtoms;
	}
	
	private void parseFormula( String formula ) throws LoaderException{
		
		int i=0;
		String token;
		State state = State.INI;		
		State nextState;
		
		int elementsCounter=0;
		
		String currentElement="";
		int    currentNumOfAtoms=0;
		
		m_parsedFormula = new TreeSet<ElementInFormula>(comp);
		
		while (i<formula.length()){
			token = ""+formula.charAt(i);
			
			if ((token).matches("[A-Z]"))				
				nextState=State.MAY;
			else
				if ((token).matches("[a-z]"))					
					nextState=State.MIN;
				else
					if ((token).matches("[0-9]"))						
						nextState=State.NUM;
					else
						throw (new LoaderException(LoaderException.NOT_ALLOWED_CHAR_IN_FORMULA));
			
			System.out.println(token+"=>"+nextState);			
			
			if (state==State.INI && nextState!=State.MAY)
				throw (new LoaderException(LoaderException.FIRST_CHAR_IN_FORMULA_HAS_TO_BE_A_Z));
			else				
				if (state==State.INI)
					currentElement=token;
				else {
					if (state==State.MAY){			
						if (nextState==State.MAY){
							ElementInFormula e = new ElementInFormula();
							e.m_element = currentElement;
							e.m_cardinality = 1;							
							m_parsedFormula.add(e);
							
							currentElement=token;
							currentNumOfAtoms=0;
							
							elementsCounter++;
						}
						
						if (nextState==state.MIN){
							currentElement+=token;
						}						
						
						if (nextState==State.NUM){
							currentNumOfAtoms=(int)Integer.parseInt(token);
						}				
						
					} else {					
						
						if (state==State.MIN){							
							if (nextState==State.MAY){
								ElementInFormula e = new ElementInFormula();
								e.m_element = currentElement;
								e.m_cardinality = 1;							
								m_parsedFormula.add(e);
								
								currentElement=token;
								currentNumOfAtoms=0;
								
								elementsCounter++;
							}
							
							if (nextState==State.MIN){
								currentElement+=token;
							}
							
							if (nextState==State.NUM){
								currentNumOfAtoms=(int)Integer.parseInt(token);
							}
						} else {
							
							if (state==State.NUM){
								
								if (nextState==State.MAY){
									ElementInFormula e = new ElementInFormula();
									e.m_element = currentElement;
									e.m_cardinality = currentNumOfAtoms;							
									m_parsedFormula.add(e);
									
									currentElement=token;
									currentNumOfAtoms=0;
									
									elementsCounter++;
								}								
								
								if (nextState==State.MIN){
									throw (new LoaderException(LoaderException.NUMBER_FOLLOWED_BY_a_z_IN_FORMULA));
								}
								
								if (nextState==State.NUM){
									currentNumOfAtoms=10*currentNumOfAtoms+(int)Integer.parseInt(token);
								}	
								
							}
							
						}
					}
				}	
			state=nextState;	
			i++;
		}//End While
		
		//Last element in formula
		if (currentElement!=""){
			ElementInFormula e = new ElementInFormula();
			e.m_element = currentElement;
			e.m_cardinality = currentNumOfAtoms;							
			m_parsedFormula.add(e);
			
			elementsCounter++;
		}
		
		m_parsedFormula.stream().forEach(element -> System.out.println("element " + element));
		System.out.println("TOTAL="+elementsCounter);
		
	}

}
