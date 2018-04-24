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
