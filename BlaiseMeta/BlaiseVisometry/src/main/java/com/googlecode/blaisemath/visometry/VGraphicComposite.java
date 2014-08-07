/**
 * VGraphicComposite.java
 * Created Jan 29, 2011
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.style.StyleContext;

/**
 * Encapsulates a collection of {@link VGraphic}s.
 *
 * @param <C> local coordinate type
 *
 * @see Graphic
 * @see GraphicComposite
 *
 * @author Elisha
 */
public class VGraphicComposite<C,G> extends VGraphicSupport<C,G> {

    /** Stores the local entries */
    protected final List<VGraphic<C,G>> entries = Collections.synchronizedList(new ArrayList<VGraphic<C,G>>());
    /** Stores the window entry */
    protected final GraphicComposite<G> windowEntry = new GraphicComposite<G>();

    /** Flag to prevent events from firing while updating convert */
    private boolean settingConvertFlag = false;

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /**
     * Return visometry
     * @return visometry
     */
    public Visometry<C> getVisometry() {
        return parent.getVisometry();
    }

    /**
     * Return unmodifiable ordered list of graphics.
     * @return list of entries (unmodifiable)
     */
    public Iterable<VGraphic<C,G>> getGraphics() {
        return Collections.unmodifiableList(entries);
    }

    public GraphicComposite<G> getWindowGraphic() {
        return windowEntry;
    }

    /**
     * Return render factory used to draw elements.
     * @return render factory used for drawing
     * @throws IllegalStateException if the object returned would be null
     */
    public StyleContext getStyleContext() {
        return windowEntry.getStyleContext();
    }

    /**
     * Sets default renderer factory for all child entries (may be null)
     * @param rend the renderer provider (may be null)
     */
    public void setStyleContext(StyleContext rend) {
        windowEntry.setStyleContext(rend);
    }
    
    //</editor-fold>
    

    //
    // COMPOSITE METHODS
    //

    /**
     * Add an entry to the composite.
     * @param gfc the entry
     */
    public final void addGraphic(VGraphic<C,G> gfc) {
        if (addHelp(gfc)) {
            fireConversionNeeded();
        }
    }

    /**
     * Remove an entry from the composite
     * @param gfc the entry to remove
     */
    public void removeGraphic(VGraphic<C,G> gfc) {
        if (removeHelp(gfc)) {
            fireConversionNeeded();
        }
    }

    /**
     * Adds several entries to the composite
     * @param add the entries to add
     */
    public void addGraphics(Iterable<? extends VGraphic<C,G>> add) {
        boolean change = false;
        for (VGraphic<C,G> en : add) {
            change = addHelp(en) || change;
        }
        if (change) {
            fireConversionNeeded();
        }
    }

    /**
     * Removes several entries from the composite
     * @param remove the entries to remove
     */
    public final void removeGraphics(Iterable<? extends VGraphic<C,G>> remove) {
        boolean change = false;
        for (VGraphic<C,G> en : remove) {
            change = removeHelp(en) || change;
        }
        if (change) {
            fireConversionNeeded();
        }
    }

    /**
     * Removes all entries from the composite and replaces them
     * @param add the entries to add
     */
    public void replaceGraphics(Iterable<? extends VGraphic<C,G>> add) {
        boolean change = false;
        synchronized(entries) {
            if (!entries.isEmpty()) {
                change = true;
            }
            for (VGraphic<C,G> en : entries) {
                if (en.getParentGraphic() == this) {
                    en.setParentGraphic(null);
                }
            }
            entries.clear();
            for (VGraphic<C,G> en : add) {
                change = addHelp(en) || change;
            }
        }
        if (change) {
            fireConversionNeeded();
        }
    }

    /**
     * Removes all entries, clearing their parents
     */
    public void clearGraphics() {
        boolean change = !entries.isEmpty();
        for (VGraphic<C,G> en : entries) {
            if (en.getParentGraphic() == this) {
                en.setParentGraphic(null);
            }
        }
        entries.clear();
        if (change) {
            fireConversionNeeded();
        }
    }

    //
    // CONVERSIONS
    //

    @Override
    public boolean isUnconverted() {
        synchronized(entries) {
            if (entries.isEmpty()) {
                return true;
            }
            for (VGraphic<C,G> en : entries) {
                if (en.isUnconverted()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setUnconverted(boolean flag) {
        settingConvertFlag = true;
        for (VGraphic<C,G> en : entries) {
            en.setUnconverted(flag);
        }
        settingConvertFlag = false;
        if (flag) {
            fireConversionNeeded();
        }
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        synchronized(entries) {
            for (VGraphic<C,G> e : entries) {
                if (e.isUnconverted()) {
                    e.convert(vis, processor);
                }
            }
            updateEntries();
        }
    }

    protected void updateEntries() {
        List<Graphic> oldWindowEntries = Lists.newArrayList(windowEntry.getGraphics());
        List<Graphic> toAdd = Lists.newArrayList();
        List<Graphic> toRemove = Lists.newArrayList(oldWindowEntries);

        for (VGraphic<C,G> e : entries) {
            Graphic en = e.getWindowGraphic();
            if (oldWindowEntries.contains(en)) {
                toRemove.remove(en);
            } else {
                toAdd.add(en);
            }
        }

        windowEntry.replaceGraphics(toRemove, toAdd);
    }


    // <editor-fold desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    /**
     * Called by graphics to notify the composite that the graphic needs to
     * be converted before being redrawn.
     *
     * @param en the graphic making the request
     */
    public void conversionNeeded(VGraphic<C,G> en) {
        if (!settingConvertFlag) {
            fireConversionNeeded();
        }
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="PRIVATE UTILITIES">
    //
    // PRIVATE UTILITIES
    //

    /** Add w/o events */
    private boolean addHelp(VGraphic<C,G> en) {
        if (entries.add(en)) {
            en.setParentGraphic(this);
            return true;
        }
        return false;
    }

    /** Remove w/o events */
    private boolean removeHelp(VGraphic<C,G> en) {
        if (entries.remove(en)) {
            if (en.getParentGraphic() == this) {
                en.setParentGraphic(null);
            }
            return true;
        }
        return false;
    }

    // </editor-fold>

}
