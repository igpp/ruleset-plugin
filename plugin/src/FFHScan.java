package igpp.ruleset.plugin;

import igpp.ruleset.Ruleset;
import igpp.ruleset.Action;
import igpp.util.Date;

// import igpp.util.*;
import igpp.util.Argument;
import igpp.util.Date;

import pds.label.*;

// import java.util.*;
import java.util.ArrayList;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/*
 * CassiniFfhScan
 *This code reads a *.FFH file and assigns values from the ffh file to these
 *keywords. If there is no value the word "NULL" will be printed. It reformats
 *the cassini SCLK into a PDS time.
 *
 *  $CTIME
 *  $BTIME
 *  $ETIME
 *  $BSCLK
 *  $ESCLK
 *  $BNATIVE
 *  $ENATIVE
 *  $RECS
 *  $COLS
 *  $REC_BYTES
 *  $FILE_NOTE
 *
 * @author      Erin Means
 * @author      Planetary Data System
 * @version     1.1, 08/18/03
 * @since       1.0
 */


public class FFHScan {

	private static ArrayList lines		= new ArrayList();
	private static String dataFile		= "NULL";
	private static String recl			= "NULL";
	private static String recs			= "NULL";
	private static String ncols			= "NULL";
	private static String startTime		= "NULL";
	private static String stopTime		= "NULL";
	private static String startSclk		= "NULL";
	private static String stopSclk		= "NULL";
	private static String startNative	= "NULL";
	private static String stopNative	= "NULL";
	private static String cTime			= "NULL";
	private static String ffAbstract	= "NULL";
	private static String file			= null;
	private static String chronosLoc	= null;
	private static String chronosSetup 	= null;

	public static void main(String[] args){
//		String spiceLibrary = System.getProperty("SPICELIB");
//		File spiceLibDir = new File(spiceLibrary);
//		File spiceDir = new File(spiceLibDir.getParent());
//		File[] dirs = spiceDir.listFiles();
//		for(int i = 0; i < dirs.length; i++){
//		}
		file			= Argument.find(args, "FFH_FILE",              null, 0);
		chronosLoc		= Argument.find(args, "CHRONOS_LOCATION",      null, 0);
		chronosSetup	= Argument.find(args, "CHRONOS_SETUP_FILE",    null, 0);
		if(file == null || chronosLoc == null || chronosSetup == null) {
			errorMessage("CassiniFFHScan called incorrectly. Usage: java CassiniFFHScan FFH_FILE=$FFH_FILE CHRONOS_LOCATION=[path to chronos] CHRONOS_SETUP_FILE=[location of the chronos setup file]", true);
		}
		ffhScan();
	}


	public static void ffhScan() {
		File ffhFile = new File(file);
		if(!ffhFile.exists()) {
			errorMessage("File does not exist.", true);
		}
		readFfhFile(ffhFile);
		parse();
		// If all goes well it prints the contents to the screen.
		Ruleset.showRule(Action.ASSIGN, "CTIME",          cTime					);
		Ruleset.showRule(Action.ASSIGN, "BTIME",          startTime				);
		Ruleset.showRule(Action.ASSIGN, "ETIME",          stopTime				);
		Ruleset.showRule(Action.ASSIGN, "BSCLK",          startSclk				);
		Ruleset.showRule(Action.ASSIGN, "ESCLK",          stopSclk				);
		Ruleset.showRule(Action.ASSIGN, "BNATIVE",        startNative				);
		Ruleset.showRule(Action.ASSIGN, "ENATIVE",        stopNative				);
		Ruleset.showRule(Action.ASSIGN, "RECS",           recs					);
		Ruleset.showRule(Action.ASSIGN, "COLS",           ncols					);
		Ruleset.showRule(Action.ASSIGN, "REC_BYTES",      recl					);
		Ruleset.showRule(Action.ASSIGN, "FILE_NOTE",      "\"\n"+ffAbstract+"\""	);
	}

	/*
	Reads the *.FFH file if it exists. If an error occurs
	while reading the file the Program prints a message
	to STD output and exits.
	*/
	private static void readFfhFile(File ffhFile){
		BufferedReader fin = null;
		String line = null;
		try{
			fin = new BufferedReader(new FileReader(ffhFile));
			line = fin.readLine();
			for(int index = 0; index < line.length(); index+=72){
				lines.add(line.substring(index,index+72));
			}
		}catch(Exception e){
			errorMessage("Error reading file.", true);
		}
	}

	/*
	Parses the file and pulls out all the relavent info.
	It also puts the time in PDS time format by calling
	Time converter.
	*/
	private static void parse(){
		Date converter = new Date();
		for(int linesIndex = 0; linesIndex < lines.size(); linesIndex++){
			if(lines.get(linesIndex).toString().trim().startsWith("DATA")){
				dataFile = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=')+1).trim();
			}
			if(lines.get(linesIndex).toString().trim().startsWith("RECL")){
				recl = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
			}
			if(lines.get(linesIndex).toString().trim().startsWith("NCOLS")){
				ncols = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
			}
			if(lines.get(linesIndex).toString().trim().startsWith("NROWS")){
				recs = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
			}
			if(lines.get(linesIndex).toString().trim().startsWith("CDATE")){
				cTime = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
				cTime = Date.convert(cTime, Date.PDS, "yy DDD MMM dd HH:mm:ss");
			}
			if(lines.get(linesIndex).toString().trim().startsWith("SPACECRAFT_CLOCK_START_COUNT")){
				startSclk = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
			}
			if(lines.get(linesIndex).toString().trim().startsWith("SPACECRAFT_CLOCK_STOP_COUNT")){
				stopSclk = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
			}
			if(lines.get(linesIndex).toString().trim().startsWith("ABSTRACT")){
				linesIndex++;
				while(!lines.get(linesIndex).toString().trim().equals("END")){
					if(lines.get(linesIndex).toString().trim().startsWith("FIRST TIME")){
						startTime = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
						startNative = Date.convert(startTime, Date.BINARY, "yy DDD MMM dd HH:mm:ss.SSS");
						startTime = Date.convert(startTime, Date.PDS, "yy DDD MMM dd HH:mm:ss.SSS");
						String startUTC = Date.convert(startTime, Date.DFS,"yy DDD MMM dd HH:mm:ss.SSS");
						if(startSclk == "NULL") {
							startSclk = convertToSclk(startUTC);
						}
						

					}else if(lines.get(linesIndex).toString().trim().startsWith("LAST TIME")){
						stopTime = lines.get(linesIndex).toString().trim().substring(lines.get(linesIndex).toString().trim().indexOf('=') + 1).trim();
						stopNative = Date.convert(stopTime, Date.BINARY, "yy DDD MMM dd HH:mm:ss.SSS");
						stopTime = Date.convert(stopTime, Date.PDS, "yy DDD MMM dd HH:mm:ss.SSS");
						String stopUTC = Date.convert(stopTime, Date.DFS, "yy DDD MMM dd HH:mm:ss.SSS");
						if(stopSclk == "NULL") {
							stopSclk = convertToSclk(stopUTC);
						}

					}else if(!lines.get(linesIndex).toString().trim().startsWith("OWNER")){
						ffAbstract += lines.get(linesIndex).toString().trim()+"\n";
					}
					linesIndex++;
				}
			}
		}
		ffAbstract = ffAbstract.substring(0,ffAbstract.length() - 1);
		if(startTime.equals("NULL")) {
			errorMessage("FIRST_TIME does not exist in header file.", false);
		} if(stopTime.equals("NULL")) {
			errorMessage("LAST_TIME does not exist in header file.",  false);
		}
	}

	public static String convertToSclk(String time) {
		String		buffer;
		String		value = "";
		ArrayList	argList;
		Process		process;
		Runtime		runtime;
		ArrayList	outputList = new ArrayList();

		buffer = chronosLoc + " -setup " + chronosSetup + " -from utc -fromtype scet -to sclk -totype sclk -time " + time + " -NOLABEL"; 
		runtime = Runtime.getRuntime();
		try{
			process = runtime.exec(buffer);
			InputStream chronos_in = process.getInputStream();
			InputStreamReader osr = new InputStreamReader(chronos_in);
			BufferedReader br = new BufferedReader(osr);
			try{
				while((value = br.readLine()) != null) {
					outputList.add(value);
				}
			} catch(Exception e){}


		}catch(Exception e){}
		// Determine how we should run application
		boolean dontParseArg = false;
		String hold = System.getProperty("os.name");
		if(hold.length() >= 6) { dontParseArg = (hold.substring(0, 6).compareToIgnoreCase("WINDOW") == 0); }
//		if(dontParseArg) {      // Let OS parse it
//			try{
//				process = runtime.exec(buffer);
//			}catch(Exception e){
//			}
//		} else {
//			argList = Ruleset.argSplit(buffer, true);
//			String[] argArray = new String[argList.size()];
//			argArray = (String[]) argList.toArray(argArray);
//			process = runtime.exec(argArray);
//		}
		value = "";
		if(outputList.size() > 1) {
			int outputIndex = 7;
			while(outputIndex < outputList.size() && outputList.get(outputIndex).toString().indexOf("traceback") < 0) {
				value += outputList.get(outputIndex).toString() + "\n\t";
				outputIndex++;
			}
			errorMessage("Problem running Chronos:\n\t " + value, false);
			return "NULL";
		} else {
			value = outputList.get(0).toString().trim();
			return value;
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
