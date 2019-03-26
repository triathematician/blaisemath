/*
 * SpringLayoutParameters.java
 * Created May 13, 2010
 */
package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.graph.layout.GraphLayoutConstraints;

/** 
 * Parameters of the SpringLayout algorithm
 */
public class SpringLayoutParameters {
    
    /** Default distance scale */
    public static final int DEFAULT_DIST_SCALE = 50;
    
    /** Desired distance between nodes */
    double distScale = DEFAULT_DIST_SCALE;
    /** Global attractive constant (keeps vertices closer to origin) */
    double globalC = 1;
    /** Attractive constant */
    double springC = .1;
    /** Natural spring length */
    double springL = .5 * distScale;
    /** Repelling constant */
    double repulsiveC = distScale * distScale;
    /** Damping constant, reducing node speeds */
    double dampingC = 0.7;
    /** Time step per iteration */
    double stepT = 1;
    /** The maximum speed (movement per unit time) */
    double maxSpeed = 10 * distScale;
    /** Distance outside which global force acts */
    double minGlobalForceDist = distScale;
    /** Maximum force that can be applied between nodes */
    double maxForce = distScale * distScale / 100;
    /** Min distance between nodes */
    double minDist = distScale / 100;
    /** Max distance to apply repulsive force */
    double maxRepelDist = 2 * distScale;
    
    /** Layout constraints */
    GraphLayoutConstraints constraints = new GraphLayoutConstraints();
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //
    
    public double getDistScale() {
        return distScale;
    }

    public void setDistScale(double distScale) {
        this.distScale = distScale;
        springL = .5 * distScale;
        repulsiveC = distScale * distScale;
        maxSpeed = 10 * distScale;
        minGlobalForceDist = distScale;
        maxForce = distScale * distScale / 100;
        minDist = distScale / 100;
        maxRepelDist = 2 * distScale;
    }

    public double getDampingConstant() {
        return dampingC;
    }

    public void setDampingConstant(double dampingC) {
        this.dampingC = dampingC;
    }

    public double getGlobalForce() {
        return globalC;
    }

    public void setGlobalForce(double globalC) {
        this.globalC = globalC;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getRepulsiveForce() {
        return repulsiveC;
    }

    public void setRepulsiveForce(double repulsiveC) {
        this.repulsiveC = repulsiveC;
    }

    public double getSpringForce() {
        return springC;
    }

    public void setSpringForce(double springC) {
        this.springC = springC;
    }

    public double getSpringLength() {
        return springL;
    }

    public void setSpringLength(double springL) {
        this.springL = springL;
    }

    public double getStepTime() {
        return stepT;
    }

    public void setStepTime(double stepT) {
        this.stepT = stepT;
    }

    public GraphLayoutConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(GraphLayoutConstraints constraints) {
        this.constraints = checkNotNull(constraints);
    }
    
    //</editor-fold>
    
}
