/**
 * GestureOrchestrator.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

import com.google.common.annotations.Beta;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.googlecode.blaisemath.util.Configurer;
import java.awt.Component;
import java.awt.Cursor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Deque;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 - 2016 Elisha Peterson
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


/**
 * <p>
 *   Manages a collection of mouse gestures, maintains an active gesture, and
 *   passes mouse events to that gesture. When the active gesture changes,
 *   the old one is canceled. Calling {@link #finishActiveGesture()} will finalize
 *   the current gesture. When "turning off" a gesture, the active gesture will
 *   change to the default gesture.
 * </p>
 * <p>
 *   Maintains a reference to the component being managed, and provides a shortcut
 *   for updating the component's cursor.
 * </p>
 * <p>
 *   A table of configuration objects of type {@link Configurer} is also maintained
 *   by the orchestrator, allowing gestures to perform additional configuration
 *   after they are finalized.
 * </p>
 * 
 * @param <V> one of the super types of the view component
 * 
 * @author elisha
 */
@Beta
public final class GestureOrchestrator<V extends Component> {

    private static final Logger LOG = Logger.getLogger(GestureOrchestrator.class.getName());
    
    public static final String ACTIVE_GESTURE_PROP = "activeGesture";

    /** The component managed by the orchestrator */
    private final V component;
    /** Configuration map, may be used for finalizing gestures */
    private final Map<Class,Configurer> configs = Maps.newHashMap();
    
    /** The gesture priority stack. The first is the highest priority gesture. */
    private final Deque<MouseGesture> gestures = Queues.newArrayDeque();
    /** The currently active gesture */
    @Nullable
    private MouseGesture activeGesture = null;

    /** Handles property listening */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Initialize the orchestrator with the given component.
     * @param component component
     */
    public GestureOrchestrator(V component) {
        this.component = component;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    /**
     * Get the component related to the gestures.
     * @return component
     */
    public V getComponent() {
        return component;
    }

    /**
     * Convenience method to set the cursor for the component.
     * @param cursor the cursor id to use
     * 
     * @see Component#setCursor(java.awt.Cursor)
     * @see Cursor#getPredefinedCursor(int)
     */
    public void setComponentCursor(int cursor) {
        component.setCursor(Cursor.getPredefinedCursor(cursor));
    }

    /**
     * Adds a gesture to the list of available gestures. It is added to the
     * highest priority point.
     * @param g the gesture to add
     */
    public void addGesture(MouseGesture g) {
        if (!gestures.contains(g)) {
            gestures.addFirst(g);
        }
    }
    
    /**
     * Gets the priority stack of gestures.
     * @return gestures in priority stack
     */
    public Deque<MouseGesture> getGestureStack() {
        return gestures;
    }

    /**
     * Sets the gesture stack, canceling the active gesture if there is one.
     * @param gestures gestures in priority order
     */
    public void setGestureStack(Iterable<MouseGesture> gestures) {
        cancel(activeGesture);
        this.gestures.clear();
        Iterables.addAll(this.gestures, gestures);
    }
    
    /**
     * Gets the currently active gesture.
     * @return active gesture, or null if there is none
     */
    public MouseGesture getActiveGesture() {
        return activeGesture;
    }
    
    /**
     * Changes the active gesture, propagates events. Intended for internal use
     * only, to propagate events.
     * @param g new active gesture
     */
    private void setActiveGesture(MouseGesture g) {
        if (activeGesture != g) {
            MouseGesture old = activeGesture;
            activeGesture = g;
            pcs.firePropertyChange(ACTIVE_GESTURE_PROP, old, activeGesture);
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SUPPLEMENTAL CONFIGURATION API">

    /**
     * Add a configurer object for gestures returning objects of the given type.
     * @param <T> type of object being configured
     * @param cls class of object being configured
     * @param cfg configurer for the object type
     */
    public <T> void addConfigurer(Class<? super T> cls, Configurer<T> cfg) {
        checkNotNull(cls);
        checkNotNull(cfg);
        configs.put(cls, cfg);
    }

    /**
     * Retrieve a configurer object for the given object type
     * @param <T> type of object being configured
     * @param cls class of object being configured
     * @return configurer for the object type
     */
    @Nullable
    public <T> Configurer<T> getConfigurer(Class<? super T> cls) {
        checkNotNull(cls);
        return configs.get(cls);
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GESTURE CONTROLS">

    /**
     * Attempt to activate the given gesture.
     * @param mg gesture to activate
     * @return true if activation succeeded
     */
    public boolean activate(MouseGesture mg) {
        if (activeGesture != mg && mg.activate()) {
            LOG.log(Level.INFO, "Activating gesture {0}", mg);
            setActiveGesture(mg);
            return true;
        }
        return false;
    }

    /**
     * Cancels the provided gesture, if it is in an active state.
     * @param gesture the gesture to cancel
     */
    public void cancel(@Nullable MouseGesture gesture) {
        if (gesture == null) {
            // do nothing
        } else if (activeGesture == gesture) {
            LOG.log(Level.INFO, "Canceling gesture {0}", gesture);
            activeGesture.cancel();
            setActiveGesture(null);
        } else {
            LOG.log(Level.WARNING, "Attempt to cancel a gesture failed because it was not active: {0}", gesture);
        }
    }
    
    /**
     * Completes the provided gesture, if it is in an active state.
     * Null arguments have no effect
     * @param gesture the gesture to cancel
     */
    public void complete(@Nullable MouseGesture gesture) {
        if (gesture == null) {
            // do nothing
        } else if (activeGesture == gesture) {
            LOG.log(Level.INFO, "Completing gesture {0}", gesture);
            activeGesture.complete();
            setActiveGesture(null);
        } else {
            LOG.log(Level.WARNING, "Attempted to complete a gesture failed because it was not active: {0}", gesture);
        }
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE LISTENING">
    //
    // PROPERTY CHANGE LISTENING
    //
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }
    
    public void addPropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(string, pl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }
    
    public void removePropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(string, pl);
    }

    //</editor-fold>
    
}
