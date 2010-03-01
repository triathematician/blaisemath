/*
 * SpaceFunction.java
 * Created on Oct 22, 2009
 */

package org.bm.blaise.specto.space.function;

import org.bm.blaise.specto.space.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;

/**
 * <p>
 *  Displays a function as a sampled grid of points.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceFunctionGraph extends AbstractPlottable<Point3D> {

    int samples = 20;
    Rectangle2D.Double domain;
    MultivariateRealFunction function;

    public SpaceFunctionGraph() {
        this(new MultivariateRealFunction(){
                public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                    return Math.cos(point[0]) + Math.sin(point[1]);
                }
            }, -5, -5, 5, 5);
    }

    public SpaceFunctionGraph(MultivariateRealFunction function, double lowX, double lowY, double highX, double highY) {
        this.function = function;
        this.domain = new Rectangle2D.Double(lowX, lowY, highX-lowX, highY-lowY);
    }

    public Rectangle2D.Double getDomain() {
        return domain;
    }

    public void setDomain(Rectangle2D.Double domain) {
        this.domain = domain;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }
    

    @Override
    public void paintComponent(VisometryGraphics<Point3D> vg) {
        List<Point3D[]> polys = getPolys(domain.getMinX(), domain.getMinY(), domain.getMaxX(), domain.getMaxY(), samples);
        ((SpaceGraphics) vg).addToScene(polys);
    }

    private List<Point3D[]> getPolys(double xmin, double ymin, double xmax, double ymax, int steps) {
        List<Point3D[]> result = new ArrayList<Point3D[]>(steps * steps);
        if (steps < 1) {
            return result;
        }
        double[] input = new double[2];
        Point3D[][] arr = new Point3D[steps+1][steps+1];
        double dx = (xmax - xmin) / steps;
        double dy = (ymax - ymin) / steps;
        try {
            for (int i = 0; i <= steps; i++) {
                for (int j = 0; j <= steps; j++) {
                    input[0] = xmin + dx * i;
                    input[1] = ymin + dy * j;
                    arr[i][j] = new Point3D(input[0], input[1], function.value(input));
                }
            }
        } catch (FunctionEvaluationException e) {
            System.out.println("getPolys exception in SpaceFunction!");
        }
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                result.add(new Point3D[]{ arr[i][j], arr[i+1][j], arr[i+1][j+1], arr[i][j+1] });
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Graph of 2-Variable Function";
    }
}
