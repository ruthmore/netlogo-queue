/**
 * 
 */
package org.cfpm.queue;

import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.PrimitiveManager;

/**
 * @author ruth
 *
 */
public class QueueExtension extends DefaultClassManager {

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
	}

}
