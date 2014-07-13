 /**
 * StaticSpringLayout.java
 * Created Feb 6, 2011
 */

package com.googlecode.blaisemath.graph.modules.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.modules.layout.SpringLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.PrintStream;
import java.util.Map;
import com.googlecode.blaisemath.graph.Graph;

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
