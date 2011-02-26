/**
 * StaticSpringLayout.java
 * Created Feb 6, 2011
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;

/**
 *
 * @author elisha
 */
public class StaticSpringLayout implements StaticGraphLayout {
    int minSteps = 100;
    int maxSteps = 5000;
    double energyChangeThreshold = 5e-7;
    double coolStart = 0.65;
    double coolEnd = 0.1;

    public Map<Object, Point2D.Double> layout(Graph g, double... parameters) {
        double irad = parameters.length > 0 ? parameters[0] : 5;
        SpringLayout sl = new SpringLayout(g, StaticGraphLayout.CIRCLE, irad);
        double lastEnergy = Double.MAX_VALUE;
        double energyChange = 9999;
        int step = 0;
        while (step < minSteps || (step < maxSteps && Math.abs(energyChange) > energyChangeThreshold)) {
            // adjust cooling parameter
            double cool01 = 1-step*step/(maxSteps*maxSteps);
            sl.dampingC = coolStart*cool01 + coolEnd*(1-cool01);
            sl.iterate(g);
            energyChange = sl.energy - lastEnergy;
            lastEnergy = sl.energy;
            step++;
            if (step % 100 == 0)
                System.out.println("step = " + step + ", energy = " + lastEnergy);
        }
        System.out.println("Stopped @ " + step + " steps.");
        return sl.getPositions();
    }
}
