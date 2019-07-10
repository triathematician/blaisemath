package com.googlecode.blaisemath.coordinate;

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


import java.util.EventObject;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Tracks a change to a set of coordinate locations, in the form of a set of added
 * locations and a set of removed objects. These collections will be propagated as
 * received to listeners; this class makes no guarantees of collection safety.
 * 
 * @param <S> type of object owning the coordinates
 * @param <C> coordinate type
 * @author Elisha Peterson
 */
public final class CoordinateChangeEvent<S,C> extends EventObject {

    /** Added coordinates */
    private Map<S,C> added = null;
    /** Removed coordinates */
    private Set<S> removed = null;

    /** 
     * Initialize with given source object
     * @param src source of event
     */
    public CoordinateChangeEvent(Object src) {
        super(src);
    }

    //region FACTORY METHODS

    /** 
     * Creates add event 
     * @param <S> type of object owning the coordinates
     * @param <C> coordinate type
     * @param src source of event
     * @param added map of added objects, keys are objects/values are coordinates
     * @return add event
     */
    public static <S,C> CoordinateChangeEvent<S,C> createAddEvent(Object src, Map<S,C> added) {
        CoordinateChangeEvent<S,C> evt = new CoordinateChangeEvent<>(src);
        evt.added = added;
        return evt;
    }

    /** 
     * Creates remove event 
     * @param <S> type of object owning the coordinates
     * @param <C> coordinate type
     * @param src source of event
     * @param removed set of removed objects
     * @return remove event
     */
    public static <S,C> CoordinateChangeEvent createRemoveEvent(Object src, Set<S> removed) {
        CoordinateChangeEvent<S,C> evt = new CoordinateChangeEvent<>(src);
        evt.removed = removed;
        return evt;
    }

    /** 
     * Creates add/remove event
     * @param <S> type of object owning the coordinates
     * @param <C> coordinate type
     * @param src source of event
     * @param added map of added objects, keys are objects/values are coordinates
     * @param removed set of removed objects
     * @return add/remove event
     */
    public static <S,C> CoordinateChangeEvent<S,C> createAddRemoveEvent(Object src, Map<S,C> added, Set<S> removed) {
        CoordinateChangeEvent<S,C> evt = new CoordinateChangeEvent<>(src);
        evt.added = added;
        evt.removed = removed;
        return evt;
    }

    //endregion

    @Override
    public String toString() {
        return String.format("CoordinateChangeEvent[%d added,%d removed,source=%s]",
                added == null ? 0 : added.size(), removed == null ? 0 : removed.size(), source);
    }

    /**
     * Whether event indicates added coords 
     * @return true if coordinates were added
     */
    public boolean isAddEvent() {
        return added != null;
    }

    /** 
     * Whether event indicates removed coords 
     * @return true if coordinates were removed
     */
    public boolean isRemoveEvent() {
        return removed != null;
    }

    /**
     * Get the collection of coordinate that were added
     * @return map whose keys are the objects and values are their coordinates
     */
    @Nullable 
    public Map<S,C> getAdded() {
        return added;
    }

    /**
     * Get the collection of objects whose coordinates were removed
     * @return set of objects removed
     */
    @Nullable
    public Set<S> getRemoved() {
        return removed;
    }

}
