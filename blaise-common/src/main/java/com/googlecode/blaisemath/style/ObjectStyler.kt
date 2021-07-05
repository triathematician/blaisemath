package com.googlecode.blaisemath.style

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.*
import java.util.function.Function
import java.util.function.Predicate

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
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
*/ /**
 * Provides delegates for draw style, label, label visibility, label style,
 * and tooltip text. It is intended to be used with objects that combine display
 * of a primitive/graphics object and an accompanying label. The same styler can
 * be used for many different graphic objects.
 *
 * @param <S> the type of source object
 *
 * @author Elisha Peterson
</S> */
class ObjectStyler<S> {
    /** Delegate for point rendering  */
    private var styler: Function<in S?, AttributeSet?>? = null

    /** Show/hide label setting  */
    private var labelFilter: Predicate<S?>? = null

    /** Delegate for point labels (only used if the styler returns a label style)  */
    private var labeler: Function<in S?, String?>? = null

    /** Delegate for point label styles  */
    private var labelStyler: Function<in S?, AttributeSet?>? = null

    /** Delegate for tooltips (with default)  */
    private var tipper: Function<in S?, String?>? = Function { s: S? -> Objects.toString(s, "null") }
    protected val pcs: PropertyChangeSupport? = PropertyChangeSupport(this)

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object
     * @return this
     */
    fun styleDelegate(styler: Function<in S?, AttributeSet?>?): ObjectStyler<S?>? {
        setStyleDelegate(styler)
        return this
    }

    /**
     * Sets a single style for all objects.
     * @param style style to use for all objects
     * @return this
     */
    fun style(style: AttributeSet?): ObjectStyler<S?>? {
        setStyle(style)
        return this
    }

    /**
     * Sets the current label filter.
     * @param filter the new filter
     * @return this
     */
    fun labelFilter(filter: Predicate<S?>?): ObjectStyler<S?>? {
        setLabelFilter(filter)
        return this
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     * @return this
     */
    fun labelDelegate(labeler: Function<in S?, String?>?): ObjectStyler<S?>? {
        setLabelDelegate(labeler)
        return this
    }

    /**
     * Sets a single label for all objects
     * @param text label text
     * @return this
     */
    fun label(text: String?): ObjectStyler<S?>? {
        setLabel(text)
        return this
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     * @return this
     */
    fun labelStyleDelegate(labelStyler: Function<in S?, AttributeSet?>?): ObjectStyler<S?>? {
        setLabelStyleDelegate(labelStyler)
        return this
    }

    /**
     * Sets a single label style for all objects.
     * @param style style to use for all objects
     * @return this
     */
    fun labelStyle(style: AttributeSet?): ObjectStyler<S?>? {
        setLabelStyle(style)
        return this
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     * @return this
     */
    fun tipDelegate(tipper: Function<in S?, String?>?): ObjectStyler<S?>? {
        setTipDelegate(tipper)
        return this
    }

    /**
     * Sets a single tooltip for all objects
     * @param tooltip tooltip
     * @return this
     */
    fun tip(tooltip: String?): ObjectStyler<S?>? {
        setTip(tooltip)
        return this
    }
    //endregion
    //region PROPERTIES
    /**
     * Returns the current style delegate
     * @return style delegate
     */
    fun getStyleDelegate(): Function<in S?, AttributeSet?>? {
        return styler
    }

    /**
     * Sets the current style delegate. If null, will use the default style
     * provided by the parent.
     * @param styler used to style object
     */
    fun setStyleDelegate(styler: Function<in S?, AttributeSet?>?) {
        if (this.styler !== styler) {
            this.styler = styler
            pcs.firePropertyChange(P_STYLE_DELEGATE, null, this.styler)
        }
    }

    /**
     * Sets a single style for all objects.
     * @param style style to use for all objects
     */
    fun setStyle(style: AttributeSet?) {
        Preconditions.checkNotNull(style)
        setStyleDelegate { s: S? -> style }
    }

    /**
     * Returns the current label filter
     * @return label filter
     */
    fun getLabelFilter(): Predicate<S?>? {
        return labelFilter
    }

    /**
     * Sets the current label filter.
     * @param filter the new filter
     */
    fun setLabelFilter(filter: Predicate<S?>?) {
        if (labelFilter !== filter) {
            val old: Any? = labelFilter
            labelFilter = filter
            pcs.firePropertyChange(P_LABEL_FILTER, old, filter)
        }
    }

    /**
     * Returns the current label delegate
     * @return label delegate
     */
    fun getLabelDelegate(): Function<in S?, String?>? {
        return labeler
    }

    /**
     * Sets the current label delegate. If null, uses a default label.
     * @param labeler the new labeler
     */
    fun setLabelDelegate(labeler: Function<in S?, String?>?) {
        if (this.labeler !== labeler) {
            val old: Any? = this.labeler
            this.labeler = labeler
            pcs.firePropertyChange(P_LABEL_DELEGATE, old, styler)
        }
    }

    /**
     * Sets a single label for all objects
     * @param text label text
     */
    fun setLabel(text: String?) {
        setLabelDelegate { s: S? -> text }
    }

    /**
     * Returns the current label style delegate
     * @return  label style delegate
     */
    fun getLabelStyleDelegate(): Function<in S?, AttributeSet?>? {
        return labelStyler
    }

    /**
     * Sets the current label style delegate. If null, uses a default style.
     * @param labelStyler the new label styler
     */
    fun setLabelStyleDelegate(labelStyler: Function<in S?, AttributeSet?>?) {
        if (this.labelStyler !== labelStyler) {
            val old: Any? = this.labelStyler
            this.labelStyler = labelStyler
            pcs.firePropertyChange(P_LABEL_STYLE_DELEGATE, old, this.labelStyler)
        }
    }

    /**
     * Sets a single label style for all objects.
     * @param style style to use for all objects
     */
    fun setLabelStyle(style: AttributeSet?) {
        Preconditions.checkNotNull(style)
        setLabelStyleDelegate { s: S? -> style }
    }

    /**
     * Returns the current tip delegate
     * @return tip delegate
     */
    fun getTipDelegate(): Function<in S?, String?>? {
        return tipper
    }

    /**
     * Sets the current tip delegate. If null, uses the default tooltip.
     * @param tipper generates tips for the object
     */
    fun setTipDelegate(tipper: Function<in S?, String?>?) {
        if (this.tipper !== tipper) {
            val old: Any? = this.tipper
            this.tipper = tipper
            pcs.firePropertyChange(P_TIP_DELEGATE, old, this.tipper)
        }
    }

    /**
     * Sets a single tooltip for all objects
     * @param tooltip tooltip
     */
    fun setTip(tooltip: String?) {
        setTipDelegate { s: S? -> tooltip }
    }
    //endregion
    //region DELEGATES
    /**
     * Get style for given object.
     * @param src object
     * @return style
     */
    fun style(src: S?): AttributeSet? {
        return if (styler == null) null else styler.apply(src)
    }

    /**
     * Get label for given object.
     * @param src object
     * @return label
     */
    fun label(src: S?): String? {
        return if (labeler == null) null else if (labelFilter == null || labelFilter.test(src)) labeler.apply(src) else null
    }

    /**
     * Get tip for given object.
     * @param src object
     * @return label
     */
    fun labelStyle(src: S?): AttributeSet? {
        return if (labelStyler == null) null else labelStyler.apply(src)
    }

    /**
     * Get tip for given object.
     * @param src object
     * @param def default label to return
     * @return label
     */
    fun tooltip(src: S?, def: String?): String? {
        return if (tipper == null) def else tipper.apply(src)
    }

    //endregion
    //region EVENTS
    fun removePropertyChangeListener(propertyName: String?, listener: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(propertyName, listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(listener)
    }

    fun addPropertyChangeListener(propertyName: String?, listener: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(propertyName, listener)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(listener)
    } //endregion

    companion object {
        val P_STYLE_DELEGATE: String? = "styleDelegate"
        val P_LABEL_FILTER: String? = "labelFilter"
        val P_LABEL_DELEGATE: String? = "labelDelegate"
        val P_LABEL_STYLE_DELEGATE: String? = "labelStyleDelegate"
        val P_TIP_DELEGATE: String? = "tipDelegate"
        //region FACTORY/BUILDER
        /**
         * Create new default styler instance.
         * @param <S> the type of source object
         * @return new styler instance
        </S> */
        fun <S> create(): ObjectStyler<S?>? {
            return ObjectStyler()
        }
    }
}