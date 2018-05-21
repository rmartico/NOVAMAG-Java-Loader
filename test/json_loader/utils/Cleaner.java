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

package json_loader.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

public class Cleaner {
	
	public static void CleanDB() throws SQLException, NamingException, IOException	
	{
		ConnectionPool p = ConnectionPool.getInstance();
		
		Connection con=null;
		Statement st_TRUNC_composition=null;
		PreparedStatement 
				pst_TRUNC_atoms=null, pst_TRUNC_molecules=null,
				pst_TRUNC_materials=null, pst_TRUNC_material_features=null;
		
		try{
			con = p.getConnection();
			st_TRUNC_composition = con.createStatement();
			
			st_TRUNC_composition.executeUpdate("TRUNCATE composition CASCADE");con.commit();
			st_TRUNC_composition.executeUpdate("TRUNCATE atoms CASCADE");con.commit();
			st_TRUNC_composition.executeUpdate("TRUNCATE molecules CASCADE");con.commit();
			st_TRUNC_composition.executeUpdate("TRUNCATE authors CASCADE");con.commit();
			st_TRUNC_composition.executeUpdate("TRUNCATE authoring CASCADE");con.commit();
			st_TRUNC_composition.executeUpdate("TRUNCATE attached_files CASCADE");con.commit();	
			st_TRUNC_composition.executeUpdate("TRUNCATE items RESTART IDENTITY CASCADE");	
			con.commit();
		} catch (SQLException e){
			p.undo(con);
			throw e;
		} finally {
			p.close(st_TRUNC_composition);
			p.close(pst_TRUNC_atoms);
			p.close(pst_TRUNC_molecules);
			p.close(pst_TRUNC_materials);
			p.close(pst_TRUNC_material_features);
			p.close(con);
		}
	}
	
	public static void insertAtom(String arg_atom) throws IOException, NamingException, SQLException {
		ConnectionPool p=null;
		Connection con=null;
		PreparedStatement pst_INS_atoms=null;
		
		try{
			p = ConnectionPool.getInstance();		
			con = p.getConnection();			
			
			pst_INS_atoms=con.prepareStatement("INSERT INTO atoms VALUES (?)");
			pst_INS_atoms.setString(1, arg_atom);
			pst_INS_atoms.executeUpdate();
			con.commit();
		} catch (Exception e){
			p.undo(con);
			throw e;
		} finally {
			p.close(pst_INS_atoms);
			p.close(con);
		}
		
	}

}
