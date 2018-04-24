package json_loader.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Test;

import json_loader.JSONparser;
import json_loader.utils.Cleaner;
import json_loader.utils.Comparators;

public class TestDBItem {
	
	@Test
	public void testInsertOK() throws NamingException, IOException, SQLException {
		 String fileName="data_for_tests/dao.dbitem/Fe12Ge6_#164_1.json";		 
		
		 Cleaner.CleanDB();
		 Cleaner.insertAtom("Fe");
		 Cleaner.insertAtom("Ge");
		 
		 InputStream is = new FileInputStream(fileName);         
         String jsonTxt = IOUtils.toString(is, "UTF-8");
         //System.out.println(jsonTxt);
         
         JSONObject obj = new JSONObject(jsonTxt);          
         JSONparser jp = new JSONparser();
         jp.parseJSON(obj);
		
         DBitem item = jp.getItem();
         item.insert(null, true);         
         
         String query = "select formula||mafid||type||name||summary||production_info||stechiometry"
         		+ " from items natural join molecules order by 1;";
         Comparators.assertEqualsResultSet(query, 4205810780L);
         
         query = "select formula||mafid||author "+
        		 "from items natural left join authoring order by 1;";
         Comparators.assertEqualsResultSet(query, 2867132551L);
         
         query = "select formula||mafid||file_name||file_type||is_text||blob_content||info "
        		 + "from items natural left join attached_files order by 1;";
         Comparators.assertEqualsResultSet(query, 2066072091L);
        
		 
	}
	

}
