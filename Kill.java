
package petri;

public class Kill implements Event {

    public Kill() {
    }

    public void diag() {
	System.out.println("Kill: "+Scheduler.instance().getTime());
    }
    
    public void action(SimEnt locale) {
	// no-op
    }
}
