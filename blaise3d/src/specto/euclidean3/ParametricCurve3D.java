/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */
package specto.euclidean3;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
import sequor.model.FunctionTreeModel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.coordinate.R3;
import scio.function.Derivative;
import scribo.tree.FunctionTreeRoot;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import specto.Decoration;
import specto.Plottable;

/**
 * Draws a parametric function on the plane. In other words, it contains two functions which give the x and y coordinates
 * of the function in terms of some other parameter (frequently 't'). The properties of this class which permit it to be
 * plotted are any function from Double->(Double,Double), and min/max values of t.
 * @author ElishaPeterson
 */
public class ParametricCurve3D extends PointSet3D implements SampleSet3D, SampleVector3D {

    // PROPERTIES
    /** Function which takes in a double and returns a pair of doubles = a point in the plane. */
    Function<Double, R3> function;
    /** Range of t values. */
    DoubleRangeModel tRange;
    /** Defines a default function which is displayed. For now its a "Lissajous" curve */
    private static final Function<Double, R3> DEFAULT_FUNCTION = new Function<Double, R3>() {

        @Override
        public R3 getValue(Double t) {
            return new R3(2 * Math.cos(t), 2 * Math.sin(2 * t), t);
        }

        @Override
        public Vector<R3> getValue(Vector<Double> x) {
            Vector<R3> result = new Vector<R3>(x.size());
            for (Double d : x) {
                result.add(getValue(d));
            }
            return result;
        }
    };

    public ParametricCurve3D() {
        this(DEFAULT_FUNCTION, 0.0, 2 * Math.PI, 1000);
    }

    public ParametricCurve3D(String string) {
        this();
    }

    public ParametricCurve3D(String fx, String fy, String fz) {
        this();
        try {
            setFunction(fx, fy, fz);
        } catch (Exception e) {
        }
    }

    /** Constructor for use with a particular function and range of t values */
    public ParametricCurve3D(Function<Double, R3> function, double tMin, double tMax, int samplePoints) {
        setColor(Color.BLUE);
        this.function = function;
        tRange = new DoubleRangeModel(tMin, tMin, tMax);
        tRange.setNumSteps(samplePoints, true);
    }

    public ParametricCurve3D(Function<Double, R3> function, DoubleRangeModel drm, int samplePoints) {
        setColor(Color.BLUE);
        this.function = function;
        tRange = new DoubleRangeModel(drm.getMinimum(), drm.getMinimum(), drm.getMaximum());
        tRange.setNumSteps(samplePoints, true);
    }

    public ParametricCurve3D(final FunctionTreeModel fm1, final FunctionTreeModel fm2, final FunctionTreeModel fm3) {
        tRange = new DoubleRangeModel(0.0, 0.0, 2 * Math.PI);
        tRange.setNumSteps(1000, true);
        function = getParametricFunction(
                (Function<Double, Double>) fm1.getRoot().getFunction(),
                (Function<Double, Double>) fm2.getRoot().getFunction(),
                (Function<Double, Double>) fm3.getRoot().getFunction());
        ChangeListener cl = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                function = getParametricFunction(
                        (Function<Double, Double>) fm1.getRoot().getFunction(),
                        (Function<Double, Double>) fm2.getRoot().getFunction(),
                        (Function<Double, Double>) fm3.getRoot().getFunction());
                fireStateChanged();
            }
        };
        fm1.addChangeListener(cl);
        fm2.addChangeListener(cl);
    }

    // HELPERS
    public static Function<Double, R3> getParametricFunction(
            final Function<Double, Double> fx, final Function<Double, Double> fy, final Function<Double, Double> fz) {
        return new Function<Double, R3>() {

            public R3 getValue(Double t) throws FunctionValueException {
                return new R3(fx.getValue(t), fy.getValue(t), fz.getValue(t));
            }

            public Vector<R3> getValue(Vector<Double> ts) throws FunctionValueException {
                Vector<Double> xs = fx.getValue(ts);
                Vector<Double> ys = fy.getValue(ts);
                Vector<Double> zs = fz.getValue(ts);
                Vector<R3> result = new Vector<R3>(xs.size());
                for (int i = 0; i < xs.size(); i++) {
                    result.add(new R3(xs.get(i), ys.get(i), zs.get(i)));
                }
                return result;
            }
        };
    }

    // BEAN PATTERNS
    public Function<Double, R3> getFunction() {
        return function;
    }

    public void setFunction(String fx, String fy, String fz) throws FunctionSyntaxException {
        function = getParametricFunction(
                (Function<Double, Double>) new FunctionTreeRoot(fx).getFunction(),
                (Function<Double, Double>) new FunctionTreeRoot(fy).getFunction(),
                (Function<Double, Double>) new FunctionTreeRoot(fz).getFunction());
    }

    public DoubleRangeModel getModel() {
        return tRange;
    }

    /** Draws the path. */
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v) {
        if (function == null) {
            return;
        }
        try {
            computePath();
            super.paintComponent(g, v);
        } catch (FunctionValueException ex) {
        }
    }

    /** Computes the path over the given range */
    public void computePath() throws FunctionValueException {
        points = function.getValue(tRange.getValueRange(true, 0.0));
    }

    // STYLE
    @Override
    public String toString() {
        return "Parametric Curve";
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
                    ParametricCurve3D.this.fireStateChanged();
                }
            });
        }

        /** Event handling... all taken care of by the models */
        @Override
        public void stateChanged(ChangeEvent e) {
        }
    }

    // RETURN LIST OF POINTS ALONG THE PATH
    /** Returns list of points along the path. */
    public Vector<R3> getSampleSet(int options) {
        DoubleRangeModel sampleRange = new DoubleRangeModel(tRange.getValue(), tRange.getMinimum(), tRange.getMaximum());
        sampleRange.setNumSteps(options, true);
        try {
            return function.getValue(sampleRange.getValueRange(true, 0.0));
        } catch (FunctionValueException ex) {
            Logger.getLogger(ParametricCurve3D.class.getName()).log(Level.SEVERE, null, ex);
            return new Vector<R3>();
        }
    }

    /** Returns list of vectors along the path in the direction of the path. */
    public Vector<R3[]> getSampleVectors(int options) {
        DoubleRangeModel sampleRange = new DoubleRangeModel(tRange.getValue(), tRange.getMinimum(), tRange.getMaximum());
        sampleRange.setNumSteps(options, true);
        try {
            Vector<R3[]> result = new Vector<R3[]>();
            for (Double t : sampleRange.getValueRange(true, 0.0)) {
                R3[] temp = {function.getValue(t), getTangentVector(function, t)};
                result.add(temp);
            }
            return result;
        } catch (FunctionValueException ex) {
            Logger.getLogger(ParametricCurve3D.class.getName()).log(Level.SEVERE, null, ex);
            return new Vector<R3[]>();
        }
    }

    // STATIC METHODS
    /** Returns tangent vector in direction of x */
    public static R3 getTangentVector(Function<Double, R3> function, double t) throws FunctionValueException {
        return Derivative.approximateDerivative(function, t, .001);
    }

    // DECORATIONS
    /** Returns collection of tangent vectors */
    public CurveTangents getTangentVectors() {
        return new CurveTangents();
    }

    /** Draws tangent vectors on the curve.
     */
    public class CurveTangents extends Plottable<Euclidean3> implements Decoration<Euclidean3, ParametricCurve3D> {

        @Override
        public void paintComponent(Graphics2D g, Euclidean3 v) {
            g.setColor(getColor());
            v.drawArrows(g, getSampleVectors(10), 5.0);
        }

        public void setParent(ParametricCurve3D parent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ParametricCurve3D getParent() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
