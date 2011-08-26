package igpp.ruleset.plugin;

import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;
import igpp.util.Argument;

import java.text.NumberFormat;

// import java.util.regex.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// import java.util.*;
import java.util.LinkedList;

 /**
 *			FormatDescription.java July 30, 2003
 *	FormatDescription is a program that returns a description indented and 
 *	wrapped. It has an option to start the description at the equals, but the
 *	default is not to. It trims all spaces indents and wraps the description. <br>
 *
 *	Mandatory parameters are:	<br><pre>
 *		PARAMETER		[character string] The parameter name you want to return.
 *		DESCRIPTION		[character string] The description to be formatted.
 *
 *	Optional parameters are:
 *		INDENT 			[integer] The number of spaces to add to the beginning 
 *						of each line.
 *		LENGTH  	    [integer] The number that tells the program the maximum 
 *						number of bytes	each string should be.
 *		START_AT_EQUALS	[integer] This is the location of the equals sign in the label
 *						template file. This will start the description at the 
 *						equals sign and wrap the first string appropriately if
 *						this value is not 0.
 *						Usage of this variable is START_AT_EQUALS=$equalsAt.
 *             </pre>
 *	default values:<br> <pre>
 *		INDENT			= 0
 *		LENGTH			= 78
 *		START_AT_EQUALS	= 0
 *              </pre>
 *
 * @author      Erin Means
 * @author      Planetary Data System
 * @version     1.2, 07/30/03
 * @since       1.0
 */

public class FormatDescription {
	
	private static int indent = 0;
	private static int indexOfEquals = 0;
	private static String prePadStr = "";
	private static String returnValue = "";
	private static int maxLength = 0;
	private static LinkedList formattedStrings = new LinkedList();
	private static Pattern p = Pattern.compile("\\s");
 	private static Matcher m = null;
	
	public static void main(String[] args) {
	
// Error checking for correct number of parameters		
		String parameter 	= Argument.find(args,	"PARAMETER",		null,	0);
		String description	= Argument.find(args,	"DESCRIPTION",		null,	0);
		String indentArg 	= Argument.find(args,	"INDENT",			"0",	0);
		String lengthArg 	= Argument.find(args,	"LENGTH",			"77",	0);
		String equals	 	= Argument.find(args,	"START_AT_EQUALS",	"0",	0);
		
		try {
			indent 			= Integer.parseInt(indentArg);
			maxLength 		= Integer.parseInt(lengthArg);
			indexOfEquals 	= Integer.parseInt(equals);
			
			if (indent < 0) {
				errorMessage("Indent can not be a negative number. Indent will be set to 0. ", false);
				indent = 0;
			}
			if (maxLength < 0) {
				errorMessage("length can not be a negative number. Line length will be set to 78. ", false);
				maxLength = 78;
			} else if (maxLength <= indent) {
				errorMessage("length can not be less than indent. Line length will be set to 78. ", false);
				maxLength = 78;
			}
			while(indent > 0) {
				prePadStr += " ";
				indent--;
			}
		} catch (Exception e) {
			description = null;
		}
		if(description == null || parameter == null) {
			errorMessage("FormatDescription called incorrectly, proper usage formatDescription 'param=<String parameter>' 'desc=<String description>' 'indent=<int indent>' 'length=<int lineLength>' 'start=$equalsAt' ", true);
		}

// Unquote string
		description = Ruleset.unquote(description);

//gets rid of character return line feeds so the program will parse correctly.
		description = description.replaceAll("\\\\r\\\\n", "\n");
		description = description.replaceAll("\\\\n", "\n");
		while(description.startsWith("\n") && indexOfEquals != 0) description = description.replaceFirst("\n","");
		
// breaks the value up into an array of lines
		String[] strings = description.split("\n");
		format(strings);
		
// cat all the strings into one string
		for (int index = 0; index < formattedStrings.size(); index++) {
			returnValue += formattedStrings.get(index).toString();
		}
		
// assign the formatted description to the given parameter.
		if(returnValue.endsWith("\n")) returnValue = returnValue.substring(0, returnValue.length()-1);
		Ruleset.showRule(Action.ASSIGN, parameter, returnValue);
	}
	
	
// format function.
	private static void format(String[] strings) {
		int lengthOfFirstString = maxLength - indexOfEquals - 2;
		int maximumStringLengthByLine = lengthOfFirstString;
		int trial = 0;
		String trialString = "";
		
// does the formatting for the block of string passed.
start:
		for(int index = 0; index < strings.length; index++) {
			
// allows for the first line to start at the equals sign
			if(index == 0 && indexOfEquals != 0) {
				maximumStringLengthByLine = lengthOfFirstString;
				strings[index] = strings[index].trim();
			} else {
				maximumStringLengthByLine = maxLength;
				strings[index] = prePadStr + strings[index].trim();
			}
			
			if (strings[index].length() > maximumStringLengthByLine) {
				String remaining = strings[index].substring(maximumStringLengthByLine);
				String preserved = strings[index].substring(0, maximumStringLengthByLine);
				int lastSpace = preserved.lastIndexOf(" ");
				if(lastSpace == -1 && preserved.length() > maximumStringLengthByLine){
					errorMessage("\tThe string " + preserved + " exceeds the line length specified. ", false);
				}
				while (preserved.length() > lastSpace) {
					remaining = preserved.charAt(preserved.length() - 1 ) + remaining;
					preserved = preserved.substring(0, preserved.length()-1);
				}
				if (remaining.startsWith(" ")) {
					remaining = remaining.substring(1);
				}
				strings[index] = preserved.substring(0, preserved.length());
				String possibleNumber = strings[index].substring(strings[index].lastIndexOf(" ")).trim();
				
//checks to see if the previous value in the string is a number.
				try {
					NumberFormat numFmt = NumberFormat.getNumberInstance();
					Number num = numFmt.parse(possibleNumber);
					remaining = possibleNumber + " " + remaining;
					preserved = preserved.substring(0, preserved.length() - possibleNumber.length() - 1);
				} catch (Exception e) {
					if (possibleNumber.equals("-")) {
						preserved = preserved.substring(0, preserved.lastIndexOf("-") - 1);
						lastSpace = preserved.lastIndexOf(" ");
						remaining = " - " + remaining;
						remaining = preserved.substring(lastSpace) + remaining;
						preserved = preserved.substring(0, lastSpace);
					} else if(possibleNumber.indexOf("-") != -1) {
						remaining = possibleNumber + " " + remaining;
						preserved = preserved.substring(0, preserved.length() - possibleNumber.length() - 1);
					}
				}
				
// adds the preserved line to the linked list.
				if (preserved.trim().length() > 0) {
					formattedStrings.add(preserved + "\n");
				}
				
				if (index < strings.length - 1 && !strings[index + 1].trim().equals("")) {
					strings[index + 1] = remaining + " " + strings[index + 1].trim();
				} else if (prePadStr.length() + remaining.length() <= maximumStringLengthByLine) {
					formattedStrings.add(prePadStr + remaining + "\n");
				} else {
					if (trialString.equals(remaining)) {
						errorMessage("The string " + remaining + " exceeds the maximum length specified. ", false);
						formattedStrings.add(prePadStr + remaining + "\n");
					} else {
						if(remaining.length() > 0) {
							strings[index] = remaining;
							index--;
							trial++;
							trialString = remaining;
							continue;
						}
					}
				}
			} else if (strings[index].trim().length() == 0) {
				formattedStrings.add("\n");
			} else if (strings[index].length() <= maximumStringLengthByLine) {
				boolean	nibbled = false;
				if (index < strings.length-1) {
					int 		residualLength = maximumStringLengthByLine - strings[index].length();
					String		temp = strings[index + 1].trim();
					if(temp.length() > 0) {	// Nibble from the next string
						String[]	words = strings[index + 1].trim().split(" ");
						int			wordIndex = 0;
						String		delim;
						strings[index + 1] = "";
						
						while (wordIndex < words.length && words[wordIndex].length() + 1 <= residualLength){
							strings[index] = strings[index] + " " + words[wordIndex++];
							residualLength = maximumStringLengthByLine - strings[index].length();
							nibbled = true;
						}
						try {
							NumberFormat numFmt = NumberFormat.getNumberInstance();
							Number num = numFmt.parse(words[wordIndex - 1]);
							strings[index] = strings[index].substring(0,strings[index].lastIndexOf(" "));
							wordIndex--;
						} catch (Exception e) {
							if(wordIndex > 0) {
								if (words[wordIndex - 1].equals("-")) {
									wordIndex -= 2;
									strings[index] = strings[index].substring(0, strings[index].lastIndexOf("-") - 1);
									int lastSpace = strings[index].lastIndexOf(" ");
									strings[index] = strings[index].substring(0, lastSpace);
								}else if(words[wordIndex - 1].indexOf("-") != -1) {
									wordIndex--;
									strings[index] = strings[index].substring(0, strings[index].length() - words[wordIndex].length() - 1);
								}
							}
						}
						delim = "";
						while(wordIndex < words.length){
							strings[index + 1] += delim + words[wordIndex++];
							delim = " ";
						}
					}
					
				}
				formattedStrings.add(strings[index] + "\n");
				// Check if nibbled to nothing - if so skip it
				if(nibbled && strings[index+1].length() == 0) index++;
			}
		}
	}
	
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

