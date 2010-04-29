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
import org.bm.firestorm.gestures.data.CoefficientClassifier;
import org.bm.firestorm.gestures.data.GestureDistance;
import org.bm.firestorm.gestures.data.TrainGesture;
import org.bm.firestorm.gestures.ui.DrawPanel;
import org.bm.firestorm.gestures.ui.GestureEvent;
import org.bm.firestorm.gestures.ui.GestureListener;

/**
 * Handles gestures that occur in general cases.
 *
 * @author ae3263
 */
public class GenericModeListener 
        implements ActionListener, GestureListener {

    /** This is the cutoff threshold for firing a gesture event. */
    float threshold = 2.5f;
    /** Stores the gestures */
    CoefficientClassifier trainedGestures;

    public GenericModeListener() {
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
        DrawPanel dp = ((Main)e.getSource()).getChalkboard();
        ModePanel mp = ((Main)e.getSource()).getModePanel();
        if (e.getActionCommand().equals("@alpha_mode")) {
            mp.setMode(ModePanel.InputMode.ALPHA);
            dp.removeActivePath();
        } else if (e.getActionCommand().equals("@calc_mode")) {
            mp.setMode(ModePanel.InputMode.CALC);
            dp.removeActivePath();
        } else if (e.getActionCommand().equals("@graph_mode")) {
            mp.setMode(ModePanel.InputMode.GRAPH);
            dp.removeActivePath();
        } else if (e.getActionCommand().equals("@clear_panel")) {
            dp.clearAllPaths();
        } else if (e.getActionCommand().equals("@undo")) {
            dp.removeLastPath();
            dp.removeActivePath();
        }
    }

    /** Uses a preset file to load a library of gestures. */
    private void loadGestureLibrary() {
        InputStream openFile = Main.class.getResourceAsStream("GenericMode.xml");
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(openFile));
        trainedGestures = (CoefficientClassifier) decoder.readObject();
        decoder.close();
        System.out.println("Successfully loaded generic mode gesture file with " + trainedGestures.size() + " trained gestures.");
    }
}
