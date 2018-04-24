package json_loader.error_handling;

import java.sql.SQLException;


/**
 * PostgresTableError.java
 * Error codes for PostgreSQL
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public class PostgresTableError implements DBMSErrorUtil {	
	
	// PostgresSQL used error codes
		private static final String UNQ_VIOLATED = "23505";
		private static final String FK_VIOLATED = "23503";
	
	@Override
	public DBMSError translate( int errorSGBD){
		return DBMSError.UNKNOWN; //PostgreSQL doesn't support error codes
	}

	@Override
	public DBMSError translate(String errorSGBD){
		
		switch (errorSGBD) {
		
			case FK_VIOLATED:				
				return DBMSError.FK_VIOLATED;
			case UNQ_VIOLATED:
				return DBMSError.UNQ_VIOLATED;
			
		}
		return DBMSError.UNKNOWN;
	}
	
	@Override
	public boolean checkExceptionToCode(SQLException ex, DBMSError error) {
		return new PostgresTableError().translate(ex.getSQLState()) == error;
	}
} 