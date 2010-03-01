/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.plane.particle;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plane.function.PlaneParametricCurve;
import org.bm.blaise.specto.plottable.VParticleField;

/**
 *
 * @author ae3263
 */
public class PlaneParticleCurve extends VParticleField<Point2D.Double> {

    PlaneParametricCurve curve;
    MultivariateVectorialFunction func;

    public PlaneParticleCurve(PlaneParametricCurve curve, MultivariateVectorialFunction func) {
        this.func = func;
        this.curve = curve;
        this.numParticles = 200;
        this.nRecycle = 1;
    }

    transient protected HashMap<Point2D.Double[], Double> tVals;

    @Override
    public void initPaths() {
        paths = new Point2D.Double[numParticles][trailLength];
        tVals = new HashMap<Point2D.Double[], Double>();
        for (int i = 0; i < paths.length; i++) {
            paths[i] = getNewPath(trailLength);
        }
    }

    @Override
    public Point2D.Double[] getNewPath(int length) {
        Point2D.Double[] result = new Point2D.Double[length];
        double randomT = curve.getDomain().randomValue();
        double[] randomPt = {0, 0};
        try {
            randomPt = curve.getFunction().value(randomT);
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParticleCurve.class.getName()).log(Level.SEVERE, null, ex);
        }
        Point2D.Double random = new Point2D.Double(randomPt[0], randomPt[1]);
        for (int i = 0; i < length; i++) {
            result[i] = random;
        }
        tVals.put(result, randomT);
        return result;
    }

    @Override
    public void advancePath(Point2D.Double[] path) {
        double dt = 1e-10;
        double tScale = 0.0005 * speed;
        for (int j = 0; j < path.length - 1; j++) {
            path[j] = path[j + 1];
        }
        if (path.length != 0) {
            double lastT = tVals.get(path);
            double lastT2 = lastT + dt;
            double[] last1 = null;
            double[] last2 = null;
            double[] vec = null;
            double[] next = new double[]{0, 0};
            double nextT = 0;
            try {
                last1 = curve.getFunction().value(lastT);
                last2 = curve.getFunction().value(lastT2);
                vec = func.value(last1);
                double[] tt = new double[]{(last2[0]-last1[0])/dt, (last2[1]-last1[1])/dt}; // this is unit tangent T
                double m = Math.sqrt(tt[0]*tt[0]+tt[1]*tt[1]); // this is magnitude of the tangent vector
                tt[0] /= m;
                tt[1] /= m;
                double dot = vec[0]*tt[0]+vec[1]*tt[1]; // this is F.T
                nextT = lastT + tScale * dot / m;
                next = curve.getFunction().value(nextT);
            } catch (FunctionEvaluationException ex) {
                Logger.getLogger(PlaneParticleCurve.class.getName()).log(Level.SEVERE, null, ex);
            }
            path[path.length - 1] = new Point2D.Double(next[0], next[1]);
            tVals.put(path, nextT);
        }
    }

}
