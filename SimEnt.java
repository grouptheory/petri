

package petri;
import java.util.*;

public abstract class SimEnt {

    public final String _name;
    
    protected final Scheduler.EventHandle send(SimEnt dst, Event ev, double t) {
	return Scheduler.instance().register(this, dst, ev, t);
    }

    public abstract void deliveryAck(Scheduler.EventHandle h);
    public abstract void recv(SimEnt src, Event ev);

    protected final void revokeSend(Scheduler.EventHandle h) {
	Scheduler.instance().deregister(h);
    }

    protected SimEnt(String name) {
	_name = name;
	Scheduler.instance().birthSimEnt(this);
    }

    protected void suicide() {
	Scheduler.instance().deathSimEnt(this);
    }
}
