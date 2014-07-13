/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.prvis;

/*
 * #%L
 * BlaiseWidgets
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


import static com.google.common.base.Preconditions.*;
import com.google.common.collect.Lists;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.googlecode.blaisemath.prvis.Units.Distance;
import com.googlecode.blaisemath.prvis.Units.Duration;
import com.googlecode.blaisemath.prvis.Units.Speed;

/**
 *
 * @author Elisha
 */
public class PrModel {
    
    /** Raw data - list of entries */
    private final List<PrEntry> entries = Lists.newArrayList();
    
    /** Min and max dates in list */
    private transient Date minDate = null, maxDate = null;
    /** Min and max distances in list */
    private transient Distance minDistance = null, maxDistance = null;
    /** Min and max durations in list */
    private transient Duration minDuration = null, maxDuration = null;
    /** Min and max speeds in list */
    private transient Speed minSpeed = null, maxSpeed = null;
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public List<PrEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    public void setEntries(List<PrEntry> entries) {
        if (this.entries != entries) {
            this.entries.clear();
            this.entries.addAll(entries);
            updateProperties();
            pcs.firePropertyChange("entries", null, this.entries);
        }
    }
    
    //</editor-fold>
    
    
    /** Update properties of the set of entries... min/max distances, times. */
    private void updateProperties() {
        //<editor-fold defaultstate="collapsed" desc="update mins and maxs of data set">
        if (entries.isEmpty()) {
            minDate = maxDate = null;
            minDistance = maxDistance = null;
            minDuration = maxDuration = null;
            minSpeed = maxSpeed = null;
        } else {
            PrEntry first = entries.get(0);
            minDate = maxDate = first.getDate();
            minDistance = maxDistance = first.getDistance();
            minDuration = maxDuration = first.getDuration();
            minSpeed = maxSpeed = first.getSpeed();
            
            for (int i = 1; i < entries.size(); i++) {
                PrEntry entry = entries.get(i);
                // update dates
                minDate = entry.getDate().before(minDate) ? entry.getDate() : minDate;
                maxDate = entry.getDate().after(maxDate) ? entry.getDate() : maxDate;
                // update distances
                minDistance = entry.getDistance().compareTo(minDistance) < 0 ? entry.getDistance() : minDistance;
                maxDistance = entry.getDistance().compareTo(maxDistance) > 0 ? entry.getDistance() : maxDistance;
                // update durations
                minDuration = entry.getDuration().compareTo(minDuration) < 0 ? entry.getDuration(): minDuration;
                maxDuration = entry.getDuration().compareTo(maxDuration) > 0 ? entry.getDuration() : maxDuration;
                // update speeds
                minSpeed = entry.getSpeed().compareTo(minSpeed) < 0 ? entry.getSpeed(): minSpeed;
                maxSpeed = entry.getSpeed().compareTo(maxSpeed) > 0 ? entry.getSpeed() : maxSpeed;
            }
        }
        //</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="COMPUTED PROPERTIES">
    //
    // COMPUTED PROPERTIES
    //
    
    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public Distance getMinDistance() {
        return minDistance;
    }

    public Distance getMaxDistance() {
        return maxDistance;
    }

    public Duration getMinDuration() {
        return minDuration;
    }

    public Duration getMaxDuration() {
        return maxDuration;
    }

    public Speed getMinSpeed() {
        return minSpeed;
    }

    public Speed getMaxSpeed() {
        return maxSpeed;
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE LISTENING">
    //
    // PROPERTY CHANGE LISTENING
    //
    
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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
    //
    // INNER CLASSES
    //
    
    public static class PrEntry {
        private final Date date;
        private final Distance distance;
        private final Duration duration;
        private transient Speed speed;

        public PrEntry(Date date, Distance distance, Duration duration) {
            checkArgument(date != null);
            checkArgument(distance != null && distance.getValue() > 0);
            checkArgument(duration != null && duration.getValue() > 0);
            this.date = date;
            this.distance = distance;
            this.duration = duration;
            this.speed = Units.speed(distance, duration);
        }
        
        @Override
        public String toString() {
            return date + " ~ " + distance + ", " + duration + ", " + speed;
        }

        public Date getDate() {
            return date;
        }

        public Distance getDistance() {
            return distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public Speed getSpeed() {
            return speed;
        }
    }
    
    //</editor-fold>
    
    
    
}
