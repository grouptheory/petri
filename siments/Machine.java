
package petri.siments;
import petri.*;
import petri.events.*;
import java.util.*;

public class Machine extends SimEnt {

    // each machine connects to this many other machines
    public final static int PRM_NUM_PEER_MACHINES = 10;

    // peer machines that ARE NOT honeypots
    protected final LinkedList _peerMachines = new LinkedList();

    // locally resident worms
    protected final HashSet _worms = new HashSet();
    // is this machine infected?
    protected boolean _infected = false;
    
    // probability that a machine is bulnerable
    public final double Machine_PRM_PROB_VULNERABLE = 
	Param.Machine_PRM_PROB_VULNERABLE;

    // diagnostics
    private static final boolean DIAG = Param.Machine_DIAG;
    protected boolean diagOn() { return Machine.DIAG; }

    protected static int _id = 0;
    
    public Machine() {
	super(new String(""+_id));
	_id++;
    }

    // how a machine responds to events
    public void recv(SimEnt src, Event ev) {

	// diag
	if (diagOn()) ev.diag();
	
	// dispatch!
	if (ev instanceof Kill) {
	    this.suicide();
	}
	else if (ev instanceof Infection) {
	    if (Chance.occurs(Machine_PRM_PROB_VULNERABLE)) {
		Infection inf = (Infection)ev;
		inf.action(this);
	    }
	}
    }

    public boolean isInfected() {
	return _infected;
    }

    public void infect() {
	// we have received an infection
	if (!_infected) {
	    _infected = true;
	}
	// we can take on more worms
	if (_worms.size() < Worm.PRM_MAX_INSTANCES) {
	    _worms.add(new Worm(this));
	    if (Machine.DIAG) {
		System.out.println("Worm arrived on "+this.name());
	    }
	}
    }

    public void deliveryAck(Scheduler.EventHandle h) {
	// no-op
    }

    public String name() {
	return "M"+_name;
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
	return s;
    }

    void addPeer(Machine h) {
	if (!_peerMachines.contains(h)) {
	    _peerMachines.add(h);
	    h.addPeer(this);
	}
    }

    void remPeer(Machine h) {
	if (_peerMachines.contains(h)) {
	    _peerMachines.remove(h);
	    h.remPeer(this);

	    // I lost a peer! Let me try and maintain connectivity...
	    Machine replacement = Env.instance().getRandomMachine();
	    if ((replacement!=null) && (replacement != this)) {
		addPeer(replacement);
	    }
	}
    }

    void linkUp() {
	// link up to other machines
	for (int i = 0; i < PRM_NUM_PEER_MACHINES; i++) {
	    Machine peer = Env.instance().getRandomMachine();
	    if ((peer!=null) && (peer != this)) {
		addPeer(peer);
	    }
	}
    }

    void outboundTrafficNotification() {
	// Machine does nothing, but Honeypots do...
    }

    // used by the Worm to determine target
    public Machine getRandomPeer() {
	if (_peerMachines.size() == 0) return null;
	int index = (int)(Math.random()*(double)_peerMachines.size());
	return (Machine)_peerMachines.get(index);
    }

    protected void suicide() {
	// disconnect from peers
	for (Iterator it=_peerMachines.iterator(); it.hasNext();) {
	    Machine h = (Machine)it.next();
	    remPeer(h);
	}
	// kill local worms
	for (Iterator it=_worms.iterator(); it.hasNext();) {
	    Worm w = (Worm)it.next();
	    this.send(w, new Kill(), 0.0);
	}
	super.suicide();
    }
}
