package json_loader.oldies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;
import json_loader.error_handling.PostgresTableError;
import json_loader.error_handling.TableError;

public class Moleculesv3 {
	private class ElementInFormula{
		public String m_element;
		public int    m_cardinality;		
		
		public String toString(){
			return m_element+m_cardinality;
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
		
		/*Test Case 1
		Molecules m = new Molecules();
		try{
			
			String formula="Fe3Ni2LiHO356";
			System.out.println(formula);
			m.parseFormula(formula);
			//System.out.println();
			
			formula="Fe3Ni2LiHO356Fe2"; //jmaudes Test TO_FIX... m_NumAtoms is incremented wrongly if a repeated symbol is added.
			System.out.println(formula);
			m.parseFormula(formula);
			
			formula="Fe3Ni2LiFe2HO356";
			System.out.println(formula);
			m.parseFormula(formula);
			
			formula="";
			System.out.println(formula);
			m.parseFormula(formula);
			
			formula="Fe";
			System.out.println(formula);
			m.parseFormula(formula);
			
			formula="O2";
			System.out.println(formula);
			m.parseFormula(formula);
			
			formula="O";
			System.out.println(formula);
			m.parseFormula(formula);
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
			*/
			
		try{	
			ConnectionPool p = ConnectionPool.getInstance();
			//String s_formula="{\"chemistry\":{\"formula\":\"Fe3Ni2LiHO356\"}}";
			//String s_formula="{\"chemistry\":{\"formula\":\"Fe3Sn\"}}";
			String s_formula="{\"chemistry\":{\"formula\":\"Fe3Ni2Sn\"}}";
			JSONObject o_formula = new JSONObject(s_formula);
			
			Moleculesv3 m = new Moleculesv3();
			m.insert( o_formula, p );
			
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}
	
	public Moleculesv3(){
		l =	LoggerFactory.getLogger(Moleculesv3.class);
	}
	
	
	public String toString(){
		return "Formula:"+m_formula;		
	}
	
	public int getNumAtoms(){
		return m_numAtoms;
	}
	
	public void insert( JSONObject obj, ConnectionPool p ) throws LoaderException{
		
		Connection con=null;
		PreparedStatement ins_composition=null;
		PreparedStatement ins_molecules=null;
		
		try{
			
			String formula = obj.getJSONObject("chemistry").getString("formula");
			parseFormula(formula);
			
			con = p.getConnection();
			
			try{
				ins_composition=con.prepareStatement("INSERT INTO molecules VALUES (?);");
				ins_composition.setString(1, m_formula);
				ins_composition.executeUpdate();
			} catch (SQLException e){
				System.err.println(e.getMessage());
				System.err.println(e.getErrorCode());
				System.err.println(e.getSQLState());				
				
				if (p.getTableError().checkExceptionToSQLState( e, TableError.UNQ_VIOLATED) ){
					throw new LoaderException(LoaderException.NON_EXISTENT_ATOMIC_SYMBOL);					
				} else {
					throw e;
				}
			}
				
			ins_molecules = con.prepareStatement("INSERT INTO composition"
					+ " (symbol,formula, numb_of_occurrences) VALUES ( ?, ?, ? );");
			ins_molecules.setString(2, m_formula);
			
			Iterator<ElementInFormula> it = m_parsedFormula.iterator();
			while(it.hasNext()) {
				ElementInFormula e = it.next();
				
				ins_molecules.setString(1, e.m_element);
				ins_molecules.setInt(3, e.m_cardinality);
				ins_molecules.executeUpdate();
			}
			
			con.commit();
			
		} catch (SQLException e) {
			
			p.undo(con);
			
			//Viola PK => formula ya existia => continuar
			//Viola FK => elemento inexistente en la BD
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());
			System.err.println(e.getSQLState());
			
			if (p.getTableError().checkExceptionToSQLState( e, TableError.FK_VIOLATED) ){
				throw new LoaderException(LoaderException.NON_EXISTENT_ATOMIC_SYMBOL);
			}
			
			l.error(e.getMessage());
			
		} finally {
			p.close(ins_composition);
			p.close(ins_molecules);
			p.close(con);
		}
		
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
			
			//System.out.println(token+"=>"+nextState);			
			
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
							
							elementsCounter++; //jmaudes TO_FIX It is incremented wrongly if a repeated symbol is added.
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
								
								elementsCounter++; //jmaudes TO_FIX It is incremented wrongly if a repeated symbol is added.
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
									
									elementsCounter++; //jmaudes TO_FIX It is incremented wrongly if a repeated symbol is added.
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
			e.m_cardinality = currentNumOfAtoms==0?1:currentNumOfAtoms;							
			m_parsedFormula.add(e);
			
			elementsCounter++; //jmaudes TO_FIX It is incremented wrongly if a repeated symbol is added.
		}

		//Compute m_numAtoms
		m_numAtoms = 0;
		m_parsedFormula.stream().forEach(element -> m_numAtoms+=element.m_cardinality);
		
		//Compute m_formula
		m_formula ="";
		m_parsedFormula.stream().forEach(element -> m_formula+=element);
		
		/*
		m_parsedFormula.stream().forEach(element -> System.out.print(element));
		System.out.println();
		System.out.println("TOTAL Elementos="+elementsCounter);
		System.out.println("TOTAL Atomos="+m_numAtoms);
		System.out.println("m_formula="+m_formula);
		*/
	}

}
