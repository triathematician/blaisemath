package com.googlecode.blaisemath.palette.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

/*
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
 * Displays a basic square color icon, handles both null and non-null colors.
 *
 * @author Elisha Peterson
 */
class BasicColorIcon @JvmOverloads constructor(val color: Color?, private val size: Int = 12, private val outline: Color? = if (color == null || color.red + color.green + color.blue > 128 * 3) Color.black else Color.white) : Icon {
    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
        if (color == null) {
            g.setColor(Color.white)
            g.fillRect(x, y, iconWidth, iconHeight)
            g.setColor(Color.red)
            g.drawLine(x, y, x + iconWidth, y + iconHeight)
            g.drawLine(x + iconWidth, y, x, y + iconHeight)
            g.setColor(Color.black)
            g.drawRect(x, y, iconWidth, iconHeight)
        } else {
            g.setColor(color)
            g.fillRect(x, y, iconWidth, iconHeight)
            if (outline != null) {
                g.setColor(outline)
                g.drawRect(x, y, iconWidth, iconHeight)
            }
        }
    }

    override fun getIconWidth(): Int {
        return size
    }

    override fun getIconHeight(): Int {
        return size
    }
}