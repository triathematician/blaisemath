package com.googlecode.blaisemath.palette.ui;

/*-
 * #%L
 * ******************************* UNCLASSIFIED *******************************
 * StyleScaleIcon.java
 * edu.jhuapl.vis:conjecture-legacy
 * %%
 * Copyright (C) 2012 - 2017 Johns Hopkins University Applied Physics Laboratory
 * %%
 * (c) Johns Hopkins University Applied Physics Laboratory. All Rights Reserved.
 * JHU-APL Proprietary Information. Do not disseminate without prior approval.
 * #L%
 */
import com.googlecode.blaisemath.palette.Palette;
import static com.googlecode.blaisemath.palette.Palette.BACKGROUND;
import com.googlecode.blaisemath.palette.Palettes;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/**
 * Paints a preview icon showing all the colors in a color list. Uses a {@link Palette} for
 * foreground and background colors, and a {@link ColorListModel} for the actual colors.
 *
 * @author petereb1
 */
public class ColorListIcon implements Icon {

    private Palette palette = Palettes.lafPalette();
    private ColorListModel colors = new ColorListModel();
    private boolean showNames = true;
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public ColorListModel getColors() {
        return colors;
    }

    public void setColors(ColorListModel colors) {
        this.colors = colors;
    }

    public boolean isShowNames() {
        return showNames;
    }

    public void setShowNames(boolean showNames) {
        this.showNames = showNames;
    }
    
    //</editor-fold>

    @Override
    public int getIconWidth() {
        return 150;
    }

    @Override
    public int getIconHeight() {
        return 150;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D gr = (Graphics2D) g;        
        gr.setColor(palette.background());
        gr.fillRect(0, 0, getIconWidth(), getIconHeight());
        
        if (showNames) {
            paintColorsWithNames(gr);
        } else {
            paintColorsWithoutNames(gr);
        }
    }
    
    private void paintColorsWithNames(Graphics2D gr) { 
        int xx = 5;
        int yy = 5;
        gr.setFont(new Font("Dialog", Font.PLAIN, 15));
        for (KeyColorBean sty : colors.getColors()) {
            new BasicColorIcon(sty.getColor(), 15, palette.foreground()).paintIcon(null, gr, xx, yy);
            gr.setColor(BACKGROUND.equals(sty.getName()) ? palette.foreground() : sty.getColor());
            if (sty.getName() != null) {
                gr.drawString(sty.getName(), 23, yy + 13);
            }
            yy += 20;
        }
    }
    
    private void paintColorsWithoutNames(Graphics2D gr) {
        int xx = 6;
        int yy = 5;
        for (KeyColorBean sty : colors.getColors()) {
            new BasicColorIcon(sty.getColor(), 15, palette.foreground()).paintIcon(null, gr, xx, yy);
            xx += 20;
            if (xx > getIconWidth()-20) {
                xx = 6;
                yy += 20;
            }
        }
    }
    
}
