/**
 * 
 */
package org.cfpm.queue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nlogo.api.ExtensionException;
import org.nlogo.api.ExtensionObject;

/**
 * @author Ruth Meyer
 *
 */
public class Queue implements ExtensionObject {
		
	public final static int FIFO = 0;
	public final static int LIFO = 1;
	
	
	int strategy;
	List<QElem> q;
	int insertionIndex;
	// statistics measures
	double maxWaitTime;
	double minWaitTime;
	double totalWaitTime;
	int maxSize;
	double totalTimeWeightedLength;
	int numInserts;
	double timeOfLastChange;
	double initTime;
	
	
	/** Default empty constructor: creates a new empty queue with FIFO strategy.
	 * 
	 */
	public Queue() {
		init(FIFO);
	}
	
	/** Constructor: creates a new empty queue with the specified strategy.
	 * 
	 */
	public Queue(int strategy) {
		init(strategy);
	}
	
	void init(int strategy) {
		// check if strategy is valid
		if (! isValidStrategy(strategy)) {
			// use default FIFO strategy
			strategy = FIFO;
		}
		this.strategy = strategy;
		this.q = new ArrayList<QElem>();
		this.insertionIndex = 0;
		resetStats(0.0);
	}
	
	public static boolean isValidStrategy(int strategy) {
		if (strategy < FIFO) return false;
		if (strategy > LIFO) return false;
		return true;
	}

	public void resetStats(double currentTime) {
		this.maxSize = 0;
		this.numInserts = 0;
		this.maxWaitTime = 0;
		this.minWaitTime = Double.MAX_VALUE;
		this.totalWaitTime = 0;
		this.totalTimeWeightedLength = 0;
		this.timeOfLastChange = currentTime; 
		this.initTime = currentTime;
	}
	
	public int size() {
		return this.q.size();
	}
	
	public void enqueue(Object elem, double currentTime) throws ExtensionException {
		// check if currentTime is valid, i.e. >= timeOfLastChange
		if (currentTime < this.timeOfLastChange) {
			throw new ExtensionException("attempt to enqueue an element in the past: current time " + currentTime +
					                     " is smaller than this queue's time of last change " + this.timeOfLastChange);
		}
		QElem qElem = new QElem(elem, currentTime);
		this.q.add(insertionIndex, qElem);
		if (strategy == FIFO) {
			this.insertionIndex++;
		}
		// update statistics
		this.numInserts++;
		if (size() > this.maxSize) {
			this.maxSize++;
		}
		this.totalTimeWeightedLength += (currentTime - this.timeOfLastChange) * (size() - 1);
		this.timeOfLastChange = currentTime;
	}
	
	public Object dequeue(double currentTime) {
		if (size() == 0) {
			// queue is empty
			return null;
		}
		QElem qElem = this.q.get(0);
		// update statistics
		double waitTime = currentTime - qElem.getTime();
		this.totalWaitTime += waitTime;
		if (waitTime > this.maxWaitTime) {
			this.maxWaitTime = waitTime;
		}
		if (waitTime < this.minWaitTime) {
			this.minWaitTime = waitTime;
		}
		this.totalTimeWeightedLength += (currentTime - this.timeOfLastChange) * size();
		this.timeOfLastChange = currentTime;
		// actually remove element from queue
		this.q.remove(0);
		if (strategy == FIFO) this.insertionIndex--;
		return qElem.getElem();
	}
	
	public double getMeanWaitTime() {
		if (this.numInserts == 0) return 0.0;
		return (this.totalWaitTime + accumulateCurrentlyWaiting()) / this.numInserts;
	}
	
	private double accumulateCurrentlyWaiting() {
		// iterate through queue and add up wait-time-so-far for each element
		double total = 0;
		for (Iterator<QElem> i = q.iterator(); i.hasNext(); ) {
			total += (this.timeOfLastChange - i.next().getTime());
		}
		return total;
	}

	public double getMaxWaitTime() {
		return this.maxWaitTime;
	}
	
	public double getMinWaitTime() {
		return this.minWaitTime;
	}
	
	public int getMaxSize() {
		return this.maxSize;
	}
	
	public double getMeanSize() {
		double duration = this.timeOfLastChange - this.initTime;
		if (duration >= 0 & duration <= 0) return 0.0;
		return this.totalTimeWeightedLength / duration;
	}

	/* (non-Javadoc)
	 * @see org.nlogo.api.ExtensionObject#dump(boolean, boolean, boolean)
	 */
	@Override
	public String dump(boolean readable, boolean exporting, boolean reference) {
		// ignore the arguments, just return a String representation of the queue
		return toString();
	}

	/* (non-Javadoc)
	 * @see org.nlogo.api.ExtensionObject#getExtensionName()
	 */
	@Override
	public String getExtensionName() {
		// TODO Auto-generated method stub
		return "queue";
	}

	/* (non-Javadoc)
	 * @see org.nlogo.api.ExtensionObject#getNLTypeName()
	 */
	@Override
	public String getNLTypeName() {
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see org.nlogo.api.ExtensionObject#recursivelyEqual(java.lang.Object)
	 */
	@Override
	public boolean recursivelyEqual(Object o) {
		if (! (o instanceof Queue)) {
			// not a queue
			return false;
		}
		Queue other = (Queue)o;
		if (other.strategy != this.strategy) {
			// not the same strategy
			return false;
		}
		if (other.size() != this.size()) {
			// not the same length
			return false;
		}
		if ((other.getMaxSize() != this.maxSize) || 
		   (other.getMaxWaitTime() != this.maxWaitTime) ||
		   (other.getMinWaitTime() != this.minWaitTime) ||
		   (other.getMeanWaitTime() != this.getMeanWaitTime()) ||
		   (other.getMeanSize() != this.getMeanSize())) 
		{
			// not the same statistics
			return false;
		}
		// compare elements
		Iterator<QElem> iThis = this.q.iterator();
		Iterator<QElem> iOther = other.q.iterator();
		boolean theSame = true;
		while (theSame && iThis.hasNext() && iOther.hasNext()) {
			theSame = iThis.next().equals(iOther.next());
		}
		return theSame;
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder("[");
		for (Iterator<QElem> i = q.iterator(); i.hasNext(); ) {
			QElem el = i.next();
			buf.append(el.toString());
		}
		buf.append("]");
		return buf.toString();
	}
	
	public static void main(String[] args) throws ExtensionException {
		// do some unit testing
		Queue q = new Queue(Queue.LIFO);
		double ct = 0.0;
		System.out.println("time\tqueue");
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());
		ct = 3.2;
		q.enqueue("Fred", ct);
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());
		ct = 3.6;
		Object o = q.dequeue(ct);
		System.out.println(o.toString() + " has left the queue");
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());
		ct = 4.3;
		q.enqueue("Ginny", ct);
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());
		ct = 5.3;
		q.enqueue("Ron", ct);
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());
		ct = 5.4;
		o = q.dequeue(ct);
		System.out.println(o.toString() + " has left the queue");
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());
		ct = 6.7;
		o = q.dequeue(ct);
		System.out.println(o.toString() + " has left the queue");
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());

		System.out.println("\naverage wait time: " + q.format(q.getMeanWaitTime(), 3));
		System.out.println("average queue length: " + q.format(q.getMeanSize(), 3));
		System.out.println("max queue length: " + q.getMaxSize());
		System.out.println("max wait time: " + q.format(q.getMaxWaitTime(), 1));
		System.out.println("min wait time: " + q.format(q.getMinWaitTime(), 1));

		ct = 7.2;
		o = q.dequeue(ct);
		System.out.println("\n" + o + " has left the queue");
		System.out.println(String.format("%.2f", ct) + "\t" + q.toString());
	}
	
	private String format(double value, int numDigits) {
		return String.format("%."+numDigits+"f", value);
	}

}
