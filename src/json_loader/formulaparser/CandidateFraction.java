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

import java.math.BigDecimal;

/**
 * CandidateFraction.java
 *  Class that stores a candidate fractions to
 *  represent the stechiometric presence of an atom in a material
 *  It contains a BigDecimal with the stechiometry of the atom in the molecule
 *  and a fraction that is supposed that approximates well to that Bigdecimal
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */

public class CandidateFraction implements Comparable<CandidateFraction> {
	
	private Fraction fractionVal;
	private BigDecimal realVal;
	
	/**
	 * Constructor of the class
	 * 
	 * @param f is a fraction that is supposed that approximates well to v argument
	 * @param v is a BigDecimal with the stechiometry of the atom in the molecule
	 */
	public CandidateFraction ( Fraction f, BigDecimal v){
		fractionVal=f;
		realVal=v;
	}
	
	/**
	 * Getter for the fraction
	 * @return the fraction
	 */
	public Fraction getFractionVal() {
		return fractionVal;
	}
	/**
	 * Setter for the fraction
	 * @param fractionVal is the fraction to set
	 */
	public void setFractionVal(Fraction fractionVal) {
		this.fractionVal = fractionVal;
	}
	/**
	 * Getter for the BigDecimal representing the stechiometry
	 * @return the Bigdecimal
	 */
	public BigDecimal getRealVal() {
		return realVal;
	}
	/**
	 * Setter for the BigDecimal representing the stechiometry
	 * @param realVal is the stechiometry to set
	 */
	public void setRealVal(BigDecimal realVal) {
		this.realVal = realVal;
	}

	@Override
	public int compareTo(CandidateFraction f) {
		
		return this.getFractionVal().getDenominator()-
			((CandidateFraction) f).getFractionVal().getDenominator();
	}
	

}
