/**
 * 
 */
package org.cfpm.queue;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.Reporter;
import org.nlogo.api.Dump;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

/**
 * @author Ruth Meyer
 *
 */
public class QGetStats implements Reporter {
	
	// expects a queue and returns a list of statistical measures
	/** The get-stats primitive expects a queue as input and returns a list of queue statistics.
	 * 
	 */
	public Syntax getSyntax() {
		return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType()}, Syntax.ListType());
	}

	/** Returns a list of statistical measures of the specified queue in the following order:
	 * mean size, max size, mean wait time, max wait time, min wait time. The first argument 
	 * {@link args[0]} has to be a queue.
	 * Generates an error if the argument is invalid.
	 * 
	 * @param args the arguments to this call of get-stats
	 * @param context the NetLogo context
	 * @return a list of statistics of the queue
	 * @throw ExtensionException if any of the arguments are invalid
	 * @see org.nlogo.api.Reporter#report(org.nlogo.api.Argument[], org.nlogo.api.Context)
	 */
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
		Object arg0 = args[0].get();
	    if (!(arg0 instanceof Queue)) {
	    	throw new ExtensionException ("not a queue: " + Dump.logoObject(arg0));
	    }
		Queue q = (Queue)arg0;
	    LogoListBuilder list = new LogoListBuilder();
	    list.add(Double.valueOf(q.getMeanSize()));
	    list.add(Double.valueOf(q.getMaxSize()));
	    list.add(Double.valueOf(q.getMeanWaitTime()));
	    list.add(Double.valueOf(q.getMaxWaitTime()));
	    list.add(Double.valueOf(q.getMinWaitTime()));
	    QueueExtension.writeToNetLogo(list.toLogoList().toString(), false, context);
		return list.toLogoList();
	}

}
