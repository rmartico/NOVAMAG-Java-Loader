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

/**
 * FileTypes.java
 *  Class to represent a java object containing a file types
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class FileType {
	
	
	private String m_type;
	private String m_expReg;
	private boolean isText;
	
	
	/**
	 * Getter for the String representation of a type (e.g., "PNG")
	 * 
	 * @return the String representing the type
	 */
	public String getType() {
		return m_type;
	}
	
	/**
	 * Setter for the String representation of a type (e.g., "PNG")
	 * 
	 * @param the String representing the type to set
	 */
	public void setType(String m_type) {
		this.m_type = m_type;
	}
	
	/**
	 *  Getter for the String containing the regular expression
	 *  to identify a type (e.g., ".*\.(jpg|JPG|jpeg|JPEG)")
	 * 
	 * @return the regular expression
	 */
	public String getExpReg() {
		return m_expReg;
	}
	/**
	 *  Setter for the String containing the regular expression
	 *  to identify a type (e.g., ".*\.(jpg|JPG|jpeg|JPEG)")

	 * @param m_expReg the regular expression to set
	 */
	public void setExpReg(String m_expReg) {
		this.m_expReg = m_expReg;
	}
	/**
	 * Getter that returns true when the file type
	 *  is a text file, and false otherwise
	 *  
	 * @return true if it is a text file
	 */
	public boolean getIsText() {
		return isText;
	}
	
	/**
	 *  Setter that returns true when the file type
	 *  is a text file, and false otherwise
	 *
	 * @param isText the boolean value to set
	 */
	public void setIsText(boolean isText) {
		this.isText = isText;
	}

}
