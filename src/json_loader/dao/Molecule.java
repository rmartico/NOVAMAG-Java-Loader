package json_loader.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;
import json_loader.error_handling.PostgresTableError;
import json_loader.error_handling.DBMSError;
import json_loader.formulaparser.FormulaParser;
import json_loader.utils.ConnectionPool;

/**
 * Molecule.java
 *  Class to represent a java object containing the molecule associated to a material
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Molecule {

	
	private static Logger l = LoggerFactory.getLogger(Molecule.class);		
	
	private FormulaParser m_formulaParser;
	
	private String m_formula; //Formula reordering atoms alphabetically
	private String m_stechiometry; //Stecheometic formula;

	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {			
		
		try{	
			ConnectionPool p = ConnectionPool.getInstance();
			//String s_formula="{\"chemistry\":{\"formula\":\"Fe3Ni2LiHO356\"}}";
			//String s_formula="{\"chemistry\":{\"formula\":\"Fe3Sn\"}}";
			//String s_formula="Fe3Ni2Sn";
			String s_formula="Ni0.333Sn0.167Fe0.5";
			
			Molecule m = new Molecule();
			m.setFormula(s_formula);						
			m.insert( null, true );		
			
			Connection con=null;
			Statement st=null;
			ResultSet rs=null;
			try{
				con = p.getConnection();
				st = con.createStatement();
				rs = st.executeQuery("select * from composition;");
				while (rs.next()){
					String s="";
					s+=rs.getString(1)+"\t";
					s+=rs.getString(2)+"\t";
					s+=rs.getString(3)+"\t";
					
					System.out.println(s);
				}
				
			} catch ( SQLException e){
				l.debug(e.getMessage());
			} finally {
				p.close(rs);
				p.close(st);
				p.close(con);
			}
			
			System.out.println(m.getNumAtoms());
			System.out.println("FIN-Molecules----------");
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		

	}
	
	/**
	 * Returns a String representing the molecule formula for debugging purposes
	 *  
	 * @return 
	 */
	public String toString(){
		return "Formula:"+m_formulaParser.getChemicalFormula();		
	}
	
	/**
	 * Returns the number of atoms in the molecule
	 * 
	 * @return
	 */
	public BigDecimal getNumAtoms(){
		return m_formulaParser.getNumAtoms();
	}
	
	/**
	 * Getter that returns the String containing the formula
	 * 
	 * @return
	 */
	public String getFormula(){
		return m_formula;
	}
	
	/**
	 * 
	 * Setter for the String containing the formula
	 * 
	 * @param formula is the String containing the formula
	 * @throws LoaderException if the formula is not well formed when parsed
	 * (e.g., first letter of all atoms is not capitalized, there's things
	 * distinct than numbers between atom symbols, ... ) It does not check if
	 * a symbol really corresponds to an existing atom.
	 */
	public void setFormula(String formula) throws LoaderException{
		
		//Check formula syntax, reorder atoms & compute stoichiometric formula
		m_formulaParser = new FormulaParser(formula);
		m_formula=m_formulaParser.getChemicalFormula();
		m_stechiometry=m_formulaParser.getStechiometryFormula();
	}
	
	/**
	 * 
	 * It tries to insert the molecule into the molecules SQL table
	 * 	if the molecule already exists do nothing
	 *  else
	 *  	It tries to insert the molecule composition in the composition SQL table
	 *  	If an atomic symbol of the molecule does not exists in the atoms SQL
	 *  	table the corresponding foreign key violation is reinterpreted as 
	 *  	LoaderException.NON_EXISTENT_ATOMIC_SYMBOL
	 * 
	 *  @param con is the database connection. If it's null a new connection is
	 * 	created and also released at the end of the method
	 *  Usually this param is not null, as this insertion is part of the item insertion
	 *  in DBitem class, and both share the same transaction, so both share the same
	 *  connection as well. However you can set it to true for debugging and testing
	 *  purposes.
	 * @param doCommit is true if the insertions must be committed at the end
	 *  of method execution, and it's false if not
	 *  Usually this param is false, as this insertion is part of the item insertion
	 *  in DBitem class. However you can set it to true for debugging and testing
	 *  purposes.
	 * @throws LoaderException if the molecule has an invented or incorrect atom symbol
	 */
	public void insert( Connection con, boolean doCommit ) 
			throws LoaderException{
		
		ConnectionPool p = null;
		boolean closeConnection=false;
		
		PreparedStatement ins_composition=null;
		PreparedStatement ins_molecules=null;
		
		boolean existentMolecule = false;
		
		try{
			p = ConnectionPool.getInstance();
			if (con==null){
				con = p.getConnection();
				closeConnection = true;
			}
			Savepoint savepoint = null;
			
			try{
				savepoint = con.setSavepoint();
				ins_composition=con.prepareStatement("INSERT INTO molecules VALUES (?,?);");
				ins_composition.setString(1, m_formula);
				ins_composition.setString(2, m_stechiometry);
				ins_composition.executeUpdate();
			} catch (SQLException e){
				//System.err.println(e.getMessage());
				//System.err.println(e.getErrorCode());
				//System.err.println(e.getSQLState());				
				
				if ((new PostgresTableError()).checkExceptionToCode( e, DBMSError.valueOf("UNQ_VIOLATED"))){
					//throw new LoaderException(LoaderException.MOLECULE_ALREADY_INSERTED);
					existentMolecule=true;
					con.rollback(savepoint);
				} else {
					throw e;
				}
			}
			
			if (!existentMolecule){ 
				ins_molecules = con.prepareStatement("INSERT INTO composition"
					+ " (symbol,formula, numb_of_occurrences) VALUES ( ?, ?, ? );");
				ins_molecules.setString(2, m_formula );			
			
				for (Map.Entry<String,BigDecimal> entry : 
					m_formulaParser.getParsedStechiometryFormula().entrySet()) {
					ins_molecules.setString(1, entry.getKey());
					ins_molecules.setBigDecimal(3, entry.getValue());
					ins_molecules.executeUpdate();
				}
			}
			
			if (doCommit)
				con.commit();
			
		} catch (SQLException e) {
			
			p.undo(con);			
			//System.err.println(e.getMessage());
			//System.err.println(e.getErrorCode());
			//System.err.println(e.getSQLState());
			
			if ((new PostgresTableError()).checkExceptionToCode( e, DBMSError.valueOf("FK_VIOLATED")) ){
				throw new LoaderException(LoaderException.NON_EXISTENT_ATOMIC_SYMBOL);
			}
			
			l.error(e.getMessage());
			
		} finally {
			p.close(ins_composition);
			p.close(ins_molecules);
			if (closeConnection) p.close(con);
		}
		
	}

}
