/**
 * 
 */
package org.cfpm.queue;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.Dump;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

/**
 * @author Ruth Meyer
 *
 */
public class QMeanWaitTime extends DefaultReporter {

	/** The mean-wt primitive expects a queue as input and returns a number (the average wait time of the given queue).
	 * 
	 */
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()}, Syntax.NumberType());
	}
	
	/** Returns the average wait time of the specified queue. The first argument 
	 * {@link args[0]} has to be a queue.
	 * Generates an error if the argument is invalid.
	 * 
	 * @param args the arguments to this call of mean-wt
	 * @param context the NetLogo context
	 * @return the mean wait time of the queue
	 * @throw ExtensionException if any of the arguments are invalid
	 * @see org.nlogo.api.Reporter#report(org.nlogo.api.Argument[], org.nlogo.api.Context)
	 */
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
	    Object arg0 = args[0].get();
	    if (!(arg0 instanceof Queue)) {
	        throw new ExtensionException ("not a queue: " + Dump.logoObject(arg0));
	    }
		return Double.valueOf(((Queue)arg0).getMeanWaitTime());
	}

}
