package igpp.ruleset;

import java.util.*;
import pds.label.*;

/**
 * Action is a class that contains a ruleset action definition.
 *
 * @author      Todd King
 * @author      Planetary Data System
 * @version     1.0, 04/21/03
 * @since       1.0
 */
public class Action {
 	// Enumeration of possible value type
 	/** Unspecified action. */		public static final int	NONE		= 0;
 	/** Assignment */				public static final int	ASSIGN	= 1;	
 	/** Run */						public static final int	RUN		= 2;
 	/** Include */					public static final int	INCLUDE	= 3;
 	/** Template */					public static final int	TEMPLATE= 4;
 	/** IF */						public static final int	IF		= 5;
 	/** ELSE */						public static final int	ELSE	= 6;
 	/** ELSEIF */					public static final int	ELSEIF	= 7;
 	/** ENDIF */					public static final int	ENDIF	= 8;
 	/** IGNORE */					public static final int	IGNORE	= 9;
 	/** OPTION */					public static final int	OPTION	= 10;
 	/** OUTPUT */					public static final int	OUTPUT	= 11;
 	/** MESSAGE */					public static final int	MESSAGE	= 12;
 	/** ABORT */					public static final int	ABORT	= 13;
 	/** COPY */						public static final int	COPY	= 14;
 	/** DUMP */						public static final int	DUMP	= 15;
 	/** GLOBAL */					public static final int	GLOBAL	= 16;
 	/** MKDIR */					public static final int	MKDIR	= 17;
 	/** EXISTS */					public static final int	EXISTS	= 18;
 	/** MOVE */						public static final int	MOVE	= 19;
 	/** DELETE */					public static final int	DELETE	= 20;
 	
	/** The list of elements in the label */
 	public int			mType = NONE;
 	public ArrayList	mArgument = new ArrayList();
 	public PDSElement	mElement = null;
 	
    /** 
     * Creates an instance of an action.
	 *
     * @param type	the token for the type of action to take.
     * @param arguments	the ArrayList of arguments for the action.
     *
     * @since           1.0
     */
 	public Action(int type, ArrayList arguments) {
 		mType = type;
 		for(int i = 0; i < arguments.size(); i++) mArgument.add((String) arguments.get(i));
	}
		
    /** 
     * Returns the token indentifier for a command.
	 * 
	 * @param buffer	the text containing the command word.
	 *
	 * @return	the token for the command word or <code>NONE</code> if no
	 *			token with the passed name exists.
	 *
     * @since           1.0
     */
	static public int token(String buffer) {
		if(buffer.compareToIgnoreCase("RUN") == 0)		return RUN;
		if(buffer.compareToIgnoreCase("INCLUDE") == 0)	return INCLUDE;
		if(buffer.compareToIgnoreCase("TEMPLATE") == 0)	return TEMPLATE;
		if(buffer.compareToIgnoreCase("IF") == 0)		return IF;
		if(buffer.compareToIgnoreCase("/IF") == 0)		return ENDIF;
		if(buffer.compareToIgnoreCase("ELSE") == 0)		return ELSE;
		if(buffer.compareToIgnoreCase("ELSEIF") == 0)	return ELSEIF;
		if(buffer.compareToIgnoreCase("IGNORE") == 0)	return IGNORE;
		if(buffer.compareToIgnoreCase("OPTION") == 0)	return OPTION;
		if(buffer.compareToIgnoreCase("OUTPUT") == 0)	return OUTPUT;
		if(buffer.compareToIgnoreCase("MESSAGE") == 0)	return MESSAGE;
		if(buffer.compareToIgnoreCase("ABORT") == 0)	return ABORT;
		if(buffer.compareToIgnoreCase("COPY") == 0)		return COPY;
		if(buffer.compareToIgnoreCase("DUMP") == 0)		return DUMP;
		if(buffer.compareToIgnoreCase("GLOBAL") == 0)	return GLOBAL;
		if(buffer.compareToIgnoreCase("MKDIR") == 0)	return MKDIR;
		if(buffer.compareToIgnoreCase("EXISTS") == 0)	return EXISTS;
		if(buffer.compareToIgnoreCase("MOVE") == 0)		return MOVE;
		if(buffer.compareToIgnoreCase("DELETE") == 0)	return DELETE;
		return NONE;
	}
	
    /** 
     * Prints out the action in tokenized form.
	 * 
     * @since           1.0
     */
	public void dump() {
		String	buffer = "";
		
		switch(mType) {
		case NONE:
			buffer += "    None: ";
			break;	
		case ASSIGN:
			buffer += "  Assign: ";
			break;	
		case RUN:
			buffer += "     Run: ";
			break;	
		case INCLUDE:
			buffer += " Include: ";
			break;	
		case TEMPLATE:
			buffer += "Template: ";
			break;	
		case IF:
			buffer += "      IF: ";
			break;	
		case ELSE:
			buffer += "    ELSE: ";
			break;	
		case ENDIF:
			buffer += "   ENDIF: ";
			break;	
		case ELSEIF:
			buffer += "  ELSEIF: ";
			break;	
		case IGNORE:
			buffer += "  IGNORE: ";
			break;	
		case OPTION:
			buffer += "  OPTION: ";
			break;	
		case OUTPUT:
			buffer += "  OUTPUT: ";
			break;	
		case MESSAGE:
			buffer += " MESSAGE: ";
			break;	
		case ABORT:
			buffer += "   ABORT: ";
			break;	
		case COPY:
			buffer += "    COPY: ";
			break;	
		case DUMP:
			buffer += "    DUMP: ";
			break;	
		case GLOBAL:
			buffer += "  GLOBAL: ";
			break;	
		case MKDIR:
			buffer += "  MKDIR: ";
			break;	
		case EXISTS:
			buffer += "  EXISTS: ";
			break;	
		case MOVE:
			buffer += "    MOVE: ";
			break;	
		case DELETE:
			buffer += "    DELETE: ";
			break;	
		}
		
		for(int i = 0; i < mArgument.size(); i++) {
			buffer += " " + (String) mArgument.get(i);
		}
		
		System.out.println(buffer);
	}
	
}