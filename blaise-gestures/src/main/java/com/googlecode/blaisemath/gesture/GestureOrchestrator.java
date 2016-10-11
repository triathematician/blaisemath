/**
 * GestureOrchestrator.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.googlecode.blaisemath.util.Configurer;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
 *   Manages a stack of mouse gestures (handlers), where the one on the top of
 *   the stack is considered to be the active gesture.
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
 *   when they are completed.
 * </p>
 * 
 * @param <V> one of the super types of the view component
 * 
 * @author elisha
 */
public final class GestureOrchestrator<V extends Component> {

    private static final Logger LOG = Logger.getLogger(GestureOrchestrator.class.getName());
    
    public static final String P_ACTIVE_GESTURE = "activeGesture";

    /** The component managed by the orchestrator */
    private final V component;
    /** Configuration map, may be used for finalizing gestures */
    private final Map<Class,Configurer> configs = Maps.newHashMap();
    
    /** The stack of gestures */
    private final Deque<MouseGesture> gestures = Queues.newArrayDeque();
    /** The default gesture. This cannot be removed from the stack */
    private MouseGesture defaultGesture;
    /** Handles property listening */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Initialize the orchestrator with the given component.
     * @param component component
     */
    public GestureOrchestrator(V component) {
        this.component = component;
        component.setFocusable(true);
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
        return gestures.peekFirst();
    }
    
    /**
     * Sets the default gesture, adding it to the bottom of the stack. This
     * cannot be removed from the stack. If there was an existing default
     * gesture, it will be removed from the stack.
     * @param g the default gesture (or null to reset default)
     */
    public void setDefaultGesture(@Nullable MouseGesture g) {
        if (g == null) {
            if (defaultGesture != null) {
                removeGestureFromStack(defaultGesture);
            }
        }
        this.defaultGesture = g;
        if (g != null) {
            addOrMoveGestureToBottomOfStack(g);
            activateTopGesture();
        }
    }

    /** Registers a gesture to be automatically activated for certain key events */
    public void addKeyGesture(int key, MouseGesture gesture) {
        // TODO
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="GESTURE API">
    
    /** 
     * Activates the provided gesture, if not already on top of the stack.
     * @param gesture gesture to activate
     */
    public void activateGesture(MouseGesture gesture) {
        checkArgument(gesture != null);
        LOG.log(Level.INFO, "Activating gesture {0}", gesture.getName());
        boolean activated = gesture.isActive();
        if (activated) {
            LOG.log(Level.INFO, "Gesture already activated: {0}", gesture);
        } else {
            activated = gesture.activate();
            LOG.log(Level.INFO, "Gesture activated: {0}", gesture);
        }
        if (activated) {
            addGestureToTopOfStack(gesture);
        } else {
            LOG.log(Level.WARNING, "Gesture activation failed: {0}", gesture);
        }
    }
    
    /**
     * Completes the currently active gesture and removes it from the gesture stack.
     */
    public void completeActiveGesture() {
        completeGesture(getActiveGesture());
        activateTopGesture();
    }
    
    /**
     * Cancels the currently active gesture and removes it from the gesture stack.
     */
    public void cancelActiveGesture() {
        cancelGesture(getActiveGesture());
        activateTopGesture();
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

    //<editor-fold defaultstate="collapsed" desc="GESTURE UTILS">
    
    /** Activates the top gesture, if not already active */
    private boolean activateTopGesture() {
        MouseGesture g = getActiveGesture();
        if (g != null && !g.isActive()) {
            return g.activate();
        }
        return false;
    }
    
    /**
     * Completes the provided gesture, if it is in an active state.
     * Null arguments have no effect
     * @param gesture the gesture to cancel
     */
    private void completeGesture(MouseGesture gesture) {
        checkArgument(gesture != null);
        LOG.log(Level.INFO, "Completing gesture {0}", gesture.getName());
        gesture.complete();
        removeGestureFromStack(gesture);
    }

    /**
     * Cancels the provided gesture, if it is in an active state.
     * @param gesture the gesture to cancel
     */
    private void cancelGesture(MouseGesture gesture) {
        checkArgument(gesture != null);
        LOG.log(Level.INFO, "Canceling gesture {0}", gesture.getName());
        gesture.cancel();
        removeGestureFromStack(gesture);
    }
    
    /** Add gesture to top of stack. */
    private void addGestureToTopOfStack(MouseGesture g) {
        MouseGesture oldActive = getActiveGesture();
        gestures.addFirst(g);
        MouseGesture newActive = gestures.peekFirst();
        pcs.firePropertyChange(P_ACTIVE_GESTURE, oldActive, newActive);
    }
    
    /** Adds or moves gesture to bottom of stack. */
    private void addOrMoveGestureToBottomOfStack(MouseGesture g) {
        MouseGesture oldActive = getActiveGesture();
        gestures.remove(g);
        gestures.addLast(g);
        MouseGesture newActive = getActiveGesture();
        pcs.firePropertyChange(P_ACTIVE_GESTURE, oldActive, newActive);
    }
    
    /** Remove gesture from stack, unless it is the default gesture. */
    private void removeGestureFromStack(MouseGesture g) {
        if (g == defaultGesture) {
            return;
        }
        MouseGesture oldActive = getActiveGesture();
        gestures.remove(g);
        MouseGesture newActive = getActiveGesture();
        pcs.firePropertyChange(P_ACTIVE_GESTURE, oldActive, newActive);
    }
    
    //</editor-fold>
    
    /**
     * Get the current active gesture, or search for a new one if none are available.
     * @param e the event
     * @return active gesture for the event, or null if none claim
     */
    @Nullable
    MouseGesture delegateFor(MouseEvent e) {
        return getActiveGesture();
    }

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
