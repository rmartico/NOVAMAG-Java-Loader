package json_loader.formulaparser;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import json_loader.utils.Comparators;

public class TestFraction {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRealToFraction() {
		Fraction f=null;
		BigDecimal epsilon=new BigDecimal(0.01);
		
		f= Fraction.RealToFraction( new BigDecimal(0.25), epsilon );
		assertEquals(f.getNumerator(),1);
		assertEquals(f.getDenominator(),4);
		//System.out.println(f+"=1/4");
		
		BigDecimal x=null;
		BigDecimal y=null;
		x = BigDecimal.ONE;
		y= new BigDecimal(7.0);
		x = x.divide(y, 3, RoundingMode.HALF_UP);
		f= Fraction.RealToFraction(x, epsilon);
				
		//System.out.println(f+"=1/7"+"="+x);
		assertEquals(f.getNumerator(),1);
		assertEquals(f.getDenominator(),7);
	}
		
	@Test
	public void testRealToAproximateFraction() {
			//TreeMap<String, Fraction> candidates = null;
			CandidateFractions candidates = null;
			
			BigDecimal epsilon=new BigDecimal(0.01);
			
			BigDecimal x=BigDecimal.ONE;
			BigDecimal y=null;
		
			y= new BigDecimal(7.0+0.001);
			x = x.divide(y, 3, RoundingMode.HALF_UP);
			candidates = Fraction.RealToAproximateFraction(x, epsilon);
			
			int i=0;
			//for (Map.Entry<String, Fraction> entry : candidates.entrySet()){
			for (Map.Entry<String, Fraction> entry : candidates.content.entrySet()){
				//String key =entry.getKey();
				Fraction f = entry.getValue();
				//System.out.println(f+"=1/7");				
				
				if (i==0){
					assertEquals(f.getNumerator(),1);
					assertEquals(f.getDenominator(),7);
				} else if (i==1){
					assertEquals(f.getNumerator(),2);
				    assertEquals(f.getDenominator(),15);
					
				} else fail();
				
				i++;
			}
		
			y= new BigDecimal(7.0-0.001);
			x = BigDecimal.ONE;
			x = x.divide(y, 3, RoundingMode.HALF_UP);
			candidates = Fraction.RealToAproximateFraction(x, epsilon);
			
			i=0;
			for (Map.Entry<String,Fraction> entry : candidates.content.entrySet()){
				//String key =entry.getKey();
				Fraction f = entry.getValue();
				//System.out.println(f+"=1/7");				
				
				if (i==0){
					assertEquals(f.getNumerator(),1);
					assertEquals(f.getDenominator(),7);				
				} else if (i==1){
					assertEquals(f.getNumerator(),2);
				    assertEquals(f.getDenominator(),15);
				} else fail();
				
				i++;
			}
			
			y= new BigDecimal(9);
			x = new BigDecimal(4);
			x = x.divide(y, 3, RoundingMode.HALF_UP);
			x = new BigDecimal(0.45);
			candidates = Fraction.RealToAproximateFraction(x, epsilon);
			
			i=0;
			for (Map.Entry<String, Fraction> entry : candidates.content.entrySet()){
				//String key =entry.getKey();
				Fraction f = entry.getValue();

				//System.out.println(f+"=4/9");		
				
				if (i==0){
					assertEquals(f.getNumerator(),4);
					assertEquals(f.getDenominator(),9);				
				} else if (i==1){
					assertEquals(f.getNumerator(),5);
				    assertEquals(f.getDenominator(),11);
				} else if (i==2){
					assertEquals(f.getNumerator(),7);
				    assertEquals(f.getDenominator(),16);
				} else if (i==3){
					assertEquals(f.getNumerator(),9);
				    assertEquals(f.getDenominator(),20);
				} else fail();
				
				i++;

			}
			
	}

}
