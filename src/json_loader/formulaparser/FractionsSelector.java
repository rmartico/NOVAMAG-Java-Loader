package json_loader.formulaparser;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class FractionsSelector {
	
	private TreeMap<String, CandidateFractions> allF;//All fractions
	private TreeMap<String, TreeMap<String,Fraction>> allC;//All combinations

	

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
	
	public FractionsSelector(){
		allF = new TreeMap<String, CandidateFractions>();
		allC = new TreeMap<String, TreeMap<String,Fraction>>();
	}
	
	public void setAllF(TreeMap<String, CandidateFractions> allF) {
		this.allF = allF;
	}
	
	public TreeMap<String, CandidateFractions> getAllF(){
		return allF;
	}
	
	public  TreeMap<String, TreeMap<String,Fraction>> getAllC(){
		return allC;
	}
	
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
	
	private void putFractions( String key, CandidateFractions value){
		allF.put(key, value);
	}
	
	private void putCombination ( String key, TreeMap<String,Fraction> value){
		allC.put(key, value);
	}
	
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
