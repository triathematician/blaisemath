/**
 * LogVisometry.java
 * Created on Jun 3, 2008
 */

package specto.transformer;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Vector;
import specto.PlotPanel;
import specto.euclidean2.Euclidean2;

/**
 * Trial visometry used for plotting on a logarithmic scale.
 * @author Elisha Peterson
 */
public class LogVisometry extends Euclidean2 {
    
    boolean logX = false;
    boolean logY = true;
    
    
    // CONSTRUCTORS

    public LogVisometry() {}
    public LogVisometry(PlotPanel p) {
        super(p);
        p.addComponentListener(this);
    }
    
    
    
    // OVERRIDE COORDINATE TRANSFORMATIONS

    @Override
    public double toGeometryX(double wx) { return logX ? Math.exp(super.toGeometryX(wx)) : super.toGeometryX(wx); }
    @Override
    public double toGeometryY(double wy) { return logY ? Math.exp(super.toGeometryY(wy)) : super.toGeometryY(wy); }
    @Override
    public double toWindowX(double vx) { return logX ? super.toWindowX(Math.log(vx)) : super.toWindowX(vx); }
    @Override
    public double toWindowY(double vy) { return logY ? super.toWindowY(Math.log(vy)) : super.toWindowY(vy); }
    @Override
    public Vector<Double> toWindowX(Vector<Double> vxs){ return logX ? super.toWindowX(vectorLog(vxs)) : super.toWindowX(vxs); }
    @Override
    public Vector<Double> toWindowY(Vector<Double> vys){ return logY ? super.toWindowY(vectorLog(vys)) : super.toWindowY(vys); }
    @Override
    public void transform(Path2D.Double path) {
        Path2D.Double transformedPath = new Path2D.Double();
        PathIterator pi = path.getPathIterator(null);
        double[] coords = new double[6];
        pi.currentSegment(coords);
        transformedPath.moveTo(toWindowX(coords[0]),toWindowY(coords[1]));
        while (!pi.isDone()) {
            pi.next();
            pi.currentSegment(coords);
            transformedPath.lineTo(toWindowX(coords[0]),toWindowY(coords[1]));
        }
        path.reset();
        path.append(transformedPath, true);
    }
    
    public static Vector<Double> vectorLog(Vector<Double> ins) {
        for (int i = 0; i < ins.size(); i++) {
            ins.set(i, Math.log(ins.get(i)));
        }
        return ins;
    }
    
    
    // EVENT HANDLING
    
    
}
