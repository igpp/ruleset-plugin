/**
 * Command line launcher of applications.
 * 
 * Entry point for executing a collection of classes
 * using a single commmand-line interface. Useful
 * when constructing an executable jar file.
 * <p>
 * Development funded by NASA's VMO project at UCLA.
 *
 * @author Todd King
 * @author UCLA/IGPP
 * @version     1.0
 * @since		1.0
 */

package igpp.ruleset.plugin;

import java.lang.reflect.Method;

public class App
{
	private String	mVersion = "1.0.0";

	boolean	mVerbose = false;
	
	String mCommands[] = {"Compare", "FFHScan", "FormatDescription", "IMath", "LabelValue", "Lookup", "SpreadSheet", "TabStartStop", "TargetPhrase", "Time" };
	
	public App() 
	{
	}
		
   /** 
	 * Command-line interface.
	 *
	 **/
	public static void main(String args[])
   {
		App me = new App();
		
		if(args.length < 1) {
			me.showHelp();
			return;
		}
		
		// create the command line parser
		try { // parse the command line arguments
		   String task = args[0];
		   boolean found = false;
		   for(String name : me.mCommands) {
		   	if(task.compareTo(name) == 0) { found = true; break; }
		   }
		   if( ! found ) {
		   	System.out.println("Unknown command: " + task);
		   	return;
		   }
		   
		   String packageName = igpp.util.Text.getFileBase(me.getClass().getName());
		   String className = igpp.util.Text.toProperCase(task);
		   String callClass = packageName + "." + className;
		   
		   Class c = Class.forName(callClass);
		   
		   // Create new arg list
		   String newArgs[] = new String[args.length - 1];
		   for(int i = 1; i < args.length; i++) newArgs[i-1] = args[i];
		   
		   // Call main()
		   // Class[] types = new Class[]{newArgs.getClass()};
		   Class[] types = new Class[]{String[].class};
		   Method method = c.getMethod("main", types);
			if(method != null) {
				Object[] passParam = new Object[1];
				passParam[0] = newArgs;
				method.invoke(c, passParam);
			}
		   
		} catch(Exception e) {
			e.printStackTrace();
		}
   }

	/**
	 * Display help information.
	 **/
   public void showHelp()
   {
		System.out.println("");
		System.out.println(getClass().getName() + "; Version: " + mVersion);
 		System.out.println("Command line launcher of applications.");
 		System.out.println("in a folder tree according to the Resource ID.");
 		System.out.println("");
 		System.out.println("Entry point for executing a collection of classes");
 		System.out.println("using a single commmand-line interface.");
		System.out.println("");

		System.out.println("");
		System.out.println("Supported commands: ");
		for(String name : mCommands) System.out.println("   " + name);
		
		System.out.println("");
		System.out.println("Acknowledgements:");
		System.out.println("Development funded by NASA's VMO project at UCLA.");
	}
}
