/**
 * VGraphicComposite.java
 * Created Jan 29, 2011
 */
package org.blaise.visometry;

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

    //
    // PROPERTIES (DELEGATES)
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

    public GraphicComposite getWindowEntry() {
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
    public void setRenderFactory(StyleContext rend) {
        windowEntry.setStyleContext(rend);
    }

    //
    // COMPOSITE METHODS
    //

    /**
     * Add an entry to the composite.
     * @param gfc the entry
     */
    public final void addGraphic(VGraphic gfc) {
        if (addHelp(gfc)) {
            fireConversionNeeded();
        }
    }

    /**
     * Remove an entry from the composite
     * @param gfc the entry to remove
     */
    public void removeGraphic(VGraphic gfc) {
        if (removeHelp(gfc)) {
            fireConversionNeeded();
        }
    }

    /**
     * Adds several entries to the composite
     * @param add the entries to add
     */
    public void addGraphics(Iterable<? extends VGraphic> add) {
        boolean change = false;
        for (VGraphic en : add) {
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
    public final void removeGraphics(Iterable<? extends VGraphic> remove) {
        boolean change = false;
        for (VGraphic en : remove) {
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
    public void replaceGraphics(Iterable<? extends VGraphic> add) {
        boolean change = false;
        synchronized(entries) {
            if (entries.size() > 0) {
                change = true;
            }
            for (VGraphic<C> en : entries) {
                if (en.getParent() == this) {
                    en.setParent(null);
                }
            }
            entries.clear();
            for (VGraphic<C> en : add) {
                change = change || addHelp(en);
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
        boolean change = entries.size() > 0;
        for (VGraphic en : entries) {
            if (en.getParent() == this) {
                en.setParent(null);
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

    /** Flag to prevent events from firing while updating convert */
    transient boolean settingConvertFlag = false;

    @Override
    public void setUnconverted(boolean flag) {
        settingConvertFlag = true;
        try {
            for (VGraphic<C> en : entries) {
                en.setUnconverted(flag);
            }
        } catch (Exception ex) {
            System.err.println(ex);
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
        ArrayList<Graphic> oldWindowEntries = new ArrayList<Graphic>();
        for (Graphic e : windowEntry.getGraphics()) {
            oldWindowEntries.add(e);
        }
        ArrayList<Graphic> toAdd = new ArrayList<Graphic>(),
                toRemove = new ArrayList<Graphic>(oldWindowEntries);

        for (VGraphic<C> e : entries) {
            Graphic en = e.getWindowEntry();
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
    public void conversionNeeded(VGraphic en) {
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
    private boolean addHelp(VGraphic en) {
        if (entries.add(en)) {
            en.setParent(this);
            return true;
        }
        return false;
    }

    /** Remove w/o events */
    private boolean removeHelp(VGraphic en) {
        if (entries.remove(en)) {
            if (en.getParent() == this) {
                en.setParent(null);
            }
            return true;
        }
        return false;
    }

    // </editor-fold>

}
