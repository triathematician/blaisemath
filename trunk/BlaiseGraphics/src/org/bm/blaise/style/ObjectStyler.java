/*
 * ObjectStyler.java
 * Created Oct 11, 2011
 */
package org.bm.blaise.style;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.bm.util.Delegator;
import org.bm.util.NonDelegator;

/**
 * Groups together a collection of delegators for points, including the object's
 * label, the tip, the object style, and the label style.
 *
 * @param <Src> the type of source object
 * @param <Style> the primary style type used to draw
 *
 * @author elisha
 */
public class ObjectStyler<Src, Style> {

    /** Delegate for point rendering */
    protected Delegator<Src, Style> styles;

    /** Delegate for point labels (only used if the styler returns a label style) */
    protected Delegator<Src, String> labels;
    /** Delegate for point label styles */
    protected Delegator<Src, StringStyle> labelStyles;

    /** Delegate for tooltips (with default) */
    protected Delegator<Src, String> tips = new Delegator<Src, String>() {
        public String of(Src src) { return src == null ? "null" : src.toString(); }
    };


    /** Constructs the delegator */
    public ObjectStyler() {
    }


    //
    // PROPERTIES
    //

    /**
     * Returns the current style delegate
     * @return style delegate
     */
    public Delegator<Src, Style> getStyleDelegate() {
        return styles;
    }

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object 
     */
    public void setStyleDelegate(Delegator<Src, Style> styler) {
        if (this.styles != styler) {
            this.styles = styler;
            pcs.firePropertyChange("styleDelegate", null, styles);
        }
    }

    /**
     * Sets the style directly.
     * @param style style to use for all objects
     */
    public void setStyle(Style style) {
        setStyleDelegate(new NonDelegator<Src,Style>(style));
    }

    /**
     * Returns the current label delegate
     * @return  label delegate
     */
    public Delegator<Src, String> getLabelDelegate() {
        return labels;
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     */
    public void setLabelDelegate(Delegator<Src, String> labeler) {
        if (this.labels != labeler) {
            this.labels = labeler;
            pcs.firePropertyChange("labelDelegate", null, styles);
        }
    }

    /**
     * Returns the current label style delegate
     * @return  label style delegate
     */
    public Delegator<Src, StringStyle> getLabelStyleDelegate() {
        return labelStyles;
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     */
    public void setLabelStyleDelegate(Delegator<Src, StringStyle> labelStyler) {
        if (this.labelStyles != labelStyler) {
            this.labelStyles = labelStyler;
            pcs.firePropertyChange("labelStyleDelegate", null, labelStyles);
        }
    }

    /**
     * Sets the label style directly.
     * @param style style to use for all objects
     */
    public void setLabelStyle(StringStyle style) {
        setLabelStyleDelegate(new NonDelegator<Src, StringStyle>(style));
    }

    /**
     * Returns the current tip delegate
     * @return tip delegate
     */
    public Delegator<Src, String> getTipDelegate() {
        return tips;
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     */
    public void setTipDelegate(Delegator<Src, String> tipper) {
        if (this.tips != tipper) {
            this.tips = tipper;
            pcs.firePropertyChange("tipDelegate", null, tips);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE HANDLING">
    //
    // PROPERTY CHANGE HANDLING
    //

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    //</editor-fold>



}
