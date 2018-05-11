
package petri.siments;
import petri.*;
import petri.events.*;
import java.util.*;

/* A Sensor is a Machine which acts as bait for infections, forwarding
 * these to its associated honeypot group.  There are two ways for a
 * Sensor to become infected: (1) spontaneous infection and (2)
 * infection by traffic from another Machine.
 *
 * (1) A Sensor wakes up periodically every PRM_SENSOR_WAKEUP seconds
 * to determine whether it has experienced spontaneous infection.  The
 * probability of this being affirmative is determined by the
 * probability parameter PRM_PROB_INFECTION.  If a spontaneous
 * infection has occurred the Sensor broadcasts the infection to
 * its honeypot group.
 *
 * (2) If a Sensor receives an infection from a generic Machine in the
 * network, it broadcasts the infection to its honeypot group.
 */

public class Sensor extends Machine {

    // a sensor sees traffic every this many seconds
    private static final double PRM_SENSOR_WAKEUP = Param.Sensor_PRM_SENSOR_WAKEUP;
    // the probability that a sensor sees a worm
    private static final double PRM_PROB_INFECTION = Param.Sensor_PRM_PROB_INFECTION;
    // each sensor feeds this many honeypots
    public final static int PRM_NUM_FEED_HONEYPOTS = Param.Sensor_PRM_NUM_FEED_HONEYPOTS;

    // peer machines that ARE honeypots
    protected final LinkedList _feedHoneypots = new LinkedList();

    // diagnostics for sensors
    private static final boolean DIAG = Param.Sensor_DIAG;
    protected boolean diagOn() { return Sensor.DIAG; }
    
    // ctor
    public Sensor() {
	super();
	// initial event which circulates to wake up this Sensor
       	TimeTick tt = new TimeTick(PRM_SENSOR_WAKEUP);
	this.send(this, tt, tt.getPeriod());	
    }

    // how a Sensor responds to events
    public void recv(SimEnt src, Event ev) {
	// dispatch!
	if (ev instanceof TimeTick) {
	    TimeTick tt = (TimeTick)ev;
	    // am I spontaneously infected?
	    if (Chance.occurs(PRM_PROB_INFECTION)) {
		// yes, spread to all connected honeypots
		for (Iterator it=_feedHoneypots.iterator(); it.hasNext();) {
		    Honeypot h = (Honeypot)it.next();
		    System.out.println("Sensor infection transmission from "+this.name()+" to "+h.name());
		    Infection inf = new Infection();
		    this.send(h, inf, 0.0);
		}
	    }

	    // register next tick
	    this.send(this, tt, tt.getPeriod());
	}
	else {
	    // respond as a Machine would, installing Worms in response to Infections...
	    super.recv(src, ev);
	}
    }

    public void deliveryAck(Scheduler.EventHandle h) {
	// no-op
    }

    public String name() {
	return "S"+_name;
    }

    // convert to String representation
    public String toString() {
	String s = this.name()+"\n";
	s += "    peers=";
	for (Iterator it=_peerMachines.iterator(); it.hasNext();) {
	    Machine h = (Machine)it.next();
	    s+=(h.name());
	    if (it.hasNext()) s+=",";
	}
	s+="    honeypots=";
	for (Iterator it=_feedHoneypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    s+=(h.name());
	    if (it.hasNext()) s+=",";
	}
	s+="\n";
	return s;
    }

    void addPeer(Machine h) {
	// Keep track of Honeypot peers separately
	if (h instanceof Honeypot) {
	    if (!_feedHoneypots.contains(h)) {
		_feedHoneypots.add(h);
		h.addPeer(this);
	    }
	}
	else super.addPeer(h);
    }

    void remPeer(Machine h) {
	// Keep track of Honeypot peers separately
	if (h instanceof Honeypot) {
	    if (_feedHoneypots.contains(h)) {
		_feedHoneypots.remove(h);
		h.remPeer(this);

		// I lost a peer! Let me try and maintain connectivity...
		Honeypot replacement = Env.instance().getRandomHoneypot();
		if (replacement!=null) {
		    addPeer(replacement);
		}
	    }
	}
	else super.remPeer(h);
    }

    void linkUp() {
	// Link up w/ Honeypots
	for (int i = 0; i < PRM_NUM_FEED_HONEYPOTS; i++) {
	    Honeypot peer = Env.instance().getRandomHoneypot();
	    if (peer!=null) {
		addPeer(peer);
	    }
	}
	// Link up with the network
	super.linkUp();
    }

    void outboundTrafficNotification() {
	/* no-op */
    }

    // used by the Worm to determine target
    public Machine getRandomPeer() {
	// only allow outbound connections to Honeypots
	if (_feedHoneypots.size() == 0) return null;
	int index = (int)(Math.random()*(double)_feedHoneypots.size());
	return (Honeypot)_feedHoneypots.get(index);
    }

    protected void suicide() {
	// disconnect from feed honeypots
	for (Iterator it=_feedHoneypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    remPeer(h);
	}
	super.suicide();
    }
}
