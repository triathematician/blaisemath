/**
 * GestureOrchestrator.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.util.Configurer;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import javax.annotation.Nullable;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 Elisha Peterson
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
 * Manages the state of an active gesture. When the active gesture changes,
 * the old one is canceled. Calling {@link #finishActiveGesture()} will finalize
 * the current gesture. When "turning off" a gesture, the active gesture will
 * change to the default gesture.
 * </p>
 * <p>
 * For convenience, a reference to a component is maintained, as this is useful
 * for certain gestures.
 * </p>
 * <p>
 * A table of configuration objects of type {@link Configurer} is also maintained
 * by the orchestrator, allowing gestures to perform additional configuration
 * after they are finalized.
 * </p>
 * 
 * TODO - consider removing reference to component
 * 
 * @author elisha
 */
public class GestureOrchestrator {
    
    public static final String ACTIVE_GESTURE_PROP = "activeGesture";

    /** The component managed by the orchestrator */
    protected final Component component;
    /** Configuration map, may be used for finalizing gestures */
    protected final Map<Class,Configurer> configs = Maps.newHashMap();
    
    /** The default mouse gesture */
    protected MouseGesture defaultGesture = null;
    /** The currently active gesture */
    protected MouseGesture activeGesture = null;

    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Initialize the orchestrator with the given component.
     * @param component component
     */
    public GestureOrchestrator(Component component) {
        this.component = component;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    /**
     * Get the component related to the gestures
     * @return component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Get the default gesture
     * @return default gesture
     */
    @Nullable
    public MouseGesture getDefaultGesture() {
        return defaultGesture;
    }

    /**
     * Set the default gesture. This will update the active gesture if the
     * active gesture is null.
     * @param defaultGesture default gesture
     */
    public void setDefaultGesture(@Nullable MouseGesture defaultGesture) {
        this.defaultGesture = defaultGesture;
        if (activeGesture == null && defaultGesture != null) {
            setActiveGesture(defaultGesture);
        }
    }
    
    /**
     * Get the active gesture.
     * @return active gesture
     */
    public MouseGesture getActiveGesture() {
        return activeGesture;
    }
    
    /**
     * Set the active gesture. This will finish any currently active gesture
     * before moving on. If the argument is null the gesture will be set to
     * the default gesture.
     * @param g the new active gesture
     */
    public void setActiveGesture(@Nullable MouseGesture g) {
        if (g == null) {
            g = defaultGesture;
        }
        Object old = this.activeGesture;
        if (this.activeGesture != g) {
            if (this.activeGesture != null) {
                this.activeGesture.cancel();
            }
            this.activeGesture = g;
            if (this.activeGesture != null) {
                this.activeGesture.initiate();
            }
            pcs.firePropertyChange(ACTIVE_GESTURE_PROP, old, this.activeGesture);
        }
    }
    
    //</editor-fold>
    
    /**
     * Called by gestures to yield control.
     * @param g the gesture yielding control
     */
    public void finishGesture(MouseGesture g) {
        if (activeGesture != null) {
            activeGesture.finish();
            setActiveGesture(null);
        }
    }

    /**
     * Call to finish the active gesture.
     */
    public void finishActiveGesture() {
        if (activeGesture != null) {
            finishGesture(activeGesture);
        }
    }

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
