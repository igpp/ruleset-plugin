package igpp.ruleset.plugin;

// import igpp.ruleset.*;
import igpp.ruleset.Ruleset;
import igpp.ruleset.Action;

/**
 *	Compare function will compare two strings or numbers. The comparison is 
 *	case sensitive for strings. (example: "equals a EQ A" returns $equals = "FALSE")
 *  It takes the parameters:
 *		String parameter
 *		String val1
 *		String operator
 *		String val2
 *
 *	Possible values for operator are:
 *		LT - less than
 *		LE - less than or equal to
 *		GT - greater than
 *		GE - greater than or equal to
 *		EQ - equals
 *		NE - not equals
 *	it prints to the screen $parameter = TRUE or FALSE
 *	If the wrong number of arguments is passed or if the operator is not one listed 
 *	above then it prints out a message and aborts the processing in the ruleset.
 */
		

public class Compare {
	
	public static void main(String[] args){
		String value1 = args[1];
		String value2 = args[3];
		boolean	error = false;
		String	op;
		
		if(args.length != 4) error = true;
		
		if(error) {
			Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
			Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
			Ruleset.showRule(Action.MESSAGE, "\tCompare called incorrectly, proper usage Compare 'String parameter' 'val1' 'String operator' 'val2'");
			Ruleset.showRule(Action.ABORT);
			System.exit(1);
		}
		
		// process arguments
		op = Ruleset.unquote(args[2].toUpperCase());
		int answer = value1.compareTo(value2);
		if(op.equals("LT") && answer < 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "TRUE");
		}else if(op.equals("LT") && answer >= 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "FALSE");
		}else if(op.equals("LE") && answer <= 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "TRUE");
		}else if(op.equals("LE") && answer > 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "FALSE");
		}else if(op.equals("NE") && answer != 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "TRUE");
		}else if(op.equals("NE") && answer == 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "FALSE");
		}else if(op.equals("EQ") && answer == 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "TRUE");
		}else if(op.equals("EQ") && answer != 0){
			Ruleset.showRule(Action.ASSIGN, args[0], "FALSE");
		}else{
			Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
			Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
			Ruleset.showRule(Action.MESSAGE, "\tCompare called incorrectly, '" + op + "' is invalid.");
			Ruleset.showRule(Action.ABORT);
			System.exit(1);
		}
	}
}