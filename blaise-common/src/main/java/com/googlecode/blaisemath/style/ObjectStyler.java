package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides delegates for draw style, label, label visibility, label style,
 * and tooltip text. It is intended to be used with objects that combine display
 * of a primitive/graphics object and an accompanying label. The same styler can
 * be used for many different graphic objects.
 * 
 * @param <S> the type of source object
 *
 * @author Elisha Peterson
 */
public final class ObjectStyler<S> {

    public static final String P_STYLE_DELEGATE = "styleDelegate";
    public static final String P_LABEL_FILTER = "labelFilter";
    public static final String P_LABEL_DELEGATE = "labelDelegate";
    public static final String P_LABEL_STYLE_DELEGATE = "labelStyleDelegate";
    public static final String P_TIP_DELEGATE = "tipDelegate";

    /** Delegate for point rendering */
    private @Nullable Function<? super S, AttributeSet> styler;

    /** Show/hide label setting */
    private @Nullable Predicate<S> labelFilter;
    /** Delegate for point labels (only used if the styler returns a label style) */
    private @Nullable Function<? super S, String> labeler;
    /** Delegate for point label styles */
    private @Nullable Function<? super S, AttributeSet> labelStyler;

    /** Delegate for tooltips (with default) */
    private @Nullable Function<? super S, String> tipper = s -> Objects.toString(s, "null");

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    //region FACTORY/BUILDER
    
    /**
     * Create new default styler instance.
     * @param <S> the type of source object
     * @return new styler instance
     */
    public static <S> ObjectStyler<S> create() {
        return new ObjectStyler<>();
    }

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object
     * @return this
     */
    public ObjectStyler<S> styleDelegate(@Nullable Function<? super S, AttributeSet> styler) {
        setStyleDelegate(styler);
        return this;
    }

    /**
     * Sets a single style for all objects.
     * @param style style to use for all objects
     * @return this
     */
    public ObjectStyler<S> style(AttributeSet style) {
        setStyle(style);
        return this;
    }

    /**
     * Sets the current label filter.
     * @param filter the new filter
     * @return this
     */
    public ObjectStyler<S> labelFilter(@Nullable Predicate<S> filter) {
        setLabelFilter(filter);
        return this;
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     * @return this
     */
    public ObjectStyler<S> labelDelegate(@Nullable Function<? super S, String> labeler) {
        setLabelDelegate(labeler);
        return this;
    }

    /**
     * Sets a single label for all objects
     * @param text label text
     * @return this
     */
    public ObjectStyler<S> label(@Nullable String text) {
        setLabel(text);
        return this;
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     * @return this
     */
    public ObjectStyler<S> labelStyleDelegate(@Nullable Function<? super S, AttributeSet> labelStyler) {
        setLabelStyleDelegate(labelStyler);
        return this;
    }

    /**
     * Sets a single label style for all objects.
     * @param style style to use for all objects
     * @return this
     */
    public ObjectStyler<S> labelStyle(AttributeSet style) {
        setLabelStyle(style);
        return this;
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     * @return this
     */
    public ObjectStyler<S> tipDelegate(@Nullable Function<? super S, String> tipper) {
        setTipDelegate(tipper);
        return this;
    }

    /**
     * Sets a single tooltip for all objects
     * @param tooltip tooltip
     * @return this
     */
    public ObjectStyler<S> tip(@Nullable String tooltip) {
        setTip(tooltip);
        return this;
    }

    //endregion

    //region PROPERTIES

    /**
     * Returns the current style delegate
     * @return style delegate
     */
    public @Nullable Function<? super S, AttributeSet> getStyleDelegate() {
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
            pcs.firePropertyChange(P_STYLE_DELEGATE, null, this.styler);
        }
    }

    /**
     * Sets a single style for all objects.
     * @param style style to use for all objects
     */
    public void setStyle(AttributeSet style) {
        Preconditions.checkNotNull(style);
        setStyleDelegate(s -> style);
    }

    /**
     * Returns the current label filter
     * @return label filter
     */
    public @Nullable Predicate<S> getLabelFilter() {
        return labelFilter;
    }

    /**
     * Sets the current label filter.
     * @param filter the new filter
     */
    public void setLabelFilter(@Nullable Predicate<S> filter) {
        if (this.labelFilter != filter) {
            Object old = this.labelFilter;
            this.labelFilter = filter;
            pcs.firePropertyChange(P_LABEL_FILTER, old, filter);
        }
    }

    /**
     * Returns the current label delegate
     * @return label delegate
     */
    public @Nullable Function<? super S, String> getLabelDelegate() {
        return labeler;
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     */
    public void setLabelDelegate(@Nullable Function<? super S, String> labeler) {
        if (this.labeler != labeler) {
            Object old = this.labeler;
            this.labeler = labeler;
            pcs.firePropertyChange(P_LABEL_DELEGATE, old, styler);
        }
    }

    /**
     * Sets a single label for all objects
     * @param text label text
     */
    public void setLabel(@Nullable String text) {
        setLabelDelegate(s -> text);
    }

    /**
     * Returns the current label style delegate
     * @return  label style delegate
     */
    public @Nullable Function<? super S, AttributeSet> getLabelStyleDelegate() {
        return labelStyler;
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     */
    public void setLabelStyleDelegate(@Nullable Function<? super S, AttributeSet> labelStyler) {
        if (this.labelStyler != labelStyler) {
            Object old = this.labelStyler;
            this.labelStyler = labelStyler;
            pcs.firePropertyChange(P_LABEL_STYLE_DELEGATE, old, this.labelStyler);
        }
    }

    /**
     * Sets a single label style for all objects.
     * @param style style to use for all objects
     */
    public void setLabelStyle(AttributeSet style) {
        Preconditions.checkNotNull(style);
        setLabelStyleDelegate(s -> style);
    }

    /**
     * Returns the current tip delegate
     * @return tip delegate
     */
    public @Nullable Function<? super S, String> getTipDelegate() {
        return tipper;
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     */
    public void setTipDelegate(@Nullable Function<? super S, String> tipper) {
        if (this.tipper != tipper) {
            Object old = this.tipper;
            this.tipper = tipper;
            pcs.firePropertyChange(P_TIP_DELEGATE, old, this.tipper);
        }
    }

    /**
     * Sets a single tooltip for all objects
     * @param tooltip tooltip
     */
    public void setTip(@Nullable String tooltip) {
        setTipDelegate(s -> tooltip);
    }
    
    //endregion

    //region DELEGATES
    
    /**
     * Get style for given object.
     * @param src object
     * @return style
     */
    public @Nullable AttributeSet style(S src) {
        return styler == null ? null : styler.apply(src);
    }
    
    /**
     * Get label for given object.
     * @param src object
     * @return label
     */
    public @Nullable String label(S src) {
        return labeler == null ? null 
                : labelFilter == null || labelFilter.test(src) ? labeler.apply(src)
                : null;
    }
    
    /**
     * Get tip for given object.
     * @param src object
     * @return label
     */

    public @Nullable AttributeSet labelStyle(S src) {
        return labelStyler == null ? null : labelStyler.apply(src);
    }
    
    /**
     * Get tip for given object.
     * @param src object
     * @param def default label to return
     * @return label
     */
    public @Nullable String tooltip(S src, @Nullable String def) {
        return tipper == null ? def : tipper.apply(src);
    }

    //endregion

    //region EVENTS

    public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    //endregion


}
