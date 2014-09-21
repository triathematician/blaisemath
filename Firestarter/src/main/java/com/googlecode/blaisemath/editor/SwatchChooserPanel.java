/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */
package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;


/**
 * Modified from the standard color swatch chooser.
 * 
 * @author Steve Wilson (original)
 * @author Elisha Peterson (updated)
 */
public final class SwatchChooserPanel extends AbstractColorChooserPanel {

    private static final String RECENT_STR = UIManager.getString("ColorChooser.swatchesRecentText");
    
    private SwatchPanel swatchPanel;
    private RecentSwatchPanel recentSwatchPanel;
    private MouseListener mainSwatchListener;
    private ColorEditor chooser;
    private ChooserComboPopup popup;

    public SwatchChooserPanel(ColorEditor c, ChooserComboPopup p) {
        super();
        this.chooser = c;
        this.popup = p;
    }

    @Override
    public String getDisplayName() {
        return UIManager.getString("ColorChooser.swatchesNameText");
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }

    @Override
    protected void buildChooser() {
        JPanel superHolder = new JPanel();
        superHolder.setLayout(new BoxLayout(superHolder, BoxLayout.Y_AXIS));
        swatchPanel = new MainSwatchPanel();
        swatchPanel.getAccessibleContext().setAccessibleName(getDisplayName());

        recentSwatchPanel = new RecentSwatchPanel();
        recentSwatchPanel.getAccessibleContext().setAccessibleName(RECENT_STR);

        mainSwatchListener = new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
                chooser.setNewValue(color);
                chooser.initEditorValue();
                recentSwatchPanel.setMostRecentColor(color);
                popup.setVisible(false);
            }
        };
        swatchPanel.addMouseListener(mainSwatchListener);
        recentSwatchPanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                Color color = recentSwatchPanel.getColorForLocation(e.getX(), e.getY());
                chooser.setNewValue(color);
                chooser.initEditorValue();
                popup.setVisible(false);
            }
        });

        JPanel mainHolder = new JPanel(new BorderLayout());
        Border border = new CompoundBorder(new LineBorder(Color.black),
                new LineBorder(Color.white));
        mainHolder.setBorder(border);
        mainHolder.add(swatchPanel, BorderLayout.CENTER);

        JPanel recentHolder = new JPanel(new BorderLayout());
        recentHolder.setBorder(border);
        recentHolder.add(recentSwatchPanel, BorderLayout.CENTER);

        superHolder.add(recentHolder);
        superHolder.add(Box.createRigidArea(new Dimension(0, 3)));
        superHolder.add(mainHolder);

        add(superHolder);
    }

    @Override
    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
        swatchPanel.removeMouseListener(mainSwatchListener);
        swatchPanel = null;
        mainSwatchListener = null;
        removeAll();
    }

    @Override
    public void updateChooser() {
        // no need to do anything here
    }

    /** This panel actually displays the colors */
    private static class SwatchPanel extends JPanel {
        protected Color[] colors;
        protected Dimension swatchSize = new Dimension(12, 12);
        protected Dimension numSwatches;
        protected Dimension gap;

        /** Sets up the panel */
        SwatchPanel() {
            initValues();
            initColors();
            setToolTipText("");
            setOpaque(true);
            setFocusable(false);
        }

        public boolean isFocusAble() {
            return false;
        }

        protected void initValues() {
            // hook method for sub-classes
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            for (int row = 0; row < numSwatches.height; row++) {
                for (int column = 0; column < numSwatches.width; column++) {
                    g.setColor(getColorForCell(column, row));
                    int x = column * (swatchSize.width + gap.width);
                    int y = row * (swatchSize.height + gap.height);
                    g.fillRect(x, y, swatchSize.width, swatchSize.height);
                    g.setColor(Color.black);
                    g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                    g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.width - 1);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            int x = numSwatches.width * (swatchSize.width + gap.width) - 1;
            int y = numSwatches.height * (swatchSize.height + gap.height) - 1;
            return new Dimension(x, y);
        }

        protected void initColors() {
            // hook method for sub-classes
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            Color color = getColorForLocation(e.getX(), e.getY());
            return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
        }

        public Color getColorForLocation(int x, int y) {
            int column = x / (swatchSize.width + gap.width);
            int row = y / (swatchSize.height + gap.height);
            return getColorForCell(column, row);
        }

        private Color getColorForCell(int column, int row) {
            return colors[(row * numSwatches.width) + column];
        }
    }

    private static final class RecentSwatchPanel extends SwatchPanel {
        @Override
        protected void initValues() {
            numSwatches = new Dimension(16, 1);
            gap = new Dimension(1, 1);
        }

        @Override
        protected void initColors() {
            Color defaultRecentColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor");
            int numColors = numSwatches.width * numSwatches.height;

            colors = new Color[numColors];
            for (int i = 0; i < numColors; i++) {
                colors[i] = defaultRecentColor;
            }
        }

        public void setMostRecentColor(Color c) {
            System.arraycopy(colors, 0, colors, 1, colors.length - 1);
            colors[0] = c;
            repaint();
        }
    }

    private static final class MainSwatchPanel extends SwatchPanel {
        @Override
        protected void initValues() {
            numSwatches = new Dimension(16, 7);
            gap = new Dimension(1, 1);
        }

        @Override
        protected void initColors() {
            colors = new Color[numSwatches.width * numSwatches.height];
            int i = 0;
            for (int j = 0; j < 16; j++) {
                colors[i++] = color(255,255,255,j,16);
            }
            i = addColorSequence(colors, 255, 0, 0, i);
            i = addColorSequence(colors, 0, 255, 0, i);
            i = addColorSequence(colors, 0, 0, 255, i);
            i = addColorSequence(colors, 255, 0, 255, i);
            i = addColorSequence(colors, 0, 255, 255, i);
            addColorSequence(colors, 255, 255, 0, i);
        }
    }
    
    private static int addColorSequence(Color[] arr, int r, int g, int b, int i0) {
        int i = i0;
        for (int j = 0; j < 8; j++) {
            arr[i++] = color(r, g, b, j, 8);
        }
        for (int j = 7; j >= 0; j--) {
            arr[i++] = color2(255 - r, 255 - g, 255 - b, j, 8);
        }
        return i;
    }

    private static Color color(int r, int g, int b, int i, int n) {
        return new Color((int)(r*i/(n-1.0)), (int)(g*i/(n-1.0)), (int)(b*i/(n-1.0)));
    }
    
    private static Color color2(int r, int g, int b, int i, int n) {
        return new Color(255-(int)(r*i/(n-1.0)), 255-(int)(g*i/(n-1.0)), 255-(int)(b*i/(n-1.0)));
    }
}
