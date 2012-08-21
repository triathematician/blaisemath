 /**
 * StaticSpringLayout.java
 * Created Feb 6, 2011
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.PrintStream;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;

/**
 *
 * @author elisha
 */
public class StaticSpringLayout implements StaticGraphLayout {
    public int minSteps = 100;
    public int maxSteps = 5000;
    public double energyChangeThreshold = 5e-7;
    public double coolStart = 0.65;
    public double coolEnd = 0.1;
    
    /** Used to print status */
    PrintStream statusStream;
    /** Used to notify status */
    ActionListener al;
    
    public StaticSpringLayout(PrintStream status, ActionListener al) {
        this.statusStream = status;
    }
    
    /** Sets output stream for updates */
    public void setStatusStream(PrintStream s) {
        this.statusStream = s;
    }
    /** Sets listener for layout updates */
    public void setLayoutListener(ActionListener al) {
        this.al = al;
    }
    
    /** Maintain singleton instance of the class */
    private static StaticSpringLayout INST;
    /** Return a singleton instance of the class (using default settings) */
    public static StaticSpringLayout getInstance() {
        if (INST == null)
            INST = new StaticSpringLayout(System.out, null);
        return INST;        
    }

    public synchronized Map<Object, Point2D.Double> layout(Graph g, double... parameters) {
        double irad = parameters.length > 0 ? parameters[0] : 5;
        SpringLayout sl = new SpringLayout(g, StaticGraphLayout.CIRCLE, irad);
        double lastEnergy = Double.MAX_VALUE;
        double energyChange = 9999;
        int step = 0;
        while (step < minSteps || (step < maxSteps && Math.abs(energyChange) > energyChangeThreshold)) {
            // adjust cooling parameter
            double cool01 = 1-step*step/(maxSteps*maxSteps);
            sl.parameters.dampingC = coolStart*cool01 + coolEnd*(1-cool01);
            sl.iterate(g);
            energyChange = sl.energy - lastEnergy;
            lastEnergy = sl.energy;
            step++;
            if (step % 100 == 0)
                updateStatus("", step, lastEnergy);
        }
        updateStatus("stop, ", step, lastEnergy);
        return sl.getPositions();
    }
    
    void updateStatus(String prefix, int step, double lastEnergy) {
        String status = String.format("%sstep = %d/%d, energy=%.2f (threshold=%.2f)", 
                prefix, step, maxSteps, lastEnergy, energyChangeThreshold);
        if (statusStream != null)
            statusStream.println(status);
        if (al != null)
            al.actionPerformed(new ActionEvent(this, 0, status));
    }
}
