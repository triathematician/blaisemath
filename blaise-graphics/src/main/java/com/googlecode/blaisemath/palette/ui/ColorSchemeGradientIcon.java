package com.googlecode.blaisemath.palette.ui;

/*-
 * #%L
 * ******************************* UNCLASSIFIED *******************************
 * ColorSchemeGradientIcon.java
 * edu.jhuapl.vis:conjecture-legacy
 * %%
 * Copyright (C) 2012 - 2017 Johns Hopkins University Applied Physics Laboratory
 * %%
 * (c) Johns Hopkins University Applied Physics Laboratory. All Rights Reserved.
 * JHU-APL Proprietary Information. Do not disseminate without prior approval.
 * #L%
 */
import com.google.common.base.Preconditions;
import com.googlecode.blaisemath.palette.ColorScheme;
import com.googlecode.blaisemath.palette.Palette;
import com.googlecode.blaisemath.palette.Palettes;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import javax.swing.Icon;
import static java.util.Objects.requireNonNull;

/**
 * Icon displaying gradient palette.
 * 
 * @author petereb1
 */
public class ColorSchemeGradientIcon implements Icon {

    private Palette palette = Palettes.lafPalette();
    private ColorScheme scheme = ColorScheme.createGradient("", Color.black, Color.white);
    private int height = 150;
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = requireNonNull(palette);
    }

    public ColorScheme getScheme() {
        return scheme;
    }

    public void setScheme(ColorScheme p) {
        Preconditions.checkArgument(!p.isDiscrete(), "Scheme must be a gradient!");
        this.scheme = p;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
    
    public void setIconHeight(int ht) {
        this.height = ht;
    }
    
    //</editor-fold>

    @Override
    public int getIconWidth() {
        return 30;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D gr = (Graphics2D) g;
        gr.setColor(palette.background());
        gr.fillRect(0, 0, getIconWidth(), getIconHeight());

        if (scheme.getColors().length > 0) {
            if (scheme.getColors().length == 1) {
                gr.setColor(scheme.getColors()[0]);
            } else if (scheme.getColors().length == 2) {
                gr.setPaint(new GradientPaint(0, 5, scheme.getColors()[0], 0, getIconHeight()-5, scheme.getColors()[1]));
            } else if (scheme.getColors().length > 2) {
                float[] stops = new float[scheme.getColors().length];
                for (int i = 0; i < stops.length; i++) {
                    stops[i] = i / (float) (stops.length - 1);
                }
                gr.setPaint(new LinearGradientPaint(0, 5, 0, getIconHeight()-5, stops, scheme.getColors()));
            }
            gr.fillRect(5, 5, getIconWidth()-11, getIconHeight()-11);
        }
        
        gr.setColor(palette.foreground());
        gr.drawRect(5, 5, getIconWidth()-11, getIconHeight()-11);
    }

}
