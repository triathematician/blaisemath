/*
 * SpaceParticleVectorFieldSurface.java
 * Created Jan 9, 2010
 */

package org.bm.blaise.specto.space.particle;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.space.function.SpaceParametricCurve;
import org.bm.blaise.specto.space.function.SpaceParametricSurface;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;

/**
 * This extends a <code>SpaceParticleVectorField</code> by having particle's initial
 * locations occur along a curve rather than along a rectangular region of space.
 * @author ae3263
 */
public class SpaceParticleVectorFieldSurface extends SpaceParticleVectorField {

    SpaceParametricSurface sfc;

    public SpaceParticleVectorFieldSurface(MultivariateVectorialFunction func, SpaceParametricSurface sfc) {
        super(func);
        this.sfc = sfc;
    }

    @Override
    public void paintComponent(VisometryGraphics<Point3D> vg) {
        sfc.paintComponent(vg);
        super.paintComponent(vg);
    }

    @Override
    public Point3D[] getNewPath(int length) {
        Point3D[] result = new Point3D[length];
        double[] randomInput = new double[] { sfc.getDomainU().randomValue(), sfc.getDomainV().randomValue() };
        double[] randomPt = {0, 0, 0};
        try {
            randomPt = sfc.getFunction().value(randomInput);
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(SpaceParticleVectorFieldSurface.class.getName()).log(Level.SEVERE, null, ex);
        }
        Point3D random = new Point3D(randomPt[0], randomPt[1], randomPt[2]);
        for (int i = 0; i < length; i++) {
            result[i] = random;
        }
        return result;
    }   

}
