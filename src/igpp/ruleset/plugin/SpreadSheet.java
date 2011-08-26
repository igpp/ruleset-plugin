package igpp.ruleset.plugin;

// import igpp.ruleset.*;
import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

// import igpp.util.*;
import igpp.util.Argument;

// import java.util.*;
import java.util.LinkedList;
import java.util.ArrayList;

// import java.io.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * This is the SpreadSheet parser with the functions:
 * row_max  -- which will return the maximum bytes that a row can occupy.
 *		arguments are row_max=yes tmp_fmt=(File)
 *
 * @author      Erin Means
 * @author      Planetary Data System
 * @version     1.0, 06/23/03
 * @since       1.0
 */
 
 public class SpreadSheet {
 	
 	private static String[] validDelimeters = {",",";","|","\t"};
 	private static String[][] field_properties = new String[200][];
 	private static LinkedList file = new LinkedList();
 	private static LinkedList records = new LinkedList();
 	private static String dataFile = "";
 	private static String time = "";
 	private static int tcol = 0;
 	private static String tStyle = "";
 	private static String tFormat = "";
 	private static String row_max = "";
 	private static String temp_fmt = "";
 	private static String record_max = "";
 	private static String field_max = "";
 	private static String delim = "";
 	private static int field_max_col = 0;
 	private static int num_of_fields = 0;
 	private static int total_fields = 0;
 	private static int total_num_delim = 0;
 	private static int skip = 0;
 	private static int recordMax = 0;
 	private static String[] minMax = null;
 	
 	public static void main(String[] args) {
// 		String[] head = {"NAME","FIELD_NUMBER","BYTES","ITEMS","ITEM_BYTES","DATA_TYPE","START_TOKEN"};
 //		field_properties[0] = head;
 		parseArguments(args);
 		try {
			assignValue("ROW_BYTES", new Integer(rowMax()).toString());
			assignValue("FIELDS", new Integer(num_of_fields).toString());
		} catch(IOException e) {
			errorMessage("error reading " + temp_fmt + ". ", true);
		}
		try {
			readAsciiFile(new File(dataFile));
			assignValue("REC_BYTES", new Integer(recordMax).toString());
		} catch(IOException e) {
			errorMessage("error reading " + temp_fmt + ". ", true);
		}
/*		parseArguments(args);
		
		Argument yes_no = new Argument();
		yes_no.add("no",0);
		yes_no.add("yes",1);
		
		switch(yes_no.token(row_max)){
			case 1: if (temp_fmt == null) {
				errorMessage("Usage for row_max: java SpreadSheet 'row_max=yes' 'tmp_fmt=FILE' ", true);
			} else {
				File tempFile = new File(temp_fmt);
				try {
					assignValue("MAX_ROW", new Integer(rowMax(tempFile)).toString());
				} catch(IOException e) {
					errorMessage("error reading " + temp_fmt + ". ", true);
				}
				
			}
			break;
		}
		
		switch (yes_no.token(record_max)) {
			case 1: if (dataFile == null) {
				errorMessage("Usage for row_max,java SpreadSheet 'rec_max=yes' 'ascii_file=FILE' ", true);
			} else {
				try{
					readAsciiFile(new File(dataFile));
					assignValue("REC_MAX", new Integer(recordMax).toString());
				}catch(IOException e) {
					errorMessage("error reading " + dataFile + ".",true);
				}
			}
			break;
		}
*/
 	}
 	
 	private static void parseArguments(String[] arguments){
 		dataFile = Argument.find(arguments,	"ASCII_FILE",	null,	0).trim();
		temp_fmt = Argument.find(arguments,	"FORMAT_FILE",	null,	0).trim();
		delim		= Argument.find(arguments,	"DELIMITER",	",",0).toUpperCase().trim();
		if(!delim.equals(validDelimeters[0]) && !delim.equals(validDelimeters[1]) && !delim.equals(validDelimeters[2]) && !delim.equals(validDelimeters[3])){
			Ruleset.showRule(Action.MESSAGE,"The delimeter " + delim + " is invalid.\nThe valid delimeters are ,:|\t");
			Ruleset.showRule(Action.ABORT, "");
			System.exit(1);
		}
 		try {
 			skip = Integer.parseInt(Argument.find(arguments,"SKIP","0",0));
 		} catch(NumberFormatException e){
 			errorMessage("SKIP has to be an integer",true);
 		}
 		
 		time = Argument.find(arguments,"TIME","mmdd",0);
 		tStyle = Argument.find(arguments,"T_STYLE","DFS",0);
 		tFormat = Argument.find(arguments,"T_FORMAT",null,0);
 		try {
 			tcol = Integer.parseInt(Argument.find(arguments,"T_COLUMN","0",0));
 		} catch(NumberFormatException e){
 			errorMessage("T_COLUMN has to be an integer",true);
 		}
 		
// 		row_max = Argument.find(arguments,"row_max","no",0);

 		
// 		record_max = Argument.find(arguments,"REC_MAX","no",0);
 		
// 		field_max = Argument.find(arguments,"FIELD_MAX","no",0);
// 		try {
// 			field_max_col = Integer.parseInt(Argument.find(arguments,"field_max_col","0",0));
// 		} catch (NumberFormatException e) {
// 			errorMessage("field_max_col has to be an integer",true);
// 		}
 		
 	}
 	
 	private static void readAsciiFile(File textFile) throws IOException {
 		if (!textFile.exists()) {
 			errorMessage("The ascii file " + dataFile + " does not exist", true);
 		}
 		BufferedReader in = new BufferedReader(new FileReader(textFile));
 		int temp = 0;
 		int rec_num = 0;
 		int token_index = 0;
 		int token_length = 0;
 		boolean quoted_value = false;
 		String record = "";
 		while ((record = in.readLine()) != null) {
 			temp++;
 			int field_num=0;
 			token_length = 0;
 			while(skip > 0){
 				record = in.readLine();
 				skip--;
 			}
 			rec_num++;
// checks for the maximum record length in the file
 			if (recordMax < record.length() + 2) {
 				recordMax = record.length() + 2;
 			}
// Counts the number of delimeters
			int delim_index = 0;
			while(delim_index != -1){
				delim_index = record.indexOf(delim, delim_index+1);
				token_length++;
			}
// subtracts the delimeter added when record.indexOf(...) = -1
			token_length--;
// assigns the total number of delimeters each record should have to total_num_delim
			total_num_delim = total_fields - 1;
// error checking for total number of delimeters.
 			if(token_length != total_num_delim){
 				Ruleset.showRule(Action.MESSAGE,"The record number "+ rec_num + " has " + token_length + " delimeters in it.\nIt should have " + total_num_delim + " delimeters in it");
 			}else{
 				String[] tok = record.split(delim);
 				if (tok.length < total_fields){
 					String[] tok2 = new String[tok.length];
 					for(int fill = 0; fill < tok.length - 1; fill++){
 						tok2[fill] = tok[fill];
 					}
 					tok = new String[total_fields];
 					for(int fill = 0; fill < tok2.length; fill++){
 						tok[fill] = tok2[fill];
 					}
 					for(int i = tok.length; i < total_fields; i++){
 						tok[i] = null;
 					}
 				}
 				for (int tokIndex = 0; tokIndex < tok.length; tokIndex++){
 					if(tok[tokIndex] == null){tok[tokIndex] = "";}
 				}
				for(int field_index = 0; field_index < num_of_fields; field_index++){
					int itemNum = Integer.parseInt(field_properties[field_index][3]);
					int StartField = Integer.parseInt(field_properties[field_index][6]);
					if(field_properties[field_index][5].equals("CHARACTER") || field_properties[field_index][5].equals("IDENTIFIER")){
						for(int itemIndex = StartField; itemIndex < StartField + itemNum; itemIndex++){
							if(!tok[itemIndex].equals("")){
								if(!tok[itemIndex].trim().startsWith("\"") && !tok[itemIndex].trim().endsWith("\"")){
									Ruleset.showRule(Action.MESSAGE,"LINE: " + rec_num + " The value "+ tok[itemIndex] + " should be quoted.");
 								}
 							}
 						}	
 					} else {
 						for(int itemIndex = StartField; itemIndex < StartField + itemNum; itemIndex++){
 							if(!tok[itemIndex].equals("")){
 								if(tok[itemIndex].startsWith("\"") && tok[itemIndex].endsWith("\"")){
 									Ruleset.showRule(Action.MESSAGE,"The value "+ tok[itemIndex] + " should NOT be quoted.");
 								}
 							}
 						}
 					}	
 				}
			}
		}
	}
	
	private static void errorMessage(String message, boolean abort) {
		Ruleset.showRule(Action.MESSAGE, "$RULE_SET");
		Ruleset.showRule(Action.MESSAGE, "\t$FILE_PATH/$FILE_NAME");
		Ruleset.showRule(Action.MESSAGE, "\t" + message);
		if(abort == true) {
			Ruleset.showRule(Action.ABORT, "");
			System.exit(1);
		}
	}
	
	private static void assignValue(String parameter, String val) {
		Ruleset.showRule(Action.ASSIGN, parameter, val);
	}
 	
	private static int rowMax() throws IOException {
		File tempFormat = new File(temp_fmt);
		String line = "";
 		ArrayList bytes = new ArrayList();
 		int start_location = 0;
		int row_bytes = 0;
		int delimiters = 0;
		int quotations = 0;
 		
		if (!tempFormat.exists()) {
 			errorMessage("the temporary format file " + temp_fmt + " does not exist. ", true);
 		}
 		
 		RandomAccessFile FormatFile = new RandomAccessFile(tempFormat,"r");
 		while ((line = FormatFile.readLine().trim()) != null && !line.endsWith("FIELD")) {}
		file.add(line);
		
		while ((line = FormatFile.readLine()) != null) {
			if(!line.trim().equals("")){
				file.add(line.trim());
			}
		}
		
		for(int file_index = 0; file_index < file.size(); file_index++){
			String file_line = file.get(file_index).toString();
			boolean itemsFound = false;
			String dataType = "";
			int items = 0;
			int itemBytes = 0;
			int totalBytes = 0;
			
			if(file_line.startsWith("OBJECT") && file_line.endsWith("FIELD")){
				field_properties[num_of_fields] = new String[7];
				field_properties[num_of_fields][3] = "1";
				field_properties[num_of_fields][4] = "0";
				field_properties[num_of_fields][6] = "0";
				num_of_fields++;
				delimiters++;
			}
			if (file_line.startsWith("BYTES")) {
				String temp_line = file_line.substring(file_line.indexOf("=") + 1).trim();
				int comment_index = temp_line.indexOf("/*");
				if (comment_index != -1) {
					temp_line = temp_line.substring(0, comment_index).trim();
				}
				totalBytes = Integer.parseInt(temp_line);
				field_properties[num_of_fields - 1][2] = temp_line;
			} else if (file_line.startsWith("ITEMS")) {
				String temp_line = file_line.substring(file_line.indexOf("=") + 1).trim();
				int comment_index = temp_line.indexOf("/*");
				if (comment_index != -1) {
					temp_line = temp_line.substring(0, comment_index).trim();
				}
				items = Integer.parseInt(temp_line);
				field_properties[num_of_fields - 1][3] = temp_line;
			} else if (file_line.startsWith("ITEM_BYTES")) {
				String temp_line = file_line.substring(file_line.indexOf("=") + 1).trim();
				int comment_index = temp_line.indexOf("/*");
				if (comment_index != -1) {
					temp_line = temp_line.substring(0, comment_index).trim();
				}
				itemsFound = true;
				itemBytes = Integer.parseInt(temp_line);
				field_properties[num_of_fields - 1][4] = temp_line;
			} else if (file_line.startsWith("DATA_TYPE")) {
				String temp_line = file_line.substring(file_line.indexOf("=") + 1).trim();
				int comment_index = temp_line.indexOf("/*");
				if (comment_index != -1) {
					temp_line = temp_line.substring(0, comment_index).trim();
				}
				dataType = temp_line;
				field_properties[num_of_fields - 1][5] = dataType;
			} else if (file_line.startsWith("NAME")){
				String temp_line = file_line.substring(file_line.indexOf("=") + 1).trim();
				int comment_index = temp_line.indexOf("/*");
				if (comment_index != -1) {
					temp_line = temp_line.substring(0, comment_index).trim();
				}
				field_properties[num_of_fields - 1][0] = temp_line;
			} else if (file_line.startsWith("FIELD_NUMBER")){
				String temp_line = file_line.substring(file_line.indexOf("=") + 1).trim();
				int comment_index = temp_line.indexOf("/*");
				if (comment_index != -1) {
					temp_line = temp_line.substring(0, comment_index).trim();
				}
				field_properties[num_of_fields - 1][1] = temp_line;
			}
		}
		for(int i = 0; i < num_of_fields; i++){
			if(field_properties[i][3].equals("0")){
				bytes.add(new Integer(field_properties[i][2]).toString());
				field_properties[i][6] = new Integer(start_location).toString();
				start_location++;
				if(field_properties[i][5].equals("CHARACTER") || field_properties[i][5].equals("IDENTIFIER")){
					quotations += 2;
				}
				total_fields += 1;
			} else {
				int items = Integer.parseInt(field_properties[i][3]);
				int itemsBytes = Integer.parseInt(field_properties[i][4]);
				int itemsListBytes = items * itemsBytes + items - 1;
				field_properties[i][6] = new Integer(start_location).toString();
				start_location += items;
				if (field_properties[i][5].equals("CHARACTER") || field_properties[i][5].equals("IDENTIFIER")) {
					itemsListBytes += 2 * (items - 1);
					quotations += 2;
				}
				total_fields += items;
				bytes.add(new Integer(itemsListBytes).toString());
			}
		}
		
		for (int arrayIndex = 0; arrayIndex < bytes.size(); arrayIndex++) {
			row_bytes += Integer.parseInt(bytes.get(arrayIndex).toString());
		}
			delimiters--;
			row_bytes += delimiters + quotations + 2;
			return row_bytes;
 	}

}
