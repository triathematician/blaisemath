package com.googlecode.blaisemath.graphics.editor

import com.googlecode.blaisemath.editor.ColorEditor
import com.googlecode.blaisemath.graphics.swing.render.MarkerRenderer
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Marker
import com.googlecode.blaisemath.style.Markers
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.geom.Point2
import java.awt.*
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
* Copyright (C) 2009 - 2021 Elisha Peterson
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

/** GUI form for editing an [AttributeSet] for points. */
class BasicPointStyleEditor(style: AttributeSet) : JPanel(), Customizer, ChangeListener, PropertyChangeListener {

    /** The style being edited  */
    private var style = Styles.DEFAULT_POINT_STYLE.copy()

    /** Spinner for radius  */
    private var radiusSp = JSpinner(SpinnerNumberModel(5.0, 0.0, 1000.0, 1.0))
    /** Spinner for stroke  */
    private var strokeSp = JSpinner(SpinnerNumberModel(1.0, 0.0, 50.0, 0.5))
    /** Color editor for fill  */
    private var fillEd = ColorEditor()
    /** Color editor for stroke  */
    private var strokeEd = ColorEditor()
    /** Combo box for shapes  */
    private var shapeCombo = JComboBox(Markers.availableMarkers.toTypedArray())

    init {
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
        add(radiusSp, gbc)
        radiusSp.toolTipText = "Radius of point"
        radiusSp.addChangeListener(this)
        gbc.fill = GridBagConstraints.NONE
        add(JLabel(" Fill:"), gbc)
        add(fillEd.customEditor, gbc)
        fillEd.addPropertyChangeListener(this)
        gbc.gridy = 1
        add(JLabel("Outline:"), gbc)
        gbc.fill = GridBagConstraints.HORIZONTAL

        add(strokeSp, gbc)
        strokeSp.toolTipText = "Width of stroke"
        strokeSp.addChangeListener(this)
        gbc.fill = GridBagConstraints.NONE
        add(JLabel(" Stroke:"), gbc)
        add(strokeEd.customEditor, gbc)
        strokeEd.addPropertyChangeListener(this)
        gbc.gridy = 0
        gbc.gridheight = 2
        gbc.weightx = 0.0
        gbc.weighty = 0.0
        gbc.anchor = GridBagConstraints.CENTER
        shapeCombo
        add(shapeCombo, gbc)
        shapeCombo.setRenderer(ShapeListCellRenderer())
        shapeCombo.addActionListener {
            style.put(Styles.MARKER, shapeCombo.selectedItem)
            fireStyleChanged()
        }
        setObject(style)
        validate()
    }

    val `object`
        get() = style

    override fun setObject(value: Any) {
        style = value as AttributeSet
        radiusSp.value = style.getFloat(Styles.MARKER_RADIUS, 10f)
        strokeSp.value = style.getFloat(Styles.STROKE_WIDTH, 1f)
        fillEd.value = style.getColor(Styles.FILL, Color.white)
        strokeEd.value = style.getColor(Styles.STROKE, Color.black)
        val marker = style[Styles.MARKER]
        if (marker == null) {
            shapeCombo.setSelectedItem(Markers.CIRCLE)
        } else {
            for (i in 0 until shapeCombo.itemCount) {
                if (shapeCombo.getItemAt(i).javaClass == marker.javaClass) {
                    shapeCombo.selectedIndex = i
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

    override fun stateChanged(e: ChangeEvent) {
        when {
            e.source === radiusSp -> style.put(Styles.MARKER_RADIUS, (radiusSp.value as Number).toFloat())
            e.source === strokeSp -> style.put(Styles.STROKE_WIDTH, (strokeSp.value as Number).toFloat())
            else -> return
        }
        fireStyleChanged()
    }

    override fun propertyChange(e: PropertyChangeEvent) {
        when {
            e.source === fillEd -> style.put(Styles.FILL, fillEd.newValue ?: fillEd.value)
            e.source === strokeEd -> style.put(Styles.STROKE, strokeEd.newValue ?: strokeEd.value)
            else -> return
        }
        fireStyleChanged()
    }

    //endregion

    //region INNER CLASSES

    /** Draws elements of the list using the settings elsewhere.  */
    private inner class ShapeListCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
            val result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
            result.toolTipText = value.toString()
            result.text = null
            result.icon = ShapeIcon(value as Marker)
            return result
        }
    }

    /** Icon for drawing stylized point on a component  */
    private inner class ShapeIcon(private val shape: Marker) : Icon {
        override fun getIconWidth() = 50
        override fun getIconHeight() = 50

        override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
            (g as Graphics2D).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            val shape1 = style[Styles.MARKER] as Marker?
            style.put(Styles.MARKER, shape)
            MarkerRenderer().render(Point2(c.width / 2.0, c.height / 2.0), style, g)
            style.put(Styles.MARKER, shape1)
        }
    }

    //endregion
}