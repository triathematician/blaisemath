/*
 * BlaiseGraphicsTestApp.java
 */

package com.googlecode.blaisemath.graphics.testui;

/*
 * #%L
 * BlaiseGraphics
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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.dev.compoundgraphics.LabeledPointGraphic;
import com.googlecode.blaisemath.dev.compoundgraphics.SegmentGraphic;
import com.googlecode.blaisemath.dev.compoundgraphics.TwoPointGraphicSupport;
import com.googlecode.blaisemath.graphics.core.BasicPointSetGraphic;
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.core.DelegatingPointSetGraphic;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.HighlightOnMouseoverHandler;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.ArrowPathRenderer;
import com.googlecode.blaisemath.graphics.swing.ArrowPathRenderer.ArrowLocation;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.MarkerRendererToClip;
import com.googlecode.blaisemath.graphics.swing.PointRenderer;
import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.CanvasPainter;
import com.googlecode.blaisemath.util.ContextMenuInitializer;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.geom.LabeledPoint;
import com.googlecode.blaisemath.util.geom.PointUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class BlaiseGraphicsTestApp extends SingleFrameApplication {
    
    JGraphicRoot root1;
    JGraphicComponent canvas1;
    AttributeSet pointsetStyle = (AttributeSet) RandomStyles.point();
    
    //<editor-fold defaultstate="collapsed" desc="GENERAL">
    
    @Action
    public void clear1() {
        root1.clearGraphics();
    }
    
    private Point2D randomPoint() {
        return new Point2D.Double(Math.random()*canvas1.getWidth(), Math.random()*canvas1.getHeight());
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="BASIC">
    
    @Action
    public void addPoint() {        
        Point2D pt = randomPoint();
        PrimitiveGraphic bp = JGraphics.point(pt, RandomStyles.point());
        bp.setDefaultTooltip("<html><b>Point</b>: <i> " + pt + "</i>");
        bp.setDragEnabled(true);
        root1.addGraphic(bp);
    }
    
    @Action
    public void addSegment() {        
        Line2D.Double line = new Line2D.Double(randomPoint(), randomPoint());
        PrimitiveGraphic bs = JGraphics.path(line, RandomStyles.path());
        bs.setDefaultTooltip("<html><b>Segment</b>: <i>" + line + "</i>");
        root1.addGraphic(bs);
    }
    
    @Action
    public void addRectangle() {        
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal(randomPoint(), randomPoint());
        PrimitiveGraphic bs = JGraphics.shape(rect, RandomStyles.shape());
        bs.setDefaultTooltip("<html><b>Rectangle</b>: <i>" + rect + "</i>");
        root1.addGraphic(bs);
    }
    
    @Action
    public void addString() {        
        Point2D pt = randomPoint();
        LabeledPoint txt = new LabeledPoint(pt, String.format("[%.4f, %.4f]", pt.getX(), pt.getY()));
        PrimitiveGraphic bg = JGraphics.text(txt, RandomStyles.string());
        bg.setDragEnabled(true);
        root1.addGraphic(bg);
    }
    
    @Action
    public void addPointSet() {      
        final BasicPointSetGraphic bp = new BasicPointSetGraphic(
                new Point2D[]{randomPoint(), randomPoint(), randomPoint()},
                this.pointsetStyle, PointRenderer.getInstance());
        bp.addContextMenuInitializer(new ContextMenuInitializer<Graphic<Graphics2D>>(){
            public void initContextMenu(JPopupMenu menu, Graphic<Graphics2D> src, Point2D point, Object focus, Set selection) {
                Point2D pt = bp.getPoint(bp.indexOf(point));
                menu.add(PointUtils.formatPoint(pt, 2));
                menu.add(getContext().getActionMap().get("editPointSetStyle"));
            }
        });
        root1.addGraphic(bp);
    }
    
    @Action
    public void editPointSetStyle() {        
        BasicPointStyleEditor ed = new BasicPointStyleEditor(pointsetStyle);
        ed.addPropertyChangeListener("style", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) { canvas1.repaint(); }
        });
        JOptionPane.showMessageDialog(getMainFrame(), ed);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="GRAPHICS WITH DELEGATORS">
        
    @Action
    public void addDelegatingPointSet() {
        Set<String> list = new HashSet<String>(Arrays.asList("Africa", "Indiana Jones", "Micah Andrew Peterson", "Chrysanthemum", "Sequoia", "Asher Matthew Peterson", "Elisha", "Bob the Builder"));
        Map<String,Point2D> crds = Maps.newLinkedHashMap();
        for (String s : list) {
            crds.put(s, new Point(10*s.length(), 50 + 10*s.indexOf(" ")));
        }
        final DelegatingPointSetGraphic<String,Graphics2D> bp = new DelegatingPointSetGraphic<String,Graphics2D>();
        // TODO - these should be labeled
        bp.addObjects(crds);
        bp.getStyler().setLabelDelegate(new Function<String, String>() {
            public String apply(String src) {
                return src;
            }
        });
        bp.getStyler().setStyleDelegate(new Function<String,AttributeSet>(){
            AttributeSet r = new AttributeSet();
            public AttributeSet apply(String src) {
                int i1 = src.indexOf("a"), i2 = src.indexOf("e"), i3 = src.indexOf("i"), i4 = src.indexOf("o");
                r.put(Styles.MARKER_RADIUS, i1+5);
                r.put(Styles.MARKER, Markers.getAvailableMarkers().get(i2+3));
                r.put(Styles.STROKE_WIDTH, 2+i3/3f);
                r.put(Styles.FILL, new Color((i4*10+10) % 255, (i4*20+25) % 255, (i4*30+50) % 255));
                return r;
            }
        });
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingPointSet2() {
        Map<Integer,Point2D> points2 = Maps.newLinkedHashMap();
        for (int i = 1; i <= 10; i++) {
            points2.put(i, randomPoint());
        }
        final DelegatingPointSetGraphic<Integer,Graphics2D> bp = new DelegatingPointSetGraphic<Integer,Graphics2D>();
        bp.addObjects(points2);
        bp.getStyler().setLabelDelegate(Functions.toStringFunction());
        bp.getStyler().setStyleDelegate(new Function<Integer,AttributeSet>(){
            AttributeSet r = new AttributeSet();
            public AttributeSet apply(Integer src) {
                r.put(Styles.TEXT_ANCHOR, Anchor.CENTER);
                r.put(Styles.MARKER_RADIUS, src+2);
                r.put(Styles.FILL, new Color((src*10+10) % 255, (src*20+25) % 255, (src*30+50) % 255));
                r.put(Styles.FONT_SIZE, 5+src.floatValue());
                return r;
            }
        });
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingGraph() {
        // initialize graph object
        Point2D[] pts = new Point2D[15];
        for (int i = 0; i < pts.length; i++) {
            pts[i] = randomPoint();
        }         
        Set<Edge<Point2D>> edges = new HashSet<Edge<Point2D>>();
        for (int i = 0; i < pts.length; i++) {
            int n = (int) (Math.random()*6);
            for (int j = 0; j < n; j++) {
                edges.add(new Edge<Point2D>(pts[i], pts[(int)(Math.random()*pts.length)]));
            }
        }
        // create graphic
        DelegatingNodeLinkGraphic<Point2D,Edge<Point2D>,Graphics2D> gr = new DelegatingNodeLinkGraphic<Point2D,Edge<Point2D>,Graphics2D>();
        ImmutableMap<Point2D, Point2D> idx = Maps.uniqueIndex(Arrays.asList(pts), Functions.<Point2D>identity());
        gr.setNodeLocations(idx);
        gr.getNodeStyler().setStyleDelegate(new Function<Point2D,AttributeSet>(){
            public AttributeSet apply(Point2D src) {
                int yy = (int) Math.min(src.getX()/3, 255);
                return AttributeSet.with(Styles.FILL, new Color(yy, 0, 255-yy))
                        .and(Styles.MARKER_RADIUS, (float) Math.sqrt(src.getY()));
            }
        });
        gr.getNodeStyler().setLabelDelegate(new Function<Point2D, String>() {
            public String apply(Point2D src) {
                return String.format("(%.1f,%.1f)", src.getX(), src.getY());
            }
        });
        gr.getNodeStyler().setLabelStyleDelegate(new Function<Point2D, AttributeSet>(){
            AttributeSet bss = Styles.defaultTextStyle();
            public AttributeSet apply(Point2D src) {
                return bss;                
            }
        });
        gr.setEdgeSet(edges);
        gr.getEdgeStyler().setStyleDelegate(new Function<Edge<Point2D>,AttributeSet>(){
            public AttributeSet apply(Edge<Point2D> src) {
                Point2D src0 = src.getNode1(), src1 = src.getNode2();
                int dx = (int) (src0.getX() - src1.getX());
                dx = Math.min(Math.abs(dx/2), 255);
                int dy = (int) (src0.getY() - src1.getY());
                dy = Math.min(Math.abs(dy/3), 255);
                
                return AttributeSet.with(Styles.STROKE, new Color(dx, dy, 255-dy))
                        .and(Styles.STROKE_WIDTH, (float) Math.sqrt(dx*dx+dy*dy)/50);
            }
        });
        root1.addGraphic(gr);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="COMPOSITES">
    
    @Action
    public void addArrow() {      
        Point2D p1 = randomPoint(), p2 = randomPoint();
        SegmentGraphic ag = new SegmentGraphic(p1, p2);
        ag.setDefaultTooltip("<html><b>Segment</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);
    }
    
    @Action
    public void addLabeledPoint() {
        Point2D p1 = randomPoint();
        LabeledPointGraphic lpg = new LabeledPointGraphic(p1,
                String.format("(%.2f,%.2f)", p1.getX(), p1.getY())
                );
        lpg.setDefaultTooltip("<html><b>Labeled Point</b>: <i> " + p1 + "</i>");
        lpg.setDragEnabled(true);
        root1.addGraphic(lpg);        
    }
    
    @Action
    public void add2Point() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
        ag.setDefaultTooltip("<html><b>Two Points</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);        
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="COOL STUFF USING SPECIAL STYLES">
    
    @Action
    public void addLabeledPointSet() {   
        BasicPointSetGraphic bp = new BasicPointSetGraphic(new Point2D[]{randomPoint(), randomPoint(), randomPoint(), randomPoint()});
        root1.addGraphic(bp);
    }
    
    @Action
    public void addRay() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
        MarkerRendererToClip rend = new MarkerRendererToClip();
        rend.setRayRenderer(new ArrowPathRenderer());
        ag.getEndGraphic().setRenderer(rend);
        ag.setDefaultTooltip("<html><b>Ray</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);        
    }
    
    @Action
    public void addLine() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
        MarkerRendererToClip rend = new MarkerRendererToClip();
        rend.setRayRenderer(new ArrowPathRenderer(ArrowLocation.BOTH));
        rend.setExtendBothDirections(true);
        ag.getStartGraphic().setRenderer(rend);
        ag.setDefaultTooltip("<html><b>Line</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);        
    }
    
    //</editor-fold>
    
        
    //<editor-fold defaultstate="collapsed" desc="APP CODE">

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        BlaiseGraphicsTestFrameView view = new BlaiseGraphicsTestFrameView(this);
        canvas1 = view.canvas1;
        root1 = view.canvas1.getGraphicRoot();
        canvas1.getUnderlays().add(new CanvasPainter<Graphics2D>(){
            public void paint(Component component, Graphics2D canvas) {
                canvas.setColor(new Color(200,200,255));
                canvas.setStroke(new BasicStroke());
//                canvas.drawRect(0, 0, 500, 500);
//
//                if (canvas1.getTransform() != null) {
//                    canvas.setTransform(canvas1.getTransform());
//                }
//                Rectangle r = canvas.getClipBounds();
//                
//                int st = 0;
//                for (int x = st; x <= r.getMaxX()+100; x+=100) {
//                    canvas.draw(new Line2D.Double(x, r.y, x, r.getMaxY()));
//                }
//                
//                int sty = 0;
//                for (int y = sty; y <= r.getMaxY()+100; y+=100) {
//                    canvas.draw(new Line2D.Double(r.x, y, (int) r.getMaxX(), y));
//                }
//                canvas.setTransform(new AffineTransform());
            }
        });
        show(view);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of BlaiseGraphicsTestApp
     */
    public static BlaiseGraphicsTestApp getApplication() {
        return Application.getInstance(BlaiseGraphicsTestApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(BlaiseGraphicsTestApp.class, args);
    }
    
    //</editor-fold>
    
    
}
