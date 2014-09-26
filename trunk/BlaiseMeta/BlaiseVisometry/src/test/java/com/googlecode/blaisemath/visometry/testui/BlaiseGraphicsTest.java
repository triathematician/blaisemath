/*
 * BlaiseGraphicsTest.java
 */

package com.googlecode.blaisemath.visometry.testui;

/*
 * #%L
 * BlaiseVisometry
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
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.graphics.core.BasicPointSetGraphic;
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.core.DelegatingPointSetGraphic;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.BasicPointStyleEditor;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.PathRenderer;
import com.googlecode.blaisemath.graphics.swing.PointRenderer;
import com.googlecode.blaisemath.graphics.swing.TextRenderer;
import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.ColorUtils;
import com.googlecode.blaisemath.util.ContextMenuInitializer;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.geom.AnchoredText;
import com.googlecode.blaisemath.visometry.VBasicPoint;
import com.googlecode.blaisemath.visometry.VBasicPointSet;
import com.googlecode.blaisemath.visometry.VBasicPolygonalPath;
import com.googlecode.blaisemath.visometry.VCustomGraph;
import com.googlecode.blaisemath.visometry.VCustomPointSet;
import com.googlecode.blaisemath.visometry.VGraphicRoot;
import com.googlecode.blaisemath.visometry.plane.PlanePlotComponent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
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
public class BlaiseGraphicsTest extends SingleFrameApplication {

    JGraphicRoot root1;
    VGraphicRoot<Point2D.Double,Graphics2D> root2;
    JGraphicComponent canvas1;
    PlanePlotComponent canvas2;
    AttributeSet bps = RandomStyles.point();

    //<editor-fold defaultstate="collapsed" desc="GENERAL">

    @Action
    public void clear1() {
        root1.clearGraphics();
    }

    @Action
    public void clear2() {
        root2.clearGraphics();
    }

    private Point2D randomPoint() {
        return new Point2D.Double(Math.random()*canvas1.getWidth(), Math.random()*canvas1.getHeight());
    }
    private Point2D randomPoint2() {
        return new Point2D.Double(Math.random()*canvas2.getWidth(), Math.random()*canvas2.getHeight());
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="BASIC">

    @Action
    public void addPoint() {
        Point2D pt = randomPoint();
        PrimitiveGraphic<Point2D,Graphics2D> bp = JGraphics.point(pt, RandomStyles.point());
        bp.setDefaultTooltip("<html><b>Point</b>: <i> " + pt + "</i>");
        root1.addGraphic(bp);
    }

    @Action
    public void addPointSet() {
        BasicPointSetGraphic<Graphics2D> bp = new BasicPointSetGraphic<Graphics2D>(
                new Point2D[]{randomPoint(), randomPoint(), randomPoint()},
                this.bps, PointRenderer.getInstance());
        bp.addContextMenuInitializer(new ContextMenuInitializer<Graphic<Graphics2D>>(){
            public void initContextMenu(JPopupMenu menu, Graphic<Graphics2D> src, Point2D point, Object focus, Set selection) {
                menu.add(getContext().getActionMap().get("editPointSetStyle"));
            }
        });
        root1.addGraphic(bp);
    }

    @Action
    public void editPointSetStyle() {
        BasicPointStyleEditor ed = new BasicPointStyleEditor(bps);
        ed.addPropertyChangeListener("style", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) { canvas1.repaint(); }
        });
        JOptionPane.showMessageDialog(getMainFrame(), ed);
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
        AnchoredText txt = new AnchoredText(pt, String.format("[%.4f, %.4f]", pt.getX(), pt.getY()));
        PrimitiveGraphic bg = JGraphics.text(txt, RandomStyles.string());
        bg.setDragEnabled(true);
        root1.addGraphic(bg);

    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="GRAPHICS WITH DELEGATORS">
        
    @Action
    public void addDelegatingPointSet() {
        Set<String> list = new HashSet<String>(Arrays.asList(
                "Africa", "Indiana Jones", "Micah Andrew Peterson", "Chrysanthemum", 
                "Sequoia", "Asher Matthew Peterson", "Elisha", "Bob the Builder"));
        Map<String,Point2D> crds = Maps.newLinkedHashMap();
        for (String s : list) {
            crds.put(s, new Point(10*s.length(), 50 + 10*s.indexOf(" ")));
        }
        DelegatingPointSetGraphic<String,Graphics2D> bp = new DelegatingPointSetGraphic<String,Graphics2D>(
                PointRenderer.getInstance(), TextRenderer.getInstance());
        bp.addObjects(crds);
        bp.setDragEnabled(true);
        bp.getStyler().setLabelDelegate(Functions.toStringFunction());
        bp.getStyler().setLabelStyleConstant(Styles.defaultTextStyle());
        bp.getStyler().setStyleDelegate(new Function<String,AttributeSet>(){
            AttributeSet r = new AttributeSet();
            public AttributeSet apply(String src) {
                int i1 = src.indexOf("a"), i2 = src.indexOf("e"), i3 = src.indexOf("i"), i4 = src.indexOf("o");
                r.put(Styles.MARKER_RADIUS, i1+5);
                r.put(Styles.MARKER, Markers.getAvailableMarkers().get(i2+3));
                r.put(Styles.STROKE, Color.BLACK);
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
        final DelegatingPointSetGraphic<Integer,Graphics2D> bp = new DelegatingPointSetGraphic<Integer,Graphics2D>(
                PointRenderer.getInstance(), TextRenderer.getInstance());
        bp.addObjects(points2);
        bp.setDragEnabled(true);
        bp.getStyler().setLabelDelegate(Functions.toStringFunction());
        bp.getStyler().setLabelStyleDelegate(new Function<Integer,AttributeSet>(){
            AttributeSet r = new AttributeSet();
            public AttributeSet apply(Integer src) {
                r.put(Styles.TEXT_ANCHOR, Anchor.CENTER);
                r.put(Styles.FONT_SIZE, 5+src.floatValue());
                return r;
            }
        });
        bp.getStyler().setStyleDelegate(new Function<Integer,AttributeSet>(){
            AttributeSet r = new AttributeSet();
            public AttributeSet apply(Integer src) {
                r.put(Styles.MARKER_RADIUS, src+2);
                r.put(Styles.FILL, new Color((src*10+10) % 255, (src*20+25) % 255, (src*30+50) % 255));
                r.put(Styles.STROKE, ColorUtils.lighterThan(r.getColor(Styles.FILL)));
                return r;
            }
        });
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingGraph() {
        // initialize graph object
        final Map<Integer,Point2D> pts = Maps.newLinkedHashMap();
        for (int i = 0; i < 15; i++) {
            pts.put(i, randomPoint());
        }         
        Set<Edge<Integer>> edges = new HashSet<Edge<Integer>>();
        for (int i = 0; i < pts.size(); i++) {
            int n = (int) (Math.random()*6);
            for (int j = 0; j < n; j++) {
                edges.add(new Edge<Integer>(i, (int)(Math.random()*pts.size())));
            }
        }
        // create graphic
        DelegatingNodeLinkGraphic<Integer,Edge<Integer>,Graphics2D> gr = JGraphics.nodeLink();
        gr.setDragEnabled(true);
        gr.setNodeLocations(pts);
        gr.getNodeStyler().setStyleDelegate(new Function<Integer,AttributeSet>(){
            public AttributeSet apply(Integer src) {
                Point2D pt = pts.get(src);
                int yy = (int) Math.min(pt.getX()/3, 255);
                return AttributeSet.with(Styles.FILL, new Color(yy, 0, 255-yy))
                        .and(Styles.MARKER_RADIUS, (float) Math.sqrt(pt.getY()));
            }
        });
        gr.getNodeStyler().setLabelDelegate(new Function<Integer, String>() {
            public String apply(Integer src) {
                Point2D pt = pts.get(src);
                return String.format("(%.1f,%.1f)", pt.getX(), pt.getY());
            }
        });
        gr.getNodeStyler().setLabelStyleDelegate(new Function<Integer, AttributeSet>(){
            AttributeSet bss = Styles.defaultTextStyle();
            public AttributeSet apply(Integer src) {
                return bss;                
            }
        });
        gr.setEdgeSet(edges);
        gr.getEdgeStyler().setStyleDelegate(new Function<Edge<Integer>,AttributeSet>(){
            public AttributeSet apply(Edge<Integer> src) {
                Point2D src0 = pts.get(src.getNode1()), src1 = pts.get(src.getNode2());
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


    //<editor-fold defaultstate="collapsed" desc="PLANE - BASIC">

    @Action
    public void addPlanePoint() {
        VBasicPoint<Point2D.Double,Graphics2D> bp = new VBasicPoint<Point2D.Double,Graphics2D>(
                new Point2D.Double(1+Math.random(), 1+Math.random()));
        bp.getWindowGraphic().setRenderer(PointRenderer.getInstance());
        bp.setStyle(Styles.defaultPointStyle());
        root2.addGraphic(bp);
    }

    @Action
    public void addPlanePointSet() {
        final Point2D.Double[] arr = new Point2D.Double[50];
        for (int i = 0; i < arr.length; i++)
            arr[i] = new Point2D.Double(-Math.random(), -Math.random());
        VBasicPointSet<Point2D.Double,Graphics2D> vps = new VBasicPointSet<Point2D.Double,Graphics2D>(arr);
        vps.getWindowGraphic().setRenderer(PointRenderer.getInstance());
        vps.setPointStyle(AttributeSet.with(Styles.FILL,Color.blue).and(Styles.MARKER_RADIUS,3f));
        root2.addGraphic(vps);
    }

    @Action
    public void addPlanePolygonalPath() {
        final Point2D.Double[] arr3 = new Point2D.Double[100];
        arr3[0] = new Point2D.Double(-1.0, 1.0);
        for (int i = 1; i < arr3.length; i++) {
            arr3[i] = new Point2D.Double(arr3[i-1].x + .02*Math.random(), arr3[i-1].y - .02*Math.random());
        }
        for (int i = 0; i < arr3.length; i++) {
            if (Math.random() < .1) arr3[i] = null;
        }
        VBasicPolygonalPath<Point2D.Double,Graphics2D> vps = new VBasicPolygonalPath<Point2D.Double,Graphics2D>(arr3);
        vps.getWindowGraphic().setRenderer(PathRenderer.getInstance());
        vps.setPathStyle(Styles.strokeWidth(Color.gray, 1.5f));
        root2.addGraphic(vps);
    }

    @Action
    public void addPlaneGraph() {
        JOptionPane.showMessageDialog(getMainFrame(), "TBD");
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="PLANE - DELEGATORS">


    @Action
    public void addCustomPlanePointSet() {
        Map<Integer, Point2D.Double> pts = Maps.newLinkedHashMap();
        for (int i = 0; i < 100; i++) {
            pts.put(i, new Point2D.Double(Math.log(1+i)*Math.cos(i/10.), Math.log(1+i)*Math.sin(i/10.)));
        }
        VCustomPointSet<Point2D.Double, Integer, Graphics2D> vps = new VCustomPointSet<Point2D.Double, Integer, Graphics2D>();
        vps.addObjects(pts);
        ((DelegatingPointSetGraphic)vps.getWindowGraphic()).setRenderer(PointRenderer.getInstance());
        vps.getPointStyler().setStyleDelegate(new Function<Integer, AttributeSet>(){
            public AttributeSet apply(Integer src) { 
                return Styles.fillStroke(new Color(255-src*2,0,src*2), null); 
            }
        });
        root2.addGraphic(vps);
    }

    @Action
    public void addCustomPlaneGraph() {
        Point2D.Double[] pts = new Point2D.Double[15];
        for (int i = 0; i < pts.length; i++) {
            pts[i] = new Point2D.Double(Math.log(1+i)*Math.cos(i/10.), Math.log(1+i)*Math.sin(i/10.));
        }
        Map<Point2D, Point2D.Double> pp = new HashMap<Point2D, Point2D.Double>();
        for (Point2D.Double p : pts) {
            pp.put(p, p);
        }
        VCustomGraph<Point2D.Double,Point2D,Edge<Point2D>,Graphics2D> gr 
                = new VCustomGraph<Point2D.Double,Point2D,Edge<Point2D>,Graphics2D>();
        gr.addObjects(pp);
        Set<Edge<Point2D>> edges = new HashSet<Edge<Point2D>>();
        for (int i = 0; i < pts.length; i++) {
            int n = (int) (Math.random()*6);
            for (int j = 0; j < n; j++) {
                edges.add(new Edge<Point2D>(pts[i], pts[(int)(Math.random()*pts.length)]));
            }
        }
        gr.setEdges(edges);
        gr.getWindowGraphic().setNodeRenderer(PointRenderer.getInstance());
        gr.getWindowGraphic().setEdgeRenderer(PathRenderer.getInstance());
        gr.getPointStyler().setStyleDelegate(new Function<Point2D, AttributeSet>(){
            public AttributeSet apply(Point2D src) { 
                int yy = (int) Math.min(src.getX()/3, 255);
                return AttributeSet.with(Styles.FILL, new Color(yy,0,255-yy))
                        .and(Styles.MARKER_RADIUS, (float) (5*Math.abs(src.getY())));
            }
        });
        gr.getEdgeStyler().setStyleDelegate(new Function<Edge<Point2D>,AttributeSet>(){
            public AttributeSet apply(Edge<Point2D> src) {
                Point2D src0 = src.getNode1(), src1 = src.getNode2();
                int dx = (int) (src0.getX() - src1.getX());
                dx = Math.min(Math.abs(100*dx), 255);
                int dy = (int) (src0.getY() - src1.getY());
                dy = Math.min(Math.abs(200*dy), 255);

                return Styles.strokeWidth(new Color(dx, dy, 255-dy),
                        (float) Math.sqrt(dx*dx+dy*dy)/50);
            }
        });
        root2.addGraphic(gr);
    }

    //</editor-fold>



    //<editor-fold defaultstate="collapsed" desc="APP CODE">

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        BlaiseGraphicsTestView view = new BlaiseGraphicsTestView(this);
        canvas1 = view.canvas1;
        canvas2 = view.canvas2;
        root1 = view.canvas1.getGraphicRoot();
        root2 = view.canvas2.getVisometryGraphicRoot();
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
     * @return the instance of BlaiseGraphicsTest
     */
    public static BlaiseGraphicsTest getApplication() {
        return Application.getInstance(BlaiseGraphicsTest.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(BlaiseGraphicsTest.class, args);
    }

    //</editor-fold>


}
