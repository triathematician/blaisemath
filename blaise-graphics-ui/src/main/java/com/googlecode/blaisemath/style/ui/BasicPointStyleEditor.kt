package com.googlecode.blaisemath.style.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.firestarter.editor.ColorEditor
import com.googlecode.blaisemath.graphics.swing.render.MarkerRenderer
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.primitive.Markers
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.geom.Point2D
import java.beans.Customizer
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

/*-
* #%L
* blaise-graphics
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
 * GUI form for editing an [AttributeSet] for points.
 *
 * @author Elisha Peterson
 */
class BasicPointStyleEditor : JPanel, Customizer, ChangeListener, PropertyChangeListener {
    /** The style being edited  */
    private var style = Styles.DEFAULT_POINT_STYLE.copy()

    /** Spinner for radius  */
    private var radiusSp: JSpinner? = null

    /** Spinner for stroke  */
    private var strokeSp: JSpinner? = null

    /** Color editor for fill  */
    private var fillEd: ColorEditor? = null

    /** Color editor for stroke  */
    private var strokeEd: ColorEditor? = null

    /** Combo box for shapes  */
    private var shapeCombo: JComboBox<*>? = null

    /** Initialize with defaults  */
    constructor() {
        initComponents()
    }

    /**
     * Initialize with defaults and a style
     * @param style the style to edit
     */
    constructor(style: AttributeSet?) {
        initComponents()
        setObject(style)
    }

    /** Sets up the panel  */
    private fun initComponents() {
        layout = GridBagLayout()
        val gbc = GridBagConstraints()
        gbc.gridx = GridBagConstraints.RELATIVE
        gbc.gridy = 0
        gbc.weightx = 0.0
        gbc.weighty = 1.0
        gbc.anchor = GridBagConstraints.EAST
        gbc.ipadx = 3
        gbc.ipady = 1
        add(JLabel("Radius:"), gbc)
        gbc.fill = GridBagConstraints.HORIZONTAL
        val m1 = SpinnerNumberModel(5.0, 0.0, 1000.0, 1.0)
        radiusSp = JSpinner(m1)
        add(radiusSp, gbc)
        radiusSp.setToolTipText("Radius of point")
        radiusSp.addChangeListener(this)
        gbc.fill = GridBagConstraints.NONE
        add(JLabel(" Fill:"), gbc)
        fillEd = ColorEditor()
        add(fillEd.getCustomEditor(), gbc)
        fillEd.addPropertyChangeListener(this)
        gbc.gridy = 1
        add(JLabel("Outline:"), gbc)
        gbc.fill = GridBagConstraints.HORIZONTAL
        val m2 = SpinnerNumberModel(1.0, 0.0, 50.0, 0.5)
        strokeSp = JSpinner(m2)
        add(strokeSp, gbc)
        strokeSp.setToolTipText("Width of stroke")
        strokeSp.addChangeListener(this)
        gbc.fill = GridBagConstraints.NONE
        add(JLabel(" Stroke:"), gbc)
        strokeEd = ColorEditor()
        add(strokeEd.getCustomEditor(), gbc)
        strokeEd.addPropertyChangeListener(this)
        gbc.gridy = 0
        gbc.gridheight = 2
        gbc.weightx = 0.0
        gbc.weighty = 0.0
        gbc.anchor = GridBagConstraints.CENTER
        shapeCombo = JComboBox<Any?>(Markers.getAvailableMarkers().toTypedArray())
        add(shapeCombo, gbc)
        shapeCombo.setRenderer(ShapeListCellRenderer())
        shapeCombo.addActionListener(ActionListener { e: ActionEvent? ->
            style.put(Styles.MARKER, shapeCombo.getSelectedItem())
            fireStyleChanged()
        })
        setObject(style)
        validate()
    }

    fun getObject(): AttributeSet? {
        return style
    }

    override fun setObject(bean: Any?) {
        require(bean is AttributeSet)
        style = bean as AttributeSet?
        radiusSp.setValue(style.getFloat(Styles.MARKER_RADIUS))
        strokeSp.setValue(style.getFloat(Styles.STROKE_WIDTH))
        fillEd.setValue(style.getColor(Styles.FILL))
        strokeEd.setValue(style.getColor(Styles.STROKE))
        val marker = style[Styles.MARKER]
        if (marker == null) {
            shapeCombo.setSelectedItem(Markers.CIRCLE)
        } else {
            for (i in 0 until shapeCombo.getItemCount()) {
                if (shapeCombo.getItemAt(i).javaClass == marker.javaClass) {
                    shapeCombo.setSelectedIndex(i)
                    break
                }
            }
        }
    }

    //region EVENTS
    private fun fireStyleChanged() {
        shapeCombo.repaint()
        firePropertyChange("style", null, style)
    }

    override fun stateChanged(e: ChangeEvent?) {
        if (e.getSource() === radiusSp) {
            style.put(Styles.MARKER_RADIUS, (radiusSp.getValue() as Number).toFloat())
        } else if (e.getSource() === strokeSp) {
            style.put(Styles.STROKE_WIDTH, (strokeSp.getValue() as Number).toFloat())
        } else {
            return
        }
        fireStyleChanged()
    }

    override fun propertyChange(e: PropertyChangeEvent?) {
        if (e.getSource() === fillEd) {
            style.put(Styles.FILL, if (fillEd.getNewValue() == null) fillEd.getValue() else fillEd.getNewValue())
        } else if (e.getSource() === strokeEd) {
            style.put(Styles.STROKE, if (strokeEd.getNewValue() == null) strokeEd.getValue() else strokeEd.getNewValue())
        } else {
            return
        }
        fireStyleChanged()
    }
    //endregion
    //region INNER CLASSES
    /** Draws elements of the list using the settings elsewhere.  */
    private inner class ShapeListCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component? {
            val result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
            result.toolTipText = value.toString()
            result.text = null
            result.icon = ShapeIcon(value as Marker?)
            return result
        }
    }

    /** Icon for drawing stylized point on a component  */
    private inner class ShapeIcon private constructor(private val shape: Marker?) : Icon {
        override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
            (g as Graphics2D?).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            val xc = c.getWidth() / 2.0
            val yc = c.getHeight() / 2.0
            val shape1 = style[Styles.MARKER] as Marker?
            style.put(Styles.MARKER, shape)
            MarkerRenderer.getInstance().render(Point2D.Double(xc, yc), style, g as Graphics2D?)
            style.put(Styles.MARKER, shape1)
        }

        override fun getIconWidth(): Int {
            return 50
        }

        override fun getIconHeight(): Int {
            return 50
        }
    } //endregion
}