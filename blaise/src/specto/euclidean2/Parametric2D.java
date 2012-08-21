/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */
package specto.euclidean2;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
import sequor.model.FunctionTreeModel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.coordinate.R2;
import scio.function.Derivative;
import scribo.tree.FunctionTreeRoot;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.component.RangeTimer;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.style.AnimateLineStyle;
import sequor.style.VisualStyle;
import specto.Animatable;
import sequor.style.LineStyle;

/**
 * Draws a parametric function on the plane. In other words, it contains two functions which give the x and y coordinates
 * of the function in terms of some other parameter (frequently 't'). The properties of this class which permit it to be
 * plotted are any function from Double->(Double,Double), and min/max values of t.
 * @author ElishaPeterson
 */
public class Parametric2D extends PointSet2D {

    // PROPERTIES
    
    /** Function which takes in a double and returns a pair of doubles = a point in the plane. */
    Function<Double, R2> function;

    /** Range of t values. */
    DoubleRangeModel tRange;

    /** Defines a default function which is displayed. For now its a "Lissajous" curve */
    private static final Function<Double, R2> DEFAULT_FUNCTION = new Function<Double, R2>() {

        @Override
        public R2 getValue(Double x) {
            return new R2(4 * Math.cos(x) + Math.sin(5 * x / 3), 4 * Math.sin(x) + Math.cos(5 * x / 3));
        }

        @Override
        public Vector<R2> getValue(Vector<Double> x) {
            Vector<R2> result = new Vector<R2>(x.size());
            for (Double d : x) {
                result.add(getValue(d));
            }
            return result;
        }
    };

    public Parametric2D() {
        this(DEFAULT_FUNCTION, 0.0, 6 * Math.PI, 1000);
    }

    public Parametric2D(String string) {
        this();
    }

    /** Constructor for use with a particular function and range of t values */
    public Parametric2D(Function<Double, R2> function, double tMin, double tMax, int samplePoints) {
        setColor(Color.BLUE);
        this.function = function;
        tRange = new DoubleRangeModel(tMin, tMin, tMax);
        tRange.setNumSteps(samplePoints, true);
    }

    public Parametric2D(Function<Double, R2> function, DoubleRangeModel drm, int samplePoints) {
        setColor(Color.BLUE);
        this.function = function;
        tRange = new DoubleRangeModel(drm.getMinimum(), drm.getMinimum(), drm.getMaximum());
        tRange.setNumSteps(samplePoints, true);
    }

    public Parametric2D(final FunctionTreeModel functionModel1, final FunctionTreeModel functionModel2) {
        tRange = new DoubleRangeModel(0.0, 0.0, 2 * Math.PI);
        tRange.setNumSteps(1000, true);
        function = getParametricFunction(
                (Function<Double, Double>) functionModel1.getRoot().getFunction(),
                (Function<Double, Double>) functionModel2.getRoot().getFunction());
        ChangeListener cl = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                function = getParametricFunction(
                        (Function<Double, Double>) functionModel1.getRoot().getFunction(),
                        (Function<Double, Double>) functionModel2.getRoot().getFunction());
                fireStateChanged();
            }
        };
        functionModel1.addChangeListener(cl);
        functionModel2.addChangeListener(cl);
    }

    // HELPERS
    public static Function<Double, R2> getParametricFunction(final Function<Double, Double> fx, final Function<Double, Double> fy) {
        return new Function<Double, R2>() {

            public R2 getValue(Double x) throws FunctionValueException {
                return new R2(fx.getValue(x), fy.getValue(x));
            }

            public Vector<R2> getValue(Vector<Double> xx) throws FunctionValueException {
                Vector<Double> xs = fx.getValue(xx);
                Vector<Double> ys = fy.getValue(xx);
                Vector<R2> result = new Vector<R2>(xs.size());
                for (int i = 0; i < xs.size(); i++) {
                    result.add(new R2(xs.get(i), ys.get(i)));
                }
                return result;
            }
        };
    }

    // BEAN PATTERNS
    public Function<Double, R2> getFunction() {
        return function;
    }

    public void setFunction(String fx, String fy) throws FunctionSyntaxException {
        function = getParametricFunction(
                (Function<Double, Double>) new FunctionTreeRoot(fx).getFunction(),
                (Function<Double, Double>) new FunctionTreeRoot(fy).getFunction());
    }

    public DoubleRangeModel getModel() {
        return tRange;
    }

    // TODO should not recompute path every time. Just when it's necessary
    /** Draws the path. */
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        if (function == null) {
            return;
        }
        try {
            computePath();
            super.paintComponent(g, v);
        } catch (FunctionValueException ex) {
            Logger.getLogger(Parametric2D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Computes the path over the given range */
    public void computePath() throws FunctionValueException {
        points = function.getValue(tRange.getValueRange(true, 0.0));
    }

    // STYLE
    @Override
    public String toString() {
        return "Parametric Function";
    }

    /** Returns t value at closest point on path. */
    public double getTime(R2 point) {
        R2 closest = getClosestPoint(point.x, point.y);
        int i;
        for (i = 0; i < points.size(); i++) {
            if (closest.equals(points.get(i))) {
                break;
            }
        }
        return tRange.getMinimum() + i * tRange.getStep();
    }

    // SETTINGS
    /** Overrides to allow setting custom options. */
    @Override
    public JMenu getOptionsMenu() {
        JMenu menu = super.getOptionsMenu();
        menu.add(new JSeparator(), 0);
        JMenuItem mi = new JMenuItem("Curve Options");
        mi.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (ps == null) {
                    ps = new ParametricSettings();
                }
                if (psd == null) {
                    psd = ps.getDialog(null, false);
                }
                psd.setVisible(true);
            }
        });
        menu.add(mi, 0);
        return menu;
    }
    ParametricSettings ps;
    JDialog psd;

    /** Inner class controls options for displaying the curve... starting point, ending point, and number of steps */
    public class ParametricSettings extends Settings {

        private IntegerRangeModel stepsModel = new IntegerRangeModel(500, 1, 10000, 10);

        /** Initializes, and sets up listening. */
        public ParametricSettings() {
            setName("Parametric Curve Settings");
            add(new SettingsProperty("Minimum", tRange.getMinModel(), Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Maximum", tRange.getMaxModel(), Settings.EDIT_DOUBLE));
            add(new SettingsProperty("# Steps", stepsModel, Settings.EDIT_INTEGER));
            stepsModel.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    tRange.setNumSteps(stepsModel.getValue(), true);
                    Parametric2D.this.fireStateChanged();
                }
            });
        }

        /** Event handling... all taken care of by the models */
        @Override
        public void stateChanged(ChangeEvent e) {
        }
    }

    // DECORATIONS
    /** Returns a point with a line through it representing the slope of the function at that point. Can also be displayed as a vector or ray
     * from the particular point.
     * @return Point2D object which can be added to a plot
     */
    public Point2D getPointSlope() {
        return new ParametricPoint();
    }
    /** Stores parallel curve */
    public Function<Double, R2> parallel;

    public void setParallel() {
        parallel = new Function<Double, R2>() {

            double offset = 2;
            R2 velocity;
            R2 normDir;

            public R2 getValue(Double t) throws FunctionValueException {
                velocity = Derivative.approximateDerivative(function, t, .01);
                normDir = velocity.normalized().rotatedBy(Math.PI / 2);
                return function.getValue(t).plus(normDir.multipliedBy(offset));
            }

            public Vector<R2> getValue(Vector<Double> tt) throws FunctionValueException {
                Vector<R2> result = new Vector<R2>(tt.size());
                for (Double t : tt) {
                    result.add(getValue(t));
                }
                return result;
            }
        };
    }

    public Parametric2D getParallel() {
        if (parallel == null) {
            setParallel();
        }
        Parametric2D result = new Parametric2D(parallel, tRange, tRange.getNumSteps());
        result.setColor(getColor().brighter());
        result.style.setValue(LineStyle.THIN);
        result.animateStyle.setValue(AnimateLineStyle.ANIMATE_TRACE);
        return result;
    }
    /** Stores involute */
    public Function<Double, R2> involute;

    public void setInvolute() {
        involute = new Function<Double, R2>() {

            R2 velocity;
            R2 acceleration;

            public R2 getValue(Double t) throws FunctionValueException {
                // TODO FILL THIS IN!!
                return new R2();
            }

            public Vector<R2> getValue(Vector<Double> tt) throws FunctionValueException {
                Vector<R2> result = new Vector<R2>(tt.size());
                for (Double t : tt) {
                    result.add(getValue(t));
                }
                return result;
            }
        };
    }

    public Parametric2D getInvolute() {
        if (involute == null) {
            setInvolute();
        }
        Parametric2D result = new Parametric2D(involute, tRange, tRange.getNumSteps());
        result.setColor(Color.YELLOW);
        result.style.setValue(LineStyle.THIN);
        result.animateStyle.setValue(AnimateLineStyle.ANIMATE_TRACE);
        return result;
    }
    /** Returns parametric curve representing the evolute */
    public Function<Double, R2> evolute;

    public void setEvolute() {
        evolute = new Function<Double, R2>() {

            R2 velocity;
            R2 acceleration;
            R2 normDir;

            public R2 getValue(Double t) throws FunctionValueException {
                velocity = Derivative.approximateDerivative(function, t, .1);
                acceleration = Derivative.approximateDerivativeTwo(function, t, .1);
                double curvature = Math.abs(velocity.cross(acceleration)) / Math.pow(velocity.magnitude(), 3);
                normDir = acceleration.componentPerpendicularTo(velocity).normalized();
                return function.getValue(t).plus(normDir.multipliedBy(1 / curvature));
            }

            public Vector<R2> getValue(Vector<Double> tt) throws FunctionValueException {
                Vector<R2> result = new Vector<R2>(tt.size());
                for (Double t : tt) {
                    result.add(getValue(t));
                }
                return result;
            }
        };
    }

    public Parametric2D getEvolute() {
        if (evolute == null) {
            setEvolute();
        }
        Parametric2D result = new Parametric2D(evolute, tRange, tRange.getNumSteps());
        result.setColor(Color.GRAY);
        result.style.setValue(LineStyle.THIN);
        result.animateStyle.setValue(AnimateLineStyle.ANIMATE_TRACE);
        return result;
    }

    // INNER CLASSES
    /** Represents a point on the parametric function with the ability to display position, velocity, acceleration vectors. */
    public class ParametricPoint extends Point2D implements Animatable<Euclidean2> {

        R2 velocity;
        R2 acceleration;
        DoubleRangeModel tModel;

        public ParametricPoint() {
            setModel(getConstraintModel());
            setColor(Color.GRAY);
            tModel = new DoubleRangeModel(getTime(getPoint()), tRange.getMinimum(), tRange.getMaximum(), tRange.getStep());
            tModel.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    if (adjusting) {
                        return;
                    }
                    try {
                        setPoint(function.getValue(tModel.getValue()));
                    } catch (FunctionValueException ex) {
                    }
                }
            });
            addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    if (!e.getSource().equals(tModel)) {
                        adjusting = true;
                        tModel.setRangeProperties(getTime(getPoint()), tRange.getMinimum(), tRange.getMaximum());
                        adjusting = false;
                    }
                }
            });

            Parametric2D.this.addChangeListener(this);
        }
        /** Whether this element animates. */
        public boolean animationOn = true;

        public void setAnimationOn(boolean newValue) {
            animationOn = newValue;
        }

        public boolean isAnimationOn() {
            return animationOn;
        }

        /** Returns data model which can be used to control the time of this point. */
        public DoubleRangeModel getTimeModel() {
            return tModel;
        }

        @Override
        public void recompute(Euclidean2 v) {
            try {
                super.setLabel("t=" + NumberFormat.getInstance().format(tModel.getValue()));
                velocity = Derivative.approximateDerivative(function, tModel.getValue(), v.getDrawWidth() / 1000);
                acceleration = Derivative.approximateDerivativeTwo(function, tModel.getValue(), v.getDrawWidth() / 1000);

            } catch (FunctionValueException ex) {
                System.out.println("error computing derivative approximation");
            }
        }

        @Override
        public void paintComponent(Graphics2D g, Euclidean2 v) {
            R2 position = getPoint();
            g.setColor(Color.BLACK);
            g.setStroke(LineStyle.STROKES[LineStyle.VERY_DOTTED]);
            g.setComposite(VisualStyle.COMPOSITE5);
            g.draw(v.arrow(new R2(), position, 8.0));
            if (velocity != null) {
                g.setColor(Color.RED);
                g.draw(v.arrow(position, position.plus(velocity), 8.0));
            }
            if (acceleration != null) {
                g.setColor(Color.PINK);
                g.draw(v.arrow(position, position.plus(acceleration), 8.0));
            }
            if (velocity != null && acceleration != null) {
                double curvature = Math.abs(velocity.cross(acceleration)) / Math.pow(velocity.magnitude(), 3);
                R2 normDir = acceleration.componentPerpendicularTo(velocity).normalized();
                g.setColor(Color.GREEN.darker());
                g.draw(v.arrow(position, position.plus(normDir), 4.0));
                g.draw(v.arrow(position, position.plus(velocity.normalized()), 4.0));
                if (curvature != 0) {
                    g.draw(v.circle(position.plus(normDir.multipliedBy(1 / curvature)), 1 / curvature));
                    g.setComposite(VisualStyle.COMPOSITE05);
                    g.fill(v.circle(position.plus(normDir.multipliedBy(1 / curvature)), 1 / curvature));
                }
            }
            g.setComposite(VisualStyle.COMPOSITE10);
            super.paintComponent(g, v);
        }

        @Override
        public String toString() {
            return "Point on Curve";
        }

        public void paintComponent(Graphics2D g, Euclidean2 v, RangeTimer t) {
            int pos = t.getCurrentIntValue();
            prm.setTo(pos >= points.size() ? points.lastElement() : points.get(pos));
            paintComponent(g, v);
        }

        public int getAnimatingSteps() {
            return Parametric2D.this.getAnimatingSteps();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!e.getSource().equals(this)) {
                changeEvent = e;
                redraw();
            }
        }
    } // class Parametric2D.ParametricPoint
}
