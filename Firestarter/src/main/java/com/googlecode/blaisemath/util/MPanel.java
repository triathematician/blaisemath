/**
 * MPanel.java
 * Created on Jun 25, 2009
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

/**
 * <p>
 *   Adds a title bar onto another component, with text and a show/hide options.
 *   The component may be minimized or maximized depending upon the user's actions.
 *   The title bar defaults to a simple label, but the user may also alter that
 *   component directly for custom displays.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class MPanel extends JPanel {

    //<editor-fold defaultstate="collapsed" desc="STATICS">
    
    private static final int ICON_SIZE = UIManager.getFont("Label.font").getSize();
    
    private static final class PlusIcon extends SquareIcon {
        private final boolean plus;
        private final Color color;
        public PlusIcon(boolean plus, Color color) {
            super(ICON_SIZE);
            this.plus = plus;
            this.color = color;
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new Line2D.Double(x+2, y+.5*sz, x+sz-2, y+.5*sz));
            if (plus) {
                g2.draw(new Line2D.Double(x+.5*sz, y+2, x+.5*sz, y+sz-2));
            }
        }
    }
    
    //</editor-fold>
    
    /** The title bar */
    private final JPanel titleBar;
    /** The label displaying the title */
    private final JLabel titleLabel;
    /** The button for minimizing */
    private final JToggleButton toggle;

    /** Component within the panel */
    private Component component;
    /** Listen for changes to component size */
    private final PropertyChangeListener componentSizeListener;
    
    //
    // CONSTRUCTORS
    //

    public MPanel() {
        this("Title", new JLabel("Component"));
    }

    public MPanel(Component component) {
        this(component.toString(), component);
    }

    public MPanel(String title, Component component) {
        this(new JLabel(title), component);
    }

    public MPanel(JLabel label, Component component) {
        titleLabel = label;
        this.component = component;
        
        int titleHt = titleLabel.getPreferredSize().height;

        toggle = new JToggleButton();
        toggle.setBorder(null);
        toggle.setBorderPainted(false);
        toggle.setMargin(new Insets(0,0,0,0));
        toggle.setContentAreaFilled(false);
        
        for (Object k : UIManager.getLookAndFeelDefaults().keySet()) {
            if (k.toString().toLowerCase().contains("selection")
                    || k.toString().toLowerCase().contains("basdutton")) {
                System.out.println(k + " -> "+UIManager.getDefaults().get(k));
            }
        }

        Color fg = UIManager.getColor("Tree.selectionForeground");
        Color bg = UIManager.getColor("Tree.selectionBackground");
        Color fg2 = new Color(
                (fg.getRed()*3+bg.getRed())/4,
                (fg.getGreen()*3+bg.getGreen())/4,
                (fg.getBlue()*3+bg.getBlue())/4);
        Color fg3 = new Color(
                (fg.getRed()*1+bg.getRed())/2,
                (fg.getGreen()*1+bg.getGreen())/2,
                (fg.getBlue()*1+bg.getBlue())/2);
        titleLabel.setForeground(fg);
        
        toggle.setIcon(new PlusIcon(false, fg3));
        toggle.setRolloverIcon(new PlusIcon(false, fg2));
        toggle.setSelectedIcon(new PlusIcon(true, fg3));
        toggle.setRolloverSelectedIcon(new PlusIcon(true, fg2));
        
        toggle.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateSize();
            }
        });

        titleBar = new JPanel();
        titleBar.setMinimumSize(new Dimension(component.getMinimumSize().width + 2, titleHt));
        titleBar.setMaximumSize(new Dimension(component.getMaximumSize().width + 2, titleHt));
        titleBar.setPreferredSize(new Dimension(component.getPreferredSize().width + 2, titleHt));
        titleBar.setBackground(bg);

        titleBar.setLayout(new BorderLayout());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        titleBar.add(toggle, BorderLayout.WEST);
        titleBar.add(titleLabel, BorderLayout.CENTER);

        setBorder(BorderFactory.createLineBorder(titleBar.getBackground(), 2));
        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);
        
        componentSizeListener = new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String prop = evt.getPropertyName();
                if ("size".equals(prop) || "minimumSize".equals(prop)
                        || "preferredSize".equals(prop) || "maximumSize".equals(prop)) {
                    updateSize();
                }
            }
        };
        setPrimaryComponent(component);

        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();
            }
        });
        updateSize();
    }
    
    private boolean isMinimized() {
        return toggle.isSelected();
    }

    private void updateSize() {
        int height;
        int minWidth;
        int prefWidth;
        if (isMinimized()) {
            if (Arrays.asList(getComponents()).contains(component)) {
                remove(component);
            }
            height = titleBar.getPreferredSize().height + 4;
            minWidth = 20;
            prefWidth = 20;
        } else {
            if (!Arrays.asList(getComponents()).contains(component)) {
                add(component);
            }
            height = component.getPreferredSize().height + titleBar.getPreferredSize().height + 4;
            minWidth = component.getMinimumSize().width;
            prefWidth = component.getPreferredSize().width;
        }
        setMinimumSize(new Dimension(minWidth + 4, height));
        setMaximumSize(new Dimension(prefWidth + 4, height));
        setPreferredSize(new Dimension(component.getPreferredSize().width + 4, height));
        revalidate();
        repaint();
    }

    //
    // BEAN PATTERNS
    //

    /** 
     * Get title string
     * @return title 
     */
    public String getTitle() {
        return titleLabel.getText();
    }

    /** 
     * Sets title string
     * @param title
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /** 
     * Get component in this panel
     * @return the component active in this panel 
     */
    public Component getPrimaryComponent() {
        return component;
    }

    /** 
     * Sets main component
     * @param c 
     */
    public void setPrimaryComponent(Component c) {
        if (component != null) {
            component.removePropertyChangeListener(componentSizeListener);
            remove(component);
        }
        component = c;
        if (component instanceof JComponent) {
            ((JComponent) component).setBorder(null);
        }
        component.addPropertyChangeListener(componentSizeListener);

        add(component, BorderLayout.CENTER);
        updateSize();
    }

    private abstract static class SquareIcon implements Icon {
        protected final int sz;
        public SquareIcon(int iconSize) {
            this.sz = iconSize;
        }
        @Override
        public int getIconWidth() {
            return sz;
        }
        @Override
        public int getIconHeight() {
            return sz;
        }
    }
    
}
