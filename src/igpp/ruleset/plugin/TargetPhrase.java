package igpp.ruleset.plugin;

import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

import igpp.util.Argument;

/**
 *			TargetPhrase.java June 11, 2003
 *	TargetPhrase takes a list of targets parses it and returns the list in a 
 *  phrase that can be used in a description or note.<br>
 *
 *	Required argument values:<br><pre>
 *		PARAMETER	[character string] The parameter name you want returned.
 *		PREPEND		[character string] The string you to add to the beginning of
 *					the phrase that is returned.
 *		VALUES		[character string] This is the comma separated list of values.
 *					Acceptable values are {value1,value2,...}
 *					(value1,value2,...)
 *	</pre>
 *
 * @author      Erin Means
 * @author      Planetary Data System
 * @version     1.2, 07/14/03
 * @since       1.0
 */

public class TargetPhrase{
	
	
	
	public static void main(String[] args){
		
		String parameter = Argument.find(args, "PARAMETER",	null,0);
		String prepend   = Argument.find(args, "PREPEND",	""  ,0);
		String list 	 = Argument.find(args, "VALUES",	null,0);
		
		if(parameter == null || prepend == null || list == null) {
			errorMessage("TargetPhrase called incorrectly, proper usage TargetPhrase 'param=<String parameter>' 'prepend=<String prepend>' 'values={val1,val2,...}' ", true);
		}
		prepend = prepend.trim();
                if(!prepend.equals("")){
			prepend = prepend.concat(" ");
		}
		
		//gets rid of all brackets, spaces, and quotes
		list = list.replaceAll("\\{","");
		list = list.replaceAll("\\}","");
		list = list.replaceAll("\\(","");
		list = list.replaceAll("\\)","");
		list = list.replaceAll("\\\"","");
		
		//splits the string into an array of values
		String[] valuesArray = list.split(",");
		
		int numberOfTargets = valuesArray.length;
		StringBuffer returnValue = new StringBuffer();
		
		// this loop formats all the strings in the array so they will print properly.
		// formatting includes trimming of spaces and changing the case.
		for(int formatIndex = 0; formatIndex < valuesArray.length; formatIndex++) {
			valuesArray[formatIndex] = formatVar(valuesArray[formatIndex]);
		}
		
		//checks to see if the first value is restricted.
		boolean restricted = restrictedValue(valuesArray[0]);
		
		//Puts the string together depending on how many targets are in the list.
		//The maximum targets is 10.
		switch(valuesArray.length) {
			case 1: 
				if(restricted) {
					returnValue.append(prepend + "in the " + valuesArray[0]);
				} else {
					returnValue.append(prepend + valuesArray[0]);
				}
				break;
			case 2: 
				restricted = restrictedValue(valuesArray[1]);
				if(restricted) {
					returnValue.append(prepend + valuesArray[0] + " and in the " + valuesArray[1]);
				} else {
					returnValue.append(prepend + valuesArray[0] + " and " + valuesArray[1]);
				}
				break;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				restricted = restrictedValue(valuesArray[valuesArray.length-1]);
				returnValue.append(prepend +  valuesArray[0] + ", ");
				for(int i = 1; i < valuesArray.length -1; i++) {
					returnValue.append(valuesArray[i] + ", ");
				}
				if(restricted) {
					returnValue.append("and in the " + valuesArray[valuesArray.length-1]);
				} else {
					returnValue.append("and " + valuesArray[valuesArray.length-1]);
				}
				break;
			default:
				errorMessage("Too many targets in list. TargetPhrase truncated at 10 targets.", false);
				restricted = restrictedValue(valuesArray[9]);
				returnValue.append(prepend + valuesArray[0] + ", ");
                                for(int i = 1; i < 8; i++) {
                                        returnValue.append(valuesArray[i] + ", ");
                                }
                                if(restricted) {
                                        returnValue.append("and in the " + valuesArray[9]);
                                } else {
                                        returnValue.append("and " + valuesArray[9]);
                                }
				break;
		}
		
		//assigns the string to the parameter passed on the argument line.
		Ruleset.showRule(Action.ASSIGN, parameter, returnValue.toString());
	}
	
	/* Checks to see if the value is a "restricted" value.
	   Parameter is the string that we want to compare to restricted values.
	   Return is true if the value is restricted, false otherwise.
	*/
	private static boolean restrictedValue(String value) {
		if(value.equals("solar wind")) {
			return true;
		}if(value.equals("Io plasma torus")) {
			return true;
		}
		return false;
	}
	
	/* Formats the given value into a proper format for a description.
	   If the value is SOLAR WIND it becomes solar wind
	   Ohterwise the first letter is upper case and the rest are lower.
	   It also trims the value.
	*/ 
	private static String formatVar(String value){
		value = value.replaceAll("_"," ").trim();
		if(value.equals("SOLAR WIND")){
			value = "solar wind";
		}else{
			String firstLetter = value.substring(0,1).toUpperCase();
			String lowerCasePart = value.substring(1).toLowerCase();
			value = firstLetter + lowerCasePart;
		}
		return value;
	}
	
	/* Prints out the error message in appropriate format for the 
	   ruleset parser.
	*/
	private static void errorMessage(String message, boolean abort) {
		Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
		Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
		Ruleset.showRule(Action.MESSAGE, "\t" + message);
		if (abort) {
			Ruleset.showRule(Action.ABORT, "");
			System.exit(1);
		}
	}
}
		
