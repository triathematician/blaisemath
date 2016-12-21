/**
 * GestureOrchestrator.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.googlecode.blaisemath.util.event.MouseEvents;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
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
 * Helps components delegate mouse events to mouse handlers.
 *
 * Mouse handlers are maintained as "gestures" in a stack. The order of the
 * stack helps determine how events are handled -- those at the top
 * get first "dibs" on events, and if they decline to use it the event is
 * passed down the stack.
 * 
 * Once a handler starts receiving events, it becomes the "active" gesture.
 * The active gesture gets first call on any subsequent events. If it passes
 * on an event, and some other handler does not, then the active gesture is
 * notified, allowing it to "complete", "cancel", or do nothing in response.
 * In the first two cases, the active gesture slot is then vacated, allowing
 * a new gesture to become the active one. Active gestures also have a chance
 * to update the current cursor.
 * 
 * Some gestures are intended to be temporary, and removed from the stack after
 * they are completed/canceled.
 * 
 * The orchestrator is designed for use with a {@link GestureLayerUI}, which
 * delegates the appropriate events to the orchestrator.
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
    /** Stack of gestures. The first is the bottom, the last is the top. */
    private final Deque<MouseGesture> gestures = Queues.newArrayDeque();

    /** Currently active gesture */
    private MouseGesture activeGesture;
    
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
                processEvent(e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                processEvent(e);
            }
        });
    }
    
    /** 
     * Processes the event, changing the active gesture if necessary, and
     * passing the event to appropriate listener.
     */
    void processEvent(InputEvent e) {
        if (e.getID() != MouseEvent.MOUSE_MOVED) {
            LOG.log(Level.INFO, "Processing event with ag = {0}, {1}", new Object[]{activeGesture, e});
        }
        MouseGesture old = activeGesture;
        if (old != null && old.shouldHandle(e)) {
            delegateEvent(e, activeGesture);
        } else if (old != null && !old.cancelsWhen(e)) {
            delegateEvent(e, topGestureFor(e).orElse(null));
        } else {
            cancelActiveGesture();
            activeGesture = tryActivate(topGestureFor(e));
            delegateEvent(e, activeGesture);
            pcs.firePropertyChange(P_ACTIVE_GESTURE, old, activeGesture);
        }
    }
    
    /** Passes the given event to the provided gesture. */
    private void delegateEvent(InputEvent e, @Nullable MouseGesture g) {
        if (g == null) {
            return;
        }
        if (e.getID() != MouseEvent.MOUSE_MOVED) {
            LOG.log(Level.INFO, "Delegating event to g = {0}, {1}", new Object[]{g, e});
        }
        int id = e.getID();
        if (e instanceof KeyEvent) {
            // XXX - todo
        } else if (id == MouseEvent.MOUSE_DRAGGED || id == MouseEvent.MOUSE_MOVED) {
            MouseEvents.delegateMotionEvent((MouseEvent) e, g);
        } else if (e instanceof MouseWheelEvent) {
            MouseEvents.delegateWheelEvent((MouseWheelEvent) e, g);
        } else if (e instanceof MouseEvent) {
            MouseEvents.delegateEvent((MouseEvent) e, g);
        }
    }
    
    @Nullable
    private static MouseGesture tryActivate(Optional<MouseGesture> optGesture) {
        if (!optGesture.isPresent()) {
            return null;
        }
        MouseGesture gesture = optGesture.get();
        LOG.log(Level.INFO, "Activating gesture {0}", gesture.getName());
        boolean success = gesture.activate();
        if (success) {
            return gesture;
        } else {
            LOG.log(Level.INFO, " ** Activation FAILED.", gesture.getName());
            return null;
        }
    }
    
    /** 
     * Cancel the active gesture if possible, and remove it from the stack.
     * @return true if successfully canceled
     */
    public boolean cancelActiveGesture() {
        if (activeGesture != null && activeGesture.cancel()) {
            LOG.log(Level.INFO, "Canceled gesture {0}", activeGesture.getName());
            gestures.remove(activeGesture);
            return true;
        }
        return false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES/MUTATORS">

    /**
     * Get the component related to the gestures.
     * @return component
     */
    public V getComponent() {
        return component;
    }
    
    int getComponentCursor() {
        return component.getCursor().getType();
    }

    /**
     * Convenience method to set the cursor for the component.
     * @param cursor the cursor id to use
     * 
     * @see Component#setCursor(java.awt.Cursor)
     * @see Cursor#getPredefinedCursor(int)
     */
    void setComponentCursor(int cursor) {
        component.setCursor(Cursor.getPredefinedCursor(cursor));
    }

    @Nullable
    public MouseGesture getActiveGesture() {
        return activeGesture;
    }
    
    /** 
     * Adds a gesture to the stack.
     * @param gesture to add
     */
    public void addGesture(MouseGesture gesture) {
        checkNotNull(gesture);
        gestures.add(gesture);
    }
    
    /**
     * Removes a gesture from the stack.
     * @param gesture to remove
     * @return true if removed
     */
    public boolean removeGesture(MouseGesture gesture) {
        checkNotNull(gesture);
        if (gestures.remove(gesture)) {
            if (gesture == activeGesture) {
                cancelActiveGesture();
            }
            return true;
        }
        return false;
    }
    
    //</editor-fold>
    
    /**
     * Get the current active gesture, or search for a new one if none are available.
     * @param e the event
     * @return active gesture for the event, or null if none claim
     */
    Optional<MouseGesture> topGestureFor(InputEvent e) {
        return topMatch(gestures, g -> g.shouldHandle(e));
    }
    
    /** Get the first match starting from the top of the deque. */
    private static <X> Optional<X> topMatch(Deque<X> deque, Predicate<X> condition) {
        List<X> reverse = Lists.newArrayList(deque.descendingIterator());
        return reverse.stream()
                .filter(condition)
                .findFirst();
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
