package json_loader.formulaparser;

import java.math.BigDecimal;

public class CandidateFraction implements Comparable {
	
	private Fraction fractionVal;
	private BigDecimal realVal;
	
	public CandidateFraction ( Fraction f, BigDecimal v){
		fractionVal=f;
		realVal=v;
	}
	
	/**
	 * @return the fractionVal
	 */
	public Fraction getFractionVal() {
		return fractionVal;
	}
	/**
	 * @param fractionVal the fractionVal to set
	 */
	public void setFractionVal(Fraction fractionVal) {
		this.fractionVal = fractionVal;
	}
	/**
	 * @return the realVal
	 */
	public BigDecimal getRealVal() {
		return realVal;
	}
	/**
	 * @param realVal the realVal to set
	 */
	public void setRealVal(BigDecimal realVal) {
		this.realVal = realVal;
	}

	@Override
	public int compareTo(Object f) {
		
		return this.getFractionVal().getDenominator()-
			((CandidateFraction) f).getFractionVal().getDenominator();
	}
	

}
