package igpp.ruleset.plugin;

// import igpp.ruleset.*; 
import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

// import igpp.util.*;
import igpp.util.Argument;

 /** 
 * Strings is a class that allows you to get certain properties about a string.<br>
 *
 * 	Required command line options:<br><pre>
 *
 *		FUNCTION	[character string] The function you want to perform on the string.
 *		PARAMETER	[character string] Parameter to return.
 *		STRING		[character string] The string that you want to run the specified
 *					function on.
 *</pre><br>
 *	Possible functions and their arguments.<br><pre>
 *		strlen		Returns the length of the string.
 *			arguments: NONE
 *		scaseup		Returns the string in all upper case.
 *			arguments: NONE
 *		scasedown	Returns the string in all lower case.
 *			arguments: NONE
 *		substr		Returns a substring of length bytes at start_byte.
 *			arguments:
 *				START_BYTE	[integer] The first byte of the substring. If the 
 *							number is negative the substring count start from 
 *							the end of the string or 
 *							"index(string substring, int occurence)"
 *				BYTES		[integer] The length of the substring to be returned
 *							or "index(string substring, int occurence)"
 *			example 1:
 * 				param=SSTIME string="1999-03-03T00:00:00.000" start=0 byte="index(:, 2)"
 *              would return $SSTIME = 1999-03-03T00:00
 *			example 2:
 *				param=STR string="This is a test run" start="index(is, 1)" byte="index(tes, 1)"
 *              would return $STR = "is is a "
 *		index		Returns the index of the first character of the given 
 *					occurrence the substring.
 *			arguments:
 *				SUBSTRING	[character string] The string to search the original 
 *							string for.
 *				OCCURRENCE	[integer] The occurence of this substring you want 
 *							to return the index of.
 *							If occurence=0 the index of the last occurence of the
 *							substring is returned.
 *		stoken		Returns the nth token after of the original string.
 *				TOKEN 	[character string] The substring to tokenize the string.
 *				INDEX	[integer] The index of the token in the array of tokens 
 *						you want to return.
 *			example 1: 
 *				PARAM=NUM STRING="1,2,3,4" FUNC=stoken TOKEN="," INDEX=2
 *				would return $NUM="2"
 *</pre>
 *
 * @author      Erin Means
 * @author      Planetary Data System
 * @version     1.2, 07/30/03
 * @since       1.0
 */
public class Strings{
	
	
	private static String function = "";
	private static String parameter = "";
	private static String string = "";
	
	public static void main(String args[]) {
		if(args.length < 3){
			errorMessage("Strings called incorrectly, proper usage Strings 'String parameter' 'String \"string\"' 'String function' 'String function_parameters' ", true);
		}
		
		parameter 		= Argument.find(args,	"PARAMETER",	"",		0);
		string 			= Argument.find(args,	"STRING",		"",		0);
		function 		= Argument.find(args,	"FUNCTION",		"",		0);

		if(parameter.equals("") || string.equals("") || function.equals("")) {
			errorMessage("Strings called incorrectly, proper usage: 'param=String parameter' 'string=String orig_string' 'func=String function_to_run' appropriate function arguments. ", true);
		}
		
		Argument func = new Argument();
		func.add("strlen",		0);
		func.add("scaseup",		1);
		func.add("scasedown",	2);
		func.add("substr",		3);
		func.add("index",		4);
		func.add("stoken",		5);
		
		switch(func.token(function)){
			case 0: assignValue(new Integer(strlen()).toString());
				break;
			case 1: assignValue(sCaseUp());
				break;
			case 2: assignValue(sCaseDown());
				break;
			case 3:	int startByte = 0;
				int cnt = 0;
				int Bytes = 0;
				String StartByteString 	= Argument.find(args, "START_BYTE", "0", 0);
				String BytesString 		= Argument.find(args, "BYTES",		 "", 0);
				BytesString = Ruleset.unquote(BytesString);
				StartByteString = Ruleset.unquote(StartByteString);
				if(BytesString.equals("")) {
					Bytes = string.length() - 1;
				}
				if(BytesString.startsWith("strlen")) {
					if(BytesString.indexOf("-") != -1){
						try{
							int subtract_part = Integer.parseInt(BytesString.substring(BytesString.indexOf("-")+1).trim());
							Bytes = string.length() - subtract_part;
						}catch(Exception e){}
					}
				}
				if(StartByteString.startsWith("i") || StartByteString.startsWith("I")){
					int argBeginIndex = StartByteString.indexOf("(");
					int argEndIndex   = StartByteString.indexOf(")");
					if(argBeginIndex < 0 || argEndIndex < 0) {
						errorMessage("Strings called inproperly, proper for Bytes\n\tBytes=\"index(string substring, int occurence)\"", true);
					}
					String arguments = StartByteString.substring(argBeginIndex + 1, argEndIndex);
					int splitIndex = arguments.lastIndexOf(",");
					if(splitIndex < 0) {
						errorMessage("Strings called inproperly, proper for Bytes\n\tBytes=\"index(string substring, int occurence)\"", true);
					}
					String sstr = arguments.substring(0, splitIndex);
					String o    = arguments.substring(splitIndex + 1).trim();
					try{
						cnt = Integer.parseInt(o);
					} catch (Exception e) {
						errorMessage("occurrence " + o + " parameter count has to be of type int. ",true);
					}
					startByte = index(sstr, cnt);
				}
				if(BytesString.startsWith("i") || BytesString.startsWith("I")){
					int argBeginIndex = BytesString.indexOf("(");
					int argEndIndex   = BytesString.indexOf(")");
					if(argBeginIndex < 0 || argEndIndex < 0) {
						errorMessage("Strings called inproperly, proper for Bytes\n\tBytes=\"index(string substring, int occurence)\"", true);
					}
					String arguments = BytesString.substring(argBeginIndex + 1, argEndIndex);
					int splitIndex = arguments.lastIndexOf(",");
					if(splitIndex < 0) {
						errorMessage("Strings called inproperly, proper for Bytes\n\tBytes=\"index(string substring, int occurence)\"", true);
					}
					String sstr = arguments.substring(0, splitIndex);
					String oc    = arguments.substring(splitIndex + 1).trim();
					try{
						cnt = Integer.parseInt(oc);
					} catch (Exception e) {
						errorMessage("occurrence " + oc + " parameter count has to be of type int. ",true);
					}
					int temp = index(sstr, cnt);
					Bytes = temp;
				}
				try{
					if(!StartByteString.toLowerCase().startsWith("i")) {
						startByte = Integer.parseInt(StartByteString);
					} if(!BytesString.equals("") && !BytesString.toLowerCase().startsWith("i") && !BytesString.toLowerCase().startsWith("strlen")) {
						Bytes = Integer.parseInt(BytesString);
					} else if (BytesString.toLowerCase().startsWith("i")) {
						Bytes = Bytes - startByte;
					}
				}catch(Exception e){
					errorMessage("substr called incorrectly START_BYTE='int' BYTES='int'.",true);
				}
				assignValue(substr(startByte, Bytes));
				break;
			case 4:	int count = 0;
				String SubString 	= Argument.find(args, "SUBSTRING",	 "",  0);
				String occurrence 	= Argument.find(args, "OCCURRENCE", "1", 0);
				if(SubString.equals("")) {
					errorMessage("index has the wrong number of parameters, proper function arguments are 'SUBSTRING=string pattern' 'OCCURRENCE=int occurrence' ", true);
				}
				if (occurrence.toLowerCase().equals("first")) {
					count = 1;
				} else if (occurrence.toLowerCase().equals("last")) {
					count = 0;
				}else{
					try {
						count = Integer.parseInt(occurrence);
					} catch (Exception e) {
						errorMessage("occurrence parameter count has to be of type int. ",true);
					}
				}
				assignValue(new Integer(index(SubString, count)).toString());
				break;
			case 5: int index = 0;
				String tok = Argument.find(args, "TOKEN", "",  0);
				String ind = Argument.find(args, "INDEX", "0", 0);
				if(tok.equals("")) {
					errorMessage("sToken has the wrong number of parameters, proper function arguments are 'TOKEN=String token' 'INDEX=int index' ",true);
				}
				if (ind.toLowerCase().equals("first")) {
					index = 1;
				} else if (ind.toLowerCase().equals("last")) {
					index = 0;
				}
				try {
					index = Integer.parseInt(ind);
				} catch (Exception e) {
					errorMessage("sToken parameter index has to be of type int. ",true);
				}
				assignValue(sToken(tok, index));
				break;
			default: errorMessage("the function " + function + " does not exist. ",true);
				break;
		}
		
	}
	
	
	/*
 	 The strlen function returns the length of the string.
 	*/
	public static int strlen() {
		return string.length();
	}
	
	/*
 	 The sCaseUp function returns the string in all caps.
 	*/
	public static String sCaseUp() {
		return string.toUpperCase();
	}
	
	/*
 	 The sCaseDown function returns the string in all lower case.
 	*/
	public static String sCaseDown() {
		return string.toLowerCase();
	}
	
	/*
 	 The substr function returns a substring.
 	
 	 param startByte	the first byte to start at.
 						if this value is < 0 the substring automatically
 						counts back from the end.
 	 param Bytes    	the number of characters you want in the substring.
 	 return				the substring
 	*/
	public static String substr(int startByte, int Bytes) {
		int stopByte = 0;
		if (startByte >= string.length()) {
			errorMessage("The START_BYTE you give is longer than the string length. \n\tProgram will quit.",true);
		}
		if (startByte >= 0) {
			stopByte = startByte + Bytes;
			if (stopByte > string.length()) {
				stopByte = string.length();
			}
			return string.substring(startByte, stopByte);
		} else {
			stopByte = string.length();
			startByte = stopByte - Bytes;
			if (startByte < 0) {
				errorMessage("substr the number of bytes is to large original string returned.", false);
				startByte = 0;
			}
		}
		return string.substring(startByte, stopByte);
	}
	
	/*
 	 The index function finds the nth occurance of a substring in the given string.
 	
 	 param substring the substring that we want to find in the string
 	 param count	 the occurance of this substring
 					 count recognizes "first" and "last"
 					 example) first occurance count should be 1 or first
 	 return			 the index of the start of the substring
 					 or -1 if there is a problem.
 	*/
	public static int index(String substring, int count){
		int ind = 0;
		int indexOfSearch = 0;
		if (count == 0) {
			ind = string.lastIndexOf(substring);
		}
		while (count-- > 0) {
			ind = string.indexOf(substring, indexOfSearch);
			if (ind == -1) {
				errorMessage("the substring does not occur in this string.", false);
				return -1;
			} else if (indexOfSearch > string.length()) {
				errorMessage("the substring does not occur "+ count + " times in this string.", false);
				return -1;
			}
			indexOfSearch = ind+1;
		}
		return ind;
	}
	
	/*
 	 The sToken function parses the string on the given substring and then
 	 returns the token in the spot for the index you passed.
 	
 	 param token      the string which the parameter string will be parsed on
 	 param index      the token you want returned
 						if the number is < 0 returns that number from the end
 						of the array.
 	
 	 return   This returns the substring in the index position of the array.
 				If the index is larger than the size of the array it returns
 				the last string in the array.
 	*/
	public static String sToken(String token, int index) {
		String[] tokenizedString = string.split(token);
		if (index < 0) {
			index = tokenizedString.length + index;
		}
		if (index > tokenizedString.length || index <= 0) {
			errorMessage("the number you passed is larger than the number of tokens. The last token will be returned. ", false);
			return tokenizedString[tokenizedString.length - 1];
		}
		
		return tokenizedString[index - 1];
	}
	
	/*
	 The errorMessage function prints out the error message to the screeen
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
	
	/*
 	 The assignValue function assigns the calculated vale to the parameter
 	 using the Ruleset showRule function.
 	*/
	private static void assignValue(String value){
		Ruleset.showRule(Action.ASSIGN, parameter, value);
	}
}

