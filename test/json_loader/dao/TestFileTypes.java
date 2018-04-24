package json_loader.dao;

import static org.junit.Assert.*;

import org.junit.Test;

import json_loader.error_handling.LoaderException;

public class TestFileTypes {
	
	@Test
	public void testGetFileType() throws LoaderException {
		
		
		FileTypes fp = FileTypes.getInstance();
		
		String type;
		String fileName;
		
		FileType ft;		
		
		
		fileName="my_test.jpg";
		ft = fp.getFileType(fileName);
		type = ft.getType();
		assertEquals(type,"JPG");
		//System.out.println(fileName+": "+type);
		
		fileName="my_test.CIF";
		ft = fp.getFileType(fileName);
		type = ft.getType();
		//System.out.println(fileName+": "+type);
		assertEquals(type,"CIF");
		
		fileName="CONTCAR_my_test";
		ft = fp.getFileType(fileName);
		type = ft.getType();
		//System.out.println(fileName+": "+type);
		assertEquals(type,"CONTCAR");
		
		fileName="my_test.my_test";
		try{
			ft = fp.getFileType(fileName);
			type = ft.getType();
		//System.out.println(fileName+": "+type);
			fail();			
		} catch (LoaderException e){
			assertEquals(e.getErrorCode(),LoaderException.NOT_ALLOWED_FILE_TYPE);
		}
		
		
	}

}
