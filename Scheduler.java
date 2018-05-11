
package petri;
import java.util.*;

public final class Scheduler implements Runnable {

    private static final boolean DIAG = false;
    protected boolean diagOn() { return Scheduler.DIAG; }
    
    private static Scheduler _instance;
    public static Scheduler instance() {
	if (_instance == null) {
	    _instance = new Scheduler();
	}
	return _instance;
    }

    public void stop() {
	_done = true;
    }

    public static Scheduler.EventHandle register(SimEnt registrar, SimEnt target, 
						 Event ev, double t) {
	double deliveryTime = instance().getTime() + t;
	EventHandle handle = new EventHandle(registrar, target, 
					     ev, new UniqueDouble(deliveryTime));
	instance().getEventsFrom(handle._registrar).add(handle);
	instance().getEventsTo(handle._target).add(handle);
	instance()._ud2ehandle.put(handle._udt, handle);
	return handle;
    }

    public static void deregister(EventHandle handle) {
	instance().getEventsFrom(handle._registrar).remove(handle);
	instance().getEventsTo(handle._target).remove(handle);
	instance()._ud2ehandle.remove(handle._udt);
    }

    void birthSimEnt(SimEnt ent) {
	// make the sets by getting them
	Set from = instance().getEventsFrom(ent);
	Set to = instance().getEventsTo(ent);
    }

    void deathSimEnt(SimEnt ent) {
	// clone to avoid concurrent modifications from deregister
	Set from = new HashSet(getEventsFrom(ent));
	for (Iterator it = from.iterator(); it.hasNext();) {
	    EventHandle h = (EventHandle)it.next();
	    deregister(h);	    
	}
	// clone to avoid concurrent modifications from deregister
	Set to = new HashSet(getEventsTo(ent));
	for (Iterator it = to.iterator(); it.hasNext();) {
	    EventHandle h = (EventHandle)it.next();
	    deregister(h);	    
	}
    }

    private boolean _done = false;
    public void run() {
	do {
	    if (_ud2ehandle.size() == 0) _done = true;
	    else {
		UniqueDouble udt = (UniqueDouble)_ud2ehandle.firstKey();
		EventHandle h = (EventHandle)_ud2ehandle.get(udt);
		if (DIAG) diag(h);

		_timeNow = udt._value.doubleValue();

		h._target.recv( h._registrar, h._ev );
		h._registrar.deliveryAck( h );

		deregister(h);
	    }
	}
	while (!_done);
    }

    private double _timeNow = 0;
    public double getTime() {
	return _timeNow;
    }

    private void diag(EventHandle h) {
        System.out.println("Scheduler: time="+h._udt+"("+h._registrar._name+"->"+h._target._name+")"+
			   ": event="+h._ev);
    }

    private final HashMap _from2set = new HashMap();
    private Set getEventsFrom(SimEnt e) {
	HashSet set = (HashSet)_from2set.get(e);
	if (set == null) {
	    set = new HashSet();
	    _from2set.put(e, set);
	}
	return set;
    }

    private final HashMap _to2set = new HashMap();
    private Set getEventsTo(SimEnt e) {
	HashSet set = (HashSet)_to2set.get(e);
	if (set == null) {
	    set = new HashSet();
	    _to2set.put(e, set);
	}
	return set;
    }

    private Scheduler() {
	_uid = 0;
    }

    // UniqueDouble(time)->Event
    private final TreeMap _ud2ehandle = new TreeMap();
    
    // The value that will be used as the discriminator in the next
    // UniqueDouble thta is made
    private int _uid;
    // UniqueDouble is used as the key to permit multiple event
    // registrations for the same time.
    private static class UniqueDouble implements Comparable {
	Double _value;
	int _discriminator;

	UniqueDouble(double value) {
	    _value = new Double(value);
	    _discriminator = (Scheduler.instance()._uid);
	    (Scheduler.instance()._uid)++;
	}

	public int compareTo(Object obj) {
	    UniqueDouble other = (UniqueDouble)obj;
	    // compare by value
	    if (this._value.doubleValue() < other._value.doubleValue())
		return -1;
	    else if (this._value.doubleValue() > other._value.doubleValue())
		return +1;
	    else { // but use the discriminator if the values agree
		if (this._discriminator < other._discriminator) 
		    return -1;
		else if (this._discriminator > other._discriminator) 
		    return +1;
		else
		    return 0;
	    }
	}

	public String toString() {
	    return _value.toString()+"("+_discriminator+")";
	}
    }

    public static class EventHandle {
	private final SimEnt _registrar;
	private final SimEnt _target;
	private final Event _ev;
	private final UniqueDouble _udt;

	private EventHandle(SimEnt registrar, SimEnt target,
			    Event ev, UniqueDouble udt) {
	    _registrar = registrar;
	    _target = target;
	    _ev = ev;
	    _udt = udt;
	}
    }
}