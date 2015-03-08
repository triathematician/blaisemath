/*
 * ObjectStyler.java
 * Created Oct 11, 2011
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.google.common.base.Predicate;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.annotation.Nullable;

/**
 * Provides delegates for draw style, label, label visibility, label style,
 * and tooltip text. It is intended to be used with objects that combine display
 * of a primitive/graphics object and an accompanying label. The same styler can
 * be used for many different graphic objects.
 * 
 * @param <S> the type of source object
 *
 * @author elisha
 */
public final class ObjectStyler<S> {

    /** Delegate for point rendering */
    @Nullable
    private Function<? super S, AttributeSet> styler = null;

    /** Show/hide label setting */
    @Nullable
    private Predicate<S> labelFilter = null;
    /** Delegate for point labels (only used if the styler returns a label style) */
    @Nullable
    private Function<? super S, String> labeler = null;
    /** Delegate for point label styles */
    @Nullable
    private Function<? super S, AttributeSet> labelStyler = null;

    /** Delegate for tooltips (with default) */
    @Nullable 
    private Function<? super S, String> tipper = new Function<S, String>() {
        @Override
        public String apply(S src) { 
            return src == null ? "null" : src.toString(); 
        }
    };
    
    
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
        return styler;
    }

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object
     */
    public void setStyleDelegate(@Nullable Function<? super S, AttributeSet> styler) {
        if (this.styler != styler) {
            this.styler = styler;
            pcs.firePropertyChange("styleDelegate", null, this.styler);
        }
    }
    
    public Predicate<S> getLabelFilter() {
        return labelFilter;
    }
    
    public void setLabelFilter(Predicate<S> labelFilter) {
        if (this.labelFilter != labelFilter) {
            Object old = this.labelFilter;
            this.labelFilter = labelFilter;
            pcs.firePropertyChange("labelFilter", old, labelFilter);
        }
    }

    /**
     * Returns the current label delegate
     * @return  label delegate
     */
    @Nullable 
    public Function<? super S, String> getLabelDelegate() {
        return labeler;
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     */
    public void setLabelDelegate(@Nullable Function<? super S, String> labeler) {
        if (this.labeler != labeler) {
            this.labeler = labeler;
            pcs.firePropertyChange("labelDelegate", null, styler);
        }
    }

    /**
     * Returns the current label style delegate
     * @return  label style delegate
     */
    @Nullable 
    public Function<? super S, AttributeSet> getLabelStyleDelegate() {
        return labelStyler;
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     */
    public void setLabelStyleDelegate(@Nullable Function<? super S, AttributeSet> labelStyler) {
        if (this.labelStyler != labelStyler) {
            this.labelStyler = labelStyler;
            pcs.firePropertyChange("labelStyleDelegate", null, this.labelStyler);
        }
    }

    /**
     * Returns the current tip delegate
     * @return tip delegate
     */
    @Nullable 
    public Function<? super S, String> getTipDelegate() {
        return tipper;
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     */
    public void setTipDelegate(@Nullable Function<? super S, String> tipper) {
        if (this.tipper != tipper) {
            this.tipper = tipper;
            pcs.firePropertyChange("tipDelegate", null, this.tipper);
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
        return styler == null ? null : styler.apply(src);
    }
    
    /**
     * Get label for given object.
     * @param src object
     * @return label
     */
    @Nullable
    public String label(S src) {
        return labeler == null ? null 
                : labelFilter == null || labelFilter.apply(src) ? labeler.apply(src)
                : null;
    }
    
    /**
     * Get tip for given object.
     * @param src object
     * @return label
     */
    @Nullable
    public AttributeSet labelStyle(S src) {
        return labelStyler == null ? null : labelStyler.apply(src);
    }
    
    /**
     * Get tip for given object.
     * @param src object
     * @param def default label to return
     * @return label
     */
    @Nullable
    public String tooltip(S src, @Nullable String def) {
        return tipper == null ? def : tipper.apply(src);
    }
    
    //</editor-fold>

    
    //
    // CONSTANT VALUE SETTERS
    //

    /**
     * Sets a single label for all objects
     * @param text label text
     */
    public void setLabelConstant(@Nullable String text) {
        setLabelDelegate(Functions.constant(text));
    }

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
