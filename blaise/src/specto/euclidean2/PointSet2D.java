/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */
package specto.euclidean2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import sequor.model.ColorModel;
import specto.Animatable;
import scio.coordinate.R2;
import sequor.component.RangeTimer;
import sequor.model.PointRangeModel;
import sequor.style.AnimateLineStyle;
import sequor.style.LineStyle;
import sequor.style.VisualStyle;
import specto.Constrains2D;
import specto.Plottable;

/**
 *
 * @author Elisha Peterson
 */
public class PointSet2D extends Plottable<Euclidean2> implements Animatable<Euclidean2>, ChangeListener, Constrains2D {

    Vector<R2> points;
    private String label;
    boolean labelVisible = true;

    public PointSet2D() {
        this(new Vector<R2>(), Color.BLACK);
    }

    public PointSet2D(Color c) {
        this(new Vector<R2>(), c);
    }

    public PointSet2D(Vector<R2> points, Color c) {
        initStyle();
        setColor(c);
        this.points = points;
    }

    public PointSet2D(Vector<R2> points, Color c, int style) {
        initStyle();
        this.style.setValue(style);
        setColor(c);
        this.points = points;
    }

    public PointSet2D(Vector<R2> points, ColorModel colorModel) {
        initStyle();
        setColorModel(colorModel);
        this.points = points;
    }

    public PointSet2D(Vector<R2> points, ColorModel colorModel, int style) {
        initStyle();
        this.style.setValue(style);
        setColorModel(colorModel);
        this.points = points;
    }
    // BEAN PATTERNS
    /** Whether this element animates. */
    public boolean animationOn = true;

    public void setAnimationOn(boolean newValue) {
        animationOn = newValue;
    }

    public boolean isAnimationOn() {
        return animationOn;
    }

    public Vector<R2> getPath() {
        return points;
    }

    public void setPath(Vector<R2> path) {
        points = path;
        fireStateChanged();
    }

    public void setLabel(String s) {
        label = s;
    }

    // DRAW METHODS
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        if (points == null || points.size() == 0) {
            return;
        }
        ((LineStyle) style).apply(g);
        if (style.getValue().equals(LineStyle.POINTS_ONLY)) {
            for (int i = 0; i < points.size(); i++) {
                g.fill(v.dot(points.get(i), 3));
            }
        } else {
            g.draw(v.path(points));
        }
        if (label != null && labelVisible) {
            java.awt.geom.Point2D.Double winCenter = v.toWindow(points.firstElement());
            g.setComposite(VisualStyle.COMPOSITE5);
            g.drawString(label, (float) winCenter.x + 5, (float) winCenter.y + 5);
            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v, RangeTimer t) {
        ((LineStyle) style).apply(g);
        try {
            if (points == null || points.size() == 0) {
                return;
            }
            int curVal = t.getCurrentIntValue();
            if (style.getValue().equals(LineStyle.POINTS_ONLY)) {
                for (int i = 0; i < curVal; i++) {
                    g.fill(v.dot(points.get(i), 3));
                }
            } else {
                ((LineStyle) style).apply(g);
                switch (animateStyle.getValue()) {
                    case AnimateLineStyle.ANIMATE_DOT:
                        g.fill(v.dot(points.get(curVal), 3));
                        break;
                    case AnimateLineStyle.ANIMATE_VEC:
                        R2 p1;
                        R2 p2;
                        if (curVal < points.size() - 1) {
                            p1 = points.get(curVal);
                            p2 = points.get(curVal + 1);
                        } else {
                            p1 = points.get(curVal - 1);
                            p2 = points.get(curVal);
                        }
                        g.setStroke(LineStyle.STROKES[LineStyle.THIN]);
                        g.draw(v.arrow(p1, p2, 8.0));
                        break;
                    case AnimateLineStyle.ANIMATE_DRAW:
                        g.draw(drawPath(v, t.getFirstIntValue(), curVal));
                        break;
                    case AnimateLineStyle.ANIMATE_TRACE:
                        g.draw(drawPath(v, 0, points.size()));
                        g.fill(v.dot(points.get(curVal), 3));
                        break;
                    case AnimateLineStyle.ANIMATE_TRAIL:
                    default:
                        g.draw(drawPath(v, 0, curVal));
                        g.draw(drawPath(v, curVal - 5, curVal));
                        g.fill(v.dot(points.get(curVal), 3));
                        break;
                }
                if (curVal >= points.size()) {
                    curVal = points.size() - 1;
                }
                if (labelVisible) {
                    java.awt.geom.Point2D.Double labelCenter = v.toWindow(points.get(curVal));
                    g.setComposite(VisualStyle.COMPOSITE5);
                    g.drawString(points.get(curVal).toString(), (float) labelCenter.x + 5, (float) labelCenter.y - 5);
                }
            }
            g.setComposite(AlphaComposite.SrcOver);
            if (label != null && labelVisible) {
                java.awt.geom.Point2D.Double winCenter = v.toWindow(points.firstElement());
                g.setComposite(VisualStyle.COMPOSITE5);
                g.drawString(label, (float) winCenter.x + 5, (float) winCenter.y + 5);
                g.setComposite(AlphaComposite.SrcOver);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

//    public Shape drawDot(Euclidean2 v,int pos){
//        int posB=pos<0?0:(pos>=points.size()?points.size()-1:pos);
//        return v.dot(points.get(posB),3.0);
//    }
    /** Returns portion of the path between starting and ending index. */
    public Path2D.Double drawPath(Euclidean2 v, int start, int end) {
        Path2D.Double result = new Path2D.Double();
        int startB = start < 0 ? 0 : (start >= points.size() ? points.size() - 1 : start);
        int endB = end < 0 ? 0 : (end >= points.size() ? points.size() - 1 : end);
        result.moveTo(points.get(startB).x, points.get(startB).y);
        for (int i = startB; i <= endB; i++) {
            result.lineTo(points.get(i).x, points.get(i).y);
        }
        v.transform(result);
        return result;
    }

    // STYLE SETTINGS
    @Override
    public String toString() {
        return "Path or Set of Points";
    }
    public AnimateLineStyle animateStyle;

    @Override
    public void initStyle() {
        super.initStyle();
        style = new LineStyle(LineStyle.MEDIUM);
        style.addChangeListener(this);
        animateStyle = new AnimateLineStyle(AnimateLineStyle.ANIMATE_TRAIL);
        animateStyle.addChangeListener(this);
    }

    public void setLabelVisible(boolean visible) {
        if (labelVisible != visible) {
            labelVisible = visible;
            fireStateChanged();
        }
    }

    // EVENT HANDLING
    @Override
    public JMenu getOptionsMenu() {
        return animateStyle.appendToMenu(super.getOptionsMenu());
    }

    // ANIMATION ELEMENTS
    public int getAnimatingSteps() {
        return (points == null) ? 0 : points.size();
    }


    // POINT MODELS
    public PointRangeModel getConstraintModel() {
        PathPointModel result = new PathPointModel();
        addChangeListener(result);
        return result;
    }

    public Point2D getConstrainedPoint() {
        return new Point2D(getConstraintModel());
    }

    /** Gets closest point out of the computed path */
    public R2 getClosestPoint(double x0, double y0) {
        R2 closestPoint = new R2(Double.MAX_VALUE, Double.MAX_VALUE);
        double closestDistance = Double.MAX_VALUE;
        try {
            for (R2 pt : points) {
                if (pt.distance(x0, y0) < closestDistance) {
                    closestPoint = pt;
                    closestDistance = pt.distance(x0, y0);
                }
            }
        } catch (NullPointerException e) {
        }
        return closestPoint;
    }

    // INNER CLASSES
    class PathPointModel extends PointRangeModel {

        public PathPointModel() {
            try {
                super.setTo(points.get(0));
            } catch (Exception e) {
                super.setTo(R2.ORIGIN);
            }
            super.setTo(R2.ORIGIN);
        }

        @Override
        public void setTo(R2 point) {
            R2 closest = getClosestPoint(point.x, point.y);
            super.setTo(closest.x, closest.y);
        }

        @Override
        public void setTo(double x0, double y0) {
            R2 closest = getClosestPoint(x0, y0);
            super.setTo(closest.x, closest.y);
        }
    } // class Function2D.ParametricPointModel
}
