/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.plane.diffeq;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.bm.blaise.specto.plane.function.PlaneParametricFunction;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.function.MultivariateVectorialFunction;

/**
 *
 * @author ae3263
 */
public class PlaneParticleVectorFieldCurve extends PlaneParticleVectorField {

    PlaneParametricFunction curve;

    public PlaneParticleVectorFieldCurve(MultivariateVectorialFunction func, PlaneParametricFunction curve) {
        super(func);
        this.curve = curve;
    }

    @Override
    public void paintComponent(VisometryGraphics<Double> vg) {
        curve.paintComponent(vg);
        super.paintComponent(vg);
    }

    @Override
    public Point2D.Double[] getNewPath(int length) {
        Point2D.Double[] result = new Point2D.Double[length];
        double randomT = (curve.getRange().getMax() - curve.getRange().getMin()) * Math.random() + curve.getRange().getMin();
        double[] randomPt = {0, 0};
        try {
            randomPt = curve.getFunc().value(randomT);
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParticleVectorFieldCurve.class.getName()).log(Level.SEVERE, null, ex);
        }
        Point2D.Double random = new Point2D.Double(randomPt[0], randomPt[1]);
        for (int i = 0; i < length; i++) {
            result[i] = random;
        }
        return result;
    }
    
    

}
