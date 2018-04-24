package json_loader.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.naming.NamingException;

import json_loader.formulaparser.ArrayFormula;

public class Comparators {
	
	public static void assertEqualsBD(ResultSet rs, String field_name, String s) throws SQLException{
		BigDecimal bd = rs.getBigDecimal(field_name);
		//assertTrue( bd.compareTo(new BigDecimal(s))==0 );
		assertEqualsBD( bd, new BigDecimal(s));
	}
	
	public static void assertEqualsBD(BigDecimal b1, BigDecimal b2){
		BigDecimal difference = b1.subtract(b2);
		difference = difference.setScale(ArrayFormula.NUMDECIMALS,
										 BigDecimal.ROUND_HALF_UP);
		difference = difference.abs();		
		assertTrue( difference.compareTo(ArrayFormula.EPSILON) <= 0);
	}
	
	public static void assertEqualsBD(BigDecimal b1, double b2){
		//System.err.println(b1+"="+(new BigDecimal(b2)));
		Comparators.assertEqualsBD(b1, new BigDecimal(b2));
	}
	
	/*
	 * The ResultSet has an only attribute that concatenates all the values to check
	 * (i.e., SELECT attribute1||attribute2||attribute3|| ... FROM ... )
	 * Otherwise only the first attribute is checked
	 */
	public static void assertEqualsResultSet(ResultSet rs, long checksumArg) throws SQLException{
		
		String concatenation="";
		
		while (rs.next()){
			concatenation+=rs.getString(1);			
		}
		
		byte[] concatArray = concatenation.getBytes();
		
		Checksum checksum = new CRC32();
		// update the current checksum with the specified array of bytes
		checksum.update(concatArray, 0, concatArray.length);
		// get the current checksum value
		long checksumValue = checksum.getValue();
		
		if (checksumArg<0){//debugging
			System.out.println(concatenation);
			System.out.println("CRC32 checksum for the result set string is: " + checksumValue);
		} else
			assertEquals(checksumValue, checksumArg);
		
	}
	
	public static void assertEqualsResultSet(String query, long checksumArg) throws SQLException, NamingException, IOException{
		ConnectionPool p=null;		
		Connection con=null;
		
		PreparedStatement pstm=null;
		ResultSet rs=null;
				
		try{
			p = ConnectionPool.getInstance();		
			con = p.getConnection();
			pstm = con.prepareStatement(query);
			rs = pstm.executeQuery();
			
			assertEqualsResultSet( rs, checksumArg);
		} catch (SQLException e){
			p.undo(con);
			throw e;			
		} finally {
			p.close(rs);
			p.close(pstm);
			p.close(con);
		}
		
	}
	
}
