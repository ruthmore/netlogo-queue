/**
 * 
 */
package org.cfpm.queue;

import org.nlogo.api.Context;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.PrimitiveManager;
import org.nlogo.api.OutputDestinationJ;

/**
 * @author ruth
 *
 */
public class QueueExtension extends DefaultClassManager {

	/** Flag to toggle output to NetLogo (if set to true, output will take place, if set to false, output will be ignored). 
	 * In any deployed version of the extension the flag is set to false.
	 */ 
	static private boolean outputToNetlogo = false;

	/* (non-Javadoc)
	 * @see org.nlogo.api.DefaultClassManager#load(org.nlogo.api.PrimitiveManager)
	 */
	@Override
	public void load(PrimitiveManager pMan) throws ExtensionException {
		pMan.addPrimitive("create", new QCreate());
		pMan.addPrimitive("empty?", new QEmpty());
		pMan.addPrimitive("length", new QLength());
		pMan.addPrimitive("insert", new QInsert());
		pMan.addPrimitive("remove", new QRemove());
		pMan.addPrimitive("get-stats", new QGetStats());
		pMan.addPrimitive("reset", new QReset());
		pMan.addPrimitive("show", new QShow());
		pMan.addPrimitive("max-length", new QMaxLength());
		pMan.addPrimitive("mean-length", new QMeanLength());
		pMan.addPrimitive("max-wt", new QMaxWaitTime());
		pMan.addPrimitive("mean-wt", new QMeanWaitTime());
		
	}

	/** Writes the given text to the NetLogo command center if the internal flag {@link #outputToNetlogo} is set to true.
	 * This method is solely intended for debugging purposes while developing the extension. 
	 * 
	 * @param mssg the text that is to be written to the command center
	 * @param toOutputArea should be set to false to achieve output to the command center; if set to true, 
	 * 					   output will go to the output area (if there is one), otherwise to the command center
	 * @param context	the NetLogo context
	 * @throws ExtensionException if writing fails for some reason
	 */
	protected static void writeToNetLogo(String mssg, Boolean toOutputArea, Context context) 
			throws ExtensionException
		{ 
				/* Instructions on writing to the command center as related by Seth Tissue: 
				* "Take your api.ExtensionContext, cast it to nvm.ExtensionContext, 
				* and then call the workspace() method to get a nvm.Workspace 
				* object, which has an outputObject() method declared as follows: 
				* void outputObject(Object object, Object owner, boolean addNewline, boolean readable, OutputDestination destination) 
				 * throws LogoException;
				 * 
				 * object: can be any valid NetLogo value; 
				* owner: just pass null; 
				* addNewline: whether to add a newline character afterwards; 
				* readable: "false" like print or "true" like write, controls whether 
				* the output is suitable for use with file-read and read-from-string 
				* (so e.g. whether strings are printed with double quotes); 
				* OutputDestination is an enum defined inside nvm.Workspace with 
				* three possible values: NORMAL, OUTPUT_AREA, FILE. NORMAL means 
				* to the command center, OUTPUT_AREA means to the output area if 
				* there is one otherwise to the command center, FILE is not 
				* relevant here. */ 

			if (outputToNetlogo) {
				//ExtensionContext extcontext = (ExtensionContext) context; // not necessary anymore
				try {
					context.workspace().outputObject(mssg, null, true, true,
							(toOutputArea) ? OutputDestinationJ.OUTPUT_AREA() : OutputDestinationJ.NORMAL()); 
				} 
				catch (LogoException e) {
					throw new ExtensionException(e); 
				} 
			}
		}	
	
}
