package igpp.ruleset.plugin;

import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

/**
 *	This is the IMATH function that does integer arithmatic. It takes the parameters:
 *		String parameter
 *		=
 *		int val1
 *		String operator
 *		int val2
 *		
 *	it prints to the screen $parameter = answer
 *	If the wrong number of arguments is passed or if val1 and val2 are not int's 
 *	then it prints out a message and aborts the processing in the ruleset.
 */
		

public class IMath {
	
	public static void main(String[] args){
		int value1 = 0;
		int value2 = 0;
		int answer = 0;
		boolean	error = false;
		String	op;
		
		if(args.length != 5) error = true;
		if(!error && !args[1].equals("=")) error = true;
		
		if(error) {
			Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
			Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
			Ruleset.showRule(Action.MESSAGE, "\tIMATH called incorrectly, proper usage IMATH 'String parameter' = 'int val1' 'String operator' 'int val2'");
			Ruleset.showRule(Action.ABORT);
			System.exit(1);
		}
		
		try{
			value1 = Integer.parseInt(args[2]);
		} catch(Exception e) {
			Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
			Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
			Ruleset.showRule(Action.MESSAGE, "\tIMATH called incorrectly, val1 must be an integer.");
			Ruleset.showRule(Action.ABORT);
			System.exit(1);
		}
		
		try{
			value2 = Integer.parseInt(args[4]);
		} catch(Exception e) {
			Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
			Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
			Ruleset.showRule(Action.MESSAGE, "\tIMATH called incorrectly, val2 must be an integer.");
			Ruleset.showRule(Action.ABORT);
			System.exit(1);
		}
		
		// process arguments
		op = args[3].toUpperCase();
		if(op.equals("+") || op.equals("ADD") || op.equals("PLUS")){
			answer = value1+value2;
			Ruleset.showRule(Action.ASSIGN, args[0], answer);
		}else if(op.equals("-") || op.equals("SUBTRACT") || op.equals("MINUS")){
			answer = value1-value2;
			Ruleset.showRule(Action.ASSIGN, args[0], answer);
		}else if(op.equals("*") || op.equals("MULTIPLY") || op.equals("TIMES")){
			answer = value1*value2;
			Ruleset.showRule(Action.ASSIGN, args[0], answer);
		}else if(op.equals("/") || op.equals("DIVIDE")){
			if(value2 == 0){
				Ruleset.showRule(Action.ASSIGN,  args[0], "-998");
			}else{
				if(value1%value2 == 0){
					answer = value1/value2;
					Ruleset.showRule(Action.ASSIGN, args[0], answer);
				}else{
					Ruleset.showRule(Action.ASSIGN, args[0], "-999");
				}
			}
		}else{
			Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
			Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
			Ruleset.showRule(Action.MESSAGE, "\tIMATH called incorrectly, operator '" + op + "' is invalid.");
			Ruleset.showRule(Action.ABORT);
			System.exit(1);
		}
	}
}