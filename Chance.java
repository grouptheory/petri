
package petri;

public class Chance {
    
    public static boolean occurs(double p) {
	if (Math.random() <= p) return true;
	else return false;
    }
}
