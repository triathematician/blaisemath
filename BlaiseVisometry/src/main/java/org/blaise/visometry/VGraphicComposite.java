/**
 * VGraphicComposite.java
 * Created Jan 29, 2011
 */
package org.blaise.visometry;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.blaise.graphics.Graphic;
import org.blaise.graphics.GraphicComposite;
import org.blaise.style.StyleContext;

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
public class VGraphicComposite<C> extends VGraphicSupport<C> {

    /** Stores the local entries */
    protected final List<VGraphic<C>> entries = Collections.synchronizedList(new ArrayList<VGraphic<C>>());
    /** Stores the window entry */
    protected final GraphicComposite windowEntry = new GraphicComposite();

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
    public Iterable<VGraphic<C>> getGraphics() {
        return Collections.unmodifiableList(entries);
    }

    public GraphicComposite getWindowGraphic() {
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
    public final void addGraphic(VGraphic<C> gfc) {
        if (addHelp(gfc)) {
            fireConversionNeeded();
        }
    }

    /**
     * Remove an entry from the composite
     * @param gfc the entry to remove
     */
    public void removeGraphic(VGraphic<C> gfc) {
        if (removeHelp(gfc)) {
            fireConversionNeeded();
        }
    }

    /**
     * Adds several entries to the composite
     * @param add the entries to add
     */
    public void addGraphics(Iterable<? extends VGraphic<C>> add) {
        boolean change = false;
        for (VGraphic<C> en : add) {
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
    public final void removeGraphics(Iterable<? extends VGraphic<C>> remove) {
        boolean change = false;
        for (VGraphic<C> en : remove) {
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
    public void replaceGraphics(Iterable<? extends VGraphic<C>> add) {
        boolean change = false;
        synchronized(entries) {
            if (!entries.isEmpty()) {
                change = true;
            }
            for (VGraphic<C> en : entries) {
                if (en.getParentGraphic() == this) {
                    en.setParentGraphic(null);
                }
            }
            entries.clear();
            for (VGraphic<C> en : add) {
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
        for (VGraphic<C> en : entries) {
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
            for (VGraphic<C> en : entries) {
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
        for (VGraphic<C> en : entries) {
            en.setUnconverted(flag);
        }
        settingConvertFlag = false;
        if (flag) {
            fireConversionNeeded();
        }
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        synchronized(entries) {
            for (VGraphic<C> e : entries) {
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

        for (VGraphic<C> e : entries) {
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
    public void conversionNeeded(VGraphic<C> en) {
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
    private boolean addHelp(VGraphic<C> en) {
        if (entries.add(en)) {
            en.setParentGraphic(this);
            return true;
        }
        return false;
    }

    /** Remove w/o events */
    private boolean removeHelp(VGraphic<C> en) {
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
