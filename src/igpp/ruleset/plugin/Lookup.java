package igpp.ruleset.plugin;

import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

import igpp.util.Argument;
import igpp.util.Date;
import igpp.table.Table;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

/** 
 * Multi-function lookup utility. This application is designed to function
 * as a plug-in the the PPI Rulesets. This plug-in supports a variety of lookup
 * related functions including the lookup of values in ASCII comma separated
 * value (CSV) tables.
 * <p>
 * The general form of usage is:<br>
 * <blockquote>
 * lookup <i>service [parameters...]</i>
 * </blockquote>
 * where <i>service</i> is the name of the service to run and
 * <i>parameters</i> is one or more parameters required by the service.
 * <p>
 * <p>
 * Supported lookup services are:<br>
 *<blockquote>
 *<b>Table</b>: Lookup values from a file which contains ASCII, comma separated
 * values.
 * </blockquote>
 *
 * @author Todd King, Steven Joy, Joe Mafi
 * @author IGPP/UCLA/PDS
 * @version     1.0, 05/21/03
 * @since		1.0
 */
public class Lookup {
	
    /** 
     * Creates an instance.
     */
     public Lookup() {
     }

	/** 
	 * Entry point for application.
	 */
	public static void main(String[] args) {
		boolean	good = false;
		
		if(args.length < 1) {
			Ruleset.showRule(Action.MESSAGE, "lookup plugin. Proper usage: time service {...}");
			Ruleset.showRule(Action.MESSAGE, "Available services are:");
			Ruleset.showRule(Action.MESSAGE, "\tTable: Lookup values in an ASCII table.");
			return;
		}
		
		if(args[0].compareToIgnoreCase("Table") == 0) { good = true; Table(args); }
		
		if(!good) Ruleset.showRule(Action.MESSAGE, "time called with unknown action request. " + args[0]);
	}
	
/** 
 * Lookup values in an ASCII CSV table.
 * The lookup plug-in returns a PDS label parameter compatible with the PDS/PPI ruleset
 * language. Returned values are based upon a lookup table search. 
 * Results are returned as a ruleset assignment and are in the form:
 *
 *<blockquote>
 *	$&lt;parameter> = value
 *</blockquote>
 *
 * Returned values are formatted according to PDS implementation of ODL lexical standards. This
 * means that strings are enclosed in double quotes, and multiple value sets are enclosed in brackets
 * "{}".
 * <p>
 * <b>Command line options</b>
 * <p>
 * Required command line options:
 * <p>
 *<blockquote>
 * <dt><b>source</b></dt><dd>[character string] Name (includ ing path) of the lookup table. Lookup tables
 * should be ASCII CSV files with the format: start_time,stop_time,value. Here
 * start_time and start_time are in PDS format (truncated at the minutes), and
 * value is the return value assigned to that interval.</dd>
 *
 * <dt><b>start</b></dt><dd>[PDS time*] Start time of the file to be labeled</dd>
 *
 * <dt><b>stop</b></dt><dd>[PDS time*] Stop time of the file to be labeled</dd>
 *
 * <dt><b>fudge</b></dt><dd>[integer] Size of the tolerance (in minutes) used 
 * in matching the <i>start</i> and <i>stop</i> to intervals in the lookup table. 
 * The way the fudge factor is applied depends upon the search <i>method</i> (see section describing search 
 * </i>method</i> below)</dd>
 *
 * <dt><b>parameter</b></dt><dd>[character string] Parameter to return (e.g. TARG_LIST).</dd>
 *
 * <dt><b>method</b></dt><dd>[character string] Name of the method to use in search of 
 * lookup table. Options:
 *<blockquote>
 *
 * <b>Phase</b> - Selection criteria: start >= start_time -
 * fudge && stop <= stop_time + fudge_factor.<br>
 * <b>Target</b> - Selection criteria: start <= stop_time -
 * fudge && stop >= start_time + fudge_factor.
 *</blockquote>
 * </dd>
 *
 * <dt><b>return</b></dt><dd>[character string] Indicates whether to return value 
 * will consist of a list or a single item. Options:<blockquote>
 * <b>LIST</b> - multiple values may be returned in a comma-separated list<br>
 * <b>SINGLE</b> - only a single value will be returned<br>
 *</blockquote>
 * Default for <i>return</i> depends upon the choice of search <i>method</i>. <br>
 * If method=0, then the default return is SINGLE. 
 * If method=1, then the default return is LIST.
 *</dd>
 *
 *</blockquote>
 * <p>
 * <b>Optional command line options:</b>
 * <p>
 *<blockquote>
 *
 * <dt><b>order</b><dd>[character string] Way in which a LIST will be sorted. If
 * return_type=SINGLE any value given for list_order is ignored. Options:<blockquote>
 *
 * <b>CHRON_ASCENDING</b> - ascending by start_time<br>
 * <b>CHRON_DESCENDING</b> - descending by start_time<br>
 * <b>ALPHA_ASCENDING</b> - ascending by ASCII sort of return value<br>
 * <b>ALPHA_DESCENDING</b> - descending by ASCII sort of return value<br>
 * <b>AS_IS</b> - preserve order of items in the lookup table<br>
 *
 * </blockquote></dd>
 *
 * <dt><b>single</b></dt><dd>[character string] Method by which to reduce the return value from multiple
 * items to a single item. Options:<blockquote>
 * <b>MAX_LOOKUP</b> - value corresponding to the interval from lookup table
 * of longest duration<br>
 * <b>MIN_LOOKUP</b> - value corresponding to the interval from lookup table of
 * shortest duration<br>
 * <b>MAX_DATA</b> - value from lookup table covering the greatest amount
 * (time span) of data interval<br>
 * <b>MIN_DATA</b> - value from lookup table covering the least amount (time
 * span) of data interval<br></blockquote></dd>
 *
 * <dt><b>max</b></dt><dd>[integer] Maximum number of items to include in a list. After a list is sorted
 * according to the list_order parameter, items in excess of max_list_items will
 * be dropped. The first item is considered item 1. max_list_items = 1 is
 * equivalent to return_type = SINGLE, using list_order rather than
 * single_method to determine what value is returned.</dd>
 * </blockquote>
 *
 * <b>Defaults</b>
 *<blockquote>
 * fudge=0<br>
 * return:<br>
 * if method=Phase, return=SINGLE<br>
 * if method=Target, return=LIST<br>
 * order = AS_IS<br>
 * single=MIN_LOOKUP<br>
 * max=No maximum (all items will be returned)<br>
 *</blockquote>
 *
 * PDS Time Note: Both year-month-day or year-day of year format PDS times are supported.
 * <p>
 * <b>Examples</b><br>
 * excerpt from targets lookup table "GLL_TARGET.TAB" ...
 * <blockquote><code><pre>
 * 1995-07-01T00:00,             EOM , JUPITER
 * 1997-12-16T11:30, 1997-12-16T12:45, EUROPA
 * 1997-12-15T09:30, 1997-12-15T10:30, GANYMEDE
 * LAUNCH          , 1995-12-06T00:00, SOLAR_WIND
 * </pre></code></blockquote>
 *<p>
 * <b>Example 1:</b><br>
 * excerpt from rule set ...
 * <blockquote><code><pre>
 * $lookup = "GLL_TARGET.TAB"
 * $start = "1997-12-15T00:00"
 * $stop = "1998-02-09T00:00"
 * $fudge = 720
 * $parameter = "TARG_LIST"
 * $search = 1
 * $return = "LIST"
 *
 * &lt;RUN java lookup table source=$lookup start=$start
 *    stop=$stop fudge=$fudge parameter=$parameter
 *    method=$search return=$return>
 * </pre></code></blockquote>
 * <p>
 * would return:<br>
 * <blockquote><code><pre>
 * $TARG_LIST = {"JUPITER","EUROPA","GANYMEDE"}
 * </pre></code></blockquote>
 *
 * <b>Example 2:</b><br>
 * excerpt from rule set ...
 * <blockquote><code><pre>
 * [same as Example 1, except]
 * $order = "CHRON_ASCENDING"
 *
 * &lt;RUN java lookup table source=$lookup start=$start
 *    stop=$stop fudge=$fudge parameter=$parameter
 *    method=$search return=$return order=$order>
 * </pre></code></blockquote>
 * <p>
 * would return:<br>
 * <blockquote><code><pre>
 * $TARG_LIST = {"JUPITER","GANYMEDE","EUROPA"}
 * </pre></code></blockquote>
 *
 * <b>Example 3:</b><br>
 * excerpt from rule set ...
 * <blockquote><code><pre>
 * [same as Example 1, except]
 * $order = "CHRON_DESCENDING"
 * $max = 1
 *
 * &lt;RUN java lookup table source=$lookup start=$start
 *    stop=$stop fudge=$fudge parameter=$parameter
 *    method=$search return=$return order=$order
 *    max=$max>
 * </pre></code></blockquote>
 * <p>
 * would return:<br>
 * <blockquote><code><pre>
 * $TARG_LIST = "EUROPA"
 * </pre></code></blockquote>
 *
 * <b>Example 4:</b><br>
 * excerpt from rule set ...
 * <blockquote><code><pre>
 * [same as Example 1, except]
 * $search = 1
 * $return = "SINGLE"
 * $single = "MIN_LOOKUP"
 *
 * &lt;RUN java lookup table source=$lookup start=$start
 *    stop=$stop fudge=$fudge parameter=$parameter
 *    method=$search return=$return single=$single>
 * </pre></code></blockquote>
 * <p>
 * would return:<br>
 * <blockquote><code><pre>
 * $TARG_LIST = "GANYMEDE"
 * </pre></code></blockquote>
 *
 * <b>Example 5 </b>(differs from example 4 only in search method):<br>
 * excerpt from rule set ...
 * <blockquote><code><pre>
 * [same as Example 1, except]
 * $search = 0
 * $return = "SINGLE"
 * $single = "MIN_LOOKUP"
 *
 * &lt;RUN java lookup table source=$lookup start=$start
 *    stop=$stop fudge=$fudge parameter=$parameter
 *    method=$search return=$return single=$single>
 * </pre></code></blockquote>
 * <p>
 * would return:<br>
 * <blockquote><code><pre>
 * $TARG_LIST = "JUPITER"<br>
 * </pre></code></blockquote>
 *
 * @since           1.0
 */
	public static void Table(String[] args) {
		// Process passed arguments
		String	source		= Argument.find(args, "SOURCE", null, 1);
		String	start		= Argument.find(args, "START", null, 1);
		String	stop		= Argument.find(args, "STOP", null, 1);
		String	fudge		= Argument.find(args, "FUDGE", "0", 1);
		String	parameter	= Argument.find(args, "PARAMETER", null, 1);
		String	method		= Argument.find(args, "METHOD", "Phase", 1);
		String	returnAs	= Argument.find(args, "RETURN", null, 1);
		String	order		= Argument.find(args, "ORDER", null, 1);
		String	maxList		= Argument.find(args, "MAX", "0", 1);
		String	single		= Argument.find(args, "SINGLE", "MIN_SPAN", 1);
		String	format		= Argument.find(args, "FORMAT", "AS-IS", 1);
		String	delim		= Argument.find(args, "DELIM", ",", 1);
		
		// Enumerated METHOD tokens
		final int	METHOD_PHASE 	= 0;
		final int	METHOD_TARGET 	= 1;
		Argument	methodOpt = new Argument();
		methodOpt.add("PHASE",	METHOD_PHASE);
		methodOpt.add("TARGET",	METHOD_TARGET);
		
		// Enumerated RETURN tokens
		final int	RETURN_LIST 	= 0;
		final int	RETURN_SINGLE 	= 1;
		Argument	returnOpt = new Argument();
		returnOpt.add("LIST",	RETURN_LIST);
		returnOpt.add("SINGLE",	RETURN_SINGLE);
		
		// Enumerated ORDER tokens
		final int	ORDER_AS_IS				= 0;
		final int	ORDER_CHRON_ASCENDING	= 1;
		final int	ORDER_CHRON_DESCENDING	= 2;
		final int	ORDER_OVERLAP_ASCENDING	= 3;
		final int	ORDER_OVERLAP_DESCENDING= 4;
		final int	ORDER_SPAN_ASCENDING	= 5;
		final int	ORDER_SPAN_DESCENDING	= 6;
		final int	ORDER_UNIQUE			= 7;
		Argument	orderOpt = new Argument();
		orderOpt.add("AS_IS",			ORDER_AS_IS);
		orderOpt.add("CHRON_ASCENDING",	ORDER_CHRON_ASCENDING);
		orderOpt.add("CHRON_DESCENDING",ORDER_CHRON_DESCENDING);
		orderOpt.add("OVERLAP_ASCENDING",	ORDER_OVERLAP_ASCENDING);
		orderOpt.add("OVERLAP_DESCENDING",	ORDER_OVERLAP_DESCENDING);
		orderOpt.add("SPAN_ASCENDING",	ORDER_SPAN_ASCENDING);
		orderOpt.add("SPAN_DESCENDING",ORDER_SPAN_DESCENDING);
		
		// Enumerated SINGLE tokens
		final int	SINGLE_MAX_OVERLAP	= 1;
		final int	SINGLE_MIN_OVERLAP	= 2;
		final int	SINGLE_MAX_SPAN		= 3;
		final int	SINGLE_MIN_SPAN		= 4;
		Argument	singleOpt = new Argument();
		singleOpt.add("MAX_OVERLAP",	SINGLE_MAX_OVERLAP);
		singleOpt.add("MIN_OVERLAP",	SINGLE_MIN_OVERLAP);
		singleOpt.add("MAX_SPAN",		SINGLE_MAX_SPAN);
		singleOpt.add("MIN_SPAN",		SINGLE_MIN_SPAN);
		
		// Enumerated METHOD tokens
		final int	FORMAT_AS_IS 		= 0;
		final int	FORMAT_STRING 		= 1;
		final int	FORMAT_IDENTIFIER 	= 2;
		Argument	formatOpt = new Argument();
		formatOpt.add("AS-IS",		FORMAT_AS_IS);
		formatOpt.add("STRING",		FORMAT_STRING);
		formatOpt.add("IDENTIFIER",	FORMAT_IDENTIFIER);
		
		ArrayList	findList = new ArrayList();
		Table	table = new Table();
		Calendar		time = Date.getNow();
		boolean		leave = false;
		String		buffer = "";
		String[]	field;
		Calendar		recordStart = Date.getNow();
		Calendar		recordStop = Date.getNow();
		
		Calendar		timeStart = Date.getNow();
		Calendar		timeStop = Date.getNow();
		Item			item;
		ArrayList	valueList = new ArrayList();
		int			cnt;
		
		// Internal values of options.
		int			max;
		double		minutes;
		int			methodID;
		int			orderID;
		int			singleID;
		int			returnID;
		int			formatID;
		
		String		header = new String("'lookup table' not called with the proper arguments.");

		// Check that required arguments were passed.	
		if(source == null) {
			Ruleset.show(header);
			Ruleset.showRule(Action.MESSAGE, "\tSOURCE not specified");
			leave = true;
		}
		if(delim == null) {
			delim = ",";
		}
		if(start == null) {
			if(!leave) Ruleset.showRule(Action.MESSAGE, header);
			Ruleset.showRule(Action.MESSAGE, "\tSTART not specified");
			leave = true;
		}
		if(stop == null) {
			if(!leave) Ruleset.showRule(Action.MESSAGE, header);
			Ruleset.showRule(Action.MESSAGE, "\tSTOP not specified");
			leave = true;
		}
		if(parameter == null) {
			if(!leave) Ruleset.showRule(Action.MESSAGE, header);
			Ruleset.showRule(Action.MESSAGE, "\tPARAMETER not specified");
			leave = true;
		}
		if(leave) return;

		// Tokenize options
		methodID = methodOpt.token(method);
		if(methodID == -1) {
			Ruleset.showRule(Action.MESSAGE, "\tInvalid option for METHOD: " + method);
			return;
		}
		
		singleID = singleOpt.token(single);
		if(singleID == -1) {
			Ruleset.showRule(Action.MESSAGE, "\tInvalid option for SINGLE: " + single);
			return;
		}
		
		formatID = formatOpt.token(format);
		if(formatID == -1) {
			Ruleset.showRule(Action.MESSAGE, "\tInvalid option for FORMAT: " + single);
			return;
		}
		minutes = Double.parseDouble(fudge);
		max = Integer.parseInt(maxList);
		timeStart = Date.parse(start, Date.PDS);
		timeStop = Date.parse(stop, Date.PDS);
		
		// Set return form
		if(returnAs == null) returnAs = "LIST";
		returnID = returnOpt.token(returnAs);
		if(returnID == -1) {
			Ruleset.showRule(Action.MESSAGE, "\tInvalid option for RETURN: " + returnAs);
			return;
		}
		
		// If single select - choose sorting so that 
		// desired value is first in list.
		if(order == null) {
			orderID = ORDER_AS_IS;
			switch(returnID) {
			case RETURN_SINGLE:
				switch(singleID) {
				case SINGLE_MAX_OVERLAP:
					orderID = ORDER_OVERLAP_DESCENDING;
					break;
				case SINGLE_MIN_OVERLAP:
					orderID = ORDER_OVERLAP_ASCENDING;
					break;
				case SINGLE_MAX_SPAN:
					orderID = ORDER_SPAN_ASCENDING;
					break;
				case SINGLE_MIN_SPAN:
					orderID = ORDER_SPAN_DESCENDING;
					break;
				}
				break;
			}
		} else {
			orderID = orderOpt.token(order);
			if(orderID == -1) {
				Ruleset.showRule(Action.MESSAGE, "\tInvalid option for ORDER: " + order);
				return;
			}
		}
		
		// Adjust time based on method
		switch(methodID) {
		case METHOD_PHASE:
			if(returnAs == null) returnAs = "SINGLE";
			break;
		case METHOD_TARGET:
			if(returnAs == null) returnAs = "LIST";
			break;
		}
		
		// Open the table
		try {
			table.open(source, delim);
		} catch(Exception e) {
			Ruleset.showRule(Action.MESSAGE, "Unable to open file: " + source);
			return;
		}

		// Scan table for records		
		while(table.readRecord() != -1) {
			try {
				recordStart = Date.parse(table.getFieldValue(0), Date.PDS);
				recordStop = Date.parse(table.getFieldValue(1), Date.PDS);
			} catch(Exception e) {
				Ruleset.showRule(Action.MESSAGE, "Error parsing record " + table.getRecordNumber());
				continue;
			}
			switch(methodID) {
			case METHOD_PHASE:
				Date.advance(recordStart, -minutes);
				Date.advance(recordStop, minutes);
				if(Date.compareTo(timeStart, recordStart) >= 0 && Date.compareTo(timeStop, recordStop) <= 0) {
					item = new Item();
					item.mStart = (Calendar) recordStart.clone();
					item.mStop = (Calendar) recordStop.clone();
					item.setOverlap(timeStart, timeStop);
					item.mValue = table.getFieldValue(2);
					findList.add(item);
				}
				break;
			case METHOD_TARGET:
				Date.advance(recordStart, minutes);
				Date.advance(recordStop, -minutes);
				if(Date.compareTo(timeStart, recordStop) <= 0 && Date.compareTo(timeStop, recordStart) >= 0) {
					item = new Item();
					item.mStart = (Calendar) recordStart.clone();
					item.mStop = (Calendar) recordStop.clone();
					item.setOverlap(timeStart, timeStop);
					item.mValue = table.getFieldValue(2);
					findList.add(item);
				}
				break;
			}
				
		}
		
		if(findList.size() > 0) {
			item = (Item) findList.get(0);
			item.mCompareOrder = orderID;
		}
		// Sort the list 
		SortedSet set = new TreeSet(findList);
		
		// Build value list
		switch(returnID) {
		case RETURN_SINGLE:
			try {
				item = (Item) set.first();
				valueList.add(item.mValue);
			} catch (Exception e) {
			}
			break;
		case RETURN_LIST:
			if(set.size() > 0) {
				Iterator it = set.iterator();
				ArrayList setArray = new ArrayList();
				while(it.hasNext()) {
					item = (Item) it.next();
					item.mCompareOrder = ORDER_UNIQUE;
					setArray.add(item);
				}
				// Sort the list and remove duplicates
				SortedSet uniqueSet = new TreeSet(setArray);
				it = uniqueSet.iterator();
				cnt = 0;
				while(it.hasNext()) {
					if(cnt == max && max > 0) break;
					cnt++;
					item = (Item) it.next();
					valueList.add(item.mValue);
				}
			}
			break;
		}
		
		// Build-up response
		String value = "";
		String sep = "";
		String quote = "";
		if(valueList.size() == 0) {
			value = "";
		} else {
			if(valueList.size() > 1 || returnID == RETURN_LIST) { value += "{ ";	quote = "\""; } // Start a set 
			for(int i = 0; i < valueList.size(); i++) {
				buffer = (String) valueList.get(i);
				switch(formatID) {
				case FORMAT_AS_IS:
					value += sep + quote + buffer + quote;
					break;
				case FORMAT_STRING:
					buffer = buffer.replace('_', ' ');
					value += sep + quote + buffer + quote;
					break;
				case FORMAT_IDENTIFIER:
					buffer = buffer.replace(' ', '_');
					buffer = buffer.toUpperCase();
					value += sep + buffer;
					break;
				}
				sep = ", ";
			}
			if(valueList.size() > 1 || returnID == RETURN_LIST) value += " }";	// End of set
		}
	
		Ruleset.showRule(Action.ASSIGN, parameter, value);
		
		table.close();
	}

	/** Nested class - internal use only.
	 *  This is a nested class which is used to store items found in the
	 *  lookup source which match the passed constraints. 
	 */
	static class Item implements Comparable {
		/** Start time of the item **/	Calendar	mStart;
		/** Stop time of the item **/	Calendar	mStop;
		/** Value of the item. **/		String		mValue;
		/** Overlap with interval **/	long		mOverlap;
		
		/** The method of comparing Items. The value assigned to this
		 *  method matches the "order" option. 
		 *  
		 */ 
		 static int	mCompareOrder = 0;
		
		/** 
		 * Creates an instanace.
		 */
	 	public Item() {
		}
		
		/** 
		 * Sets the overlap of the passed start and stop times with this 
		 * interval. Should be called after the mStart and mStop times
		 * are set.
		 *
		 * @param	start	the start time of the interval.
		 * @param stop		the stop time of the interval.
		 *
		 */
		public void setOverlap(Calendar start, Calendar stop) {
			long	startMilli;
			long	stopMilli;
			
			if(Date.compareTo(mStart, start) < 0) {
				startMilli = start.getTimeInMillis();
			} else {
				startMilli = mStart.getTimeInMillis();
			}
			
			if(Date.compareTo(mStop, stop) > 0) {
				stopMilli = stop.getTimeInMillis();
			} else {
				stopMilli = mStop.getTimeInMillis();
			}
			
			mOverlap = stopMilli - startMilli;
		}
		
		/** 
		 * Compares two Items.
		 */
		public int compareTo(Object o) {
			double	diff;
			int		c = 0;
			int		d = 1;
			Item to = (Item) o;
			
			switch(mCompareOrder) {
			case 0:	// AS_IS
				return 1;
			case 1: // CHRON_ASCENDING
				c = Date.compareTo(mStart, to.mStart);
				d = mValue.compareTo(to.mValue);
				break;
			case 2:	// CHRON_DESCENDING
				c = -Date.compareTo(mStart, to.mStart);
				d = mValue.compareTo(to.mValue);
				break;
			case 3:	// OVERLAP_ASCENDING
				diff = mOverlap - to.mOverlap;
				if(diff == 0) d = 0;
				if(diff < 0) d = -1;
				if(diff > 0) d = 1;
				break;
			case 4:	// OVERLAP_DESCENDING
				diff = to.mOverlap - mOverlap;
				if(diff == 0) d = 0;
				if(diff < 0) d = -1;
				if(diff > 0) d = 1;
				break;
			case 5: // SPAN_ASCENDING
				diff = Date.span(mStart, mStop) - Date.span(to.mStart, to.mStop);
				if(diff == 0) d = 0;
				if(diff < 0) d = -1;
				if(diff > 0) d = 1;
				break;
			case 6:	// SPAN_DESCENDING
				diff = Date.span(to.mStart, to.mStop) - Date.span(mStart, mStop);
				if(diff == 0) d = 0;
				if(diff < 0) d = -1;
				if(diff > 0) d = 1;
				break;
			case 7:	// Remove duplicates - UNIQUE
				d = mValue.compareTo(to.mValue);
				if(d != 0) d = 1;	// Keep same order - throw out duplicates
				break;
			}
			if(c == 0) return d;
			else return c;
		}
	}
}
