package com.googlecode.blaisemath.palette.ui;

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
 * @author Elisha Peterson
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
        gr.fillRect(x, y, getIconWidth(), getIconHeight());
        
        if (showNames) {
            paintColorsWithNames(gr, x, y);
        } else {
            paintColorsWithoutNames(gr, x, y);
        }
    }
    
    private void paintColorsWithNames(Graphics2D gr, int x, int y) { 
        int xx = x + 5;
        int yy = y + 5;
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
    
    private void paintColorsWithoutNames(Graphics2D gr, int x, int y) {
        int xx = x + 6;
        int yy = y + 5;
        for (KeyColorBean sty : colors.getColors()) {
            new BasicColorIcon(sty.getColor(), 15, palette.foreground()).paintIcon(null, gr, xx, yy);
            xx += 20;
            if (xx > getIconWidth()-20) {
                xx = x + 6;
                yy += 20;
            }
        }
    }
    
}
