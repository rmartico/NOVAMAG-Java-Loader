package json_loader.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * JSONformatter.java
 * 
 * Class that performs concatenation and nesting of json blocks using an String
 * This class is implemented to keep the order of elements from original JSON
 * files given by researchers. This way the JSON is more readable to them.
 * 
 * Note: One alternative implementation would store the JSON file as it is into
 * the database. However, when the file contains an array of several materials
 * such implementation is not straightforward.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
public class JSONformatter {
	private static Logger l = LoggerFactory.getLogger(JSONformatter.class);
	
	private static String TAB="  ";
	
	private String m_formattedJson = "";
	private String m_name = null; //null => root section 
	
	public JSONformatter( String sectionName ){
		m_name = sectionName;
		if (sectionName==null)
			m_formattedJson = "{\n";		
		else
			m_formattedJson = "\""+sectionName+"\": {\n";
	}
	
	/**	
	 * Concatenates a given OBJECT-attribute to the json block represented
	 * by the current JSONformatter object.
	 * 
	 * @param name is the name of the attribute
	 * @param value is the value of the attribute. It must be converted to
	 * string in the invocation
	 * 
	 * For example
	 * 
	 * myJSONformatter.addItem( "myAttributeBoolean", ""+true);
	 * 
	 * For String type values use addStringItem (see below) because it
	 * puts the required quotes.
	 */	
	public void addItem( String name, String value){
		m_formattedJson += ("\"" + name + "\": {\n");
		m_formattedJson += (JSONformatter.TAB +	"\"value\": "+value+"\n");
		m_formattedJson += "},\n";
	}
	
	/**	
	 * Concatenates a given String-OBJECT-attribute to the json block represented
	 * by the current JSONformatter object.
	 * 
	 * @param name is the name of the attribute
	 * @param value is the value of the attribute. 
	 * 
	 * For example
	 * 
	 * myJSONformatter.addStringItem( "myAttributeString", "Hello World");
	 *	 
	 */	
	public void addStringItem( String name, String value){
		
		m_formattedJson += ("\"" + name + "\": {\n");
		if (value==null)
			m_formattedJson += (JSONformatter.TAB +	"\"value\": null\n");
		else
			m_formattedJson += (JSONformatter.TAB +	"\"value\": \""+value+"\"\n");
		
		m_formattedJson += "},\n";
	}
	
	/**	
	 * Concatenates a given VALUE-attribute to the json block represented
	 * by the current JSONformatter object.
	 * 
	 * @param name is the name of the attribute
	 * @param value is the value of the attribute. It must be converted to
	 * string in the invocation
	 * 
	 * For example
	 * 
	 * myJSONformatter.addValue( "myAttributeBoolean", ""+true);
	 * 
	 * For String type values use addStringItem (see below) because it
	 * puts the required quotes.
	 */	
	public void addValue( String name, String value){
		m_formattedJson += ("\"" + name + "\": " + value+",\n");
	}
	
	/**
	 * It nests a JSON block at the end of the JSON document in the formatter. 
	 * 
	 * @param jsf is the JSON BLOCK to nest
	 */
	public void addSection ( JSONformatter jsf ){
		
		String jsfStr = jsf.getJSONformatted();
		jsfStr = jsfStr.substring(0, jsfStr.length() - 2); //Remove last comma and return		
	
		//Identation
		int whereFirstReturn = jsfStr.indexOf("\n");
		
		String firstLine = jsfStr.substring(0,whereFirstReturn);
		String theRest = jsfStr.substring(whereFirstReturn+1);
		theRest = JSONformatter.TAB + theRest.replaceAll("\n", "\n"+JSONformatter.TAB);
		jsfStr = firstLine +"\n"+theRest;
		
		jsfStr += "\n},\n"; //Close }, 	
		
		m_formattedJson += jsfStr;
	}

	/**
	 * It closes the current JSON document in the formatter
	 * It cares for last "}" and indentation within first "{"
	 * and last "}"
	 */
	public void closeDocument (){
		String jsfStr = m_formattedJson;
		jsfStr = jsfStr.substring(0, jsfStr.length() - 2); //Remove last comma and return		
	
		//Indentation
		int whereFirstReturn = jsfStr.indexOf("\n");
		
		String firstLine = jsfStr.substring(0,whereFirstReturn);
		String theRest = jsfStr.substring(whereFirstReturn+1);
		theRest = JSONformatter.TAB + theRest.replaceAll("\n", "\n"+JSONformatter.TAB);
		jsfStr = firstLine +"\n"+theRest;
		
		jsfStr += "\n}"; //Close } EOF, 	
		
		m_formattedJson = jsfStr;
	}
	
	/**
	 * It returns the String containing the JSON document
	 * formatted at it is in this moment.  
	 * 
	 * @return the String containing the JSON document
	 */
	public String getJSONformatted(){
		return m_formattedJson;
	}
	
	/**
	 * 
	 * toString method for debugging purposes
	 * 
	 * It returns the String containing the JSON document
	 * formatted at it is in this moment.  
	 */
	public String toString(){
		return getJSONformatted();
	}
}
