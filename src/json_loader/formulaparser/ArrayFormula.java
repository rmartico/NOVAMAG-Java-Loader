package json_loader.formulaparser;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.LoaderException;

//import json_loader.error_handling.LoaderException;

public class ArrayFormula {
	
	public static final int NUMDECIMALS=3;	
	public static final BigDecimal EPSILON = new BigDecimal(Math.pow(10, -ArrayFormula.NUMDECIMALS+1));
	
	private static Logger l = null;	
	
	private BigDecimal m_numAtoms=BigDecimal.ZERO; //Sum of all symbol sub-indexes
	private int m_numElements=0;	//Number of different elments present in any proportion in the formula
	private int m_typeOfFormula;
	
	TreeMap<String, BigDecimal> chemicalDict;
	TreeMap<String, BigDecimal> stechiometryDict;
	TreeMap<String, Fraction> fractStechiometryDict;

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
	
	public ArrayFormula(int typeOfFormula) {
		l =	LoggerFactory.getLogger(ArrayFormula.class);
		
        chemicalDict = new TreeMap<String, BigDecimal>();
        stechiometryDict = new TreeMap<String, BigDecimal>();
        fractStechiometryDict = new TreeMap<String, Fraction>();
        m_typeOfFormula=typeOfFormula;
    }
	
    public BigDecimal getChemical(String key){
        return chemicalDict.get(key);
    }
    
    public BigDecimal getStechiometry(String key){
        return stechiometryDict.get(key);
    }
    
    private void setChemical(String key, BigDecimal value){    	
    		chemicalDict.put(key, value);    		
    }
    
    private void setStechiometry(String key, BigDecimal value){    	
			stechiometryDict.put(key, value);    		
    }
   
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
    
    //It compuetes the FractFormula given the stechiometry in the stechiometryDict
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
    
    public String toString() {
        return chemicalDict.toString();
    }
    
    
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
    
    public Set<Map.Entry<String, BigDecimal>> getEntrySetChemical(){
    	return chemicalDict.entrySet();
    }
    
    public Set<Map.Entry<String, BigDecimal>> getEntrySetStechiometry(){
    	return stechiometryDict.entrySet();
    }
    
    // to illustrate the access with Map.entries
    public void print() {
        for (Map.Entry<String,BigDecimal> entry : chemicalDict.entrySet() ) {
            String key = entry.getKey();
            BigDecimal val = entry.getValue();
            System.out.println(key+"=>"+val);
        }

    }
    
    public BigDecimal getNumAtoms(){
		return m_numAtoms;
	}
	
	public int getNumElements(){
		return m_numElements;
	}

	public TreeMap<String, BigDecimal> getParsedChemicalFormula(){
		return chemicalDict;
	}
	
	public TreeMap<String, BigDecimal> getParsedStechiometryFormula(){
		return stechiometryDict;
	}
}
