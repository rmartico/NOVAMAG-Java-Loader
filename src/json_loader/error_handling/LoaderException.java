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
package json_loader.error_handling;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoaderException.java
 * Error codes for logical errors in the application
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public class LoaderException extends SQLException{

	private static final long serialVersionUID = 1L;
	
	
	//Command line exceptions and fatal exceptions
	public static final int MISSING_ARG = 0;
	public static final int INTERRUPTED_BACK_UP = 1;
	public static final int UNSUPPORTED_OS = 2;
	
	//Material_features Table exceptions
	public static final int INCORRECT_TYPE = 11;
	
	public static final int LATTICE_PARAMETERS_DISTINCT_THAN_3 = 12;
	public static final int A_LATTICE_PARAMETER_IS_NOT_NUMERIC = 13;
	public static final int LATTICE_PARAMETER_OUT_OF_RANGE = 14;
	
	public static final int LATTICE_ANGLES_DISTINCT_THAN_3 = 15;
	public static final int A_LATTICE_ANGLE_IS_NOT_NUMERIC = 16;
	public static final int LATTICE_ANGLE_OUT_OF_RANGE = 17;
	
	//public static final int ATOMIC_POSITIONS_INCOMPLETE = 18;	
	public static final int AN_ATOMIC_POSITION_IS_NOT_NUMERIC = 19;
	public static final int ATOMIC_POSITIONS_OUT_OF_RANGE = 20;
	public static final int MORE_ATOMIC_POSITIONS_THAN_ATOMS = 21;
	public static final int ATOMIC_POSITIONS_EMPTY = 22;
	public static final int ANISOTROPY_ENENRGY_TYPE_INCORRECT = 23;
	public static final int KIND_OF_ANISOTROPY_INCORRECT = 24;	
	
	//Parsing formula exceptions
	//public static final int NOT_ALLOWED_CHAR_IN_FORMULA = 100;
	//public static final int FIRST_CHAR_IN_FORMULA_HAS_TO_BE_A_Z = 101;
	//public static final int NUMBER_FOLLOWED_BY_a_z_IN_FORMULA = 102;
	
	public static final int BAD_FORMULA = 103;
	public static final int REPEATED_ELEMENT_IN_FORMULA = 104;
	public static final int MISSING_TYPE_OF_FORMULA = 105;
	public static final int CENTESIMAL_FORMULA_DOES_NOT_SUM_ONE = 106;
	public static final int ATOM_INDEX_IS_NOT_A_NUMBER = 107;
	
	//Molecule table exceptions
	public static final int NON_EXISTENT_ATOMIC_SYMBOL = 200;
	public static final int MOLECULE_ALREADY_INSERTED = 201;
	
	//Attached files exceptions
	public static final int MISSING_ATTACHED_FILE = 300;
	public static final int NOT_ALLOWED_FILE_TYPE = 301;
	
	//Config file exceptions
	public static final int MISSING_CONFIG_FILE = 400;
	public static final int BAD_CONFIG_FILE = 401;
	public static final int MISSING_TEMP_FOLDER = 410;
	public static final int MISSING_BACKUP_FOLDER = 411;
	public static final int MISSING_PG_HOME_FOLDER = 412;
	
	
	private int code; // = -1;
	private String msg;
	
	private static Logger l = LoggerFactory.getLogger(LoaderException.class);		
	
	/**
	 * Constructor of the class
	 * Given the error code the constructor assigns the corresponding error message
	 * 
	 * @param arg_code
	 */
	public LoaderException(int arg_code) {
		code = arg_code;
		msg = null;
		
		switch (code) {
		case MISSING_ARG:
			msg = "The command line usage requires the name of the JSON file as first argument";
			break;
		
		case INTERRUPTED_BACK_UP:
			msg = "The backup process has been aborted. Check the doBackup script is in the res folder and has excution permissions.";
			break;
			
		case UNSUPPORTED_OS:
			msg = "This application is only runnable in Windows or Unix SOps.";
			break;
			
		case INCORRECT_TYPE:
			msg = "Approach value must be 'experimental' or 'theory'";
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
		/*
		case ATOMIC_POSITIONS_INCOMPLETE:
			msg = "There must be an atomic position for each atom";
			break;
		*/
		case MORE_ATOMIC_POSITIONS_THAN_ATOMS:						//TODO al guardarlo en la BD
			msg = "There are more atomic positions than atoms";
			break;
			
		case ATOMIC_POSITIONS_EMPTY:
			msg = "Atomic positions is empty";
			break;
			
		case AN_ATOMIC_POSITION_IS_NOT_NUMERIC:
			msg = "All the atomic positions be numeric";
			break;	
		case ATOMIC_POSITIONS_OUT_OF_RANGE:
			msg = "A atomic postion must range in [0,1]";
			break;
		case ANISOTROPY_ENENRGY_TYPE_INCORRECT:
			msg = "The anisotropy energy type only can take the values uniaxial or cubic";
			break;
		case KIND_OF_ANISOTROPY_INCORRECT:
			msg = "The kind of anisotropy can only take de values 'easy axis', 'easy plane' or 'easy cone'";
			break;
			
		/*
		case NOT_ALLOWED_CHAR_IN_FORMULA:
			msg = "Strange character in formula";
			break;
			
		case FIRST_CHAR_IN_FORMULA_HAS_TO_BE_A_Z:
			msg = "The 1st char of a formula has to be a capital letter";
			break;
			
		case NUMBER_FOLLOWED_BY_a_z_IN_FORMULA:
			msg = "Bad formula specification, a number cannot be follobed by a lowercase character";
			break;
		*/
			
		case BAD_FORMULA:
			msg = "The formula is not well formed";
			break;
					
		case REPEATED_ELEMENT_IN_FORMULA:
			msg = "In this application each element can only appear once";
			break;
			
		case MISSING_TYPE_OF_FORMULA:
			msg = "Internal error: The type of formula is not initialized";
			break;
			
		case CENTESIMAL_FORMULA_DOES_NOT_SUM_ONE:
			msg = "Stechiometric formula element indexes do not sum one.";
			break;
			
		case ATOM_INDEX_IS_NOT_A_NUMBER:
			msg = "Internal error: Not numeric atomic index in formula.";
			break;
					
		case NON_EXISTENT_ATOMIC_SYMBOL:	
			msg = "The formula uses an atomic symbol that had not be inserted into the database";
			break;
		
		case MOLECULE_ALREADY_INSERTED:
			msg = "The molecule formula alredy exists in th molecules table";
			break;
			
		case MISSING_ATTACHED_FILE:
			msg = "Json file references an inexistent attached file";
			break;
			
		case NOT_ALLOWED_FILE_TYPE:	
			msg = "Json file references a file type the application can't cope with";
			break;
			
		case MISSING_CONFIG_FILE:
			msg="The config.json file is missing. Please allocate it in the 'res' folder";
			break;
			
		case BAD_CONFIG_FILE:
			msg="Unable to read res/config,json file. Please check is a JSON encoded in utf-8 file.";
			break;
			
		case MISSING_TEMP_FOLDER:
			msg = "Unnable to unzip file. Missing TEMP_FOLDER entry in config.json file or the TEMP_FOLDER does not exists";
			break;
			
		case MISSING_BACKUP_FOLDER:
			msg = "Unnable to perform backups. Missing BACKUP_FOLDER entry in config.json file or the BACKUP_FOLDER does not exists";
			break;
			
		case MISSING_PG_HOME_FOLDER:
			msg = "Unnable to perform backups. Missing PG_HOME entry in config.json file or the PG_HOME folder does not exists";
			break;
		
		}

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
