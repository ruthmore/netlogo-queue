/**
 * 
 */
package org.cfpm.queue;


/** An element of the queue. Consists of the actual object to be queued and a time stamp denoting time of insertion.
 * 
 * @author Ruth Meyer
 *
 */
public class QElem {
	
	Object elem;
	double time;
	
	public QElem(Object elem, double time) {
		this.elem = elem;
		this.time = time;
	}

	public Object getElem() {
		return elem;
	}

	public double getTime() {
		return time;
	}
	
	public boolean equals(Object o) {
		if (! (o instanceof QElem)) {
			return false;
		}
		QElem other = (QElem)o;
		if (other.getElem().equals(this.elem) && 
			other.getTime() >= this.time && 
			other.getTime() <= this.time)
		{
			return true;
		}
		return false;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		buf.append(String.format("%.5f", time));
		buf.append(" ");
		buf.append(elem.toString());
		buf.append("]");
		return buf.toString();
	}

}
