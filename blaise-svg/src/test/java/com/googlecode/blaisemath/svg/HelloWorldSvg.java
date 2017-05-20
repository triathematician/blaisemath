package com.googlecode.blaisemath.svg;

import com.googlecode.blaisemath.graphics.svg.SVGGraphicComponent;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author elisha
 */
public class HelloWorldSvg extends JFrame {

    public HelloWorldSvg() {
        try {
            String svg = "<svg height=\"210\" width=\"400\"><path style=\"fill:#ff0000\" d=\"M150 0 L75 200 L225 200 Z\"/></svg>";
            SVGRoot root = SVGRoot.load(svg);
            SVGGraphicComponent comp = SVGGraphicComponent.create(root);
            comp.setPreferredSize(new Dimension(401,211));
            setContentPane(comp);
        } catch (IOException ex) {
            Logger.getLogger(HelloWorldSvg.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        String svg = "<svg height=\"210\" width=\"400\"><path style=\"fill:#ff0000\" d=\"M150 0 L75 200 L225 200 Z\"/></svg>";
//        SVGGraphicComponent comp = SVGGraphicComponent.create(svg);
//        setContentPane(comp);
        
//        String svg = "<svg viewBox=\"75 0 150 200\" height=\"210\" width=\"400\"><path style=\"fill:#ff0000\" d=\"M150 0 L75 200 L225 200 Z\"/></svg>";
//        SVGGraphic gfc = SVGGraphic.create(svg);
//        gfc.setGraphicBounds(new Rectangle(50, 50, 100, 100));
//        JGraphicComponent gc = new JGraphicComponent();
//        gc.addGraphic(gfc);
//        setSize(new Dimension(400, 400));
//        setContentPane(gc);
        
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new HelloWorldSvg().setVisible(true));
    }
    
}
