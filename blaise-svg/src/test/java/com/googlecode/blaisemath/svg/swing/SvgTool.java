package com.googlecode.blaisemath.svg.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import com.google.common.io.Files;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.render.SvgRenderer;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgPath;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SvgTool extends javax.swing.JFrame {
    private static final Logger LOG = Logger.getLogger(SvgTool.class.getName());

    private SvgRootGraphic gsvg = new SvgRootGraphic();
    private SvgGraphicComponent svgComponent = new SvgGraphicComponent();
    private SvgGraphicComponent svgComponent2 = new SvgGraphicComponent();
    private SvgIcon icon = new SvgIcon();

    private final JFileChooser chooser = new JFileChooser();
    private JTextArea text = new JTextArea();
    private JGraphicComponent canvas;

    public SvgTool() {
        initComponents();
        setMinimumSize(new Dimension(400,400));
        setPreferredSize(new Dimension(500,500));
        setMaximumSize(new Dimension(600,600));

        icon.setRenderViewport(true);
        icon.setRenderViewBox(true);
        svgComponent.setRenderViewport(true);
        svgComponent.setRenderViewBox(true);
        svgComponent2.setRenderViewport(true);
        svgComponent2.setRenderViewBox(true);

        gsvg.setStyle(Styles.strokeWidth(Color.blue, 2f));
        gsvg.setRenderViewport(true);
        gsvg.setRenderViewBox(true);
        gsvg.setRenderBackground(true);
        canvas.addGraphic(gsvg);

        canvas.addGraphic(JGraphics.path(new Rectangle2D.Double(0, 0, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f)));
        canvas.addGraphic(JGraphics.path(new Rectangle2D.Double(500, 500, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f)));
        canvas.addGraphic(JGraphics.path(new Rectangle2D.Double(-500, -500, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f)));

        PanAndZoomHandler.install(canvas);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JToolBar tb1 = new JToolBar();
        tb1.setRollover(true);
        tb1.setFloatable(false);
        tb1.add(button("Load", this::loadButtonAction));
        tb1.add(button("Save", this::saveButtonAction));
        tb1.add(button("Print", this::printButtonAction));
        tb1.add(button("Reset Graphic Bounds", this::boundButtonAction));
        getContentPane().add(tb1, java.awt.BorderLayout.PAGE_START);

        // initialize input panel side
        JToolBar tb2 = new JToolBar();
        tb2.setRollover(true);
        tb2.setFloatable(false);
        tb2.add(button("Draw Path", this::drawPathButtonAction));
        tb2.add(button("Draw Graphic", this::drawGraphicButtonAction));

        text = new JTextArea();
        text.setColumns(20);
        text.setRows(5);

        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(new JScrollPane(text), java.awt.BorderLayout.CENTER);
        panel.add(tb2, java.awt.BorderLayout.PAGE_START);

        // initialize graphics view side
        canvas = new JGraphicComponent();

        JTabbedPane tabs = new JTabbedPane();
        tabs.add(canvas, "Graphic in Canvas");
        JPanel p = new JPanel(new GridLayout(1, 2));
        p.add(svgComponent);
        JPanel p2 = new JPanel(new FlowLayout());
        p2.add(svgComponent2);
        p.add(p2);
        tabs.add(p, "Entire Canvas");
        tabs.add(new JLabel(icon), "Icon");

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.7);
        splitPane.setLeftComponent(tabs);
        splitPane.setRightComponent(panel);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);
        pack();
    }

    private static JButton button(String text, ActionListener l) {
        JButton b = new JButton(text);
        b.addActionListener(l);
        return b;
    }

    private void drawPathButtonAction(java.awt.event.ActionEvent evt) {
        gsvg.setElement(SvgRoot.create(SvgPath.create(text.getText())));
    }

    private void drawGraphicButtonAction(ActionEvent evt) {
        try {
            SvgRoot root = SvgIo.read(text.getText());
            gsvg.setElement(root);
            svgComponent.setElement(root);
            svgComponent2.setElement(root);
            icon.setElement(root);
            icon.setIconWidth(100);
            icon.setIconHeight(100);
            repaint();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveButtonAction(java.awt.event.ActionEvent evt) {
        if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) {
            try (FileOutputStream out = new FileOutputStream(chooser.getSelectedFile())) {
                SvgRoot el = gsvg.getElement();
                SvgIo.write(el, out);
            } catch (IOException x) {
                LOG.log(Level.SEVERE, null, x);
            }
        }
    }

    private void printButtonAction(java.awt.event.ActionEvent evt) {
        SvgRoot root = SvgRenderer.componentToSvg(canvas);
        try {
            BlaiseGraphicsTestApp.printAndCopyToClipboard(SvgIo.writeToString(root));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadButtonAction(java.awt.event.ActionEvent evt) {
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            try (FileInputStream fis = new FileInputStream(chooser.getSelectedFile())) {
                SvgRoot r = SvgIo.read(fis);
                gsvg.setElement(r);
                String fs = Files.asCharSource(chooser.getSelectedFile(), Charset.defaultCharset()).read();
                text.setText(fs);
            } catch (IOException x) {
                LOG.log(Level.SEVERE, null, x);
            }
        }
    }

    private void boundButtonAction(java.awt.event.ActionEvent evt) {
        gsvg.setViewport(new Rectangle(20, 50, 40, 100));
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new SvgTool().setVisible(true));
    }
}
