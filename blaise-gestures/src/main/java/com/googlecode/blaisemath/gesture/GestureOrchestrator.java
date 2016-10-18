/**
 * GestureOrchestrator.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
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
 *   Manages a stack of mouse handlers for a component, where mouse handlers
 *   are coded as {@link MouseGesture}s.
 * </p>
 * <p>
 *   The stack determines how mouse events are processed. Generally, the top
 *   of the stack will be the "active" handler and receive all events. However,
 *   in some cases, e.g. handling {@link MouseWheelEvent}s, some gestures may
 *   choose not to handle the event and pass the event down the stack.
 * </p>
 * <p>
 *   Internally, gestures have three possible modes. <b>Persistent</b> gestures
 *   are always available unless explicitly removed. <b>Transient</b> gestures
 *   are removed from the stack when they are "completed" or "canceled".
 *   <b>Key</b> gestures override other gestures when a certain key is pressed.
 * </p>
 * <p>
 *   This is intended for use on a {@link GestureLayerUI}, which intercepts the
 *   mouse events for the component, and passes them along to the orchestrator,
 *   which passes them along to the appropriate delegate. This class maintains
 *   a reference to the component being managed, and provides a shortcut
 *   for updating the component's cursor.
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
    
    /** Stack of gesture configs */
    private final Deque<MouseGestureConfig> gestures = Queues.newArrayDeque();
    
    /** Current active gesture */
    private MouseGestureConfig activeGesture;
    /** Current depressed key */
    private Integer key;
    
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
                key = e.getID();
                updateActiveGesture();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                key = null;
                updateActiveGesture();
            }
        });
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES/MUTATORS">

    /**
     * Get the component related to the gestures.
     * @return component
     */
    public V getComponent() {
        return component;
    }
    
    public int getComponentCursor() {
        return component.getCursor().getType();
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
        return activeGesture.getGesture();
    }
    
    /** 
     * Adds a persistent gesture (won't be removed when canceled or completed).
     * @param gesture gesture
     */
    public void addPersistentGesture(MouseGesture gesture) {
        checkNotNull(gesture);
        gestures.add(new MouseGestureConfig(gesture, MouseGestureType.PERSISTENT));
        updateActiveGesture();
    }
    
    /** 
     * Adds a transient gesture (will be removed when canceled or completed).
     * @param gesture gesture
     */
    public void addTransientGesture(MouseGesture gesture) {
        checkNotNull(gesture);
        gestures.add(new MouseGestureConfig(gesture, MouseGestureType.TRANSIENT));
        updateActiveGesture();
    }
    
    /** 
     * Sets a transient gesture, canceling/removing the current active gesture if it is transient.
     * @param gesture gesture
     */
    public void setTransientGesture(MouseGesture gesture) {
        checkNotNull(gesture);
        if (activeGesture.getType() == MouseGestureType.TRANSIENT) {
            cancelGesture(activeGesture.getGesture());
        }
        gestures.add(new MouseGestureConfig(gesture, MouseGestureType.TRANSIENT));
        updateActiveGesture();
    }

    /** 
     * Registers a gesture to be automatically activated for certain key events.
     * @param keyCode code that triggers the gesture
     * @param gesture gesture
     */
    public void addKeyGesture(int keyCode, MouseGesture gesture) {
        checkNotNull(gesture);
        gestures.add(new MouseGestureConfig(gesture, keyCode));
        updateActiveGesture();
    }
    
    //</editor-fold>
    
    /** Cancels the currently active gesture. */
    public void cancelActiveGesture() {
        cancelGesture(activeGesture.getGesture());
        updateActiveGesture();
    }
    
    /** Completes the currently active gesture. */
    public void completeActiveGesture() {
        completeGesture(activeGesture.getGesture());
        updateActiveGesture();
    }
    
    /** Completes the given gesture, if active. */
    void completeGesture(MouseGesture g) {
        if (!g.isActive()) {
            LOG.log(Level.WARNING, "Gesture not active: {0}", g);
        }
        LOG.log(Level.INFO, "Completing gesture: {0}", g);
        g.complete();
        removeTransientGesture(g);
    }
    
    /** Cancels the given gesture, if active. */
    void cancelGesture(MouseGesture g) {
        if (!g.isActive()) {
            LOG.log(Level.WARNING, "Gesture not active: {0}", g);
        }
        LOG.log(Level.INFO, "Canceling gesture: {0}", g);
        g.cancel();
        removeTransientGesture(g);
    }
    
    /**
     * Get the current active gesture, or search for a new one if none are available.
     * @param e the event
     * @return active gesture for the event, or null if none claim
     */
    @Nullable
    MouseGesture delegateFor(MouseEvent e) {
        return getActiveGesture();
    }
    
    /** Updates the active gesture. */
    private void updateActiveGesture() {
        MouseGestureConfig old = activeGesture;
        this.activeGesture = locateActiveGesture();
        if (old != activeGesture) {
            MouseGesture ag = activeGesture.getGesture();
            LOG.log(Level.INFO, "Activating gesture {0}", ag.getName());
            boolean activated = ag.isActive();
            if (activated) {
                LOG.log(Level.INFO, "Gesture already activated: {0}", ag);
            } else {
                activated = ag.activate();
                LOG.log(Level.INFO, "Gesture activation {0}: {1}", 
                        new Object[]{activated ? "succeeded" : "failed", ag});
            }
        }
        pcs.firePropertyChange(P_ACTIVE_GESTURE,
                old == null ? null : old.getGesture(), 
                activeGesture == null ? null : activeGesture.getGesture());
    }
    
    /** Locates the active gesture */
    private MouseGestureConfig locateActiveGesture() {
        Optional<MouseGestureConfig> keyGesture = keyGesture();
        if (keyGesture.isPresent()) {
            return keyGesture.get();
        } else {
            return topMatch(gestures, g -> g.getType() != MouseGestureType.KEY);
        }
    }
    
    /** Get the first match starting from the top of the deque. */
    private <X> X topMatch(Deque<X> deque, Predicate<X> condition) {
        List<X> reverse = Lists.newArrayList(deque.descendingIterator());
        return reverse.stream()
                .filter(condition)
                .findFirst().orElse(null);
    }
    
    private Optional<MouseGestureConfig> keyGesture() {
        if (key == null) {
            return Optional.absent();
        }
        for (MouseGestureConfig gc : gestures) {
            if (gc.getType() == MouseGestureType.KEY 
                    && Objects.equals(gc.getKeyCode(), key)) {
                return Optional.of(gc);
            }
        }
        return Optional.absent();
    }
    
    private void removeTransientGesture(MouseGesture g) {
        Optional<MouseGestureConfig> config = configOf(g);
        if (config.isPresent() && config.get().getType() == MouseGestureType.TRANSIENT) {
            gestures.remove(config.get());
        } else {
            LOG.log(Level.INFO, "Not removing {0}: config was absent or wrong type: {1}", 
                    new Object[]{g, config.orNull()});
        }
        updateActiveGesture();
    }
    
    private Optional<MouseGestureConfig> configOf(MouseGesture g) {
        for (MouseGestureConfig c : gestures) {
            if (c.getGesture() == g) {
                return Optional.of(c);
            }
        }
        return Optional.absent();
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

    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
    private static class MouseGestureConfig {
        private final MouseGesture gesture;
        private final MouseGestureType type;
        private final Integer keyCode;

        private MouseGestureConfig(MouseGesture g, MouseGestureType type) {
            this.gesture = g;
            this.type = type;
            this.keyCode = null;
        }

        private MouseGestureConfig(MouseGesture g, int keyCode) {
            this.gesture = g;
            this.type = MouseGestureType.KEY;
            this.keyCode = keyCode;
        }

        @Override
        public String toString() {
            return "MouseGestureConfig{" + gesture + ", type=" + type + ", keyCode=" + keyCode + '}';
        }

        private MouseGesture getGesture() {
            return gesture;
        }

        private MouseGestureType getType() {
            return type;
        }

        private Integer getKeyCode() {
            return keyCode;
        }
    }
    
    private static enum MouseGestureType {
        /** Gestures that never get removed */
        PERSISTENT,
        /** Gestures that are temporary and removed when completed/canceled */
        TRANSIENT,
        /** Gestures that are only activated when a key is depressed */
        KEY;
    }
    
    //</editor-fold>
    
}
