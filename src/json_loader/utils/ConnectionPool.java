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

import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConnectionPool.java
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */
@SuppressWarnings("deprecation")
public class ConnectionPool {	
	
	private static final String USER = "postgres";
	private static final String PASSWORD = "postgres";	
	private static final String HOST = "localhost";
	private static final String DATABASE = "postgres";
	
	private static ConnectionPool m_pool = null;
	
	private DataSource ds = null;		
		
	private static Logger l = LoggerFactory.getLogger(ConnectionPool.class);	
	
	/**
	 * Main method: You can use it to rewrite the res/.bindings file
	 * 	that contains the JNDI context with the pooled datasource configuration
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		rewriteConfiguration(); 
		System.out.println("END--ConnectionPool");
	}
	
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
	 * It changes the JNDI context where the connection pool configuration is recorded
	 * At this version the new values comes from the private static final variables in the class 
	 * 
	 * @throws NamingException (probably) if the resource to rebind is not found
	 * @throws SQLException
	 */
	static void rewriteConfiguration() throws NamingException, SQLException {
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.fscontext.RefFSContextFactory");
		properties.setProperty(Context.PROVIDER_URL, "file:./res");

		Context context = new InitialContext(properties);
		
		/*
		 * 
		 */
		PGPoolingDataSource source = new PGPoolingDataSource();
		source.setDataSourceName("Novamag Loader Datasource");
		source.setServerName(HOST);
		source.setDatabaseName(DATABASE);
		source.setUser(USER);
		source.setPassword(PASSWORD);
		source.setMaxConnections(10);
		
		context.rebind("jdbc/novamag", source);
	}	

}
