
package petri.siments;
import petri.*;
import petri.events.*;
import java.util.*;

public class Env extends SimEnt {

    // number of machines that are neither honeypots, not sensors
    public static final int PRM_NUM_MACHINES  = Param.Env_PRM_NUM_MACHINES;
    // number of honeypots
    public static final int PRM_NUM_HONEYPOTS = Param.Env_PRM_NUM_HONEYPOTS;
    // number of sensors
    public static final int PRM_NUM_SENSORS   = Param.Env_PRM_NUM_SENSORS;

    private final LinkedList _machines = new LinkedList();
    private final LinkedList _honeypots = new LinkedList();
    private final LinkedList _sensors = new LinkedList();
    
    // diagnostics
    private static final boolean DIAG = Param.Env_DIAG;
    protected boolean diagOn() { return Env.DIAG; }

    private static Env _instance;
    public static Env instance() {
	if (_instance == null) {
	    _instance = new Env();
	}
	return _instance;
    }
    
    public void deliveryAck(Scheduler.EventHandle h) {
	// no-op
    }

    public void recv(SimEnt src, Event ev) {
	Boot b = (Boot)ev;
	b.action(this);
    }

    public void initialize() {
	// make machines
	for (int i=0; i<PRM_NUM_MACHINES; i++) {
	    this.addMachine();
	}
	// make honeypots
	for (int i=0; i<PRM_NUM_HONEYPOTS; i++) {
	    this.addHoneypot();
	}
	// make sensors
	for (int i=0; i<PRM_NUM_SENSORS; i++) {
	    this.addSensor();
	}
	// link it up
	this.linkMachines();
	this.linkHoneypots();
	this.linkSensors();

	if (diagOn()) {
	    System.out.println("** Initial Environment **\n"+this.toString());
	}
    }

    public double percentageInfected_Honeypots() {
	int num=0;
	int den=0;
	for (Iterator it=_honeypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    if (h.isInfected()) num++;
	    den++;
	}
	return ((double)num/(double)den);
    }

    public double percentageDiagnosed_Honeypots() {
	int num=0;
	int den=0;
	for (Iterator it=_honeypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    if (h.isDiagnosed()) num++;
	    den++;
	}
	return ((double)num/(double)den);
    }

    public double percentageInfected_Machines() {
	int num=0;
	int den=0;
	for (Iterator it=_machines.iterator(); it.hasNext();) {
	    Machine m = (Machine)it.next();
	    if (m.isInfected()) num++;
	    den++;
	}
	return ((double)num/(double)den);
    }

    public void addMachine() {
	Machine m = new Machine();
	_machines.add(m);
    }

    public void addHoneypot() {
	Honeypot h = new Honeypot();
	_honeypots.add(h);
    }

    public void addSensor() {
	Sensor s = new Sensor();
	_sensors.add(s);
    }

    public void remMachine(Machine m) {
	this.send(m, new Kill(), 0.0);
	_machines.remove(m);
    }

    public void remHoneypot(Honeypot h) {
	this.send(h, new Kill(), 0.0);
	_honeypots.remove(h);
    }

    public void remSensor(Sensor s) {
	this.send(s, new Kill(), 0.0);
	_sensors.remove(s);
    }

    public void linkMachines() {
	for (Iterator it=_machines.iterator(); it.hasNext();) {
	    Machine m = (Machine)it.next();
	    m.linkUp();
	}
    }

    public void linkHoneypots() {
	for (Iterator it=_honeypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    h.linkUp();
	}
    }

    public void linkSensors() {
	for (Iterator it=_sensors.iterator(); it.hasNext();) {
	    Sensor s = (Sensor)it.next();
	    s.linkUp();
	}
    }

    public Machine getRandomMachine() {
	if (_machines.size() == 0) return null;
	int index = (int)(Math.random()*(double)_machines.size());
	return (Machine)_machines.get(index);
    }

    public Honeypot getRandomHoneypot() {
	if (_honeypots.size() == 0) return null;
	int index = (int)(Math.random()*(double)_honeypots.size());
	return (Honeypot)_honeypots.get(index);
    }

    public Sensor getRandomSensor() {
	if (_sensors.size() == 0) return null;
	int index = (int)(Math.random()*(double)_sensors.size());
	return (Sensor)_sensors.get(index);
    }

    public String toString() {
	String s = "Env:\n";
	s += "Machines:\n";
	for (Iterator it=_machines.iterator(); it.hasNext();) {
	    Machine m = (Machine)it.next();
	    s += m.toString();
	}
	s += "Honeypots:\n";
	for (Iterator it=_honeypots.iterator(); it.hasNext();) {
	    Honeypot h = (Honeypot)it.next();
	    s += h.toString();
	}
	s += "Sensors:\n";
	for (Iterator it=_sensors.iterator(); it.hasNext();) {
	    Sensor snsr = (Sensor)it.next();
	    s += snsr.toString();
	}
	return s;
    }

    private Env() {
	super("Env");
	Boot b = new Boot();
	this.send(this, b, 0.0);
    }
}
