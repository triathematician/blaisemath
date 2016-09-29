/**
 * GestureOrchestrator.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.util.Configurer;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
 *   Manages a priority stack of mouse gestures, maintains an active gesture, and
 *   passes mouse events to that gesture. When the active gesture changes,
 *   the old one is canceled. Calling {@link #finishActiveGesture()} will finalize
 *   the current gesture. When "turning off" a gesture, the active gesture will
 *   change to the default gesture.
 * </p>
 * <p>
 *   This is intended for use on a {@link GestureLayerUI}, which intercepts the
 *   mouse events for the component, and passes them along to the orchestrator,
 *   which passes them along to the appropriate delegate. This class maintains
 *   a reference to the component being managed, and provides a shortcut
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
public final class GestureOrchestrator<V extends Component> {

    private static final Logger LOG = Logger.getLogger(GestureOrchestrator.class.getName());
    
    public static final String ACTIVE_GESTURE_PROP = "activeGesture";

    /** The component managed by the orchestrator */
    private final V component;
    /** Configuration map, may be used for finalizing gestures */
    private final Map<Class,Configurer> configs = Maps.newHashMap();
    
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
        component.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("press: "+e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("release: "+e);
            }
        });
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
    public void setActiveGesture(@Nullable MouseGesture g) {
        if (activeGesture != g) {
            MouseGesture old = activeGesture;
            if (old != null && old.isActive()) {
                cancelGesture(old);
            }
            if (g == null) {
                activeGesture = null;
            } else {
                activeGesture = tryActivate(g);
            }
            pcs.firePropertyChange(ACTIVE_GESTURE_PROP, old, activeGesture);
        }
    }
    
    private MouseGesture tryActivate(MouseGesture g) {
        boolean activated = g.isActive();
        MouseGesture res = g;
        if (activated) {
            LOG.log(Level.INFO, "Gesture already activated: {0}", g);
        } else {
            activated = activateGesture(g);
            LOG.log(Level.INFO, "Gesture activated: {0}", g);
        }
        if (!activated) {
            res = null;
            LOG.log(Level.WARNING, "Gesture activation failed: {0}", g);
        }
        return res;
    }
    
    //</editor-fold>
    
    /**
     * Get the current active gesture, or search for a new one if none are available.
     * @param e the event
     * @return active gesture for the event, or null if none claim
     */
    @Nullable
    MouseGesture delegateFor(MouseEvent e) {
        return activeGesture;
    }
    
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
    
    /**
     * Completes the currently active gesture.
     */
    public void completeActiveGesture() {
        completeGesture(activeGesture);
        setActiveGesture(null);
    }
    
    /**
     * Cancels the currently active gesture.
     */
    public void cancelActiveGesture() {
        setActiveGesture(null);
    }

    //<editor-fold defaultstate="collapsed" desc="GESTURE UTILS">

    /**
     * Attempt to activate the given gesture.
     * @param mg gesture to activate
     * @return true if activation succeeded
     */
    private boolean activateGesture(MouseGesture mg) {
        checkArgument(activeGesture == null || activeGesture == mg);
        checkNotNull(mg);
        return mg.activate();
    }
    
    /**
     * Completes the provided gesture, if it is in an active state.
     * Null arguments have no effect
     * @param gesture the gesture to cancel
     */
    private void completeGesture(MouseGesture gesture) {
        checkArgument(gesture != null && gesture == activeGesture);
        LOG.log(Level.INFO, "Completing gesture {0}", gesture.getName());
        gesture.complete();
    }

    /**
     * Cancels the provided gesture, if it is in an active state.
     * @param gesture the gesture to cancel
     */
    private void cancelGesture(MouseGesture gesture) {
        checkArgument(gesture != null && gesture == activeGesture);
        LOG.log(Level.INFO, "Canceling gesture {0}", gesture.getName());
        gesture.cancel();
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
