package igpp.ruleset.plugin;

import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

// import igpp.util.*;
import igpp.util.Argument;

// import java.io.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 *TabStartStop is a function that will return the first and last values in the table
 *you give, between the bytes you give.
 *
 *parameters are: <br><pre>
 *	TABLE      = [character string] The location of the file.
 *	START_BYTE = [integer] The byte The column starts on (indexed from 1).
 *	STOP_BYTE  = [integer] The byte The column ends on (indexed from 1).
 *	FIRST      = [character string] The parameter to set the first value to.
 *	LAST       = [character string] The parameter to set the last value to.
 *	SKIP_ROWS  = [integer] The the number of rows in the beginning of the file to skip.
 *      </pre>
 *
 *defaults: <br><pre>
 *	SKIP_ROWS = 0
 *      </pre>
 *
 *returns:<br><pre>
 *	$FIRST = first value
 *	$LAST = last value
 *      </pre>
 *
 * @author      Erin Means
 * @author      Planetary Data System
 * @version     1.0, 07/15/03
 * @since       1.0
 */
 
 public class TabStartStop {
 	
 	
 	public static void main(String[] args) {
 		//the values to return from the program
 		String valueForFirst = "";
 		String valueForLast  = "";
 		
 		//the indeces for the substring and the number of rows to skip.
 		int startIndex = 0;
 		int stopIndex  = 0;
 		int skip       = 0;
 		
 		//sets all the values to the appropriate things from the command line arguments
 		String tabStr	 = Argument.find(args, "TABLE",      null, 0);
 		String startByte = Argument.find(args, "START_BYTE", null, 0);
 		String stopByte	 = Argument.find(args, "STOP_BYTE",  null, 0);
 		String first	 = Argument.find(args, "FIRST", 	  null, 0);
 		String last		 = Argument.find(args, "LAST", 	  null, 0);
 		String skipStr   = Argument.find(args, "SKIP_ROWS",  "0",  0);
 		
 		//This is error checking to see if all the values have been passed.
 		if(tabStr == null || startByte == null || stopByte == null || first == null || last == null) {
 			errorMessage("TabStartStop called incorrectly, usage java TabStartStop TABLE='location of the Table' START_BYTE='int start byte of column' STOP_BYTE='int stop byte of the column' FIRST='String Paramter for the first time' LAST='String parameter for the last time'. ", true);
 		}
 		
 		//Creates a file object so the table can be read
 		File tab = new File(tabStr);
 		
 		//Error checking to make sure the file exists.
 		if (!tab.exists()) {
 			errorMessage("The file " + tabStr + " does not exist. ", true);
 		}
 		
 		//Parses all the integers.
 		try {
 			//Since indexing is from 1 in PDS and 0 in java I subtract 1.
 			startIndex = Integer.parseInt(startByte) - 1;
 			//Substring in java you have to add 1 to get the proper substring.
 			stopIndex  = Integer.parseInt(stopByte);
 			skip       = Integer.parseInt(skipStr);
 		}catch(Exception e) {
 			errorMessage("TabStartStop called incorrectly, START_BYTE, STOP_BYTE, and SKIP must all be of type int. ", true);
 		}
 		
 		//Reads the file and gets the values
 		try {
 			BufferedReader fin = new BufferedReader(new FileReader(tab));
 			String line = "";
 			while (skip > 0) {
 				line = fin.readLine();
 			}
 			line = fin.readLine();
 			valueForFirst = line.substring(startIndex, stopIndex);
 			String previousLine = line;
 			while ((line = fin.readLine()) != null) {
 				previousLine = line;
 			}
 			valueForLast = previousLine.substring(startIndex, stopIndex);
 		}catch(Exception e) {
 			errorMessage("There was a problem reading the table " + tabStr, true);
 		}
 		
 		//Prints them to STDOUT.
 		Ruleset.showRule(Action.ASSIGN, first, valueForFirst);
 		Ruleset.showRule(Action.ASSIGN,  last, valueForLast );
 		
 	}
 	
 	/* This is my error message function.
 	 It prints the error in proper label ruleset format.
 	 If abort = true it quits the ruleset otherwise it prints
 	 a message and continues.
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