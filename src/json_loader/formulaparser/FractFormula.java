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
import java.util.TreeMap;

/**
 * FractFormula.java
 *  Class that represents a molecule formula using fractions for each atom
 *  instead of integers
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class FractFormula {
	private String fract_formula;
	private TreeMap<String, Fraction> components;	
	
	private int commonDen = 0;
	
	/**
	 * Constructor of the class
	 *  
	 * @param arg_fract_formula is a String identifying the formula (i.e., a key)
	 * 	For H2 it could be "H1/1 O1/2" 
	 * @param arg_components is the list of pairs (atom, fraction) for each element 
	 * (e.g., ("H", 1/1),("O", 1/2) for H2O)
	 */
	public FractFormula(String arg_fract_formula, 
			TreeMap<String, Fraction> arg_components){
		
		fract_formula=arg_fract_formula;
		components=arg_components;
		
		Fraction f1 = null;
		Fraction f2 = null;
		
		int i=0;
		
		for(Map.Entry<String, Fraction> entry : components.entrySet()){
			
			String   key = entry.getKey();
            Fraction val = entry.getValue();
 
			
			if (i==0){				
				f2 = val;
			}  else {
				f1=f2;
				f2=val;
				
				int n1 = f1.getNumerator();
        		int d1 = f1.getDenominator();
        		int n2 = f2.getNumerator();
        		int d2 = f2.getDenominator();
        		int int_gcd = Fraction.greaterCommonDenominator(d1, d2);
        		int temp = (int) (d2)/int_gcd; 
        		
        		for (Map.Entry<String,Fraction> inner_entry :  components.entrySet() ) {
        			if (inner_entry.getKey()!=key){
        				f1 = inner_entry.getValue();
        				String inner_key = inner_entry.getKey();
        				
        				n1 = f1.getNumerator();
        				d1 = f1.getDenominator();
        				
        				Fraction newF1 = new Fraction(n1*temp, d1*temp);
                		components.put(inner_key, newF1);
                		//System.out.println(f1);		
		
        				 
        			} else
        				break;
        		}
        		temp = (int) (d1)/int_gcd; 
        		
        		Fraction newF2 = new Fraction(n2*temp, d2*temp);
        		f2=newF2;
        		
        		//System.out.println(f2);
        		components.put(key, newF2);
        		//System.out.println("-----------------");            	
        }
        i++;			
		}
		
		if (i==0){
			commonDen=1;
		} else {
			commonDen=f2.getDenominator();
		}
	}

	/**
	 * Getter for the Greater Commn Denominator of
	 * the fractions in the formula
	 * @return
	 */
	public int getCommonDen(){
		return commonDen;
	}
	
	/**
	 * String representation of the object for debugging purposes
	 * @return an String with the key that identifies the formula 
	 * (e.g., "H1/1 O1/2" for H2O), its Greater Common Denominator
	 * (e.g., 2 for  "H1/1 O1/2") and the list of pairs (atom, fraction) for each element 
	 * (e.g., ("H", 1/1),("O", 1/2) for "H1/1 O1/2")
	 */
	public String toString(){
		String toReturn=fract_formula+"\n";
		toReturn+="GCD="+commonDen+"\n";
		for(Map.Entry<String, Fraction> entry:components.entrySet()){
			toReturn+=entry.getKey();
			toReturn+=entry.getValue()+"\n";		
		}
		
		
		return toReturn;
	}
	
	/**
	 * Getter for the list of pairs (atom, fraction) for each element 
	 * (e.g., ("H", 1/1),("O", 1/2) for "H1/1 O1/2")
	 * @return
	 */
	public TreeMap<String, Fraction> getComponents(){
		return components;
	}
}
