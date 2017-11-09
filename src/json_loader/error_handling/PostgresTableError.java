package json_loader.error_handling;

import java.sql.SQLException;



public class PostgresTableError implements DBMSErrorUtil {	
	
	// Códigos de error en PostgresSQL
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
	
	public boolean checkExceptionToCode(SQLException ex, DBMSError error) {
		return new PostgresTableError().translate(ex.getSQLState()) == error;
	}
} 