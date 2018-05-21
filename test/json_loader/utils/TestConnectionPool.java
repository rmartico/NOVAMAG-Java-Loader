package json_loader.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestConnectionPool {

	@Test
	public void testGetPassword() {
		
		ConnectionPool p = ConnectionPool.getInstance();
		assertEquals(p.getPassword(),"postgres");		
	}

}
