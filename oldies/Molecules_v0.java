package json_loader.oldies;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Molecules_v0 {
	private class ElementInFormula{
		public String m_element;
		public int    m_cardinality;		
		
		public String toString(){
			return m_element+"_"+m_cardinality;
		}
		
	}	
	
	private static Logger l = null;	
	
	private String m_formula;
	private int m_numAtoms;
	
	private List<ElementInFormula> m_parsedFormula;

	public static void main(String[] args) {
		
		Molecules_v0 m = new Molecules_v0();
		try{
			m.parseFormula("Fe3Ni2LiHO356");
			//System.out.println();
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}
	
	public Molecules_v0(){
		l =	LoggerFactory.getLogger(Molecules_v0.class);
	}
	
	
	public String toString(){
		return "Formula:"+m_formula;		
	}
	
	public int getNumAtoms(){
		return m_numAtoms;
	}
	
	private void parseFormula( String formula ) throws LoaderException{
		
		//Parser states
		int INI=0, MAY=1, MIN=2, NUM=3;
		
		int i=0;
		String token;
		int state=INI;
		int nextState;
		
		int elementsCounter=0;
		
		String currentElement="";
		int    currentNumOfAtoms=0;
		
		m_parsedFormula = new ArrayList<ElementInFormula>();
		
		while (i<formula.length()){
			token = ""+formula.charAt(i);
			
			if ((token).matches("[A-Z]"))
				nextState=MAY;
			else
				if ((token).matches("[a-z]"))
					nextState=MIN;
				else
					if ((token).matches("[0-9]"))
						nextState=NUM;
					else
						throw (new LoaderException(LoaderException.NOT_ALLOWED_CHAR_IN_FORMULA));
			
			System.out.println(token+"=>"+nextState);
			
			if (state==INI && nextState!=MAY)
				throw (new LoaderException(LoaderException.FIRST_CHAR_IN_FORMULA_HAS_TO_BE_A_Z));
			else
				if (state==INI)
					currentElement=token;
				else {
			
					if (state==MAY){
						if (nextState==MAY){
							ElementInFormula e = new ElementInFormula();
							e.m_element = currentElement;
							e.m_cardinality = 1;							
							m_parsedFormula.add(e);
							
							currentElement=token;
							currentNumOfAtoms=0;
							
							elementsCounter++;
						}
						
						if (nextState==MIN){
							currentElement+=token;
						}
						
						if (nextState==NUM){
							currentNumOfAtoms=(int)Integer.parseInt(token);
						}				
						
					} else {
					
						if (state==MIN){
							if (nextState==MAY){
								ElementInFormula e = new ElementInFormula();
								e.m_element = currentElement;
								e.m_cardinality = 1;							
								m_parsedFormula.add(e);
								
								currentElement=token;
								currentNumOfAtoms=0;
								
								elementsCounter++;
							}
							
							if (nextState==MIN){
								currentElement+=token;
							}
							
							if (nextState==NUM){
								currentNumOfAtoms=(int)Integer.parseInt(token);
							}
						} else {					
						
							if (state==NUM){
								if (nextState==MAY){
									ElementInFormula e = new ElementInFormula();
									e.m_element = currentElement;
									e.m_cardinality = currentNumOfAtoms;							
									m_parsedFormula.add(e);
									
									currentElement=token;
									currentNumOfAtoms=0;
									
									elementsCounter++;
								}
								
								if (nextState==MIN){
									throw (new LoaderException(LoaderException.NUMBER_FOLLOWED_BY_a_z_IN_FORMULA));
								}
								
								if (nextState==NUM){
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
