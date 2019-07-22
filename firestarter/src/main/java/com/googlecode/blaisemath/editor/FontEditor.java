package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
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
public final class FontEditor extends MPanelEditorSupport {

    /** List of font sizes to show in dropdown */
    private static final int[] PT_SIZES = {3, 5, 8, 9, 10, 11, 12, 14, 18, 24, 36, 48, 72, 96, 108, 120};
    /** Static list of fonts. Will be loaded only once. */
    private static final List<String> FONTS = new ArrayList<>();
    
    private static boolean loadStarted = false;
    private static boolean fontsLoaded = false;

    private JComboBox familyNameCombo;
    private JComboBox fontSizeCombo;
    private JComboBox styleCombo;
    
    private boolean editing = false;

    @Override
    public void initCustomizer() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        initializeComboBoxes();

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(panel.getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 1, 0, 1);
        p.add(familyNameCombo, gbc);
        gbc.weightx = .3;
        p.add(fontSizeCombo, gbc);
        gbc.weightx = .5;
        p.add(styleCombo, gbc);
        panel.add(p);
    }
    
    
    /**
     * Creates the ComboBoxes. The fons and point sizes must be initialized.
     */
    private void initializeComboBoxes() {
        if (fontsLoaded) {
            familyNameCombo = new JComboBox(FONTS.toArray());
        } else {
            familyNameCombo = new JComboBox(new Object[]{"<html><i>Loading fonts...</i>"});
            familyNameCombo.setEnabled(false);
        }

        familyNameCombo.setMinimumSize(new Dimension(60, 0));
        familyNameCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        familyNameCombo.setAlignmentY(Component.CENTER_ALIGNMENT);

        fontSizeCombo = new JComboBox();
        for (int ptSize : PT_SIZES) {
            fontSizeCombo.addItem("" + ptSize);
        }
        fontSizeCombo.setMinimumSize(new Dimension(40, 0));
        fontSizeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        fontSizeCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        styleCombo = new JComboBox(new Object[]{Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD+Font.ITALIC});
        styleCombo.setRenderer(new StyleComboRenderer());
        styleCombo.setMinimumSize(new Dimension(50, 0));
        styleCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        ActionListener comboListener = e -> handleComboChange();
        familyNameCombo.addActionListener(comboListener);
        fontSizeCombo.addActionListener(comboListener);
        styleCombo.addActionListener(comboListener);
        
        loadFonts();
    }

    private void handleComboChange() {
        if (editing) {
            return;
        }
        String family = familyNameCombo.getSelectedIndex() == -1 
                ? newValue == null ? "Dialog" : ((Font) newValue).getFamily()
                : (String) familyNameCombo.getSelectedItem();
        int size = fontSizeCombo.getSelectedIndex() == -1 ? 12
                : PT_SIZES[fontSizeCombo.getSelectedIndex()];
        int style = styleCombo.getSelectedIndex() == -1 ? Font.PLAIN
                : (Integer) styleCombo.getSelectedItem();
        setNewValue(new Font(family, style, size));
        initEditorValue();
    }
    
    /** Loads font names in a background thread so GUI isn't blocked */
    private synchronized void loadFonts() {
        if (fontsLoaded || loadStarted) {
            return;
        }
        loadStarted = true;
        final java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                loadFontsInBackground();
                t.cancel();
            }
        }, 0);
    }
    
    private void loadFontsInBackground() {
        String[] ff = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        synchronized(FONTS) {
            FONTS.addAll(Arrays.asList(ff));
            if (newValue != null) {
                SwingUtilities.invokeLater(() -> {
                    familyNameCombo.setModel(new DefaultComboBoxModel(FONTS.toArray()));
                    familyNameCombo.setEnabled(true);
                    familyNameCombo.setSelectedItem(((Font)newValue).getFamily());
                });
            }
            fontsLoaded = true;
            panel.revalidate();
            panel.repaint();
        }
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
        if (panel == null) {
            return;
        }
        
        editing = true;
        if (newValue == null) {
            familyNameCombo.setSelectedIndex(-1);
            fontSizeCombo.setSelectedIndex(-1);
            styleCombo.setSelectedIndex(-1);
        } else {
            Font font = (Font) newValue;
            familyNameCombo.setSelectedItem(font.getFamily());
            styleCombo.setSelectedItem(font.getStyle());

            for (int i = 0; i < PT_SIZES.length; i++) {
                if (font.getSize() <= PT_SIZES[i]) {
                    fontSizeCombo.setSelectedIndex(i);
                    break;
                }
            }

            panel.revalidate();
            panel.repaint();
        }
        editing = false;
    }
    
    private static class StyleComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Integer iVal = (Integer) value;
            if (iVal == null) {
                return this;
            }
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
                default:
                    throw new IllegalStateException();
            }
            return this;
        }
    }

}
