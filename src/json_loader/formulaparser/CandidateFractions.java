package json_loader.formulaparser;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CandidateFractions {
	public TreeMap<String,Fraction> content;
	
	public CandidateFractions(){
		content = new TreeMap<String,Fraction>();
	}
	
	public Set<Map.Entry<String,Fraction>> entrySet(){
		return content.entrySet();
	}
}
