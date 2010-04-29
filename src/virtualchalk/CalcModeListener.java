/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package virtualchalk;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.InputStream;
import org.bm.firestorm.gestures.data.CoefficientClassifier;
import org.bm.firestorm.gestures.data.GestureDistance;
import org.bm.firestorm.gestures.data.TrainGesture;
import org.bm.firestorm.gestures.ui.GestureEvent;
import org.bm.firestorm.gestures.ui.GestureListener;

/**
 * Handles gestures in the calc mode
 * @author ae3263
 */
public class CalcModeListener implements GestureListener {

    /** This is the cutoff threshold for firing a gesture event. */
    float threshold = 2f;
    /** Stores the gestures */
    CoefficientClassifier trainedGestures;

    public CalcModeListener() {
        loadGestureLibrary();
    }

    public void gesturePerformed(GestureEvent e) {
        Main cls = (Main) e.getSource();
        if (trainedGestures != null) {
            TrainGesture closest = trainedGestures.closestTo(e.getGesture());
            String bestString = (String) trainedGestures.get(closest);
            float bestDistance = (float) GestureDistance.value(closest, e.getGesture());
            if (bestString != null && bestDistance < threshold)
                cls.appendToOutput(bestString);
        }
    }

    /** Uses a preset file to load a library of gestures. */
    private void loadGestureLibrary() {
        InputStream openFile = Main.class.getResourceAsStream("Calculator.xml");
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(openFile));
        trainedGestures = (CoefficientClassifier) decoder.readObject();
        decoder.close();
        System.out.println("Successfully loaded calc mode gesture file with " + trainedGestures.size() + " trained gestures.");
    }
}
