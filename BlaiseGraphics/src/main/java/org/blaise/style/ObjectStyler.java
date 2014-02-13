/*
 * ObjectStyler.java
 * Created Oct 11, 2011
 */
package org.blaise.style;

import static com.google.common.base.Preconditions.*;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.annotation.Nullable;

/**
 * Groups together a collection of delegators for a generic object that can be
 * used to customize its style, tooltip, label, and label style. Notifies listeners
 * when any of these styles change.
 *
 * @param <S> the type of source object
 * @param <T> the primary style type used to draw
 *
 * @author elisha
 */
public class ObjectStyler<S, T> {

    /** Delegate for point rendering */
    @Nullable protected Function<? super S, T> styles = null;

    /** Delegate for point labels (only used if the styler returns a label style) */
    @Nullable protected Function<? super S, String> labels = null;
    /** Delegate for point label styles */
    @Nullable protected Function<? super S, TextStyle> labelStyles = null;

    /** Delegate for tooltips (with default) */
    @Nullable protected Function<? super S, String> tips = new Function<S, String>() {
        public String apply(S src) { 
            return src == null ? "null" : src.toString(); 
        }
    };

    /** Constructs the delegator */
    public ObjectStyler() {
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="STATIC FACTORY METHODS">
    //
    // STATIC FACTORY METHODS
    //
    
    /**
     * Create new default styler instance.
     */
    public static <S,T> ObjectStyler<S,T> create() {
        return new ObjectStyler<S,T>();
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /**
     * Returns the current style delegate
     * @return style delegate
     */
    @Nullable 
    public Function<? super S, T> getStyleDelegate() {
        return styles;
    }

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object
     */
    public void setStyleDelegate(@Nullable Function<? super S, T> styler) {
        if (this.styles != styler) {
            this.styles = styler;
            pcs.firePropertyChange("styleDelegate", null, styles);
        }
    }

    /**
     * Returns the current label delegate
     * @return  label delegate
     */
    @Nullable 
    public Function<? super S, String> getLabelDelegate() {
        return labels;
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     */
    public void setLabelDelegate(@Nullable Function<? super S, String> labeler) {
        if (this.labels != labeler) {
            this.labels = labeler;
            pcs.firePropertyChange("labelDelegate", null, styles);
        }
    }

    /**
     * Returns the current label style delegate
     * @return  label style delegate
     */
    @Nullable 
    public Function<? super S, TextStyle> getLabelStyleDelegate() {
        return labelStyles;
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     */
    public void setLabelStyleDelegate(@Nullable Function<? super S, TextStyle> labelStyler) {
        if (this.labelStyles != labelStyler) {
            this.labelStyles = labelStyler;
            pcs.firePropertyChange("labelStyleDelegate", null, labelStyles);
        }
    }

    /**
     * Returns the current tip delegate
     * @return tip delegate
     */
    @Nullable 
    public Function<? super S, String> getTipDelegate() {
        return tips;
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     */
    public void setTipDelegate(@Nullable Function<? super S, String> tipper) {
        if (this.tips != tipper) {
            this.tips = tipper;
            pcs.firePropertyChange("tipDelegate", null, tips);
        }
    }
    
    //</editor-fold>

    
    //
    // CONSTANT VALUE SETTERS
    //

    /**
     * Sets the style directly.
     * @param style style to use for all objects
     */
    public void setStyle(T style) {
        setStyleDelegate(Functions.constant(checkNotNull(style)));
    }

    /**
     * Sets the label style directly.
     * @param style style to use for all objects
     */
    public void setLabelStyle(TextStyle style) {
        setLabelStyleDelegate(Functions.constant(checkNotNull(style)));
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
