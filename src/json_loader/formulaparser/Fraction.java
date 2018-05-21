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
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Fraction.java
 *  Class to represent a numeric fraction
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class Fraction {
	 
	 private int numerator;
	 private int denominator;
	 
	 /**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		Fraction f=null;
		BigDecimal epsilon=new BigDecimal(0.01);
		
		f= Fraction.RealToFraction( new BigDecimal(0.25), epsilon );
		System.out.println(f+"=1/4");
		
		BigDecimal x=null;
		BigDecimal y=null;
		x = BigDecimal.ONE;
		y= new BigDecimal(7.0);
		x = x.divide(y, 3, RoundingMode.HALF_UP);
		f= Fraction.RealToFraction(x, epsilon);
				
		System.out.println(f+"=1/7"+"="+x);
		
		y= new BigDecimal(7.0+0.001);
		x = x.divide(y, 3, RoundingMode.HALF_UP);
		f= Fraction.RealToFraction(x, epsilon);
		
		System.out.println("y="+y);
		System.out.println(f+"=1/7"+"="+x);
		
		y= new BigDecimal(7.0-0.001);
		x = x.divide(y, 3, RoundingMode.HALF_UP);
		f= Fraction.RealToFraction(x, epsilon);
		
		System.out.println("y="+y);
		System.out.println(f+"=1/7"+"="+x);


	}
	
	/**
	 * Constructor of the class.
	 * Creates a new fraction by setting the numerator and denominator
	 * 
	 * @param numerator
	 * @param denominator
	 */
	public Fraction(int numerator, int denominator) {
        if(denominator == 0) {
            throw new IllegalArgumentException("denominator is zero");
        }
        if(denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }
	
	/**
	 * It converts a BigDecimal into an approximated Fraction
	 * 
	 * @param value is the BigDecimal to be converted
	 * @param accuracy is a value in range (0,1) such 
	 * 		the BigDecimal division ||numerator/denominator||<=accuracy
	 * 
	 * @return the approximated fraction
	 * @throws IllegalArgumentException when accuracy is out of range
	 */
	public static Fraction RealToFraction(BigDecimal value, BigDecimal accuracy)
	{
	    //if (accuracy <= 0.0 || accuracy >= 1.0)
		if (accuracy.compareTo(BigDecimal.ZERO)<=0||accuracy.compareTo(BigDecimal.ONE)>=0)
	    {
	        throw new IllegalArgumentException("accuracy, Must be > 0 and < 1.");
	    }

	    //int sign = (int) Math.signum(value);
		int sign = value.signum();

	    if (sign == -1)
	    {
	        //value = Math.abs(value);
	    	value = value.abs();
	    }

	    // Accuracy is the maximum relative error; convert to absolute maxError
	    //double maxError = sign == 0 ? accuracy : value * accuracy;
	    double maxError = sign==0 ?  accuracy.doubleValue() : value.multiply(accuracy).doubleValue();
	    //double maxError = accuracy.doubleValue();
	    

	    //int n = (int) Math.floor(value);
	    int n = value.setScale(0, RoundingMode.FLOOR).intValue();
	    //value -= n;
	    double double_value = value.setScale(3, RoundingMode.HALF_UP).doubleValue() - n;

	    if (double_value < maxError)
	    {
	        return new Fraction(sign * n, 1);
	    }

	    if (1 - maxError < double_value)
	    {
	        return new Fraction(sign * (n + 1), 1);
	    }

	    double z = double_value;
	    int previousDenominator = 0;
	    int denominator = 1;
	    int numerator;

	    do
	    {
	        z = 1.0 / (z - (int) z);
	        int temp = denominator;
	        denominator = denominator * (int) z + previousDenominator;
	        previousDenominator = temp;
	        numerator = (int)(double_value * denominator);
	    }
	    while (Math.abs(double_value - (double) numerator / denominator) > maxError && z != (int) z);

	    return new Fraction((n * denominator + numerator) * sign, denominator);
	}
	
	
	/**
	 * 
	 * It computes a CandidateFractions object containing a list of fractions that
	 * approximate well a BigDecimal value for a given accuracy
	 * 	
	 * @param value is the BigDecimal to be approximated by the candidate fractions
	 * @param accuracy is an upper bound of the accuracy for the candidate fractions 
	 * @return the list of candidate fractions
	 */
	public static CandidateFractions RealToAproximateFraction(
			BigDecimal value, BigDecimal accuracy){
		
		/* Algorithm details:
		 * 
		 * Returns a faction that approximates a BigDecimal.
		 * 
		 * Accuracy is the accuracy arg in RealToFraction
		 * (i.e., it is used to limit the rounding error) 
		 * and it is used to compute a candidate fraction
		 * 
		 * Once the candidate fraction is computed the denominator is checked
		 * to be less than MAXDENOMINATOR
		 * 
		 * If it were bigger the bigdecimal is modified adding/substracting
		 * an INCREMENT (10^-numMaxDecimals) during less than MAXTRIALS trials
		 * 
		 * If the denominator keeps being bigger than MAXDENOMINATOR, the original
		 * fraction is returned
		 * (TODO: Another variant is to return the fraction with the lowest denominator
		 * during the exploration)
		 * 
		 * IMPORTANT: MAXDENOMINATOR has to do with the total amount of atoms in the
		 * molecule. If it is increased you can get more strange molecules having 
		 * a high number atoms for some symbols (because rounding errors and/or impurities
		 * in experimental data). So, it can't be high.
		 * On the other hand, if it is so low, it is probably that for some molecules
		 * the algorithm can't find a simplified approximate fraction, so it returns the
		 * original candidate, that at the end is likely to have a denominator greater than
		 * MAXDENOMINATOR
		 * 
		 * That's why a tradeoff must be selected. We've choose MAXDENOMINATOR=20
		 * because we think that it is not possible having materials with more than 20
		 * atoms in its base cell. 
		 * 
		 */	
				
		final BigDecimal INCREMENT =
				 new BigDecimal(Math.pow(10, -ArrayFormula.NUMDECIMALS)); 
		final int MAXTRIALS = 10;
		final int MAXDENOMINATOR = 20;	
		
		//TreeMap<String, Fraction> candidates = new TreeMap<String, Fraction>();
		CandidateFractions candidates = new CandidateFractions();
		Fraction f = RealToFraction( value, accuracy );
		
		Fraction f_first = f;		
		BigDecimal val_copy = BigDecimal.ZERO.add(value);
		BigDecimal delta = INCREMENT;
		
	    if( f.getDenominator() <= MAXDENOMINATOR)	
	    	candidates.content.put(f.toString(), f);
		
		for (int nTrials=0; nTrials < MAXTRIALS; nTrials++){
			
			value = value.add(delta);
			f = RealToFraction( value, accuracy );			
			if (f.getDenominator() < MAXDENOMINATOR){
				candidates.content.put(f.toString(), f);
				
			}
						
			value=val_copy;
			value = value.subtract(delta);
			f = RealToFraction( value, accuracy );			
			if (f.getDenominator() < MAXDENOMINATOR){
				candidates.content.put(f.toString(), f);
			}
			
			delta = delta.add(INCREMENT);
			value=val_copy;
		}
		
		if (candidates.content.size()==0){
			candidates.content.put(f_first.toString(),f_first);
		}			
			
		return candidates;
	}
	
	/**
	 * String representation of a Fraction for debugging and testing purposes
	 * @return
	 */
	public String toString(){
		return ""+numerator+"/"+denominator;
	}

	/**
	 * Getter of the numerator of a fraction (it's an integer)
	 * @return
	 */
	public int getNumerator() {
        return this.numerator;
    }

	/**
	 *Getter of the denominator of a fraction (it's an integer)
	 * @return
	 */
    public int getDenominator() {
        return this.denominator;
    }
    
    /**
     * Setter of the numerator of a fraction (it's an integer)
     * @param n
     */
    public void setNumerator(int n){
    	this.numerator = n;
    }
    
    /**
     * 	Setter of the denominator of a fraction (it's an integer)
     * 
     * @param n
     */
    public void setDenominator(int n){
    	this.denominator = n;
    }
    
    /**
     * Given 2 integers a and b representing 2 denominators,
     * it returns the greater common denominator of them
     * 
     * @param a
     * @param b
     * @return
     */
    public static int greaterCommonDenominator(int a, int b) {
	    BigInteger b1 = BigInteger.valueOf(a);
	    BigInteger b2 = BigInteger.valueOf(b);
	    BigInteger gcd = b1.gcd(b2);
	    return gcd.intValue();
	}
}
