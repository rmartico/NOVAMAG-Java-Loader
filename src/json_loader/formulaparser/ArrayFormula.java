package json_loader.formulaparser;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import json_loader.error_handling.LoaderException;

/**
 * ArrayFormula.java
 *  Class that stores both chemical and stechiometric representation of a formula
 *  In the chemical representation all atoms sub-indexes are integer
 *  In the stechiometric representation the atom sub-indexes are decimal numbers
 *  
 *  Each object consists of
 *  	A dictionary to store the chemical formula 
 *  		( key=atomic symbol, value=integer)
 *  	A dictionary to store the stechiometric formula using BigDecimals
 *  		( key=atomic symbol, value=BigDecimal)
 *  	A dictionary to store the stechiometric formula using Fractions
 *  		( key=atomic symbol, value=Fraction)
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class ArrayFormula {
	
	public static final int NUMDECIMALS=3;	
	public static final BigDecimal EPSILON = new BigDecimal(Math.pow(10, -ArrayFormula.NUMDECIMALS+1));
	
	private BigDecimal m_numAtoms=BigDecimal.ZERO; //Sum of all symbol sub-indexes
	private int m_numElements=0;	//Number of different elements present in any proportion in the formula
	private int m_typeOfFormula;
	
	TreeMap<String, BigDecimal> chemicalDict;
	TreeMap<String, BigDecimal> stechiometryDict;
	TreeMap<String, Fraction> fractStechiometryDict;

	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 * @throws LoaderException
	 */
	public static void main(String[] args) throws LoaderException {
		// Test
		
		ArrayFormula af = new ArrayFormula(FormulaParser.CHEMICAL_FORMULA);
		af.insertElement("H", "");
		af.insertElement("O", "2");
		
				
		for (Map.Entry<String,BigDecimal> entry : af.getEntrySetChemical() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            System.out.println(key+"=>"+val);
        }
		
		System.out.println("Atoms="+af.getNumAtoms());
		System.out.println("Symbols="+af.getNumElements());
		
		af.checkAndNormalize();
		for (Map.Entry<String,BigDecimal> entry : af.getEntrySetStechiometry() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            System.out.println(key+"=>"+val);
        }
		
		System.out.println("FIN1------------");
		
		af = new ArrayFormula(FormulaParser.STECHIOMETRY_FORMULA);
		//H2SO4
		af.insertElement("H", "0.286");
		af.insertElement("S", "0.143");
		af.insertElement("O", "0.571");
		
		af.checkAndNormalize();
		for (Map.Entry<String,BigDecimal> entry : af.getEntrySetStechiometry() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            System.out.println(key+"=>"+val);
        }
		
		for (Map.Entry<String,BigDecimal> entry : af.getEntrySetChemical() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            System.out.println(key+"=>"+val);
        }
		
		System.out.println("FIN2------------");
		
		af = new ArrayFormula(FormulaParser.STECHIOMETRY_FORMULA);
		//X0.2Y0.45Z0.35
		af.insertElement("X", "0.2");
		af.insertElement("Y", "0.45");
		af.insertElement("Z", "0.35");
		
		af.checkAndNormalize();
		for (Map.Entry<String,BigDecimal> entry : af.getEntrySetChemical() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            System.out.println(key+"=>"+val.intValueExact());
        }
		
		System.out.println("FIN3------------");

	}
	
	/**
	 * Constructor for this class
	 * @param typeOfFormula
	 */
	public ArrayFormula(int typeOfFormula) {
		
        chemicalDict = new TreeMap<String, BigDecimal>();
        stechiometryDict = new TreeMap<String, BigDecimal>();
        fractStechiometryDict = new TreeMap<String, Fraction>();
        m_typeOfFormula=typeOfFormula;
    }
	
	/**
	 * Getter of the integer corresponding to a given
	 * atomic symbol in the chemical representation
	 * of the formula
	 * Note: It must be an integer, but was implemented for convenience
	 * as BigDecimal to store it, because in an intermediate state
	 * it can contains a decimal (i.e., when it is a stechiometric formula
	 * and the chemical formula still wasn't computed) 
	 * 
	 * @param key is the atomic symbol to search
	 * @return the integer corresponding to the searched atomic symbol
	 */
    public BigDecimal getChemical(String key){
        return chemicalDict.get(key);
    }
    
	/**
	 * Setter of the integer corresponding to a given
	 * atomic symbol in the chemical representation
	 * of the formula
	 * Note: It must be an integer, but was implemented for convenience
	 * as BigDecimal to store it, because in an intermediate state
	 * it can contains a decimal (i.e., when it is a stechiometric formula
	 * and the chemical formula still wasn't computed) 	 
	 *  
     * @param key is the atomic symbol to set
     * @param value is the integer corresponding to this atomic symbol
     */
    private void setChemical(String key, BigDecimal value){    	
			chemicalDict.put(key, value);    		
	}

	/**
	 * Getter of the BigDecimal corresponding to a given
	 * atomic symbol in the stechiometric representation
	 * with decimal numbers of the formula
	 * 
	 * @param key is the atomic symbol to search
	 * @return the BigDecimal corresponding to the searched atomic symbol
	 */
    public BigDecimal getStechiometry(String key){
        return stechiometryDict.get(key);
    }
    
    /**
     * Setter of the BigDecimal corresponding to a given
	 * atomic symbol in the stechiometric representation
	 * with decimal numbers of the formula
	 * 
     * @param key is the atomic symbol to set
     * @param value is the decimal number corresponding to this atomic symbol
     */
    private void setStechiometry(String key, BigDecimal value){    	
			stechiometryDict.put(key, value);    		
    }
   
    /**
     * It inserts a new symbol in the formula along with the sub-index
     * Both the symbol and the sub-index are represented as strings, as it
     * is supposed this method is invoked directly from the FormulaPaser.java
     * 
     * The sub-index is cleaned, getting rid of spaces, and converted to BigDecimal
     * BigDecimal is the common representation of sub-indexes, as they could be decimal
     * if it is an stechiometric formula
     * When a symbol has no sub-index, sub-index "1" is assigned (e.g., O in H2O is transformed to O1)
     * The new pair symbol+sub-index is inserted in the formula by calling
     *  insertElement(String key, BigDecimal value)
     * 
     * @param key is the atomic symbol
     * @param value is the symbol sub-index
     * @throws LoaderException if when converting the value to BigDecimal it turns out that 
     * it it not a number.
     */
    public void insertElement(String key, String value) throws LoaderException{
		
		BigDecimal bd=null;
		value.replaceAll("\\s",""); //Get rid of spaces and non-visible chars
		
		// In case there there's no number in a classical formula, it means ONE
		if (m_typeOfFormula==FormulaParser.CHEMICAL_FORMULA &&
				value.length()==0)
			bd = new BigDecimal(1);
		else
			try{
				bd = new BigDecimal(value);
			} catch (NumberFormatException e){
				throw new LoaderException(LoaderException.ATOM_INDEX_IS_NOT_A_NUMBER);
			}    	
		
		insertElement( key, bd);
	}

	/**
     * Try to add a new atomic symbol along with its numeric sub-index in a formula
     * In this method the sub-index is a BigDecimal.
     * It adds a new pair symbol+sub-index in the formula
     * once the sub-index as String is transformed to a BigDecimal
     * by insertElement(String key, String value)
     * 
     * @param key is an atomic symbol
     * @param value is the corresponding numeric sub-index
     * @throws LoaderException if the atom was already in the formula or the type of formula
     * doesn't be specified as chemical or stechiometric yet
     */
    public void insertElement(String key, BigDecimal value) throws LoaderException {
    	
    	BigDecimal nAtoms = getChemical(key);    	
    	if (nAtoms == null){
    		m_numAtoms=m_numAtoms.add(value);
    		m_numElements++;
    		
    		if (m_typeOfFormula==FormulaParser.CHEMICAL_FORMULA)
    			setChemical(key, value );
    		else if (m_typeOfFormula==FormulaParser.STECHIOMETRY_FORMULA){
    			setStechiometry(key, value );
    			setChemical(key, value );    			
    		} else 
    			throw new LoaderException(LoaderException.MISSING_TYPE_OF_FORMULA);
    	} else {
    		throw new LoaderException(LoaderException.REPEATED_ELEMENT_IN_FORMULA);
    	}    	
    	
    }
    
    /**
     * If the type of formula is set as chemical:
     * 1) It normalizes the molecule (i.e., it computes a stechiometric decimal representation
     *    such that all the atoms stechiometric simbols sum 1)
     * 2) and stores it in the corresponding dictionary 
     * 
     * If the type of formula is set as stechiometric
     * 1) It tests that the formula is normalized (i.e., it tests that in the stechiometric
     *    decimal representation all the atoms stechiometric simbols sum 1).
     *    It throws an exception if the test fails.
     * 2) It computes the fractional representation 
     * 		and stores it in the corresponding dictionary
     * 3) It computes the chemical representation
     * 		and stores it in the corresponding dictionary 
     * 
     * @throws LoaderException 
     * 		when trying to compute chemical representation it finds out that 
     * 			the stechiometric sub-indexes does not sum one
     * 		or when the type of formula doesn't be specified as chemical or stechiometric yet
     */
    public void checkAndNormalize() throws LoaderException{    	
    	
    	if (m_typeOfFormula==FormulaParser.CHEMICAL_FORMULA){
    		//Compute and store stechiometric formula
    		for (Map.Entry<String,BigDecimal> entry : chemicalDict.entrySet() ) {
                String key = entry.getKey();                
                BigDecimal val = entry.getValue();
                val = val.divide(m_numAtoms, ArrayFormula.NUMDECIMALS, BigDecimal.ROUND_HALF_UP);
                
                setStechiometry( key, val);
                //System.out.println(key+"=>"+val);
            }
    		
    	} else  if (m_typeOfFormula==FormulaParser.STECHIOMETRY_FORMULA){
    		
    		BigDecimal difference = (BigDecimal.ONE).subtract(m_numAtoms);
    		difference = difference.setScale(ArrayFormula.NUMDECIMALS,
    										 BigDecimal.ROUND_HALF_UP);
    		difference = difference.abs();
    		
    		if (difference.compareTo(EPSILON) > 0)
    			throw new LoaderException(LoaderException.CENTESIMAL_FORMULA_DOES_NOT_SUM_ONE);
    		
    		computeChemical();
    		
    	} else 
    		throw new LoaderException(LoaderException.MISSING_TYPE_OF_FORMULA);
    }
    
    /**
     * 
     * It computes the fractional representation of the Formula given the stechiometry in the stechiometryDict
     * With the numerators in the fractions it gets the chemical representation 
     * It also updates the  number of atoms with the sum of atom sub-indexes in the chemical formula 
     **/
	void computeChemical(){
		
		TreeMap<String, CandidateFractions> allFractions = computeAllFractions();
		
		FractionsSelector fs = new FractionsSelector();
		fs.setAllF(allFractions);
		fs.computeAllCombinations();
		
		FractFormula cf = fs.getLowestDenominator();
		fractStechiometryDict = cf.getComponents();
		
		m_numAtoms=BigDecimal.ZERO;
		for (Map.Entry<String, Fraction> entry : fractStechiometryDict.entrySet()  ){
			
			String key = entry.getKey();
			Fraction f = entry.getValue();
			
			chemicalDict.put(key, new BigDecimal(f.getNumerator()));
			m_numAtoms=m_numAtoms.add(new BigDecimal(f.getNumerator()));
		}
		
	}

	/**
	 * It computes all fractions that are a good approximation to the decimal sub-index
	 * of each symbol in the stechiometric formula
	 *  
	 * @return the tree map of pairs key-value, in which the key is an atomic symbol
	 * and the value a collection of cantidate fractions that are a good approximation
     * to the decimal sub-index of each symbol in the stechiometric formula
	 */
	private TreeMap<String,CandidateFractions> computeAllFractions(){
    	
    	TreeMap<String, CandidateFractions> allFractions =
    			new TreeMap<String, CandidateFractions>();    	
    	
    	for (Map.Entry<String,BigDecimal> entry : getEntrySetStechiometry() ) {
    		String key = entry.getKey();
            BigDecimal val = entry.getValue();
            
            CandidateFractions cf = Fraction.RealToAproximateFraction(val, EPSILON);
            
            allFractions.put(key, cf);
    	}
    	
    	return allFractions;
    }
    
	/**
	 * 
	 * String reperesentation of this object. For debugging purposes.
	 */
    public String toString() {
        return chemicalDict.toString();
    }
    
    /**
     * 
     * It returns the chemical or the stechiometric formula depending
     * on the argument. The returned formula is a String reperesentation
     * of the formula.
     * 
     * @param typeOfFormula
     * @return
     */
    public String getFormula(int typeOfFormula){
    	String formula="";
    	
    	TreeMap<String, BigDecimal> dict;
    	if (typeOfFormula==FormulaParser.CHEMICAL_FORMULA)
    		dict=chemicalDict;
    	else
    		dict=stechiometryDict;
    	
    	for (Map.Entry<String,BigDecimal> entry : dict.entrySet() ) {
            String key = entry.getKey();
            formula+=key;
            BigDecimal val = entry.getValue();
            if (!(typeOfFormula==FormulaParser.CHEMICAL_FORMULA 
            		&& val.compareTo(BigDecimal.ONE)==0)) 
            	formula+=val; 
            //System.out.println(key+"=>"+val);
        }
    	
    	return formula;    	
    }
    
    /**
	 * It returns the chemical dictionary (for chemical formula)
	 * as a Set view of the mappings contained in this map.
	 * Typically to use it for iteration
	 * 
	 * For example:
	 * 
	 *  public void print() {
     *    for (Map.Entry<String,BigDecimal> entry : getEntrySetChemical() ) {
     *       String key = entry.getKey();
     *       BigDecimal val = entry.getValue();
     *       System.out.println(key+"=>"+val);
     *   }
     * }
	 * 
	 * @return
	 */
    public Set<Map.Entry<String, BigDecimal>> getEntrySetChemical(){
    	return chemicalDict.entrySet();
    }
    
    /**
   	 * It returns the stechiometric dictionary (for stechiometric formula)
   	 * as a Set view of the mappings contained in this map.
   	 * Typically to use it for iteration
 	 * 
	 * For example:
	 * 
	 *  public void print() {
     *    for (Map.Entry<String,BigDecimal> entry : getEntrySetStechiometry() ) {
     *       String key = entry.getKey();
     *       BigDecimal val = entry.getValue();
     *       System.out.println(key+"=>"+val);
     *   }
     * }
   	 * 
   	 * @return
   	 */
    public Set<Map.Entry<String, BigDecimal>> getEntrySetStechiometry(){
    	return stechiometryDict.entrySet();
    }
    
    /**
     * Getter to return the number of atoms in a formula
     * It is a BigDecimal, because temporaly it can stores the number of atoms 
     * that sums the stechiometric representation before calculating
     * the chemical representation
     *  
     * @return
     */
    public BigDecimal getNumAtoms(){
		return m_numAtoms;
	}
	
    /**
     * Getter for the number of atoms (i.e., symbols) in a formula
     * @return
     */
	public int getNumElements(){
		return m_numElements;
	}

	/**
	 * Getter to retrieve the chemical formula in a dictionary
	 * where each key is an atomic symbol, and the value is a BigDecimal
	 * containing the integer sub-index of this symbol
	 * 
	 * @return 
	 */
	public TreeMap<String, BigDecimal> getParsedChemicalFormula(){
		return chemicalDict;
	}
	
	/**
	 * Getter to retrieve the stechiometric formula in a dictionary
	 * where each key is an atomic symbol, and the value is a BigDecimal
	 * containing the decimal sub-index of this symbol
	 * 
	 * @return 
	 */
	public TreeMap<String, BigDecimal> getParsedStechiometryFormula(){
		return stechiometryDict;
	}
}
