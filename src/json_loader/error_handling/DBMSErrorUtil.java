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
package json_loader.error_handling;

import java.sql.SQLException;

/**
 * DBMSErrorUtil.java
 * Utility to translate SQL error codes returned by the DBMS into
 * codes in DBMSError enumeration
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */

public interface DBMSErrorUtil {
	
	/**
	 * It translates the integer for the error code returned
	 * by a DBMS for a given error, to the error represented by DBMSError enumeration
	 * 
	 * @param errorSGBD SQL error code returned by the DBMS
	 * @return the corresponding DBMSError enumeration element
	 */
	DBMSError translate(int errorSGBD);
	
	/**
	 * It translates the String for the SQLState returned
	 * by a DBMS for a given error, to the error represented by DBMSError enumeration
	 * 
	 * @param errorSGBD SQLState returned by the DBMS
	 * @return the corresponding DBMSError enumeration element
	 */
	DBMSError translate(String errorSGBD);
	
	/**
	 * Test if the exception code matches to a given candidate error 
	 * represented by the DBMSError enumeration
	 * 
	 * @param ex is the DBMS exception having an error code which is distinct 
	 * 		for each DBMS 
	 *
	 * @param error represented by DBMSError enumeration
	 *            
	 * @return true if they match, false if not
	 */
	boolean checkExceptionToCode(SQLException ex, DBMSError error);	


}