package com.googlecode.blaisemath.palette.ui;

/*-
 * #%L
 * ******************************* UNCLASSIFIED *******************************
 * BasicColorIcon.java
 * edu.jhuapl.vis:conjecture-legacy
 * %%
 * Copyright (C) 2012 - 2017 Johns Hopkins University Applied Physics Laboratory
 * %%
 * (c) Johns Hopkins University Applied Physics Laboratory. All Rights Reserved.
 * JHU-APL Proprietary Information. Do not disseminate without prior approval.
 * #L%
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * Displays a basic square color icon, handles both null & non-null colors.
 * 
 * @author petereb1
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
