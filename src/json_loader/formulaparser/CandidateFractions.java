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
