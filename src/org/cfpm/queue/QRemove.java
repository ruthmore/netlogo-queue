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
import org.nlogo.api.Nobody$;
import org.nlogo.api.Syntax;

/**
 * @author Ruth Meyer
 *
 */
public class QRemove extends DefaultReporter {
	
	/** The remove primitive expects a queue and the current time as input and returns the object at the top of the queue 
	 *  (or nobody, if the queue is empty)
	 * 
	 */
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(), Syntax.NumberType()}, Syntax.WildcardType());
	}
	

	/** Returns the first element in the specified queue, or nobody if the queue is empty. The first argument 
	 * {@link args[0]} has to be a queue, the second argument {@link args[1]} has to be a double value representing
	 * the current time.
	 * Generates an error if the arguments are invalid.
	 * 
	 * @param args the arguments to this call of remove
	 * @param context the NetLogo context
	 * @return the first element of the queue
	 * @throw ExtensionException if any of the arguments are invalid
	 * @see org.nlogo.api.Reporter#report(org.nlogo.api.Argument[], org.nlogo.api.Context)
	 */
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
		Object arg0 = args[0].get();
		if (! (arg0 instanceof Queue)) {
	        throw new ExtensionException ("not a queue: " + Dump.logoObject(arg0));			
		}
		Queue q = (Queue)arg0;
		double arg1;
		try {
			arg1 = args[1].getDoubleValue();
			Object elem = q.dequeue(arg1);
			if (elem == null) {
				// turn it into nobody
				return Nobody$.MODULE$;
			}
			return elem;
		}
		catch (LogoException e) {
			throw new ExtensionException ("not a number: " + Dump.logoObject(args[1]));
		}
	}

}
