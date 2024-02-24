package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 - 2024 Elisha Peterson
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


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * Displays a basic square color icon, handles both null and non-null colors.
 * 
 * @author Elisha Peterson
 */
public class BasicColorIcon implements Icon {
    
    public final Color color;
    private final Color outline;
    private final int size;

    public BasicColorIcon(Color color) {
        this(color, 12);
    }
    
    public BasicColorIcon(Color color, int sz) {
        this(color, sz, color == null || color.getRed()+color.getGreen()+color.getBlue() > (128*3) ? Color.black : Color.white);
    }
    
    public BasicColorIcon(Color color, int sz, Color outline) {
        this.color = color;
        this.size = sz;
        this.outline = outline;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (color == null) {
            g.setColor(Color.white);
            g.fillRect(x, y, getIconWidth(), getIconHeight());
            g.setColor(Color.red);
            g.drawLine(x, y, x+getIconWidth(), y+getIconHeight());
            g.drawLine(x+getIconWidth(), y, x, y+getIconHeight());
            g.setColor(Color.black);
            g.drawRect(x, y, getIconWidth(), getIconHeight());
        } else {
            g.setColor(color);
            g.fillRect(x, y, getIconWidth(), getIconHeight());
            if (outline != null) {
                g.setColor(outline);
                g.drawRect(x, y, getIconWidth(), getIconHeight());
            }
        }
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

}
