/*
 * ObjectStyler.java
 * Created Oct 11, 2011
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
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


import com.google.common.base.Function;
import com.google.common.base.Functions;
import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.annotation.Nullable;

/**
 * Provides delegates for getting draw styles, labels, label styles, and tooltips
 * for an object of a given type.
 *
 * @param <S> the type of source object
 *
 * @author elisha
 */
public class ObjectStyler<S> {

    /** Delegate for point rendering */
    @Nullable
    protected Function<? super S, AttributeSet> styles = null;

    /** Delegate for point labels (only used if the styler returns a label style) */
    @Nullable
    protected Function<? super S, String> labels = null;
    /** Delegate for point label styles */
    @Nullable
    protected Function<? super S, AttributeSet> labelStyles = null;

    /** Delegate for tooltips (with default) */
    @Nullable 
    protected Function<? super S, String> tips = new Function<S, String>() {
        @Override
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
     * @param <S> the type of source object
     * @return new styler instance
     */
    public static <S> ObjectStyler<S> create() {
        return new ObjectStyler<S>();
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
    public Function<? super S, AttributeSet> getStyleDelegate() {
        return styles;
    }

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object
     */
    public void setStyleDelegate(@Nullable Function<? super S, AttributeSet> styler) {
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
    public Function<? super S, AttributeSet> getLabelStyleDelegate() {
        return labelStyles;
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     */
    public void setLabelStyleDelegate(@Nullable Function<? super S, AttributeSet> labelStyler) {
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
    
    
    //<editor-fold defaultstate="collapsed" desc="DELEGATE METHODS">
    
    /**
     * Get style for given object.
     * @param src object
     * @return style
     */
    @Nullable
    public AttributeSet style(S src) {
        return styles == null ? null : styles.apply(src);
    }
    
    /**
     * Get label for given object.
     * @param src object
     * @return label
     */
    @Nullable
    public String label(S src) {
        return labels == null ? null : labels.apply(src);
    }
    
    /**
     * Get tip for given object.
     * @param src object
     * @return label
     */
    @Nullable
    public AttributeSet labelStyle(S src) {
        return labelStyles == null ? null : labelStyles.apply(src);
    }
    
    /**
     * Get tip for given object.
     * @param src object
     * @param def default label to return
     * @return label
     */
    @Nullable
    public String tooltip(S src, @Nullable String def) {
        return tips == null ? def : tips.apply(src);
    }
    
    //</editor-fold>

    
    //
    // CONSTANT VALUE SETTERS
    //

    /**
     * Sets a single style for all objects.
     * @param style style to use for all objects
     */
    public void setStyleConstant(AttributeSet style) {
        setStyleDelegate(Functions.constant(checkNotNull(style)));
    }

    /**
     * Sets a single label style for all objects.
     * @param style style to use for all objects
     */
    public void setLabelStyleConstant(AttributeSet style) {
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
