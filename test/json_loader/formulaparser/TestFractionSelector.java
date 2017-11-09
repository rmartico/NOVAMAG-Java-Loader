package json_loader.formulaparser;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFractionSelector {
	TreeMap<String, CandidateFractions> allFractions = new TreeMap<String, CandidateFractions>();
	FractionsSelector fs;
	
	@Before
	public void setUp() throws Exception {
		
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
		
		fs = new FractionsSelector();
		fs.setAllF(allFractions);		
	}

	CandidateFractions cs = null;

	
	@Test
	public void testGetFirst(){
		
		
		FractionsSelector head = fs.getFirst();
		TreeMap<String, CandidateFractions> allF=head.getAllF();
		assert(allF.size()==1);
		
		TreeMap<String, TreeMap<String,Fraction>> allC=head.getAllC();
		assert(allC.size()==0);
		
		for(Map.Entry<String,CandidateFractions> entry1 : allF.entrySet() ) {
			assert(entry1.getKey()=="X");

			CandidateFractions c= entry1.getValue();
			int i=0;
			for(Map.Entry<String,Fraction> entry2 : c.entrySet() ) {
				
				switch (i){
					case 0:
						assert(entry2.getKey()=="1/2");
						assert(entry2.getValue().getNumerator()==1);
						assert(entry2.getValue().getDenominator()==2);
						break;
						
					case 1:
						assert(entry2.getKey()=="1/3");
						assert(entry2.getValue().getNumerator()==1);
						assert(entry2.getValue().getDenominator()==3);
						break;
						
					case 2:
						assert(entry2.getKey()=="1/4");
						assert(entry2.getValue().getNumerator()==1);
						assert(entry2.getValue().getDenominator()==4);
						break;
						
					case 3:
						fail();
						break;
				}	
				
				i++;
			}
		}		
	}
	
	@Test
	public void testGetTail(){
		/*
		ALL FRACTIONS______________size: 2
		Y
		2/5=2/5
		2/6=2/6
		2/7=2/7
		Z
		3/10=3/10
		3/8=3/8
		3/9=3/9
		ALL COMBINATIONS______________size: 0
		 */
		FractionsSelector tail = fs.getTail();
		TreeMap<String, CandidateFractions> allF=tail.getAllF();
		assert(allF.size()==2);
		
		TreeMap<String, TreeMap<String,Fraction>> allC=tail.getAllC();
		assert(allC.size()==0);

		int j=0;
		for(Map.Entry<String,CandidateFractions> entry1 : allF.entrySet() ) {
			
			switch (j){
				case 0:			
					assert(entry1.getKey()=="Y");
					break;
				case 1:
					assert(entry1.getKey()=="Z");
					break;
			}

			CandidateFractions c= entry1.getValue();
			int i=0;
			for(Map.Entry<String,Fraction> entry2 : c.entrySet() ) {
				int k=j*10+i;
				switch (k){
					case 0:
						assert(entry2.getKey()=="2/5");
						break;						
					case 1:
						assert(entry2.getKey()=="2/6");
						break;						
					case 2:
						assert(entry2.getKey()=="2/7");
						break;						
					case 3:
						fail();
						break;
					case 10:
						assert(entry2.getKey()=="3/10");
						break;
					case 11:
						assert(entry2.getKey()=="3/8");
						break;
					case 12:
						assert(entry2.getKey()=="3/9");
						break;
					case 13:
						fail();
						break;						
				}	
				
				i++;
			}
			
			j++;
		}
	}

	@Test
	public void testComputeAllCombinations(){
		String expectedToString=
				"ALLFRACTIONS______________size:3"+
				"X1/2=1/21/3=1/31/4=1/4Y2/5=2/52/6=2/62/7=2/7Z3/10=3/103/8=3/83/9=3/9"+
				"ALLCOMBINATIONS______________size:27"+
				"X1/2Y2/5Z3/10X=1/2Y=2/5Z=3/10"+"X1/2Y2/5Z3/8X=1/2Y=2/5Z=3/8"+"X1/2Y2/5Z3/9X=1/2Y=2/5Z=3/9"+
				"X1/2Y2/6Z3/10X=1/2Y=2/6Z=3/10"+"X1/2Y2/6Z3/8X=1/2Y=2/6Z=3/8"+"X1/2Y2/6Z3/9X=1/2Y=2/6Z=3/9"+
				"X1/2Y2/7Z3/10X=1/2Y=2/7Z=3/10"+"X1/2Y2/7Z3/8X=1/2Y=2/7Z=3/8"+"X1/2Y2/7Z3/9X=1/2Y=2/7Z=3/9"+
				"X1/3Y2/5Z3/10X=1/3Y=2/5Z=3/10"+"X1/3Y2/5Z3/8X=1/3Y=2/5Z=3/8"+"X1/3Y2/5Z3/9X=1/3Y=2/5Z=3/9"+
				"X1/3Y2/6Z3/10X=1/3Y=2/6Z=3/10"+"X1/3Y2/6Z3/8X=1/3Y=2/6Z=3/8"+"X1/3Y2/6Z3/9X=1/3Y=2/6Z=3/9"+
				"X1/3Y2/7Z3/10X=1/3Y=2/7Z=3/10"+"X1/3Y2/7Z3/8X=1/3Y=2/7Z=3/8"+"X1/3Y2/7Z3/9X=1/3Y=2/7Z=3/9"+
				"X1/4Y2/5Z3/10X=1/4Y=2/5Z=3/10"+"X1/4Y2/5Z3/8X=1/4Y=2/5Z=3/8"+"X1/4Y2/5Z3/9X=1/4Y=2/5Z=3/9"+
				"X1/4Y2/6Z3/10X=1/4Y=2/6Z=3/10"+"X1/4Y2/6Z3/8X=1/4Y=2/6Z=3/8"+"X1/4Y2/6Z3/9X=1/4Y=2/6Z=3/9"+
				"X1/4Y2/7Z3/10X=1/4Y=2/7Z=3/10"+"X1/4Y2/7Z3/8X=1/4Y=2/7Z=3/8"+"X1/4Y2/7Z3/9X=1/4Y=2/7Z=3/9";
		
		fs.computeAllCombinations();
		String resultToString=fs.toString();
		
		//System.out.println(expectedToString);
		//System.out.println("=================");
		//System.out.println(resultToString.replaceAll("\\s",""));
		
		assertEquals(expectedToString,resultToString.replaceAll("\\s","")); //Get rid of spaces and non-visible chars)
	}
	
	@Test
	public void testLetLowestDenominator(){
		fs.computeAllCombinations();
		FractFormula cf = fs.getLowestDenominator();

		String expectedToString="X1/2Y2/5Z3/10"+"GCD=10"+"X5/10Y4/10Z3/10";
		String resultToString=cf.toString();
		
		assertEquals(expectedToString,resultToString.replaceAll("\\s","")); //Get rid of spaces and non-visible chars)
	}
	
}
