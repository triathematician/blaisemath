/*
 * GoalFusion.java
 * Created on Aug 28, 2007, 10:28:54 AM
 */
package behavior;

import tasking.Task;
import java.util.Vector;
import simulation.Agent;
import scio.coordinate.R2;
import tasking.Tasking;

/**
 * This class will eventually handle the job of fusing together multiple & possibly
 * conflicting taskings, e.g. head towards goal G while avoiding player P.
 * <br><br>
 * There are a few ways to do this. One is the "breach of radii" technique, whereby
 * the task is dependent on the distanceTo to goal. Another is the "gradient" technique,
 * whereby the gradient is computed in order to minimize/maximize the total distanceTo
 * to goal.
 * <br><br>
 * @author ae3263
 */
public class TaskFusion {

    public TaskFusion() {
    }

    /** 
     * Returns a unit vector in the direction corresponding to the fusion of the tasks shown.
     * I do not currently have a way in which to manipulate how the tasks are fused. I'll go with
     * the gradient to start.
     */
    public static R2 getVector(Agent agent, Vector<Task> tasks, double time) {
        R2 result = new R2();
        double multiplier;
        // if no tasks, generate a null task to use
        if (tasks.size() == 0) {
            return agent.getBehavior().direction(agent, null, time).normalized();
        } 
        for (Task t : tasks) {
            // translate... distinguish between seek and flee behaviors here
            multiplier = (t.getTaskType() == Tasking.FLEE ? -1 : 1) * (agent.getBehaviorCode() == Behavior.REVERSE ? -1 : 1);
            // scales the result by the task priority
            result.translateBy(agent.getBehavior().direction(agent, t.getTarget(), time)
                    .multipliedBy(multiplier * t.getPriority()));
        }
        return result.normalized();
    }

    /**
     * Find the direction which MAXIMIZES the sum of distanceTo powers between the agent at loc and the
     * specified points.
     */
    public static R2 gradient(R2 loc, Vector<WR2> points) {
        R2 dir = new R2();
        for (WR2 p : points) {
            dir = dir.plus(new R2(p.x - loc.x, p.y - loc.y).scaledToLength(Math.pow(p.distance(loc), p.weight - 1.0)));
        }
        return dir;
    }

    /**
     * Returns the direction to the highest weighted element
     */
    public static R2 highest(R2 loc, Vector<WR2> points) {
        double hWeight = 0;
        R2 dir = new R2(0, 0);
        for (WR2 p : points) {
            if (p.weight > hWeight) {
                hWeight = p.weight;
                dir = p.minus(loc);
            }
        }
        return dir;
    }

    /** A weighted point. */
    public class WR2 extends R2 {

        public double weight;

        public WR2(R2 pt, double w) {
            super(pt);
            weight = w;
        }

        public WR2(double x, double y, double w) {
            super(x, y);
            weight = w;
        }
    } // Inner Class TaskFusion.WR2
}
