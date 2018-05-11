
package petri.events;
import petri.*;

public class TimeTick implements Event {

    private final double _period;

    public TimeTick(double period) {
	_period = period;
    }

    public void diag() {
	System.out.println("TimeTick: "+Scheduler.instance().getTime());
    }
    
    public void action(SimEnt locale) {
	// no-op
    }

    public double getPeriod() {
	return _period;
    }
}
