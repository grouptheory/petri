
package petri.events;
import petri.*;
import petri.siments.*;

public class Infection implements Event {

    public Infection() {}

    public void diag() {
	// System.out.println("Infection: "+Scheduler.instance().getTime());
    }
    
    public void action(SimEnt locale) {
	Machine m = (Machine)locale;
	m.infect();
    }
}
