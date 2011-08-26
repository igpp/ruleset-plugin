package igpp.ruleset.plugin;

import igpp.ruleset.Action;
import igpp.ruleset.Ruleset;

import igpp.util.Argument;
import igpp.util.Date;

import java.util.Calendar;

/** 
 * Multi-function time utility. This application is designed to function
 * as a plug-in the the PPI Rulesets. This plug-in supports a variety of time
 * related functions including the conversion between different time formats.
 * <p>
 * The general form of usage is:<br>
 * <blockquote>
 * time <i>service name [parameters...]</i>
 * </blockquote>
 * where <i>service</i> is the name of the service to run and
 * <i>name</i> is the variable name to use in the rule that is output
 * with the results of the requested service and <i>parameters</i> is one
 * or more parameters required by the service.
 * <p>
 * Supported services are:<br>
 * <blockquote>
 * <b>Convert</b>: Convert from one time format to another.<br>
 * <b>Now</b>: Return the current time.
 * </blockquote>
 * See the descriptions for each service for required parameters.
 * <p>
 * All time formats are specified using the format specified in {@link PPITime}
 * and also supports formats specified in {@link Date}.
 * <p>
 * Results are returned as a ruleset assignment and are in the form:
 *
 *<blockquote>
 *	$&lt;name> = value
 *</blockquote>
 *
 * @see Date
 * @author Todd King
 * @author IGPP/UCLA/PDS
 * @version     1.0, 05/21/03
 * @since		1.0
 */
public class Time {
    /** 
     * Creates an instance.
     */
     public Time() {
     }

	/** Entry point for the application
	 */
	public static void main(String[] args) {
		boolean	good = false;
		
		if(args.length < 1) {
			Ruleset.showRule(Action.MESSAGE, "time plugin. Proper usage: time service {...}");
			Ruleset.showRule(Action.MESSAGE, "Available services are:");
			Ruleset.showRule(Action.MESSAGE, "\tConvert: Convert time in one format into another.");
			Ruleset.showRule(Action.MESSAGE, "\tNow: Output the current time a specified format.");
			return;
		}
		
		if(args[0].compareToIgnoreCase("Convert") == 0) { good = true; Convert(args); }
		if(args[0].compareToIgnoreCase("Now") == 0) { good = true; Now(args); }
		
		if(!good) System.out.println("<MESSAGE time called with unknown action request. " + args[0] + ">");
	}
	
/** 
 * Convert time from one format into another.
 * Proper use: <blockquote><code>
 *
 *    Convert <i>name time format output</i>
 *
 * </code></blockquote>
 * where: <br>
 * <blockquote>
 * <dt><b>Parameter</b></dt><dd>the name of the variable to assign the output to.</dd>
 * <dt><b>time</b></dt><dd>the string with the time value to convert.</dd>
 * <dt><b>format</b></dt><dd>the name of the standard time format or a string containing the
 * 						the specification of the time format <i>time</i> is in. See {@link PPITime}
 *						for details of how to specify a time format.</dd>
 * <dt><b>output</b></dt><dd>the name of the standard time format or a string containing the
 * 						the specification of the time format to convert <i>time</i>
 *                      into and to assign to <i>name</i>. See {@link PPITime}
 *						for details of how to specify a time format.</dd>
 * </blockquote>
 */
	public static void Convert(String[] args) {
		String	parameter	= Argument.find(args, "PARAMETER", null, 1);
		String	timeValue	= Argument.find(args, "TIME",		null, 1);
		String	formatArg	= Argument.find(args, "FORMAT", 	null, 1);
		String	outputArg	= Argument.find(args, "OUTPUT", 	null, 1);

		String	format;
		String	output;
		if(parameter == null || timeValue == null || formatArg == null || outputArg == null) {
			Ruleset.showRule(Action.MESSAGE, "'time convert' not called with the proper arguments.");
			Ruleset.showRule(Action.MESSAGE, "   proper usage 'time convert parameter time format output'.");
			return;
		}
		
		format = Date.findTimePattern(formatArg);
		output = Date.findTimePattern(outputArg);
		Calendar time = Date.parse(timeValue, format);
		Ruleset.showRule(Action.ASSIGN, parameter, Date.getDateString(time, output));
	}
	
/** 
 * Determine the current time and assign it to a variable.
 * Proper use: <blockquote><code>
 *
 *    Now <i>name output</i>
 *
 * </code></blockquote>
 * where: <br>
 * <blockquote>
 * <dt><b>parameter</b></dt><dd>the name of the variable to assign the output to.</dd>
 * <dt><b>output</b></dt><dd>the name of the standard time format or a string containing the
 * 						the specification of the time format <i>time</i> is in. See {@link PPITime}
 *						for details of how to specify a time format.</dd>
 * </blockquote>
 */
	public static void Now(String[] args) {
		String	parameter	= Argument.find(args, "PARAMETER", null, 1);
		String	outputArg	= Argument.find(args, "OUTPUT", 	null, 1);
		
		Calendar	time = Date.getNow();
		String	format;

		if(parameter == null || outputArg == null) {
			Ruleset.showRule(Action.MESSAGE, "'time now' not called with the proper arguments.>");
			Ruleset.showRule(Action.MESSAGE, "   proper usage 'time now parameter output'.");
			return;
		}
		
		format = Date.findTimePattern(outputArg);

		Ruleset.showRule(Action.ASSIGN, parameter, Date.getDateString(time, format));
	}
}
