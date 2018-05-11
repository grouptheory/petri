
package petri;
import petri.siments.*;
import petri.events.*;

public class Main {

    public static void main(String[] args) {
	try {
	    System.out.println("# Starting Scheduler...");
	    
	    Param.initialize();
	    Env env = Env.instance();
	    Experimenter exp = new Experimenter(env, 1.0);

	    Scheduler.instance().run();
	    System.out.println("# ...Scheduler exiting.");
	}
	catch (Exception ex) {
	    Thread.dumpStack();
	}
    }
}
