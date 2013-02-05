/*
 * ObjectStyler.java
 * Created Oct 11, 2011
 */
package org.blaise.style;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Groups together a collection of delegators for a generic object that can be
 * used to customize its style, tooltip, label, and label style.
 *
 * @param <Src> the type of source object
 * @param <Style> the primary style type used to draw
 *
 * @author elisha
 */
public class ObjectStyler<Src, Style> {

    /** Delegate for point rendering */
    protected Function<? super Src, Style> styles;

    /** Delegate for point labels (only used if the styler returns a label style) */
    protected Function<? super Src, String> labels;
    /** Delegate for point label styles */
    protected Function<? super Src, StringStyle> labelStyles;

    /** Delegate for tooltips (with default) */
    protected Function<? super Src, String> tips = new Function<Src, String>() {
        public String apply(Src src) { 
            return src == null ? "null" : src.toString(); 
        }
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
    public Function<? super Src, Style> getStyleDelegate() {
        return styles;
    }

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object
     */
    public void setStyleDelegate(Function<? super Src, Style> styler) {
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
        setStyleDelegate(Functions.constant(style));
    }

    /**
     * Returns the current label delegate
     * @return  label delegate
     */
    public Function<? super Src, String> getLabelDelegate() {
        return labels;
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     */
    public void setLabelDelegate(Function<? super Src, String> labeler) {
        if (this.labels != labeler) {
            this.labels = labeler;
            pcs.firePropertyChange("labelDelegate", null, styles);
        }
    }

    /**
     * Returns the current label style delegate
     * @return  label style delegate
     */
    public Function<? super Src, StringStyle> getLabelStyleDelegate() {
        return labelStyles;
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     */
    public void setLabelStyleDelegate(Function<? super Src, StringStyle> labelStyler) {
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
        setLabelStyleDelegate(Functions.constant(style));
    }

    /**
     * Returns the current tip delegate
     * @return tip delegate
     */
    public Function<? super Src, String> getTipDelegate() {
        return tips;
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     */
    public void setTipDelegate(Function<? super Src, String> tipper) {
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

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    //</editor-fold>



}
