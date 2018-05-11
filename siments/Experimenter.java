
package petri.siments;
import petri.*;
import petri.events.*;

public class Experimenter extends SimEnt {

    // the experimenter 

    // where this worm lives
    private Env _env;
    private boolean _virgin = true;

    private static final boolean DIAG = Param.Experimenter_DIAG;
    protected boolean diagOn() { return Experimenter.DIAG; }

    private double _pih, _pim, _pdh;
    private double _lastpih, _lastpim, _lastpdh;
    private double _pihThreshold = -1;
    private double _pimThreshold = -1;
    private double _pdhThreshold = -1;
    private double _pihThresholdTime, _pimThresholdTime, _pdhThresholdTime;

    public Experimenter(Env env, double period) {
	super("Experimenter");
	_env = env;
       	TimeTick tt = new TimeTick(period);
	this.send(this, tt, tt.getPeriod());	
    }

    public void recv(SimEnt src, Event ev) {

	// dispatch!
	if (ev instanceof Kill) {
	    this.suicide();
	    Scheduler.instance().stop();
	}
	else if (ev instanceof TimeTick) {
	    TimeTick tt = (TimeTick)ev;

	    if (_virgin) {
		System.out.println("# time \t %mach-inf \t %hp-inf \t % hp-diag");
		_virgin = false;
		_lastpim = _pim = _env.percentageInfected_Machines();
		_lastpih = _pih = _env.percentageInfected_Machines();
		_lastpdh = _pdh = _env.percentageDiagnosed_Honeypots();
	    }
	    else {
		_lastpim = _pim;
		_lastpih = _pih;
		_lastpdh = _pdh;

		_pim = _env.percentageInfected_Machines();
		_pih = _env.percentageInfected_Honeypots();
		_pdh = _env.percentageDiagnosed_Honeypots();
	    }

	    double time = Scheduler.instance().getTime();
	    // determine stats

	    if ((_lastpih < 0.5) && (_pih >= 0.5)) {
		_pihThresholdTime = time;
		_pihThreshold = _pim;
	    }
	    if ((_lastpim < 0.5) && (_pim >= 0.5)) {
		_pimThresholdTime = time;
		_pimThreshold = _pim;
	    }
	    if ((_lastpdh < 0.5) && (_pdh >= 0.5)) {
		_pdhThresholdTime = time;
		_pdhThreshold = _pim;
	    }

	    if (diagOn()) {
		System.out.println(""+time+
				   "\t"+_pim+
				   "\t"+_pih+
				   "\t"+_pdh);
	    }

	    if ((_pim >= 0.5) && (_pih >= 0.5)) {
		this.send(this, new Kill(), 0);
		System.out.println("infection="+(100.0*_pihThreshold)+"X\tlead="+(100.0*_pihThresholdTime/_pimThresholdTime)+"Y");
	    }

	    // register next tick
	    this.send(this, tt, tt.getPeriod());
	}

    }

    public void deliveryAck(Scheduler.EventHandle h) {
	//no-op
    }

    protected void suicide() {
	_env = null;
	super.suicide();
    }
}
