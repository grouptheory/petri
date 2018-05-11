
package petri.siments;
import petri.*;
import petri.events.*;
import java.util.*;

/* A Honeypot is a Machine which acts as part of the petri dish for
 * infections, forwarding these to its associated honeypot group.
 *
 * If a Honeypot receives an infection from a Sensor or another
 * Honeypot network, it installs the associated Worm, which begins to
 * send the infection to its honeypot group.
 */

public class Honeypot extends Machine {

    // each honeypot attempts to connect to this many other honeypots
    public final static int PRM_NUM_PEER_HONEYPOTS = Param.Honeypot_PRM_NUM_PEER_HONEYPOTS;
    // the probability that outbound traffic is detected as being a worm
    public static final double PRM_PROB_TRAFFIC_OBSERVED = Param.Honeypot_PRM_PROB_TRAFFIC_OBSERVED;
    // how many streams are needed to construct the filter signature
    public static final int PRM_SIG_MINSTREAMS = Param.Honeypot_PRM_SIG_MINSTREAMS;
    // is the honeypot selfish? i.e. does it disconnect after
    // discovering the antidote?
    public static final boolean PRM_SELFISH_HONEYPOT = Param.Honeypot_PRM_SELFISH_HONEYPOT;
    
    // peer machines in honeypot network
    protected final LinkedList _peerHoneypots = new LinkedList();
    // sensors that feed me
    protected final LinkedList _feedSensors = new LinkedList();

    // has this honeypot observed outbound traffic
    private boolean _seentraffic = false;
    // does this honeypot have the antidote?
    private boolean _antidote = false;
    
    // diagnostics for sensors
    private static final boolean DIAG = Param.Honeypot_DIAG;
    protected boolean diagOn() { return Honeypot.DIAG; }

    // ctor
    public Honeypot() {
	super();
    }

    // how a honeypot responds to events
    public void recv(SimEnt src, Event ev) {
	// like a normal machine... installing Worms on receipt of
	// Infections...
	// 
	// note however that when the worm sends outbound traffic, it
	// gives its residence (machine) an
	// outboundTrafficNotification... which only the honeypots
	// react to.
	super.recv(src, ev);
    }

    public void deliveryAck(Scheduler.EventHandle h) {
	// no-op
    }

    public String name() {
	return "H"+_name;
    }

    public String toString() {
	String s = this.name()+"\n";
	s += "    peers=";
	for (Iterator it=_peerMachines.iterator(); it.hasNext();) {
	    Machine h = (Machine)it.next();
	    s+=(h.name());
	    if (it.hasNext()) s+=",";
	}
	s+="\n";
	s+="    honeypots=";
	for (Iterator it=_peerHoneypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    s+=(h.name());
	    if (it.hasNext()) s+=",";
	}
	s+="\n";
	return s;
    }

    void addPeer(Machine h) {
	// A Honeypot's peers are only other Honeypots or Sensors
	if (h instanceof Honeypot) {
	    if (!_peerHoneypots.contains(h)) {
		_peerHoneypots.add(h);
		h.addPeer(this);
	    }
	}
	else if (h instanceof Sensor) {
	    if (!_feedSensors.contains(h)) {
		_feedSensors.add(h);
		h.addPeer(this);
	    }
	}
    }

    void remPeer(Machine h) {
	// A Honeypot's peers are only other Honeypots or Sensors
	if (h instanceof Honeypot) {
	    if (_peerHoneypots.contains(h)) {
		_peerHoneypots.remove(h);
		h.remPeer(this);

		// I lost a peer! Let me try and maintain connectivity...
		Honeypot replacement = Env.instance().getRandomHoneypot();
		if ((replacement!=null) && (replacement != this)) {
		    addPeer(replacement);
		}
	    }
	}
	else if (h instanceof Sensor) {
	    if (_feedSensors.contains(h)) {
		_feedSensors.remove(h);
		h.remPeer(this);

		// I lost a peer! Let me try and maintain connectivity...
		Sensor replacement = Env.instance().getRandomSensor();
		if (replacement!=null) {
		    addPeer(replacement);
		}
	    }
	}
    }

    void linkUp() {
	// A Honeypot's peers are only other Honeypots and Sensors...
	// Connect to Honeypots...
	for (int i = 0; i < PRM_NUM_PEER_HONEYPOTS; i++) {
	    Honeypot peer = Env.instance().getRandomHoneypot();
	    if ((peer!=null) && (peer != this)) {
		addPeer(peer);
	    }
	}
	// The Sensors are responsible for linking to Honeypots...
	// not the other way round, so we are done here...
    }

    void outboundTrafficNotification() {
	// Does the traffic "slip past us"?
	if (Chance.occurs(PRM_PROB_TRAFFIC_OBSERVED)) {
	    // outbound traffic has been observed!
	    _seentraffic = true;

	    int numStreams = 0;
	    int totalPeers = 0;
	    // begin Rabinizing...
	    for (Iterator it=_peerHoneypots.iterator(); it.hasNext();) {
		Honeypot h = (Honeypot)it.next();
		if (h._seentraffic) numStreams++;
		totalPeers++;
	    }
	    
	    if (numStreams >= PRM_SIG_MINSTREAMS) {
		// signature construction succeeds
		_antidote = true;

		if (diagOn()) {
		    System.out.println("Antidote developed on "+this.name());
		}

		if (PRM_SELFISH_HONEYPOT) {
		    // the admin takes the honeypot offline
		    this.suicide();
		}
	    }
	}
    }

    public boolean isDiagnosed() {
	return _antidote;
    }

    // used by the Worm to determine target
    public Machine getRandomPeer() {
	// only allow outbound connections to Honeypots
	if (_peerHoneypots.size() == 0) return null;
	int index = (int)(Math.random()*(double)_peerHoneypots.size());
	return (Honeypot)_peerHoneypots.get(index);
    }

    protected void suicide() {
	// disconnect from peer honeypots
	for (Iterator it=_peerHoneypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    remPeer(h);
	}
	// disconnect from feed sensors
	for (Iterator it=_feedSensors.iterator(); it.hasNext();) {
	    Sensor h = (Sensor)it.next();
	    remPeer(h);
	}
	super.suicide();
    }
}
