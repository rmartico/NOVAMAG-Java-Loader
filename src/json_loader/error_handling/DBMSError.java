package json_loader.error_handling;

/**
 * DBMSError.java
 * Enumeration containing the SQL errors that can happen in a RDBMS application
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public enum DBMSError {
	FK_VIOLATED, // Foreign key violation
	UNQ_VIOLATED, // Primary key violation
	NOT_EXISTS_SEQUENCE, // The sequence does not exists
	// Add more errors below if necessary
	
	UNKNOWN; // No determinado.
}
