/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.util.Vector;
import scio.coordinate.R2;
import scio.random.Random2D;
import simulation.Agent;
import simulation.Team;
import simulation.Team;

/**
 *
 * @author ae3263
 */
public class StartingPositionsFactory {

    // METHODS FOR SETTING INITIAL POSITIONS OF TEAM MEMBERS
    /** Places all team members at zero. */
    public static void startZero(Vector<Agent> team) {
        for (Agent agent : team) {
            agent.setInitialPosition(R2.ORIGIN);
        }
    }

    /** Places team members at random within a rectangle.
     * @param spread sets the rectangle to [-spread,-spread]->[spread,spread] */
    public static void startRandom(Vector<Agent> team, double spread) {
        for (Agent agent : team) {
            agent.setInitialPosition(Random2D.rectangle(spread));
        }
    }

    /** Places team members along a line.
     * @param p1   position of the first agent
     * @param pn   position of the last agent */
    public static void startLine(Vector<Agent> team, R2 p1, R2 pn) {
        if (team.size() == 1) {
            team.get(0).setInitialPosition(p1.plus(pn).multipliedBy(.5));
        } else {
            R2 step = pn.minus(p1).multipliedBy(1.0 / (team.size() - 1.0));
            for (int i = 0; i < team.size(); i++) {
                team.get(i).setInitialPosition(p1.x + step.x * i, p1.y + step.y * i);
            }
        }
    }  

    /** Places team members in a circle.
     * @param point center of the circle
     * @param r     radius of the circle */
    public static void startCircle(Vector<Agent> team, R2 point, double r) {
        for (int i = 0; i < team.size(); i++) {
            team.get(i).setInitialPosition(point.x + r * Math.cos(2 * Math.PI * i / (double) team.size()), point.y + r * Math.sin(2 * Math.PI * i / (double) team.size()));
        }
    }

    /** Places team members in a circular arc.
     * @param point center of the arc
     * @param r     radius of the arc
     * @param th1   starting angle of the arc
     * @param th2   ending angle of the arc */
    public static void startArc(Vector<Agent> team, R2 point, double r, double th1, double th2) {
        if (team.size() == 1) {
            team.get(0).setInitialPosition(point.x + r * Math.cos((th1 + th2) / 2), point.y + r * Math.sin((th1 + th2) / 2));
        } else {
            for (int i = 0; i < team.size(); i++) {
                team.get(i).setInitialPosition(point.x + r * Math.cos(th1 + i * (th2 - th1) / (team.size() - 1.0)), point.y + r * Math.sin(th1 + i * (th2 - th1) / (team.size() - 1.0)));
            }
        }
    }

    /** Places team members at specified locations. If the size of the team does not match the specified locations,
     * any extra locations are not used; any extra agents are placed at the origin.
     * @param team the team to initialize
     * @param locations an array of locations to use for initialization
     */
    public static void startSpecific(Vector<Agent> team, R2[] locations) {
        if (locations == null) {
            locations = new R2[0];
        }
        for (int i = 0; i < Math.min(team.size(), locations.length); i++) {
            team.get(i).setInitialPosition(locations[i]);
        }
        if (team.size() > locations.length) {
            for (int i = locations.length; i < team.size(); i++) {
                team.get(i).setInitialPosition(R2.ORIGIN);
            }
        }
    }

    /** Example utilizing the above specific starting locations. */
    public static void startSpecificTest(Vector<Agent> team) {
        R2[] starts = {new R2(-30, 10), new R2(-20, 10), new R2(-10, 10), new R2(0, 10), new R2(10, 10), new R2(20, 10), new R2(30, 10)};
        startSpecific(team, starts);
    }


    public static void startSideline(Vector<Agent> team, R2[] locations) {
             for (int i = 0; i < team.size();) {
             team.get(i).setInitialPosition(-45+5*i, (Math.pow(-1, i))*50+50);
             i++;
           }
    }
   
    public static void startEndzone(Vector<Agent> team, R2[] locations) {
           for (int i = 0; i < team.size();) {
           team.get(i).setInitialPosition(-50, 10+i*10);
          i ++;
        }
    }

    public static void startOffense(Vector<Agent> team, R2[] locations) {
        for (int i = 0; i < team.size();) {
           team.get(i).setInitialPosition(40, 15+i*8);
           i++;
        }
    }

    public static void startDefense(Vector<Agent> team, R2[] locations) {
        for (int i = 0; i < team.size();) {
           team.get(i).setInitialPosition(-20, 15+i*8);
           i++;
                   }
    }
}
      
  

    
