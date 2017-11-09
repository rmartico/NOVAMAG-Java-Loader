package json_loader.error_handling;

/**
 * Errores en un SGBD.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public enum DBMSError {
	FK_VIOLATED, // Violación de clave foránea
	UNQ_VIOLATED, // Violación de clave primaria
	NOT_EXISTS_SEQUENCE, // No existe la secuencia utilizada
	// Añadir antes de esta línea si fuera necesario
	
	UNKNOWN; // No determinado.
}
