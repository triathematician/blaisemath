/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package virtualchalk;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.InputStream;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.firestorm.gestures.data.CoefficientClassifier;
import org.bm.firestorm.gestures.data.GestureDistance;
import org.bm.firestorm.gestures.data.TrainGesture;
import org.bm.firestorm.gestures.ui.GestureEvent;
import org.bm.firestorm.gestures.ui.GestureListener;
import visometry.plane.PlaneAxes;
import visometry.plane.PlaneFunctionGraph;
import visometry.plane.PlaneParametricCurve;
import visometry.plane.PlanePlotComponent;
import visometry.plottable.Plottable;

/**
 * Handles actions in the graph mode.
 * @author ae3263
 */
public class GraphModeListener implements ActionListener, GestureListener {

    /** This is the cutoff threshold for firing a gesture event. */
    float threshold = 2f;
    /** Stores the gestures */
    CoefficientClassifier trainedGestures;

    public GraphModeListener() {
        loadGestureLibrary();
    }

    public void gesturePerformed(GestureEvent e) {
        if (trainedGestures != null) {
            TrainGesture closest = trainedGestures.closestTo(e.getGesture());
            String bestString = (String) trainedGestures.get(closest);
            float bestDistance = (float) GestureDistance.value(closest, e.getGesture());
            if (bestString != null && bestDistance < threshold)
                actionPerformed(new ActionEvent(e.getSource(), 0, bestString));
        }
    }

    public void actionPerformed(ActionEvent e) {
        PlanePlotComponent plot = ((Main)e.getSource()).getPlot();
        if (e.getActionCommand().equals("circle")) {
            plot.add(new PlaneParametricCurve());
        } else if (e.getActionCommand().equals("axes")) {
            Plottable[] parr = plot.getPlottableArray();
            for (int i = 0; i < parr.length; i++)
                if (parr[i] instanceof PlaneAxes)
                    return;
            plot.add(new PlaneAxes());
        } else if (e.getActionCommand().equals("sin")) {
            plot.add(new PlaneFunctionGraph(new UnivariateRealFunction(){public double value(double x) { return Math.sin(x); } }));
        } else if (e.getActionCommand().equals("cos")) {
            plot.add(new PlaneFunctionGraph(new UnivariateRealFunction(){public double value(double x) { return Math.cos(x); } }));
        } else if (e.getActionCommand().equals("log") || e.getActionCommand().equals("ln")) {
            plot.add(new PlaneFunctionGraph(new UnivariateRealFunction(){public double value(double x) { return Math.log(x); } }));            
        } else if (e.getActionCommand().equals("quadratic")) {
            plot.add(new PlaneFunctionGraph(new UnivariateRealFunction(){public double value(double x) { return x*x; } }));
        } else if (e.getActionCommand().equals("cubic")) {
            plot.add(new PlaneFunctionGraph(new UnivariateRealFunction(){public double value(double x) { return x*x*x-x; } }));
        } else if (e.getActionCommand().equals("exponential")) {
            plot.add(new PlaneFunctionGraph(new UnivariateRealFunction(){public double value(double x) { return Math.exp(x); } }));
        }
    }

    /** Uses a preset file to load a library of gestures. */
    private void loadGestureLibrary() {
        InputStream openFile = Main.class.getResourceAsStream("GraphModeGestures.xml");
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(openFile));
        trainedGestures = (CoefficientClassifier) decoder.readObject();
        decoder.close();
        System.out.println("Successfully loaded gesture file with " + trainedGestures.size() + " trained gestures.");
    }
}
