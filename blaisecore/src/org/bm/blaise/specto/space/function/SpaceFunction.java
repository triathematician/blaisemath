/*
 * SpaceFunction.java
 * Created on Oct 22, 2009
 */

package org.bm.blaise.specto.space.function;

import org.bm.blaise.specto.space.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.P3D;

/**
 * <p>
 *  Displays a function as a sampled grid of points.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceFunction extends AbstractPlottable<P3D> {

    int samples = 20;
    Rectangle2D.Double domain = new Rectangle2D.Double(-5.0, -5.0, 5.0, 5.0);

    MultivariateRealFunction function = new MultivariateRealFunction(){
        public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            return Math.cos(point[0]) + Math.sin(point[1]);
        }
    };

//    public ShapeStyle getStyle() {
//        return style;
//    }
//
//    public void setStyle(ShapeStyle style) {
//        this.style = style;
//    }

    public Double getDomain() {
        return domain;
    }

    public void setDomain(Double domain) {
        this.domain = domain;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }
    

    @Override
    public void paintComponent(VisometryGraphics<P3D> vg) {
        List<P3D[]> polys = getPolys(domain.getMinX(), domain.getMinY(), domain.getMaxX(), domain.getMaxY(), samples);
        ((SpaceGraphics) vg).addToScene(polys);
    }

    private List<P3D[]> getPolys(double xmin, double ymin, double xmax, double ymax, int steps) {
        List<P3D[]> result = new ArrayList<P3D[]>(steps * steps);
        if (steps < 1) {
            return result;
        }
        double[] input = new double[2];
        P3D[][] arr = new P3D[steps+1][steps+1];
        double dx = (xmax - xmin) / steps;
        double dy = (ymax - ymin) / steps;
        try {
            for (int i = 0; i <= steps; i++) {
                for (int j = 0; j <= steps; j++) {
                    input[0] = xmin + dx * i;
                    input[1] = ymin + dy * j;
                    arr[i][j] = new P3D(input[0], input[1], function.value(input));
                }
            }
        } catch (FunctionEvaluationException e) {
            System.out.println("getPolys exception in SpaceFunction!");
        }
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                result.add(new P3D[]{ arr[i][j], arr[i+1][j], arr[i+1][j+1], arr[i][j+1] });
            }
        }
        return result;
    }
}
