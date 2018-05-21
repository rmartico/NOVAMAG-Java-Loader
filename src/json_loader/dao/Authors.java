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
package json_loader.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.DBMSError;
import json_loader.error_handling.PostgresTableError;
import json_loader.utils.Cleaner;
import json_loader.utils.ConnectionPool;

/**
 * Authors.java
 *  Class to represent a java object containing the list of authors of a material
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Authors {
	private static Logger l = LoggerFactory.getLogger(Authors.class);	
	
	private JSONArray m_authors;
	private long lastMafId;

	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 * @throws IOException
	 * @throws NamingException
	 */
	public static void main(String[] args) throws IOException, NamingException {		
		
		String		js ="[\"Autor1\",\"Autor2\",\"Autor3\",\"Autor4\"]";
		JSONArray	ja = new JSONArray(js);
		
		ConnectionPool p=null;
		Connection con=null;
		Statement st=null;
		
		ResultSet rs=null;		
		
		try{
			p = ConnectionPool.getInstance();
			con = p.getConnection();			
			st = con.createStatement();
			
			Cleaner.CleanDB();
			
			st.executeUpdate("INSERT INTO molecules (formula,stechiometry) VALUES ('F3Sn','F0.75Sn0.25')");			
			
			st.executeUpdate(
					  "INSERT INTO items"
					  + "(type, name, summary, formula, production_info,compound_space_group,"
					  + " unit_cell_volume, lattice_parameters, lattice_angles,	atomic_positions,"
					  + " crystal_info, unit_cell_energy, unit_cell_formation_enthalpy, energy_info,"
					  + "unit_cell_spin_polarization, atomic_spin_specie, saturation_magnetization,"
					  + "magnetization_temperature, magnetization_info,	magnetocrystalline_anisotropy_energy,"
					  + "anisotropy_energy_type, magnetocrystalline_anisotropy_constants, kind_of_anisotropy,"
					  + "anisotropy_field, anisotropy_info,exchange_integrals,exchange_info,magnetic_order,"
					  + "curie_temperature, curie_temperature_info, remanence, coercivity, energy_product,"
					  + "hysteresis_info, domain_wall_width, domain_wall_info, exchange_stiffness,"
					  + "exchange_stiffness_info,reference, comments)"
					  + " VALUES ( "
					  + " 'E', 'Fe3Sn', 'Solid state reaction, crystal, anisotropy, magnetization, Tc', 'F3Sn',"
					  + " '2 Solid State Reactions at 800ºC, for 48h',"
					  + "  194, null, '[5.4621, 5.4621,4.3490]', null,"
					  + " '[{\"atom\":\"Fe1\",\"vals\":[0.8442,0.6912,\"1/4\"]},"
					  + "	{\"atom\":\"Sn1\",\"vals\":[\"1/3\",\"2/3\",\"1/4\"]}]',"
					  + "'Cu-Kα radiation, room temperature, atmospheric pressure',"
					  + " null, null, null,  null, null, 1.2, 300, null,"
					  + " null, 'U', null, 'P', 2.5, null, null, null,"
					  + " 'ferromagnet', 747, null, null, null, null,"
					  + " null, null, null, null, null, null, null  )"
					  , Statement.RETURN_GENERATED_KEYS);
									
			rs = st.getGeneratedKeys();
			long key=-1;
			if(rs.next())
				key = rs.getLong(1);
			con.commit();
			
			Authors 	a  = new Authors(key);
			a.setAuthors(ja);
			a.insert(con, true);
			
			rs = st.executeQuery("SELECT mafId, author FROM authoring");
			while (rs.next()){
				System.out.println(rs.getString("mafId")+rs.getString("author"));
			}
			
		} catch (SQLException e){			
			l.error(e.getMessage());
		} finally {
			p.close(st);
			p.close(con);
		}

	}
	
	/**
	 * Class constructor
	 * It sets the foreign key that references items table
	 * 
	 * @param itemKey is the items.mafId value of the material related
	 * with the list of authors in the Authors object
	 */
	public Authors(long itemKey){		
		lastMafId = itemKey;
	}
	
	/**
	 * Setter for the list of authors
	 * 
	 * @param j is a json array of strings, each string is an author
	 */
	public void setAuthors( JSONArray j ){
		m_authors=j;
	}
	
	/**
	 * Getter for the list of authors 
	 * 
	 * @return a json array of strings, where each string is an author
	 */
	public JSONArray getAutors(){
		return m_authors;
	}
	
	/**
	 * Makes the necessary SQL insertions in the authoring table
	 * to associate the list of authors in the Authors object to its
	 * item
	 * If an author is not present in authors table, then it is inserted in this table
	 * 
	 * @param con is the database connection. If it's null a new connection is
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
	 */
	public void insert( Connection con, boolean doCommit ){
		
		ConnectionPool p = null;
		boolean closeConnection=false;
		
		PreparedStatement ins_authoring=null;
		
		try{
			p = ConnectionPool.getInstance();
			if (con==null){
				con = p.getConnection();
				closeConnection = true;
			}
			
			for (int i = 0, size = m_authors.length(); i < size; i++){
				String theAuthor = m_authors.get(i).toString();
				
				//Try to insert the author just in case it wasn't already inserted				 
				insertAuthor( theAuthor, con, false);
				
				ins_authoring=con.prepareStatement("INSERT INTO authoring (author, mafId) VALUES (?,?);");
				ins_authoring.setString(1, theAuthor);
				ins_authoring.setLong(2, lastMafId);
				
				ins_authoring.executeUpdate();
			}
			
			if (doCommit)
				con.commit();			
			
		} catch (SQLException e) {
			p.undo(con);
			l.error(e.getMessage());
			
		} finally {			
			p.close(ins_authoring);
			if (closeConnection) p.close(con);
		}
		
	}
	
	/**
	 * 
	 * It inserts a new author in the authors SQL table
	 * If the author exists in the database do nothing
	 * 
	 * @param theAuthor is the author to be inserted
	 * @param con is the connection.  If it's null a new connection is
	 * 	created and also released at the end of the method
	 *  Usually this param is not null, as this insertion is part of the authoring insertion
	 *  (see insert method above), and both share the same transaction, so both share the same
	 *  connection as well. However you can set it to true for debugging and testing
	 *  purposes.
	 * @param doCommit is true if the insertion must be committed at the end
	 *  of method execution, and it's false if not
	 *  Usually this param is false, as this insertion is part of the authoring insertion
	 *  (see insert method above). However you can set it to true for debugging and testing
	 *  purposes.
	 * @throws SQLException
	 */
	private void insertAuthor( String theAuthor, Connection con, boolean doCommit )
			throws SQLException{
		
		ConnectionPool p = null;
			boolean closeConnection=false;
		
			PreparedStatement ins_author=null;
			
			try{
				p = ConnectionPool.getInstance();
				if (con==null){
					con = p.getConnection();
					closeConnection = true;
				}
				Savepoint savepoint = null;
				
				try{
					savepoint = con.setSavepoint();
					
					ins_author=con.prepareStatement("INSERT INTO authors VALUES (?);");
					ins_author.setString(1, theAuthor);			
					ins_author.executeUpdate();
					
					if (doCommit)
						con.commit();
					
				} catch (SQLException e){
					if ((new PostgresTableError()).checkExceptionToCode( e, DBMSError.valueOf("UNQ_VIOLATED"))){						
						//It's OK the author has already been inserted before => Do nothing
						con.rollback(savepoint);
					} else {
						p.undo(con);
						throw e;
					}
				} 
			} finally {
				p.close(ins_author);				
				if (closeConnection) p.close(con);
			}
	}

}
