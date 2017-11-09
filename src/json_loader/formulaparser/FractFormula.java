package json_loader.formulaparser;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class FractFormula {
	private String fract_formula;
	private TreeMap<String, Fraction> components;	
	
	private int commonDen = 0;
	
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

	public int getCommonDen(){
		return commonDen;
	}
	
	public String toString(){
		String toReturn=fract_formula+"\n";
		toReturn+="GCD="+commonDen+"\n";
		for(Map.Entry<String, Fraction> entry:components.entrySet()){
			toReturn+=entry.getKey();
			toReturn+=entry.getValue()+"\n";		
		}
		
		
		return toReturn;
	}
	
	public TreeMap<String, Fraction> getComponents(){
		return components;
	}
}
