/**
 * AnimationStep.java
 * Created Jul 12, 2014
 */
package com.googlecode.blaisemath.util.animation;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a single step of logic in a basic animation. A static method is
 * provided to launch a basic thread to do the animation.
 * @author Elisha
 */
public abstract class AnimationStep {
    
    /** The executor service to use for animations */
    private static ScheduledExecutorService ANIMATION_EXECUTOR_SERVICE;
    
    /**
     * Execute logic for one step in the animation.
     * @param idx the index of the step
     * @param pct percent completion, between 0 and 1
     */
    public abstract void run(int idx, double pct);
    
    /**
     * Get executor service used for animations launched from this class
     * @return executor service
     */
    public static ScheduledExecutorService getExecutorService() {
        if (ANIMATION_EXECUTOR_SERVICE == null) {
            ANIMATION_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
        }
        return ANIMATION_EXECUTOR_SERVICE;
    }
    
    /**
     * Launch a thread to perform the given animation step. All indices between
     * min and max, inclusive, will be passed to the step. The steps will be
     * executed on fixed delay, meaning each step must complete before the next starts.
     * @param min minimum index to pass to the step
     * @param max maximum index to pass to the step
     * @param delay how long to wait between steps
     * @param unit unit for delay
     * @param step the step logic to execute
     */
    public static void animate(final int min, final int max, int delay, TimeUnit unit, final AnimationStep step) {
        ScheduledExecutorService svc = getExecutorService();
        NRunnable run = new NRunnable(min, max, step);
        run.runNTimes(svc, delay, unit);
    }
    
    /** Runnable that cancels after a given number of steps */
    private static class NRunnable implements Runnable {
        private final int min;
        private final int max;
        private final AnimationStep step;
        
        private int runStep = 0;
        private volatile ScheduledFuture<?> self;

        public NRunnable(int min, int max, AnimationStep step) {
            checkArgument(max >= min);
            checkNotNull(step);
            this.min = min;
            this.max = max;
            this.step = step;
        }

        @Override
        public void run() {
            double pct = min == max ? 1 : (runStep-min)/(double)(max-min);
            step.run(runStep, pct);
            if (runStep == max) {
                self.cancel(true);
            }
            runStep++;
        }
        
        public void runNTimes(ScheduledExecutorService executor, long period, TimeUnit unit) {
            checkState(self == null);
            runStep = min;
            self = executor.scheduleWithFixedDelay(this, 0, period, unit);
        }
    }
    
}
