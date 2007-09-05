/*
 * Main.java
 * Created on Aug 28, 2007, 10:24:00 AM
 * First runtime on Sep 4, 2007, 2:55 PM (utter failure!)
 * First successful run on Sep 4, 2007, 3:52 PM
 */

package pursuitevasion;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class will setup and run the visual Pursuit/Evasion scenario window.
 */
public class Main {

    /** Creates a new instance of Main */
    public Main() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim=new Simulation();
        sim.run(100);
    }

}
