/**
 * IterativeGraphLayoutService.java
 * Created Jan 16, 2016
 */
package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service used for executing iterative graph layouts on a background thread.
 * The "runOneIteration" method is assumed to be run from a background or alternate thread.
 */
class IterativeGraphLayoutService extends AbstractScheduledService {

    //<editor-fold defaultstate="collapsed" desc="CONSTANTS">
    
    private static final Logger LOG = Logger.getLogger(IterativeGraphLayoutService.class.getName());
    
    /** Default time between layout iterations. */
    private static final int DEFAULT_ITER_DELAY = 10;        
    
    //</editor-fold>
    
    /** Delay between loops */
    private final int loopDelay = DEFAULT_ITER_DELAY;
    
    private final IterativeGraphLayoutManager manager;

    IterativeGraphLayoutService(IterativeGraphLayoutManager mgr) {
        this.manager = mgr;
        addListener(new Listener() {
            @Override
            public void failed(Service.State from, Throwable failure) {
                LOG.log(Level.SEVERE, "Layout service failed", failure);
            }
        }, MoreExecutors.sameThreadExecutor());
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    boolean isLayoutActive() {
        return isRunning();
    }
    
    //</editor-fold>

    @Override
    protected synchronized void runOneIteration() throws Exception {
        try {
            manager.runOneLoop();
        } catch (InterruptedException x) {
            LOG.log(Level.FINE, "Background layout interrupted", x);
            // restore interrupt after bypassing update
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, loopDelay, TimeUnit.MILLISECONDS);
    }

}
