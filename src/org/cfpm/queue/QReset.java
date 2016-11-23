/**
 * 
 */
package org.cfpm.queue;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.Dump;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

/**
 * @author Ruth Meyer
 *
 */
public class QReset extends DefaultCommand {

	/** The reset primitive expects a queue and the current time (as double) as inputs.
	 * 
	 */
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[]{Syntax.WildcardType(), Syntax.NumberType()});
	}

	/** Performs the reset of the statistical counters. First argument {@link args[0]} has to be a queue, second argument
	 * {@link args[1]} has to be a double value representing the current time.
	 * 
	 *  @param args the arguments to this call of reset
	 *  @param context the NetLogo context
	 *  @throws ExtensionException if any of the arguments are of the wrong type
	 * @see org.nlogo.api.Command#perform(org.nlogo.api.Argument[], org.nlogo.api.Context)
	 */
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		Object arg0 = args[0].get();
		if (! (arg0 instanceof Queue)) {
	        throw new ExtensionException ("not a queue: " + Dump.logoObject(arg0));			
		}
		Queue q = (Queue)arg0;
		double arg1;
		try {
			arg1 = args[1].getDoubleValue();
			q.resetStats(arg1);
		}
		catch (LogoException e) {
			throw new ExtensionException ("not a number: " + Dump.logoObject(args[2]));
		}

	}

}
