/**
 * 
 */
package org.cfpm.queue;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

/**
 * @author Ruth Meyer
 *
 */
public class QCreate extends DefaultReporter {

	/** The create primitive expects a strategy as input and returns a queue.
	 * 
	 */
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(new int[]{Syntax.NumberType()}, Syntax.WildcardType());
	}
	
	/** Performs the creation of a new queue and reports it. The first argument {@link args[0]} has
	 * to contain the queueing strategy.
	 * 
	 * @param args the arguments to this call of create
	 * @param context the NetLogo context
	 * @return a reference to the newly created queue
	 * @throw ExtensionException if the argument is invalid
	 * @see org.nlogo.api.Reporter#report(org.nlogo.api.Argument[], org.nlogo.api.Context)
	 */
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
		try {
			int strategy = args[0].getIntValue();
			if (! Queue.isValidStrategy(strategy)) {
				throw new ExtensionException(strategy + " is not a valid queue strategy.");
			}
			Queue queue = new Queue(strategy);
			return queue;
		}
		catch (LogoException e) {
			throw new ExtensionException (e.getMessage());
		}
	}

}
