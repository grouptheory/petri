
package petri.siments;
import petri.*;
import petri.events.*;

public class Worm extends SimEnt {

    // can multiple instances of the worm exist on a given honeypot?
    // If so what is the maximum number?
    public static final int PRM_MAX_INSTANCES = Param.Worm_PRM_MAX_INSTANCES;
    // the worm makes up every this many seconds
    public static final double PRM_WORM_WAKEUP = Param.Worm_PRM_WORM_WAKEUP;

    // where this worm lives
    private Machine _residence;

    private static final boolean DIAG = Param.Worm_DIAG;
    protected boolean diagOn() { return Worm.DIAG; }

    public Worm(Machine residence) {
	super("Worm");
	_residence = residence;
       	TimeTick tt = new TimeTick(PRM_WORM_WAKEUP);
	this.send(this, tt, tt.getPeriod());	
    }

    public void recv(SimEnt src, Event ev) {

	// dispatch!
	if (ev instanceof Kill) {
	    this.suicide();
	}
	else if (ev instanceof TimeTick) {
	    TimeTick tt = (TimeTick)ev;
	    Machine h = _residence.getRandomPeer();
	    if (h!=null) {
		// diag
		Infection inf = new Infection();
		// System.out.println("Worm transmission from "+_residence.name()+" to "+h.name());
		this.send(h, inf, 0.0);
		_residence.outboundTrafficNotification();
	    }

	    // register next tick
	    this.send(this, tt, tt.getPeriod());
	}

    }

    public void deliveryAck(Scheduler.EventHandle h) {
	//no-op
    }

    protected void suicide() {
	_residence = null;
	super.suicide();
    }

}
