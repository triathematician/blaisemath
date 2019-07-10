package com.googlecode.blaisemath.util.swing;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkArgument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.util.Objects.requireNonNull;

/**
 * Represents a single step of logic in a basic animation. A static method initializes
 * a swing {@link javax.swing.Timer} to execute the animation step for every value
 * between a min and max iteration index, at which point the timer stops. Note that
 * the animation logic runs on the event dispatch thread.
 * 
 * @see javax.swing.Timer
 * 
 * @author Elisha Peterson
 */
public abstract class AnimationStep {
    
    /**
     * Execute logic for one step in the animation.
     * @param idx the index of the step
     * @param pct percent completion, between 0 and 1
     */
    public abstract void run(int idx, double pct);
    
    /**
     * Launch an animation characterized by the given step. All indices between
     * min and max, inclusive, will be passed to the step. The steps will be
     * executed on fixed delay, meaning each step must complete before the next starts.
     * The events fire on the event dispatch thread.
     * @param min minimum index to pass to the step
     * @param max maximum index to pass to the step
     * @param delay how long to wait between steps, in milliseconds
     * @param step the step logic to execute
     * @return timer executing the animation
     */
    public static javax.swing.Timer animate(int min, int max, int delay, AnimationStep step) {
        AnimationRunner runner = new AnimationRunner(min, max, step);
        return runner.startTimer(delay);
    }
    
    /**
     * Runnable that cancels after a given number of steps. The {@link AnimationStep}
     * is called for every value between a min and a max integer value.
     */
    private static final class AnimationRunner implements ActionListener {
        private final int min;
        private final int max;
        private final AnimationStep step;
        
        private int runStep = 0;
        private javax.swing.Timer timer;

        public AnimationRunner(int min, int max, AnimationStep step) {
            checkArgument(max >= min);
            requireNonNull(step);
            this.min = min;
            this.max = max;
            this.step = step;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            double pct = min == max ? 1 : (runStep-min)/(double)(max-min);
            step.run(runStep, pct);
            if (runStep == max) {
                timer.stop();
            }
            runStep++;
        }
        
        /** 
         * Runs the animation cycle, with the given # of millis between firing.
         * @param period how long between animation steps
         * @return timer executing the animation
         */
        public javax.swing.Timer startTimer(int period) {
            runStep = min;
            timer = new javax.swing.Timer(0, this);
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.setDelay(period);
            timer.start();
            return timer;
        }
    }
    
}
