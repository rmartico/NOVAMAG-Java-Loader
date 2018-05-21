/*
 *      Copyright (C) 2017-2018 UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056  see the file COPYING.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 */

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
