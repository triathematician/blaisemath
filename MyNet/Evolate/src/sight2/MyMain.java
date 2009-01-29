
package sight2;

/**
 *
 * @author elisha
 */
public class MyMain {
    // Simulation Constants
    static int NSTEPS = 10000;

    /** The main method, which runs the simulation */
    public static void main(String[] args) {
        MyTeam t = new MyTeam(5);
        new MySim(t).run();
    }
}
