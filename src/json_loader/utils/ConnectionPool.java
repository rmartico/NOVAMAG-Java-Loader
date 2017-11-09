package json_loader.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//import org.postgresql.ds.PGConnectionPoolDataSource;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json_loader.error_handling.PostgresTableError;
import json_loader.error_handling.DBMSErrorUtil;

public class ConnectionPool {	
	
	private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
	private static final String USER = "postgres";
	private static final String PASSWORD = "postgres";
	
	private static final String HOST = "localhost";
	private static final String DATABASE = "postgres";
	
	private static final DBMSErrorUtil m_tableError = new PostgresTableError();
	
	private static ConnectionPool m_pool = null;
	
	private Context context = null;
	private DataSource ds = null;		
		
	private static Logger l = null;	

	private void setLogger() throws IOException {
		if (l == null) {			
			l =	LoggerFactory.getLogger(ConnectionPool.class);			
			l.info("Comienzo Ejecución");			
		}
		return;
	}
	
	public static void main(String[] args) throws Exception{
		rewriteConfiguration(); 
		System.out.println("END--ConnectionPool");
	}
	
	private ConnectionPool() throws NamingException, SQLException, IOException {			
		setLogger();
		
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.fscontext.RefFSContextFactory");
		properties.setProperty(Context.PROVIDER_URL, "file:./res");

		Context context = new InitialContext(properties);

		ds = (DataSource) context.lookup("jdbc/novamag");
		
		return;
	}
	
	public static ConnectionPool getInstance() throws NamingException, SQLException, IOException{
		if (m_pool==null){
			m_pool = new ConnectionPool();
		}
		return m_pool;
	}
	
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
	
	public String traceConnectionSettings(Connection conn) throws SQLException{
		
		String retorno="Activacion de Autocommit="+ conn.getAutoCommit()+"\n";
		
		retorno += "Nivel de Aislamiento=";
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
		
		
		/*
		PGConnectionPoolDataSource pds = null;
		// Creación de un objeto DataSource		
		pds = new PGConnectionPoolDataSource();

		pds.setUrl(URL);
		pds.setUser(USER);
		pds.setPassword(PASSWORD);
		
		context.rebind("jdbc/novamag", pds);
		*/
		context.rebind("jdbc/novamag", source);
	}	
	
	public DBMSErrorUtil getTableError(){
		return m_tableError;
	}
}
