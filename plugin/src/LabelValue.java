package igpp.ruleset.plugin;

import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

import igpp.util.Argument;

// import pds.label.*;
import pds.label.PDSItem;
import pds.label.PDSElement;
import pds.label.PDSLabel;

/**
 *			LabelValue.java July 22, 2003
 *	LabelValue is a program that returns every item in the label as a value of 
 *  the following type:
 *
 *		  $KEYWORD_OCCURENCE = VALUE
 *	
 *	Where the keyword is the keyword in the label. The occurence is the 
 *  number of the object the keyword occurs in.
 *
 *	parameters are:	<br><pre>
 *		LABEL 		The complete path of the label file
 *              </pre>
 *	default values:<br> <pre>
 *		NONE
 *              </pre>
 *
 *	error handling: <br><blockquote>
 *		if the label does not exist or can not be parsed an error message is 
 *		returned.
 *              </blockquote>
 *
 * @author      Erin Means
 * @author      Planetary Data System
 * @version     2.0, 09/19/03
 * @since       1.0
 */

public class LabelValue {

	public static void main(String args[]) {
		
		//Get the parameters from the command line.
		String lbl 		= Argument.find(args, "LABEL",      null, 0);
		PDSItem item = null;
		PDSElement element = null;
		String value = null;
		
		if(lbl == null) {
			errorMessage("LabelValue called incorrectly, usage java LabelVal LABEL='location of the label'. ", true);
		}
		PDSLabel label = new PDSLabel();
		
		//If the label is able to be read by the PDSLabel it finds the element and
		//and sets the value variable equal to the label value.
		//If the file is not able to be read it prints an error message to the screen.
		try {
			label.parse(lbl);
			label.printVariable(System.out);
		} catch(Exception e) {
			errorMessage("LabelValue called incorrectly", true);
			label.printMessage(e.getMessage());
		}
	}
	
	// Prints out the error message in appropriate format for the 
	// ruleset parser.
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
