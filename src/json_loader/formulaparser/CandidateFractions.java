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
package json_loader.formulaparser;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * CandidateFractions.java
 *  Class that stores a list of candidate fractions to
 *  represent the stechiometric presence of an atom in a material
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class CandidateFractions {
	public TreeMap<String,Fraction> content;
	
	/**
	 * Constructor of the class
	 *  Usage example:
	 *  cs = new CandidateFractions();		
	 *	cs.content.put("1/2", new Fraction(1,2));
	 *	cs.content.put("1/3", new Fraction(1,3));
	 *	cs.content.put("1/4", new Fraction(1,4));
	 * 
	 */
	public CandidateFractions(){
		content = new TreeMap<String,Fraction>();
	}
	
	/**
	 * It returns the content attribute with the list of candidate fractions
	 * as a Set view of the mappings contained in this map.
	 * Typically to use it for iteration
	 * Example:
	 * CandidateFractions c = ... ;
	 *   for(Map.Entry<String,Fraction> entry2 : c.entrySet() ) { ...
	 * 
	 * @return
	 */
	public Set<Map.Entry<String,Fraction>> entrySet(){
		return content.entrySet();
	}
}
