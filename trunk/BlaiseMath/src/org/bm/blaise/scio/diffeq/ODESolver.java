/**
 * ODESolver.java
 * Created on Sep 25, 2009
 */

package org.bm.blaise.scio.diffeq;

import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.nonstiff.*;

/**
 * <p>
 *   <code>ODESolver</code> is an enum class for selecting between
 *   various ODE solution techniques.
 * </p>
 *
 * @author Elisha Peterson
 */
public enum ODESolver {
    /** Retrieves the Euler solution method. */
    EULER("Euler") {
        public FirstOrderIntegrator getIntegrator(double stepSize) {
            return new EulerIntegrator(stepSize);
        }
    },
    /** Retrieves the midpoint solution method. */
    MIDPOINT("Midpoint") {
        public FirstOrderIntegrator getIntegrator(double stepSize) {
            return new MidpointIntegrator(stepSize);
        }
    },
    /** Retrieves the classical Runge-Kutta solution method. */
    RUNGE_KUTTA("Runge-Kutta") {
        public FirstOrderIntegrator getIntegrator(double stepSize) {
            return new ClassicalRungeKuttaIntegrator(stepSize);
        }
    },
    /** Retrieves the Gill solution method. */
    GILL("Gill") {
        public FirstOrderIntegrator getIntegrator(double stepSize) {
            return new GillIntegrator(stepSize);
        }
    },
    /** Retrieves the 3/8 solution method. */
    THREE_EIGHTHS("3/8") {
        public FirstOrderIntegrator getIntegrator(double stepSize) {
            return new ThreeEighthesIntegrator(stepSize);
        }
    };

    String name;

    @Override
    public String toString() { return name; }

    /**
     * Returns integrator of the type specified by the enum, with the given step size.
     * @param stepSize difference in time for the ODE solver
     * @return class that can perform the ODE solution
     */
    abstract public FirstOrderIntegrator getIntegrator(double stepSize);

    /** Construct with given name and integrator class. */
    ODESolver(String name) {
        this.name = name;
    }
}
