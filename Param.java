
package petri;
import java.util.*;
import java.io.*;

public class Param {

    private static final Properties _prop = new Properties();
    private static final String PROPERTIES_FILE = "petri.properties";
    public static void initialize() { 
	try {
	    initialize(PROPERTIES_FILE); 
	}
	catch (IOException ex) {
	    System.out.println("Property file '"+PROPERTIES_FILE+"' could not be loaded!");
	}
    }

    public static void save() { 
	try {
	    save(PROPERTIES_FILE); 
	}
	catch (IOException ex) {
	    System.out.println("Property file '"+PROPERTIES_FILE+"' could not be saved!");
	}
    }

    private static void initialize(String path) throws IOException {
	_prop.load(new FileInputStream(path));
	forceLoad();
    }

    private static void save(String path) throws IOException {
	forceSave();
	_prop.store(new FileOutputStream(path), "# Petri Properties File ");
    }
 
    // Debugging
    public static boolean Env_DIAG = false;
    public static boolean Machine_DIAG = false;
    public static boolean Honeypot_DIAG = false;
    public static boolean Sensor_DIAG = false;
    public static boolean Worm_DIAG = false;
    public static boolean Scheduler_DIAG = false;
    public static boolean Experimenter_DIAG = false;

    // Environment

    // number of honeypots
    public static int     Env_PRM_NUM_HONEYPOTS = 8;
    // number of sensors
    public static int     Env_PRM_NUM_SENSORS   = 10;
    // number of machines that are neither honeypots, nor sensors
    public static int     Env_PRM_NUM_MACHINES  = Env_PRM_NUM_SENSORS*100;

    // Machines

    // each machine connects to this many other machines
    public static int     Machine_PRM_NUM_PEER_MACHINES = 4;
    // probability that a machine is bulnerable
    public static double  Machine_PRM_PROB_VULNERABLE = 0.99;

    // Sensors

    // a sensor sees spontaneous traffic every this many seconds
    public static double Sensor_PRM_SENSOR_WAKEUP = 5.0;
    // the probability that a sensor sees a worm in spontaneous traffic
    public static double Sensor_PRM_PROB_INFECTION = 0.0;
    // each sensor feeds this many honeypots
    public static int Sensor_PRM_NUM_FEED_HONEYPOTS = 2;

    // Honeypots

    // each honeypot attempts to connect to this many other honeypots
    public static int     Honeypot_PRM_NUM_PEER_HONEYPOTS = Env_PRM_NUM_HONEYPOTS/2;
    // the probability that outbound traffic is detected as being a worm
    public static double  Honeypot_PRM_PROB_TRAFFIC_OBSERVED = 1.0;
    // how many streams are needed to construct the filter signature
    public static int     Honeypot_PRM_SIG_MINSTREAMS = Honeypot_PRM_NUM_PEER_HONEYPOTS/2;
    // is the honeypot selfish? 
    // i.e. it disconnects after discovering the antidote?
    public static boolean Honeypot_PRM_SELFISH_HONEYPOT = false;

    // Worm

    // can multiple instances of the worm exist on a given honeypot?
    // If so how many at most?
    public static int     Worm_PRM_MAX_INSTANCES = 1;
    // the worm makes up every this many seconds
    public static double Worm_PRM_WORM_WAKEUP = 5.0;


    private static void forceSave() {
	_prop.setProperty("Env_DIAG", ""+Env_DIAG);
	_prop.setProperty("Machine_DIAG", ""+Machine_DIAG);
	_prop.setProperty("Honeypot_DIAG", ""+Honeypot_DIAG);
	_prop.setProperty("Sensor_DIAG", ""+Sensor_DIAG);
	_prop.setProperty("Worm_DIAG", ""+Worm_DIAG);
	_prop.setProperty("Scheduler_DIAG", ""+Scheduler_DIAG);
	_prop.setProperty("Experimenter_DIAG", ""+Experimenter_DIAG);

	_prop.setProperty("Env_PRM_NUM_HONEYPOTS", ""+Env_PRM_NUM_HONEYPOTS);
	_prop.setProperty("Env_PRM_NUM_SENSORS", ""+Env_PRM_NUM_SENSORS);
	_prop.setProperty("Env_PRM_NUM_MACHINES", ""+Env_PRM_NUM_MACHINES);

	_prop.setProperty("Machine_PRM_NUM_PEER_MACHINES", ""+Machine_PRM_NUM_PEER_MACHINES);
	_prop.setProperty("Machine_PRM_PROB_VULNERABLE", ""+Machine_PRM_PROB_VULNERABLE);

	_prop.setProperty("Sensor_PRM_SENSOR_WAKEUP", ""+Sensor_PRM_SENSOR_WAKEUP);
	_prop.setProperty("Sensor_PRM_PROB_INFECTION", ""+Sensor_PRM_PROB_INFECTION);
	_prop.setProperty("Sensor_PRM_NUM_FEED_HONEYPOTS", ""+Sensor_PRM_NUM_FEED_HONEYPOTS);

	_prop.setProperty("Honeypot_PRM_NUM_PEER_HONEYPOTS", ""+Honeypot_PRM_NUM_PEER_HONEYPOTS);
	_prop.setProperty("Honeypot_PRM_PROB_TRAFFIC_OBSERVED", ""+Honeypot_PRM_PROB_TRAFFIC_OBSERVED);
	_prop.setProperty("Honeypot_PRM_SIG_MINSTREAMS", ""+Honeypot_PRM_SIG_MINSTREAMS);
	_prop.setProperty("Honeypot_PRM_SELFISH_HONEYPOT", ""+Honeypot_PRM_SELFISH_HONEYPOT);

	_prop.setProperty("Worm_PRM_MAX_INSTANCES", ""+Worm_PRM_MAX_INSTANCES);
	_prop.setProperty("Worm_PRM_WORM_WAKEUP", ""+Worm_PRM_WORM_WAKEUP);
    }

    private static void forceLoad() {
	try {
	    Env_DIAG = Boolean.valueOf(_prop.getProperty("Env_DIAG")).booleanValue();
	    Machine_DIAG = Boolean.valueOf(_prop.getProperty("Machine_DIAG")).booleanValue();
	    Honeypot_DIAG = Boolean.valueOf(_prop.getProperty("Honeypot_DIAG")).booleanValue();
	    Sensor_DIAG = Boolean.valueOf(_prop.getProperty("Sensor_DIAG")).booleanValue();
	    Worm_DIAG = Boolean.valueOf(_prop.getProperty("Worm_DIAG")).booleanValue();
	    Scheduler_DIAG = Boolean.valueOf(_prop.getProperty("Scheduler_DIAG")).booleanValue();
	    Experimenter_DIAG = Boolean.valueOf(_prop.getProperty("Experimenter_DIAG")).booleanValue();

	    Env_PRM_NUM_HONEYPOTS = Integer.parseInt(_prop.getProperty("Env_PRM_NUM_HONEYPOTS"));
	    Env_PRM_NUM_SENSORS = Integer.parseInt(_prop.getProperty("Env_PRM_NUM_SENSORS"));
	    Env_PRM_NUM_MACHINES = Integer.parseInt(_prop.getProperty("Env_PRM_NUM_MACHINES"));

	    Machine_PRM_NUM_PEER_MACHINES = Integer.parseInt(_prop.getProperty("Machine_PRM_NUM_PEER_MACHINES"));
	    Machine_PRM_PROB_VULNERABLE = Double.parseDouble(_prop.getProperty("Machine_PRM_PROB_VULNERABLE"));

	    Sensor_PRM_SENSOR_WAKEUP = Double.parseDouble(_prop.getProperty("Sensor_PRM_SENSOR_WAKEUP"));
	    Sensor_PRM_PROB_INFECTION = Double.parseDouble(_prop.getProperty("Sensor_PRM_PROB_INFECTION"));
	    Sensor_PRM_NUM_FEED_HONEYPOTS = Integer.parseInt(_prop.getProperty("Sensor_PRM_NUM_FEED_HONEYPOTS"));

	    Honeypot_PRM_NUM_PEER_HONEYPOTS = Integer.parseInt(_prop.getProperty("Honeypot_PRM_NUM_PEER_HONEYPOTS"));
	    Honeypot_PRM_PROB_TRAFFIC_OBSERVED = Double.parseDouble(_prop.getProperty("Honeypot_PRM_PROB_TRAFFIC_OBSERVED"));
	    Honeypot_PRM_SIG_MINSTREAMS = Integer.parseInt(_prop.getProperty("Honeypot_PRM_SIG_MINSTREAMS"));
	    Honeypot_PRM_SELFISH_HONEYPOT = Boolean.valueOf(_prop.getProperty("Honeypot_PRM_SELFISH_HONEYPOT")).booleanValue();
	    
	    Worm_PRM_MAX_INSTANCES = Integer.parseInt(_prop.getProperty("Worm_PRM_MAX_INSTANCES"));
	    Worm_PRM_WORM_WAKEUP = Double.parseDouble(_prop.getProperty("Worm_PRM_WORM_WAKEUP"));
	}
	catch (Exception ex) {
	    System.out.println("# Error parsing property file '"+PROPERTIES_FILE+"'!!!"+ex);
	    Thread.dumpStack();
	}

	System.out.println("# Env_DIAG="+Env_DIAG);
	System.out.println("# Machine_DIAG="+Machine_DIAG);
	System.out.println("# Honeypot_DIAG="+Honeypot_DIAG);
	System.out.println("# Sensor_DIAG="+Sensor_DIAG);
	System.out.println("# Worm_DIAG="+Worm_DIAG);
	System.out.println("# Scheduler_DIAG="+Scheduler_DIAG);
	System.out.println("# Experimenter_DIAG="+Experimenter_DIAG);
	System.out.println("# Env_PRM_NUM_HONEYPOTS="+Env_PRM_NUM_HONEYPOTS);
	System.out.println("# Env_PRM_NUM_SENSORS="+Env_PRM_NUM_SENSORS);
	System.out.println("# Env_PRM_NUM_MACHINES="+Env_PRM_NUM_MACHINES);
	System.out.println("# Machine_PRM_NUM_PEER_MACHINES="+Machine_PRM_NUM_PEER_MACHINES);
	System.out.println("# Machine_PRM_PROB_VULNERABLE="+Machine_PRM_PROB_VULNERABLE);
	System.out.println("# Sensor_PRM_SENSOR_WAKEUP="+Sensor_PRM_SENSOR_WAKEUP);
	System.out.println("# Sensor_PRM_PROB_INFECTION="+Sensor_PRM_PROB_INFECTION);
	System.out.println("# Sensor_PRM_NUM_FEED_HONEYPOTS="+Sensor_PRM_NUM_FEED_HONEYPOTS);
	System.out.println("# Honeypot_PRM_NUM_PEER_HONEYPOTS="+Honeypot_PRM_NUM_PEER_HONEYPOTS);
	System.out.println("# Honeypot_PRM_PROB_TRAFFIC_OBSERVED="+Honeypot_PRM_PROB_TRAFFIC_OBSERVED);
	System.out.println("# Honeypot_PRM_SIG_MINSTREAMS="+Honeypot_PRM_SIG_MINSTREAMS);
	System.out.println("# Honeypot_PRM_SELFISH_HONEYPOT="+Honeypot_PRM_SELFISH_HONEYPOT);
	System.out.println("# Worm_PRM_MAX_INSTANCES="+Worm_PRM_MAX_INSTANCES);
	System.out.println("# Worm_PRM_WORM_WAKEUP="+Worm_PRM_WORM_WAKEUP);
    }

};


