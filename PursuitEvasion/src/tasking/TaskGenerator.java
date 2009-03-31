/**
 * TaskGenerator.java
 * Created on Mar 24, 2009
 */

package tasking;

import java.util.Collection;
import scio.coordinate.V2;
import simulation.Agent;
import simulation.Team;
import utility.DistanceTable;

/**
 * Abstract class with routines for generating tasks.
 * @author Elisha Peterson
 */
public abstract class TaskGenerator {
    protected Team target;
    protected int type;
    public TaskGenerator(Team target,int type){this.target=target;this.type=type;}
    public abstract void generate(Collection<Agent> team, DistanceTable table, double priority);
    public void setTarget(Team target) { this.target = target; }
    public Team getTarget() { return target; }
}
