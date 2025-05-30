package com.googlecode.blaisemath.graphics.testui;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2019 - 2025 Elisha Peterson
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
import com.google.common.graph.EndpointPair;
import com.googlecode.blaisemath.geom.Points;
import com.googlecode.blaisemath.graphics.impl.BasicPointSetGraphic;
import com.googlecode.blaisemath.graphics.impl.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.impl.DelegatingPointSetGraphic;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.impl.LabeledPointGraphic;
import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.*;
import com.googlecode.blaisemath.graphics.swing.render.*;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.style.ui.BasicPointStyleEditor;
import com.googlecode.blaisemath.graphics.impl.SegmentGraphic;
import com.googlecode.blaisemath.graphics.impl.TwoPointGraphic;
import com.googlecode.blaisemath.primitive.Anchor;
import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.primitive.ArrowLocation;
import com.googlecode.blaisemath.primitive.Markers;
import com.googlecode.blaisemath.util.Colors;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.JOptionPane;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class BlaiseGraphicsTestApp extends SingleFrameApplication {
    
    JGraphicRoot root1;
    JGraphicComponent canvas1;
    final AttributeSet pointSetStyle = RandomStyles.point();
    
    //region GENERAL ACTIONS
    
    @Action
    public void clear1() {
        root1.clearGraphics();
    }

    private double randomX() {
        return Math.random()*canvas1.getWidth();
    }

    private double randomY() {
        return Math.random()*canvas1.getHeight();
    }

    private Point2D.Double randomPoint() {
        return new Point2D.Double(Math.random()*canvas1.getWidth(), Math.random()*canvas1.getHeight());
    }
    
    @Action
    public void zoomAll() {
        canvas1.zoomToAll();
    }
    
    @Action
    public void zoomSelected() {
        canvas1.zoomToSelected();
    }
    
    @Action
    public void zoomAllOutsets() {
        canvas1.zoomToAll(new Insets(50, 50, 50, 50));
    }
    
    @Action
    public void zoomSelectedOutsets() {
        canvas1.zoomToSelected(new Insets(50, 50, 50, 50));
    }
    
    //endregion

    //region BASIC GRAPHICS
    
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
        Shape line = new Line2D.Double(randomPoint(), randomPoint());
        if (Math.random() < .3) {
            GeneralPath gp = new GeneralPath();
            double x = randomX();
            gp.moveTo(x, randomY());
            gp.lineTo(x, randomY());
            gp.lineTo(x, randomY());
            line = gp;
        } else if (Math.random() < .3) {
            Line2D.Double line2 = new Line2D.Double(randomPoint(), randomPoint());
            line = new Line2D.Double(line2.getX1(), line2.getY1(), line2.getX2(), line2.getY1());
        } else if (Math.random() < .3) {
            line = new Ellipse2D.Double();
            Line2D.Double line2 = new Line2D.Double(randomPoint(), randomPoint());
//            ((Ellipse2D.Double) line).setFrameFromDiagonal(randomPoint(), randomPoint());
            ((Ellipse2D.Double) line).setFrameFromDiagonal(line2.getX1(), line2.getY1(), line2.getX2(), line2.getY1());
        }
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
        if (Math.random() < .3) {
            bg.setRenderer(new SlopedTextRenderer(Math.random()));
        }
        bg.setDragEnabled(true);
        root1.addGraphic(bg);
    }
    
    @Action
    public void addPointSet() {      
        final BasicPointSetGraphic bp = new BasicPointSetGraphic(
                new Point2D[]{randomPoint(), randomPoint(), randomPoint()},
                this.pointSetStyle, MarkerRenderer.getInstance());
        bp.addContextMenuInitializer((ContextMenuInitializer<Graphic<Graphics2D>>) (menu, src, point, focus, selection) -> {
            Point2D pt = bp.getPoint(bp.indexOf(point, null));
            menu.add(Points.format(pt, 2));
            menu.add(getContext().getActionMap().get("editPointSetStyle"));
        });
        root1.addGraphic(bp);
    }
    
    @Action
    public void editPointSetStyle() {        
        BasicPointStyleEditor ed = new BasicPointStyleEditor(pointSetStyle);
        ed.addPropertyChangeListener("style", evt -> canvas1.repaint());
        JOptionPane.showMessageDialog(getMainFrame(), ed);
    }
    
    //endregion
    
    //region GRAPHICS WITH DELEGATION
    
    @Action
    public void addWrappedText() {
        if (Math.random() < .33) {
            addWrappedTextEndChar();
        } else if (Math.random() < .5) {
            addWrappedTextRandom();
        } else {
            addWrappedTextSmall();
        }
    }

    @Action
    public void addWrappedTextPlain() {
        PrimitiveGraphic<AnchoredText, Graphics2D> textGraphic = new PrimitiveGraphic<>();
        textGraphic.setPrimitive(new AnchoredText(0.0, 0.0, "This is a long label for a rectangle that should get wrapped, "
                + "since it needs to be really big so we can adequately test something with a\n long\n label\n"
                + "and\n\n\n\n new line characters\nx but then we can continue writing some more with some more"
                + "\n\nline breaks before\n\n other lines."));
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal(randomPoint(), randomPoint());
        textGraphic.setRenderer(new WrappedTextRenderer().clipPath(rect));
        root1.addGraphic(textGraphic);
    }
    
    private void addWrappedTextRandom() {
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal(randomPoint(), randomPoint());
        LabeledShapeGraphic gfc = new LabeledShapeGraphic();
        gfc.setPrimitive(rect);
        gfc.getObjectStyler().setLabel("this is a long label for a rectangle that should get wrapped, "
                + "since it needs to be really big so we can adequately test something with a\n long\n label\n"
                + "and\n\n\n new line characters\nx but then we can continue writing some more with some more"
                + "\n\nline breaks before\n\n other lines.");
        gfc.getObjectStyler().setLabelStyle(Styles.text(RandomStyles.color(), RandomStyles.fontSize(), Anchor.NORTHWEST));
        root1.addGraphic(gfc);
    }
    
    private void addWrappedTextSmall() {
        LabeledShapeGraphic gfc = new LabeledShapeGraphic();
        Random r = new Random();
        gfc.setPrimitive(new Rectangle2D.Double(r.nextInt(100)+100, r.nextInt(100)+100, r.nextInt(20)+5, r.nextInt(20)+5));
        gfc.getObjectStyler().setLabel(r.nextBoolean() ? "ab" : "a");
        gfc.getObjectStyler().setLabelStyle(Styles.text(RandomStyles.color(), RandomStyles.fontSize(), Anchor.NORTHWEST));
        root1.addGraphic(gfc);
    }

    private void addWrappedTextEndChar() {
        LabeledShapeGraphic gfc = new LabeledShapeGraphic();
        Random r = new Random();
        gfc.setPrimitive(new Rectangle2D.Double(r.nextInt(100)+100, r.nextInt(100)+100, r.nextInt(100)+5, r.nextInt(100)+5));
        gfc.getObjectStyler().setLabel("a\nb\nc");
        gfc.getObjectStyler().setLabelStyle(Styles.text(RandomStyles.color(), RandomStyles.fontSize(), Anchor.NORTHWEST));
        root1.addGraphic(gfc);
    }
        
    @Action
    public void addDelegatingPointSet() {
        Set<String> list = new HashSet<>(Arrays.asList(
                "Africa", "Indiana Jones", "Micah Andrew Peterson", "Chrysanthemum",
                "Sequoia", "Asher Matthew Peterson", "Elisha Peterson", "Bob the Builder"));
        Map<String,Point2D.Double> crds = Maps.newLinkedHashMap();
        for (String s : list) {
            crds.put(s, new Point2D.Double(10*s.length(), 50 + 10*s.indexOf(" ")));
        }
        DelegatingPointSetGraphic<String, Graphics2D> bp = new DelegatingPointSetGraphic<>(
                MarkerRenderer.getInstance(), TextRenderer.getInstance());
        bp.addObjects(crds);
        bp.setDragEnabled(true);
        bp.getStyler().setLabelDelegate(Functions.toStringFunction());
        bp.getStyler().setLabelStyle(Styles.DEFAULT_TEXT_STYLE);
        bp.getStyler().setStyleDelegate(new Function<String,AttributeSet>(){
            final AttributeSet r = new AttributeSet();
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
        bp.setPointSelectionEnabled(true);
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingPointSet2() {
        Map<Integer, Point2D.Double> points2 = Maps.newLinkedHashMap();
        for (int i = 1; i <= 10; i++) {
            points2.put(i, randomPoint());
        }
        final DelegatingPointSetGraphic<Integer,Graphics2D> bp = new DelegatingPointSetGraphic<>(
                MarkerRenderer.getInstance(), TextRenderer.getInstance());
        bp.addObjects(points2);
        bp.setDragEnabled(true);
        bp.getStyler().setLabelDelegate(Functions.toStringFunction());
        bp.getStyler().setLabelStyleDelegate(new Function<Integer,AttributeSet>(){
            final AttributeSet r = new AttributeSet();
            @Override
            public AttributeSet apply(Integer src) {
                r.put(Styles.TEXT_ANCHOR, Anchor.CENTER);
                r.put(Styles.FONT_SIZE, 5+src.floatValue());
                return r;
            }
        });
        bp.getStyler().setStyleDelegate(new Function<Integer,AttributeSet>(){
            final AttributeSet r = new AttributeSet();
            public AttributeSet apply(Integer src) {
                r.put(Styles.MARKER_RADIUS, src+2);
                r.put(Styles.FILL, new Color((src*10+10) % 255, (src*20+25) % 255, (src*30+50) % 255));
                r.put(Styles.STROKE, Colors.lighterThan(r.getColor(Styles.FILL)));
                return r;
            }
        });
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingGraph() {
        // initialize graph object
        final Map<Integer, Point2D.Double> pts = Maps.newLinkedHashMap();
        for (int i = 0; i < 15; i++) {
            pts.put(i, randomPoint());
        }         
        Set<EndpointPair<Integer>> edges = new HashSet<>();
        for (int i = 0; i < pts.size(); i++) {
            int n = (int) (Math.random()*6);
            for (int j = 0; j < n; j++) {
                edges.add(EndpointPair.unordered(i, (int)(Math.random()*pts.size())));
            }
        }
        // create graphic
        DelegatingNodeLinkGraphic<Integer,EndpointPair<Integer>,Graphics2D> gr = JGraphics.nodeLink();
        gr.setDragEnabled(true);
        gr.setNodeLocations(pts);
        gr.getNodeStyler().setStyleDelegate((Integer src) -> {
            Point2D pt = pts.get(src);
            int yy = (int) Math.min(pt.getX()/3, 255);
            return AttributeSet.of(Styles.FILL, new Color(yy, 0, 255-yy),
                    Styles.MARKER_RADIUS, (float) Math.sqrt(pt.getY()));
        });
        gr.getNodeStyler().setLabelDelegate((Integer src) -> {
            Point2D pt = pts.get(src);
            return String.format("(%.1f,%.1f)", pt.getX(), pt.getY());
        });
        gr.getNodeStyler().setLabelStyle(Styles.DEFAULT_TEXT_STYLE);
        gr.setEdgeSet(edges);
        gr.getEdgeStyler().setStyleDelegate((EndpointPair<Integer> src) -> {
            Point2D src0 = pts.get(src.nodeU());
            Point2D src1 = pts.get(src.nodeV());
            int dx = (int) (src0.getX() - src1.getX());
            dx = Math.min(Math.abs(dx/2), 255);
            int dy = (int) (src0.getY() - src1.getY());
            dy = Math.min(Math.abs(dy/3), 255);
            return AttributeSet.of(Styles.STROKE, new Color(dx, dy, 255-dy),
                    Styles.STROKE_WIDTH, (float) Math.sqrt(dx*dx+dy*dy)/50);
        });
        root1.addGraphic(gr);
    }
    
    //endregion
    
    //region COMPOSITE GRAPHICS
    
    @Action
    public void addLabeledPoint() {
        Point2D p1 = randomPoint();
        LabeledPointGraphic<Point2D, Graphics2D> lpg = new LabeledPointGraphic<>(p1, p1, new ObjectStyler());
        lpg.setRenderer(MarkerRenderer.getInstance());
        lpg.setLabelRenderer(new TextRenderer());
        lpg.getObjectStyler().setLabelDelegate(p -> String.format("(%.2f,%.2f)", p.getX(), p.getY()));
        lpg.getObjectStyler().setLabelStyle(Styles.text(Color.red, 14f, Anchor.SOUTHWEST));
        lpg.setDefaultTooltip("<html><b>Labeled Point</b>: <i> " + p1 + "</i>");
        lpg.setDragEnabled(true);
        root1.addGraphic(lpg);        
    }
    
    @Action
    public void add2Point() {
        Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphic ag = new TwoPointGraphic(p1, p2, MarkerRenderer.getInstance());
        ag.setDefaultTooltip("<html><b>Two Points</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);
    }
    
    @Action
    public void addDraggableSegment() {
        Point2D p1 = randomPoint(), p2 = randomPoint();
        SegmentGraphic ag = new SegmentGraphic(p1, p2, ArrowLocation.NONE, MarkerRenderer.getInstance(), PathRenderer.getInstance());
        ag.setDefaultTooltip("<html><b>Segment</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);        
    }
    
    @Action
    public void addArrow() {      
        Point2D p1 = randomPoint(), p2 = randomPoint();
        SegmentGraphic ag = new SegmentGraphic(p1, p2, ArrowLocation.END, MarkerRenderer.getInstance(), PathRenderer.getInstance());
        ag.setDefaultTooltip("<html><b>Arrow</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);
    }
    
    //endregion

    //region SPECIAL STYLES
    
    @Action
    public void addRay() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphic ag = new TwoPointGraphic(p1, p2, MarkerRenderer.getInstance());
        MarkerRendererToClip rend = new MarkerRendererToClip();
        rend.setRayRenderer(new ArrowPathRenderer(ArrowLocation.END));
        ag.getStartGraphic().setRenderer(rend);
        ag.setDefaultTooltip("<html><b>Ray</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);        
    }
    
    @Action
    public void addLine() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphic ag = new TwoPointGraphic(p1, p2, MarkerRenderer.getInstance());
        MarkerRendererToClip rend = new MarkerRendererToClip();
        rend.setRayRenderer(new ArrowPathRenderer(ArrowLocation.BOTH));
        rend.setExtendBothDirections(true);
        ag.getStartGraphic().setRenderer(rend);
        ag.setDefaultTooltip("<html><b>Line</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.setDragEnabled(true);
        root1.addGraphic(ag);        
    }
    
    //endregion

    //region APP CODE

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        BlaiseGraphicsTestFrameView view = new BlaiseGraphicsTestFrameView(this);
        canvas1 = view.canvas1;
        root1 = view.canvas1.getGraphicRoot();
        canvas1.setSelectionEnabled(true);
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
    
    //endregion

}
