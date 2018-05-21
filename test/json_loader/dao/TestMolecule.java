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

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import json_loader.error_handling.LoaderException;
import json_loader.utils.Cleaner;
import json_loader.utils.ConnectionPool;

public class TestMolecule {
	static private ConnectionPool p;

	@Before
	public void setUp() throws Exception {
		
		p = ConnectionPool.getInstance();
		Cleaner.CleanDB();
	}	
	@Test
	public void testInsertOKChemical() throws Exception {
		
		Connection con=null;
		PreparedStatement pst_SEL_composition=null, pst_SEL_formula=null;
		ResultSet rs_SEL_formula=null, rs_SEL_composition=null;
		
		try{
			Cleaner.insertAtom("Fe");
			Cleaner.insertAtom("Sn");
			Cleaner.insertAtom("Ni");
			
			con = p.getConnection();			
			
			String s_formula="SnNi2Fe3"; //Not in alphabetical order
	
			Molecule m = new Molecule();
			m.setFormula(s_formula);
			m.insert(null, true);					
			
			pst_SEL_formula=con.prepareStatement(
					"SELECT COUNT(*) FROM molecules WHERE formula='Fe3Ni2Sn'");
			rs_SEL_formula=pst_SEL_formula.executeQuery();
			rs_SEL_formula.next();
			assertEquals(rs_SEL_formula.getInt(1),1);
			
			pst_SEL_composition=con.prepareStatement(
					"SELECT symbol||formula||stechiometry||numb_of_occurrences as aRow "
					+ "FROM composition join molecules using(formula) "
					+ "WHERE formula='Fe3Ni2Sn' "
					+ "ORDER BY symbol");
			rs_SEL_composition=pst_SEL_composition.executeQuery();
			
			int numRows=0;
			String content="";
			while (rs_SEL_composition.next()){
				content+=rs_SEL_composition.getString("aRow");				
				numRows++;
			}
			assertEquals(numRows,3);
			assertEquals(content,
					"FeFe3Ni2SnFe0.500Ni0.333Sn0.1670.500"+
					"NiFe3Ni2SnFe0.500Ni0.333Sn0.1670.333"+
					"SnFe3Ni2SnFe0.500Ni0.333Sn0.1670.167");
			con.commit();
			
			assertEquals( m.getNumAtoms().intValue(), 6);			
			
		} catch (SQLException e){
			p.undo(con);
			throw e;
		} finally {
			p.close(rs_SEL_formula);
			p.close(rs_SEL_composition);

			p.close(pst_SEL_composition);
			p.close(pst_SEL_formula);
			
			p.close(con);
		}
	}
	
	@Test
	public void testInsertOKStechiometry() throws Exception {
		
		Connection con=null;
		PreparedStatement pst_INS_atoms=null, pst_SEL_composition=null, pst_SEL_formula=null;
		ResultSet rs_SEL_formula=null, rs_SEL_composition=null;
		
		try{
			con = p.getConnection();			
			
			Cleaner.insertAtom("Fe");
			Cleaner.insertAtom("Sn");
			Cleaner.insertAtom("Ni");
		
			String s_formula="Sn0.167Ni0.333Fe0.5"; //Not in alphabetical order
	
			Molecule m = new Molecule();
			m.setFormula(s_formula);
			m.insert(null, true);					
			
			pst_SEL_formula=con.prepareStatement(
					"SELECT COUNT(*) FROM molecules WHERE formula='Fe3Ni2Sn'");
			rs_SEL_formula=pst_SEL_formula.executeQuery();
			rs_SEL_formula.next();
			assertEquals(rs_SEL_formula.getInt(1),1);
			
			pst_SEL_composition=con.prepareStatement(
					"SELECT symbol||formula||stechiometry||numb_of_occurrences as aRow "
					+ "FROM composition join molecules using(formula) "
					+ "WHERE formula='Fe3Ni2Sn' "
					+ "ORDER BY symbol");
			rs_SEL_composition=pst_SEL_composition.executeQuery();
			
			int numRows=0;
			String content="";
			while (rs_SEL_composition.next()){
				content+=rs_SEL_composition.getString("aRow");				
				numRows++;
			}
			//System.out.println(content);
			assertEquals(numRows,3);
			assertEquals(content,        "FeFe3Ni2SnFe0.5Ni0.333Sn0.1670.500"
			      + "NiFe3Ni2SnFe0.5Ni0.333Sn0.1670.333"
			      + "SnFe3Ni2SnFe0.5Ni0.333Sn0.1670.167");
			
			con.commit();
			
			//System.out.println(m.getNumAtoms());
			assertEquals( m.getNumAtoms().intValue(), 6);			
			
		} catch (SQLException e){
			p.undo(con);
			throw e;
		} finally {
			p.close(rs_SEL_formula);
			p.close(rs_SEL_composition);
			
			p.close(pst_INS_atoms);
			p.close(pst_SEL_composition);
			p.close(pst_SEL_formula);
			
			p.close(con);
		}
	}
	
	
	@Test
	public void notExistingAtomicSymbol() throws Exception {
		
		Connection con=null;
		PreparedStatement pst_INS_atoms=null;
		
		try{
			con = p.getConnection();			
			
			Cleaner.insertAtom("Fe");
			Cleaner.insertAtom("Sn");
			Cleaner.insertAtom("Ni");
		
			String s_formula="O2";
			
			Molecule m = new Molecule();
			m.setFormula(s_formula);
			m.insert(null, true);
			
			
		} catch (Exception e){
			p.undo(con);
			
			if (	(e instanceof LoaderException) &&
					((LoaderException) e).getErrorCode()== 
					LoaderException.NON_EXISTENT_ATOMIC_SYMBOL)
				
				assertTrue( true );
			else
				throw e;
			
		} finally {
			
			p.close(pst_INS_atoms);			
			p.close(con);
		}		
	}
	
}
