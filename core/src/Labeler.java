package igpp.ruleset;

// import igpp.util.*
import igpp.util.MultiPrinter;
import igpp.util.Variable;
import igpp.util.VariableList;

//import java.io.*;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;

// import java.util.*;
import java.util.Iterator;

/** 
 * Loads a rule set and processes one or more files.
 * Labeler will load a ruleset (@see Ruleset) and apply it to one or more files
 * in a directory tree. If a directory name is passed as the starting
 * point then the ruleset will be applied to all files in the directory
 * and subdirectories. 
 *<p>
 * Options:<blockquote>
 *    -d: Scan only the current directory. Do not recurse sub-directories.
 *    -r: Scan the current directory and recursely scan sub-directories.
 *</blockquote>
 *
 * Usage:<blockquote>
 *     labeler ruleset [-d | -R] pathname [pathname ...]
 * </blockquote>
 *
 * @author Todd King
 * @author UCLA/IGPP
 * @version     1.0, 05/22/03
 * @since		1.0
 */
public class Labeler {
	/** Preserves global variables between executions of the ruleset.
	 */
	 public VariableList	mGlobalList = new VariableList();
	 public VariableList	mCommandList = new VariableList();
	 public String		mStartPath;
	 public String		mStartRule;
	 public boolean		mRecurse = true;
	 
	 public MultiPrinter	mOutput = new MultiPrinter(System.out);
	 
	 private static final String		mVersion = "1.0.2";
	 
	/**
	 * Create an instance.
	 */
	public Labeler() {
	}
	
	/**
	 * Entry point for the application.
	 *
	 * @param args the commandline arguments.
	 */
	public static void main(String[] args) 
	{
		String[]	part;
		Labeler	me = new Labeler();
		
		// Check arguments
		if(args.length < 2) {
			System.out.println("Version: " + me.mVersion);
			System.out.println("Usage: labeler ruleset [-d|-R] [variable=value] pathname [pathname ...]");
			return;
		}
		
		try {
			// Process arguments
			me.mStartRule = args[0];
			for(int i = 1; i < args.length; i++) {
				part = args[i].split("=", 2);
				if(part.length != 2) continue;	// Non-Assignments are skipped
				me.setVariable(part[0], part[1]);
			}
			
			for(int i = 1; i < args.length; i++) {
				if(args[i].compareTo("-d") == 0) { me.setRecurse(false); continue; }
				if(args[i].compareTo("-r") == 0) { me.setRecurse(true); continue; }
				if(args[i].indexOf('=') != -1) continue;	// Assignments are skipped
				me.setStartPath(args[i]);
				if(!me.processItem(me.mStartRule, me.mStartPath)) break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Process a file or directory using a ruleset.
	 * If recusion is set to true then each file in a directory
	 * is also processed.
	 *
	 * @param ruleset	the pathname to the ruleset file.
	 * @param pathName the pathname of the file to process. 
	 *                 If <italic>pathName</italic> is a directory and
	 *                 recusion is true, then each file in the directory
	 *                 will be processed.
	 *
	 * @return true if processing was successful, false otherwise.
	 */
	public boolean processItem(String ruleset, String pathName) 
		throws Exception
	{
		File	item = new File(pathName);
		
		if( ! item.exists()) {
			System.out.println("File does not exist: " + pathName);
			return false;
		}
		if(item.isDirectory()) {
			File[] list = item.listFiles();
			
			for(int i = 0; i < list.length; i++) {
				if(list[i].isDirectory() && mRecurse) {
					processItem(ruleset, list[i].getPath()); 
				} else { 
					if(!runRuleset(ruleset, list[i].getPath())) return false; 
				}
			}
			return true;
		}
		return runRuleset(ruleset, pathName);
		
	}
		
	/** 
	 * Process a single file using a ruleset.
	 * Defines the initial environment for the ruleset and preserves
	 * the global variables between ruleset executions.
	 *
	 * @param ruleFile	the pathname to the ruleset file.
	 * @param pathName the pathname of the file to process. 
	 *                 If <italic>pathName</italic> is a directory and
	 *                 recusion is true, then each file in the directory
	 *                 will be processed.
	 *
	 * @return true if processing was successful, false otherwise.
	 */
	public boolean runRuleset(String ruleFile, String pathName) 
		throws Exception
	{
		Ruleset ruleset = new Ruleset();
		boolean	good = false;
		Iterator	it;
		
		// Start command line starting points
		mGlobalList.findAndSet(mCommandList);
		mGlobalList.findAndSet("START_RULE", mStartRule);
		mGlobalList.findAndSet("START_PATH", mStartPath);
		
		ruleset.setOutput(mOutput);
		
		// Set global variables in the ruleset processor
		it = mGlobalList.iterator();
		while(it.hasNext()) { ruleset.mGlobalList.add((Variable) it.next()); }
		
		if(!ruleset.parse(ruleFile)) {
			ruleset.showMessage(false, "An error occurred while parsing the ruleset.");
			return false;
		}
	
		// Run the ruleset
		if(!ruleset.run(pathName)) {
			ruleset.showMessage(false, "One or more errors occurred while processing file: " + pathName);
			ruleset.showMessage(false, "No output file was created.");
			return false;
		}
		
		if(ruleset.mWriteOutput) {
			if(ruleset.update()) {	// Update template
				ruleset.output();	// Write the template out to PPI standards
			}
		}
		
		// Save global variables
		mGlobalList.clear();
		for(int i = 0; i < ruleset.mGlobalList.size(); i++) mGlobalList.findAndSet((Variable) ruleset.mGlobalList.get(i));
		
		return true;
	}

	/** 
	 * Define a variable which set at the start of each exceution of a ruleset.
	 *
	 * @param name	the name of the variable.
	 * @param value	the value to the set the variable.
	 */
	public void setVariable(String name, String value) 
	{
		mCommandList.findAndSet(name, value);
	}
	
	public void setRecurse(boolean state) { mRecurse = state; }
	public boolean getRecurse() { return mRecurse; }
	
	public void setStartPath(String path) { mStartPath = path; }
	public String getStartPath() { return mStartPath; }

	public void setOutput(PrintStream stream) { mOutput.setOut(stream); }
	public void setOutput(PrintWriter writer) { mOutput.setOut(writer); }
	public MultiPrinter getOutput() { return mOutput; }

}