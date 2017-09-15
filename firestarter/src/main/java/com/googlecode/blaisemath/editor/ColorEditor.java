/**
 * ColorEditor.java
 * Created on Jul 1, 2009
 */
package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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

import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_MITER;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * <p>
 *   Provides multiple controls for editing a color.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ColorEditor extends MPanelEditorSupport {
    
    private static final String COLOR_ICON = "beaninfo.ColorIcon";
    private static final String COLOR_PRESSED_ICON = "beaninfo.ColorPressedIcon";

    public static final Dimension SQ_DIM = new Dimension(15, 15);
    public static final Dimension RGB_DIM = new Dimension(100, 15);
    public static final Dimension CC_DIM = new Dimension(16, 16);
    public static final Insets CC_INSETS = new Insets(0, 0, 0, 0);
    private Color color = Color.black;
    private JTextField rgbaValue;
    private JButton colorChooserButton;
    private ChooserComboButton colorChooserCombo;

    public ColorEditor() {
        setNewValue(new Color(0, 0, 0));
    }

    @Override
    public void initCustomizer() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        rgbaValue = new JTextField();
        rgbaValue.setBorder(null);
        rgbaValue.setPreferredSize(RGB_DIM);
        rgbaValue.setMaximumSize(RGB_DIM);
        rgbaValue.setMinimumSize(RGB_DIM);
        rgbaValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        rgbaValue.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(rgbaValue);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        colorChooserCombo = new ChooserComboButton();
        colorChooserCombo.setPreferredSize(CC_DIM);
        colorChooserCombo.setMaximumSize(CC_DIM);
        colorChooserCombo.setMinimumSize(CC_DIM);
        colorChooserCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorChooserCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(colorChooserCombo);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        UIDefaults table = UIManager.getDefaults();
        table.put(COLOR_ICON, LookAndFeel.makeIcon(getClass(), "resources/ColorIcon.gif"));
        table.put(COLOR_PRESSED_ICON, LookAndFeel.makeIcon(getClass(), "resources/ColorPressedIcon.gif"));
        Icon colorIcon = UIManager.getIcon(COLOR_ICON);
        Icon colorPressedIcon = UIManager.getIcon(COLOR_PRESSED_ICON);
        colorChooserButton = new JButton(colorIcon);
        colorChooserButton.setPressedIcon(colorPressedIcon);
        colorChooserButton.setToolTipText("press to bring up color chooser");
        colorChooserButton.setMargin(CC_INSETS);
        colorChooserButton.setBorderPainted(false);
        colorChooserButton.setContentAreaFilled(false);
        colorChooserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorChooserButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(colorChooserButton);

        rgbaValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setNewValue(getColor(rgbaValue.getText()));
                    initEditorValue();
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(ColorEditor.class.getName()).log(Level.FINE, 
                            "Not a color: "+rgbaValue.getText(), ex);
                    JOptionPane.showMessageDialog(panel.getParent(), ex.toString());
                }
            }
        });

        colorChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                color = JColorChooser.showDialog(panel.getParent(), "Color Chooser", color);
                if (color != null) {
                    setNewValue(color);
                    initEditorValue();
                }
            }
        });
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics g, Rectangle rect) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.white);
        g2.fill(rect);
        if (color.getAlpha() != 255) {        
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
            g2.fill(new Rectangle(rect.x, rect.y, rect.width, rect.height/2));
            g2.setColor(color);
            g2.fill(new Rectangle(rect.x, rect.y+rect.height/2, rect.width, rect.height/2));
        } else {
            g2.setColor(color);
            g2.fill(rect);
        }
        
        Color outline = Color.black;
        BasicStroke bs = color.getAlpha() != 255 ? new BasicStroke(1f, CAP_SQUARE, JOIN_MITER, 10.0f, new float[]{2f}, 0.0f) : new BasicStroke();
        g2.setColor(outline);
        g2.setStroke(bs);
        g2.draw(new Rectangle(rect.x, rect.y, rect.width-1, rect.height-1));
    }

    @Override
    public String getJavaInitializationString() {
        return "new java.awt.Color(" + getAsText() + ")";
    }

    @Override
    public String getAsText() {
        return getAsText((Color) getValue());
    }

    public String getAsText(Color c) {
        String result = c.getRed() + "," + c.getGreen() + "," + c.getBlue();
        if (c.getAlpha() == 255) {
            return result;
        } else {
            return result + "," + c.getAlpha();
        }
    }

    @Override
    public void setAsText(String s) {
        setValue(getColor(s));
    }

    public static Color getColor(String s) {
        String[] spl = s.split(",");
        if (spl.length < 3 || spl.length > 4) {
            Logger.getLogger(ColorEditor.class.getName()).log(Level.WARNING, 
                    "Invalid color {0}", s);
            return null;
        }
        
        try {
            int r = Math.max(0, Math.min(255, Integer.parseInt(spl[0])));
            int g = Math.max(0, Math.min(255, Integer.parseInt(spl[1])));
            int b = Math.max(0, Math.min(255, Integer.parseInt(spl[2])));
            int a = spl.length == 4 ? Math.max(0, Math.min(255, Integer.parseInt(spl[3]))) : 255;
            return new Color(r, g, b, a);
        } catch (NumberFormatException x) {
            Logger.getLogger(ColorEditor.class.getName()).log(Level.WARNING,
                    "Invalid color "+s, x);
            return null;
        }
    }

    @Override
    protected void initEditorValue() {
        Color c = (Color) newValue;
        if (c == null) {
            if (panel != null) {
                rgbaValue.setText("");
                colorChooserCombo.setBackground(panel.getBackground());
            }
            return;
        }
        color = c;
        if (panel != null) {
            rgbaValue.setText(getAsText(c));
            colorChooserCombo.setBackground(c);
        }
    }

    /** combo-like rect button */
    private class ChooserComboButton extends JButton {
        ChooserComboPopup popup;

        ChooserComboButton() {
            super("");
            popup = new ChooserComboPopup(ColorEditor.this);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    popup.show(e.getComponent(), 0, 0);
                }
            });
            popup.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    setNewValue(getBackground());
                    initEditorValue();
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            paintValue(g, new Rectangle(0, 0, getWidth(), getHeight()));
        }
    }
}
