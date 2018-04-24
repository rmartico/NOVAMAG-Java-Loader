package json_loader.formulaparser;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * FractionsSelector.java
 *  Class that computes a set of candidate fractions for each atom
 *   that approximate well a given BigDecimal representing its stechiometric composition,
 *   and then picks the best candidate fractions for getting
 *   the corresponding molecule formula
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class FractionsSelector {
	
	private TreeMap<String, CandidateFractions> allF;//All fractions
	private TreeMap<String, TreeMap<String,Fraction>> allC;//All combinations	

	/**
	 * 
	 * main method containing examples about using this class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TreeMap<String, CandidateFractions> allFractions = new TreeMap<String, CandidateFractions>();
		CandidateFractions cs = null;
		
		cs = new CandidateFractions();		
		cs.content.put("1/2", new Fraction(1,2));
		cs.content.put("1/3", new Fraction(1,3));
		cs.content.put("1/4", new Fraction(1,4));		
		allFractions.put("X", cs);
		
		cs = new CandidateFractions();		
		cs.content.put("2/5", new Fraction(2,5));
		cs.content.put("2/6", new Fraction(2,6));
		cs.content.put("2/7", new Fraction(2,7));		
		allFractions.put("Y", cs);
		
		cs = new CandidateFractions();		
		cs.content.put("3/8", new Fraction(3,8));
		cs.content.put("3/9", new Fraction(3,9));
		cs.content.put("3/10", new Fraction(3,10));		
		allFractions.put("Z", cs);
		
		FractionsSelector fs = new FractionsSelector();
		fs.setAllF(allFractions);
		
		FractionsSelector head = fs.getFirst();
		
		System.out.println(head);		

		System.out.println("FIN1--------------");
		
		FractionsSelector tail = fs.getTail();
		System.out.println(tail);
				
		System.out.println("FIN2--------------");
		
		fs.computeAllCombinations();
		System.out.println(fs);
		
		System.out.println("FIN3--------------");
		
		
		FractFormula cf = fs.getLowestDenominator();
		System.out.println(cf);
		
		System.out.println("FIN4--------------");
		
	}
	
	/**
	 * Constructor of the class
	 */
	public FractionsSelector(){
		allF = new TreeMap<String, CandidateFractions>();
		allC = new TreeMap<String, TreeMap<String,Fraction>>();
	}
	
	/**
	 * Setter to assign a list of candidate fractions to an element (i.e., atom)
	 * 
	 * @param allF is a TreeMap<String, CandidateFractions> where the String
	 * represents an atom, and the CandidateFrations is the list of fractions
	 * that approximate well its stechiometry in the molecule.
	 */
	public void setAllF(TreeMap<String, CandidateFractions> allF) {
		this.allF = allF;
	}
	
	/**
	 * Getter to retrieve a list of candidate fractions to an element (i.e., atom)
	 * 
	 * @return a TreeMap<String, CandidateFractions> where the String
	 * represents an atom, and the CandidateFrations is the list of fractions
	 * that approximate well its stechiometry in the molecule.
	 */
	public TreeMap<String, CandidateFractions> getAllF(){
		return allF;
	}
	
	/**
	 * Getter to retrieve a list of candidate formulas to represent a molecule
	 * In these candidate formulas the quantity for each atom is represented
	 * by a fraction
	 * 
	 * @return 
	 */
	public  TreeMap<String, TreeMap<String,Fraction>> getAllC(){
		return allC;
	}
	
	/**
	 * It gets the first element of the list of candidate formulas
	 * It is used to compute recursively all combinations of fractions
	 * in computeAllCombinations method
	 * @return the first element of the formula along with its list of candidate fractions
	 */
	FractionsSelector getFirst(){
    	CandidateFractions cf=null;
    	String key=null;
    	FractionsSelector toReturn=new FractionsSelector();
    
    	try{
    		key = allF.firstKey();
    		cf = allF.get(key);
    		
    		toReturn.putFractions(key, cf); 
    		
    	}catch(NoSuchElementException e){
    		return null;
    	}
    			
    	return toReturn;
    }

	/**
	 * It gets all the elements but the first of the list of candidate formulas
	 * It is used to compute recursively all combinations of fractions
	 * in computeAllCombinations method
	 * @return a list of elements of the formula along with their lists of candidate fractions
	 */
	FractionsSelector getTail(){
    	int i=0;
    	FractionsSelector tail= new FractionsSelector();
    	
    	for (Map.Entry<String,CandidateFractions> entry : allF.entrySet() ) {
    		
    		if (i>0){
    			String key = entry.getKey();
    			CandidateFractions cf = entry.getValue();
    			
    			tail.putFractions(key, cf);
    		}
    		i++;
    	}
    	return tail;
    }
	
	/**
	 * It computes all possible combinations of candidate fractions
	 * for each element in a formula
	 */
	public void computeAllCombinations(){
    	    	
        if (allF.size()==1){//Non-recursive exit
        	String theElement = allF.firstKey();        	
        	CandidateFractions cf = allF.firstEntry().getValue();
        	
        	for (Map.Entry<String, Fraction> entry:cf.entrySet()){
        		String theKey=theElement+entry.getValue();
        		TreeMap<String,Fraction> f = new TreeMap<String,Fraction>();
        		f.put(theElement, entry.getValue());
        		
        		putCombination( theKey, f);	
        	}
        	
        	
        	
        	return;
        }    	
        FractionsSelector head=getFirst();
        FractionsSelector tail = getTail();
        tail.computeAllCombinations(); 
    	
    	String mainKey,concatKey;
    	CandidateFractions cfs;
    	
    	for(Map.Entry<String, CandidateFractions> entry:head.getAllF().entrySet()){    		
    		
    		mainKey=entry.getKey();
    		cfs = entry.getValue(); 		
    		
    		for(Map.Entry<String,Fraction> cf_entry:cfs.entrySet()){
    			Fraction f=cf_entry.getValue();
    			
    			for(Map.Entry<String,TreeMap<String,Fraction>> tail_entry:tail.getAllC().entrySet()){
    				concatKey=mainKey+f+" ";
        			TreeMap<String,Fraction> tail_values = tail_entry.getValue();
        			
        			TreeMap<String,Fraction> value = new TreeMap<String,Fraction>();
        			value.put(mainKey, f);
        			for(Map.Entry<String, Fraction> entryFacts:tail_values.entrySet()){
        				String key = entryFacts.getKey();
        				Fraction other_f = entryFacts.getValue();
        				
        				value.put(key, other_f);
        			}
        			
        			concatKey+=tail_entry.getKey();        			
        			putCombination(concatKey, value);
    			}	
    		}
    	}
	}
	
	/**
	 * It adds a new candidate fraction to an element
	 * It is supposed this fraction approximates well
	 * the stechiometric presence of this element in the molecule
	 * 
	 * @param key is the element
	 * @param value is the new candidate fraction
	 */
	private void putFractions( String key, CandidateFractions value){
		allF.put(key, value);
	}
	
	/**
	 * It adds a new combination of candidate fractions
	 * (where each fraction represents the stechiometric presence
	 *  of an element in the molecule)
	 * to the list of candidate formulas
	 * 
	 * @param key a String representing the molecule with
	 * the fractions (e.g., "H1/1 O1/2" for H2O)
	 * This representation identifies uniquely this candidate molecule
	 * within the list of candidate molecules
	 * @param value is the list of pairs (atom, fraction) for each element
	 * in the candidate molecule
	 * (e.g., ("H", 1/1),("O", 1/2) for H2O)
	 */
	private void putCombination ( String key, TreeMap<String,Fraction> value){
		allC.put(key, value);
	}
	
	/**
	 * String representation of the object for debugging purposes
	 * @return the String. It has 2 sections
	 * 	In the first section (i.e., ALL FRACTIONS) each element in the molecule is listed
	 *  along with its candidate fractions that approximate well its stechiometric
	 *  presence in the molecule
	 *  In the second secion (i.e., ALL COMBINATIONS), is showed the combinations of each
	 *   candidate fraction of an atom with the other candidate fractions of the atoms 
	 *   in the same molecule
	 */
	public String toString(){
		String toReturn="ALL FRACTIONS______________"; 
		toReturn += "size: "+allF.size()+"\n";
				
		for(Map.Entry<String,CandidateFractions> entry1 : allF.entrySet() ) {
			toReturn+="\t"+entry1.getKey()+"\n";
			CandidateFractions c= entry1.getValue();
			for(Map.Entry<String,Fraction> entry2 : c.entrySet() ) {
				toReturn+="\t\t"+entry2.getKey();
				toReturn+="="+entry2.getValue()+"\n";
			}
		}
		
		toReturn+="ALL COMBINATIONS______________";
		toReturn += "size: "+allC.size()+"\n";
		
		for(Map.Entry<String,TreeMap<String,Fraction>> entry1: allC.entrySet()){
			toReturn+="\t"+entry1.getKey()+"\n";
			TreeMap<String,Fraction> fractions = entry1.getValue();
			for(Map.Entry<String,Fraction> entry2 : fractions.entrySet() ) {
				toReturn+="\t\t"+entry2.getKey();
				toReturn+="="+entry2.getValue()+"\n";
			}
			
		}
		
		return toReturn;
	}
	
	/**
	 * It selects from all computed combinations of atoms
	 * the combination with the lowest Greater Common Denominator (GCD),
	 * as it is the simplest molecule representation according 
	 * a given stechiometry
	 * 
	 * @return a FractFormula object representing the best candidate formula
	 */
	public FractFormula getLowestDenominator(){
		FractFormula toReturn=null;
		
		int lowest=Integer.MAX_VALUE;
		for(Map.Entry<String, TreeMap<String, Fraction>> entry:allC.entrySet()){
			FractFormula cf = new FractFormula(
					entry.getKey(),
					entry.getValue());
			
			if (toReturn==null||lowest>cf.getCommonDen()){				
				toReturn=cf;
				lowest=toReturn.getCommonDen();
			} 
			
		}
		
		return toReturn;
	}
}
