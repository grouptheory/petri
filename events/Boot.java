
package petri.events;
import petri.*;
import petri.siments.*;

public class Boot implements Event {

    public Boot() {
    }

    public void diag() {
	System.out.println("Boot: "+Scheduler.instance().getTime());
    }
    
    public void action(SimEnt locale) {
	Env env = (Env)locale;
	env.initialize();

	// the initial infection!
	Machine m = env.getRandomMachine();
	System.out.println("# Initial infection on: "+m.name());
	m.infect();
    }
}
