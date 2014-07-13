/*
 * FontEditor.java
 * Created Nov 8, 2011
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Editor for fonts.
 */
public class FontEditor extends MPanelEditorSupport implements ActionListener {

    public static final Dimension FAMILY_COMBO_DIM = new Dimension(80, 20);
    public static final Dimension FONT_SIZE_DIM = new Dimension(40, 20);
    public static final Dimension STYLE_DIM = new Dimension(55, 20);
    private static final int[] PT_SIZES = {3, 5, 8, 10, 12, 14, 18, 24, 36, 48};
    
    private static Vector fonts = new Vector();
    private static boolean loadStarted = false;
    private static boolean fontsLoaded = false;

    private JComboBox familyNameCombo;
    private JComboBox fontSizeCombo;
    private JComboBox styleCombo;

    public FontEditor() {
    }

    public void initCustomizer() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        initializeComboBoxes();

        JPanel p = new JPanel();
        p.setBackground(panel.getBackground());
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(familyNameCombo);
        p.add(Box.createRigidArea(new Dimension(3, 0)));
        p.add(fontSizeCombo);
        p.add(Box.createRigidArea(new Dimension(3, 0)));
        p.add(styleCombo);
        panel.add(p);
    }
    
    
    /**
     * Creates the ComboBoxes. The fons and point sizes must be initialized.
     */
    private void initializeComboBoxes() {
        if (fontsLoaded)
            familyNameCombo = new JComboBox(fonts);
        else {
            familyNameCombo = new JComboBox(new Object[]{"<html><i>Loading fonts...</i>"});
            familyNameCombo.setEnabled(false);
        }

        familyNameCombo.setPreferredSize(FAMILY_COMBO_DIM);
        familyNameCombo.setMinimumSize(FAMILY_COMBO_DIM);
        familyNameCombo.setMaximumSize(FAMILY_COMBO_DIM);
        familyNameCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        familyNameCombo.setAlignmentY(Component.CENTER_ALIGNMENT);

        fontSizeCombo = new JComboBox();
        for (int i = 0; i < PT_SIZES.length; i++)
            fontSizeCombo.addItem("" + PT_SIZES[i]);
        fontSizeCombo.setPreferredSize(FONT_SIZE_DIM);
        fontSizeCombo.setMaximumSize(FONT_SIZE_DIM);
        fontSizeCombo.setMinimumSize(FONT_SIZE_DIM);
        fontSizeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        fontSizeCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        styleCombo = new JComboBox(new Object[]{Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD+Font.ITALIC});
        styleCombo.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Integer iVal = (Integer) value;
                if (iVal == null)
                    return this;
                switch (iVal) {
                    case Font.PLAIN:
                        setText("Plain");
                        break;
                    case Font.BOLD:
                        setText("<html><font style=\"color:black\"><b>Bold</b></font>");
                        break;
                    case Font.ITALIC:
                        setText("<html><font style=\"color:black\"><i>Italic</i></font>");
                        break;
                    case Font.BOLD+Font.ITALIC:
                        setText("<html><font style=\"color:black\"><b><i>Bold Italic</i></b></font>");
                        break;
                }
                return this;
            }
        });
        styleCombo.setMinimumSize(STYLE_DIM);
        styleCombo.setMaximumSize(STYLE_DIM);
        styleCombo.setPreferredSize(STYLE_DIM);
        styleCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        familyNameCombo.addActionListener(this);
        fontSizeCombo.addActionListener(this);
        styleCombo.addActionListener(this);
        
        loadFonts();
    }
    
    /** Loads font names in a background thread so GUI isn't blocked */
    private synchronized void loadFonts() {
        if (fontsLoaded || loadStarted)
            return;
        loadStarted = true;
        final java.util.Timer t = new java.util.Timer();
        final java.util.TimerTask tt = new java.util.TimerTask(){
            @Override
            public void run() {
                String[] ff = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
                synchronized(fonts) {
                    fonts.addAll(Arrays.asList(ff));
                    if (newValue != null) {
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run() {
                                familyNameCombo.setModel(new DefaultComboBoxModel(fonts));
                                familyNameCombo.setEnabled(true);
                                familyNameCombo.setSelectedItem(((Font)newValue).getFamily());
                            }
                        });
                    }
                    fontsLoaded = true;
                    panel.revalidate();
                    panel.repaint();
                }
            }
        };
        t.schedule(tt, 0);
    }
    
    private boolean editing = false;

    public void actionPerformed(ActionEvent evt) {
        if (editing)
            return;
        Object obj = evt.getSource();
        String family = newValue == null ? "Dialog" : ((Font) newValue).getFamily();
        int size = 12;
        int style = Font.PLAIN;

        if (obj instanceof JComboBox) {
            family = familyNameCombo.getSelectedIndex() == -1 ? family
                    : (String) familyNameCombo.getSelectedItem();
            size = fontSizeCombo.getSelectedIndex() == -1 ? size
                    : PT_SIZES[fontSizeCombo.getSelectedIndex()];
            style = styleCombo.getSelectedIndex() == -1 ? style
                    : (Integer) styleCombo.getSelectedItem();
        }
        setNewValue(new Font(family, style, size));
        initEditorValue();
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics g, Rectangle rect) {
        Font oldFont = g.getFont();
        g.setFont((Font) getValue());
        FontMetrics fm = g.getFontMetrics();
        int vpad = (rect.height - fm.getAscent()) / 2;
        g.drawString("Abcde", 0, rect.height - vpad);
        g.setFont(oldFont);
    }

    @Override
    public String getJavaInitializationString() {
        Font font = (Font) getValue();

        return "new java.awt.Font(\"" + font.getFamily() + "\", " +
                font.getStyle() + ", " + font.getSize() + ")";
    }

    @Override
    protected void initEditorValue() {
        editing = true;
        if (panel != null) {
            Font font = (Font) newValue;
            
            if (font == null) {
                familyNameCombo.setSelectedIndex(-1);
                fontSizeCombo.setSelectedIndex(-1);
                styleCombo.setSelectedIndex(-1);
            } else {
                familyNameCombo.setSelectedItem(font.getFamily());
                styleCombo.setSelectedItem(font.getStyle());

                for (int i = 0; i < PT_SIZES.length; i++) {
                    if (font.getSize() <= PT_SIZES[i]) {
                        if (fontSizeCombo.getSelectedIndex() != i)
                            fontSizeCombo.setSelectedIndex(i);
                        break;
                    }
                }

                panel.revalidate();
                panel.repaint();
            }
        }
        editing = false;
    }

}
