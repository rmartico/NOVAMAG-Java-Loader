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
package json_loader.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RebindJNDI.java
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */

public class ConnectionPool {	
	private static ConnectionPool m_pool = null;
	
	private DataSource ds = null;		
		
	private static Logger l = LoggerFactory.getLogger(ConnectionPool.class);	
	
	private String password = null; //The db password
	
	
	/**
	 * Private constructor for Singleton design pattern implementation
	 * It uses JNDI to get the pool configuration
	 */
	private ConnectionPool() {			
	
		try {
			Properties properties = new Properties();
			properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.fscontext.RefFSContextFactory");
			properties.setProperty(Context.PROVIDER_URL, "file:./res");
	
			Context context = new InitialContext(properties);
	
			ds = (DataSource) context.lookup("jdbc/novamag");
			
			password = ((org.postgresql.ds.common.BaseDataSource) ds).getPassword();
		} catch (NamingException e) {
			l.error("FATAL: Can't find the JNDI resuorce for the connection pool");
			l.error(e.getMessage());
			
			throw new RuntimeException();
		}		
		return;
	}
	/**
	 * Connection pool factory that implements Singleton design pattern
	 * It gets a connection pool instance if it doesn't exists yet
	 * 
	 * @return the connection pool instance
	 */
	public static ConnectionPool getInstance(){
		if (m_pool==null){
			m_pool = new ConnectionPool();
		}
		return m_pool;
	}
	
	/**
	 * 
	 * Make rollback. It surrounds the rollback in a try-catch block
	 * (to avoid a try-catch nested in catch block in methods implementing transactions)
	 * 
	 * @param conn is the connection where rollback is performed
	 */
	public void undo(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback(); 
				l.info("Rollback  OK");				
			} catch (SQLException e) {
				l.error("Problem trying ROLLBACK");				
			}
		}
	}
	
	/**
	 * 
	 * Close the connection. It surrounds the close statement in a try-catch block
	 * (to avoid a try-catch nested in finally block in methods implementing transactions)
	 * 
	 * @param conn is the connection to close
	 */
	public void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
				l.info("Connection closed OK");				
			} catch (SQLException e) {
				l.error("Problem trying to close connection");
			}
		}
	}
	
	/**
	 * 
	 * Close a statement. It surrounds it in a try-catch block
	 * (to avoid a try-catch nested in finally block in methods implementing transactions)
	 * 
	 * @param statement is the statement to close
	 */
	public void close(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
				l.info("Statement closed OK");
			} catch (SQLException e) {				
				l.error("Problem trying to close statement");
			}
		}
	}
	
	/**
	 * 
	 * Close a result set. It surrounds it in a try-catch block
	 * (to avoid a try-catch nested in finally block in methods implementing transactions)
	 * 
	 * @param rs is the result set to close
	 */
	public void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				l.info("Resultset closed OK");
			} catch (SQLException e) {				
				l.error("Problem trying to close Resultset");
			}
		}
	}
	
	/**
	 * 
	 * It gets a connection from the connection pool
	 * 
	 * @return the new logical connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException{
		
		Connection conn=null;
		
		conn=ds.getConnection();			
		
		conn.setAutoCommit(false);
		l.info("Autocommit activated={}",conn.getAutoCommit());	
		
		
		//conn.setTransactionIsolation(Connection.TRANSACTION_NONE);  				//No válido en Oracle
		conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		//conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); //No válido en Oracle
		//conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);	//No valido en Oracle
		//conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);		
			
		l.info(traceConnectionSettings(conn));
		
		return conn;
	}
	
	/**
	 * 
	 * It builds an string to be printed for debugging purposes
	 * The string contains details on a given connection configuration 
	 * 
	 * @param conn is the connection which info is showed by the method
	 * @return the string containing the configuration info
	 * @throws SQLException
	 */
	public String traceConnectionSettings(Connection conn) throws SQLException{
		
		String retorno="Autocommit activation="+ conn.getAutoCommit()+"\n";
		
		retorno += "Isolation level=";
		switch (conn.getTransactionIsolation()){
		case Connection.TRANSACTION_NONE:
			retorno += "TRANSACTION_NONE";
			break;
		case Connection.TRANSACTION_READ_COMMITTED:
			retorno += "TRANSACTION_READ_COMMITTED";
			break;
		case Connection.TRANSACTION_READ_UNCOMMITTED:
			retorno += "TRANSACTION_READ_UNCOMMITTED";
			break;
		case Connection.TRANSACTION_REPEATABLE_READ:
			retorno += "TRANSACTION_REPEATABLE_READ";
			break;
		case Connection.TRANSACTION_SERIALIZABLE:
			retorno += "TRANSACTION_REPEATABLE_READ";
			break;
		}

		return retorno;
	}

	/**
	 * 
	 * This method is necessary for the Backup.java class
	 * which invokes pg_dump. pg_dump asks for password when invoked
	 * So, the JNDI property containing the password is re-used to
	 * provide this info.    
	 * 
	 * @return the db connection password
	 */
	public String getPassword(){
		return password;
	}
}
