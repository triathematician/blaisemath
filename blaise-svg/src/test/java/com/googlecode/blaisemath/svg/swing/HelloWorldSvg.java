package com.googlecode.blaisemath.svg.swing;

/*
 * #%L
 * blaise-svg
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


import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.render.SvgRenderer;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import static com.googlecode.blaisemath.svg.swing.BlaiseGraphicsTestApp.printAndCopyToClipboard;

public class HelloWorldSvg extends JFrame {

    public HelloWorldSvg() {
        JGraphicComponent gc = new JGraphicComponent();
        Graphic g1 = JGraphics.path(new Rectangle2D.Double(0, 0, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f));
        Graphic g2 = JGraphics.path(new Rectangle2D.Double(500, 500, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f));
        Graphic g3 = JGraphics.path(new Rectangle2D.Double(-500, -500, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f));
        gc.getGraphicRoot().setGraphics((List) Arrays.asList(g1, g2, g3));
        PanAndZoomHandler.install(gc);

        JPanel p = new JPanel(new BorderLayout());
        p.add(gc, BorderLayout.CENTER);
        JToolBar tb = new JToolBar();
        tb.add(new AbstractAction("Load test"){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String svg = "<svg viewBox=\"75 0 150 200\" height=\"200\" width=\"200\">"
                            + "<path style=\"fill:#ff0000\" d=\"M150 0 L75 200 L225 200 Z\"/>"
                            + "<path style=\"fill:#0000ff\" d=\"M150 0 L75 200 L05 100 Z\"/>"
                            + "</svg>";
                    SvgRootGraphic gfc = SvgRootGraphic.create(svg);
                    gfc.setBoundingBoxVisible(true);
                    gfc.setRenderViewBox(true);
                    gfc.setRenderViewport(true);
                    gfc.setViewport(new Rectangle2D.Double(50, 50, 400, 300));
                    gc.getGraphicRoot().setGraphics((List) Arrays.asList(g1, g2, g3, gfc));
                } catch (IOException x) {
                    // unexpected
                }
            }
        });
        tb.add(new AbstractAction("Print SVG"){
            @Override
            public void actionPerformed(ActionEvent e) {
                SvgRoot root = SvgRenderer.componentToSvg(gc);
                try {
                    printAndCopyToClipboard(SvgIo.writeToString(root));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        p.add(tb, BorderLayout.NORTH);
        
        setContentPane(p);
        setSize(new Dimension(800, 800));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new HelloWorldSvg().setVisible(true));
    }
    
}
