package json_loader.error_handling;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoaderException extends SQLException{

	private static final long serialVersionUID = 1L;
	
	//Material_features Table exceptions
	public static final int INCORRECT_TYPE = 1;
	
	public static final int LATTICE_PARAMETERS_DISTINCT_THAN_3 = 2;
	public static final int A_LATTICE_PARAMETER_IS_NOT_NUMERIC = 3;
	public static final int LATTICE_PARAMETER_OUT_OF_RANGE = 4;
	
	public static final int LATTICE_ANGLES_DISTINCT_THAN_3 = 5;
	public static final int A_LATTICE_ANGLE_IS_NOT_NUMERIC = 6;
	public static final int LATTICE_ANGLE_OUT_OF_RANGE = 7;
	
	public static final int ATOMIC_POSITIONS_INCOMPLETE = 8;
	public static final int ATOMIC_POSITIONS_IS_NOT_NUMERIC = 9;
	public static final int ATOMIC_POSITIONS_OUT_OF_RANGE = 10;
	
	//Parsing formula exceptions
	public static final int NOT_ALLOWED_CHAR_IN_FORMULA = 100;
	public static final int FIRST_CHAR_IN_FORMULA_HAS_TO_BE_A_Z = 101;
	public static final int NUMBER_FOLLOWED_BY_a_z_IN_FORMULA = 102;
	
	public static final int BAD_FORMULA = 103;
	//public static final int REPEATED_ELEMENT_IN_FORMULA = 104;
	
	//Molecule table exceptions
	public static final int NON_EXISTENT_ATOMIC_SYMBOL = 200;
	public static final int MOLECULE_ALREADY_INSERTED = 201;
	
	private int code; // = -1;
	private String msg;
	
	private static Logger l = null;
	
	public LoaderException(int arg_code) {
		code = arg_code;
		msg = null;
		
		switch (code) {
		case INCORRECT_TYPE:
			msg = "Approach value must be 'experimental' or 'therory'";
			break;
			
		case LATTICE_PARAMETERS_DISTINCT_THAN_3:
			msg = "The number of lattice parameters must be exactly 3";
			break;
		case A_LATTICE_PARAMETER_IS_NOT_NUMERIC:
			msg = "All the lattice parameters must be numeric";
			break;	
		case LATTICE_PARAMETER_OUT_OF_RANGE:
			msg = "A lattice parameter is out of range";
			break;	
		
		case LATTICE_ANGLES_DISTINCT_THAN_3:
			msg = "The number of lattice angles must be exactly 3";
			break;
		case A_LATTICE_ANGLE_IS_NOT_NUMERIC:
			msg = "All the lattice angles must be numeric";
			break;	
		case LATTICE_ANGLE_OUT_OF_RANGE:
			msg = "A lattice angle is out of range";
			break;	
		
		case ATOMIC_POSITIONS_INCOMPLETE:
			msg = "There must be an atomic position for each atom";
			break;
		case ATOMIC_POSITIONS_IS_NOT_NUMERIC:
			msg = "All the atomic positions be numeric";
			break;	
		case ATOMIC_POSITIONS_OUT_OF_RANGE:
			msg = "A atomic postion must range in [0,1]";
			break;	
			
		case NOT_ALLOWED_CHAR_IN_FORMULA:
			msg = "Strange character in formula";
			break;
			
		case FIRST_CHAR_IN_FORMULA_HAS_TO_BE_A_Z:
			msg = "The 1st char of a formula has to be a capital letter";
			break;
			
		case NUMBER_FOLLOWED_BY_a_z_IN_FORMULA:
			msg = "Bad formula specification, a number cannot be follobed by a lowercase character";
			break;
			
		case BAD_FORMULA:
			msg = "The formula is not well formed";
			break;
			
		/*	
		case REPEATED_ELEMENT_IN_FORMULA:
			msg = "In this application each element can only appear once";
			break;
		*/
			
		case NON_EXISTENT_ATOMIC_SYMBOL:	
			msg = "The formula uses an atomic symbol that had not be inserted into the database";
			break;
		
		case MOLECULE_ALREADY_INSERTED:
			msg = "The molecule formula alredy exists in th molecules table";
			break;
		}
		
		l =	LoggerFactory.getLogger(LoaderException.class);			

		l.error(msg);

		// Stack trace
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			l.info(ste.toString());
		}
		
	}
	
	@Override
	public String getMessage() {
		return msg;
	}

	@Override
	public int getErrorCode() {
		return code;
	}
	
	
	
}
