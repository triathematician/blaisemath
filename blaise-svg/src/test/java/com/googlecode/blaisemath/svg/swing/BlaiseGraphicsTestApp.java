package com.googlecode.blaisemath.svg;

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

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.graph.EndpointPair;
import com.googlecode.blaisemath.geom.Points;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.impl.*;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.LabeledShapeGraphic;
import com.googlecode.blaisemath.graphics.swing.render.*;
import com.googlecode.blaisemath.primitive.*;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.style.ui.BasicPointStyleEditor;
import com.googlecode.blaisemath.svg.render.SvgRenderer;
import com.googlecode.blaisemath.svg.xml.SvgImage;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgRoot;
import com.googlecode.blaisemath.util.Colors;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BlaiseGraphicsTestApp extends SingleFrameApplication {
    
    JGraphicRoot root1;
    JGraphicComponent canvas1;
    final AttributeSet pointSetStyle = RandomStyles.point();

    private static final DOMImplementation DOM_IMPL = GenericDOMImplementation.getDOMImplementation();
    private static final String SVG_NS = "http://www.w3.org/2000/svg";
    private static final CachedImageHandlerBase64Encoder IMAGE_HANDLER = new CachedImageHandlerBase64Encoder();
    private static final Document DOCUMENT = DOM_IMPL.createDocument(SVG_NS, "svg", null);
    private static final SVGGeneratorContext GENERATOR_CONTEXT = SVGGeneratorContext.createDefault(DOCUMENT);
    static {
        GENERATOR_CONTEXT.setGenericImageHandler(IMAGE_HANDLER);
    }

    @Action
    public void testBatik() throws IOException {
        printAndCopyToClipboard(batikSvg(canvas1));
    }

    @Action
    public void testBatik1000() throws IOException {
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            batikSvg(canvas1);
        }
        long t1 = System.currentTimeMillis();
        System.out.println((t1-t0)+"ms to write 1000 strings");
    }

    @Action
    public void printSVG() throws IOException {
        SvgRoot root = SvgRenderer.componentToSvg(canvas1);
        printAndCopyToClipboard(SvgIo.writeToString(root));
    }

    @Action
    public void printSVG1000() throws IOException {
        long t0 = System.currentTimeMillis();
        SvgRoot root = SvgRenderer.componentToSvg(canvas1);
        for (int i = 0; i < 1000; i++) {
            SvgIo.writeToString(root);
        }
        long t1 = System.currentTimeMillis();
        System.out.println((t1-t0)+"ms to write 1000 strings");
        for (int i = 0; i < 1000; i++) {
            SvgIo.writeToString(SvgRenderer.componentToSvg(canvas1));
        }
        long t2 = System.currentTimeMillis();
        System.out.println((t2-t1)+"ms to convert and write to string 1000 times");
    }

    static void printAndCopyToClipboard(Object o) {
        System.out.println(o);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(o+""), null);
    }

    private static String batikSvg(Component c) throws SVGGraphics2DIOException {
        SVGGraphics2D svgGraphics = new SVGGraphics2D(GENERATOR_CONTEXT, false);
        c.paint(svgGraphics);
        StringWriter sw = new StringWriter();
        svgGraphics.stream(sw, true);
        return sw.toString();
    }

    //region GENERAL
    
    @Action
    public void clear1() {
        root1.clearGraphics();
    }
    
    private Point2D.Double randomPoint() {
        return new Point2D.Double(Math.random()*canvas1.getWidth(), Math.random()*canvas1.getHeight());
    }
    
    //endregion

    //region BASIC
    
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
        AnchoredText txt = new AnchoredText(pt, String.format("[%.4f, %.4f]", pt.getX(), pt.getY()));
        PrimitiveGraphic bg = JGraphics.text(txt, RandomStyles.string());
        bg.setDragEnabled(true);
        root1.addGraphic(bg);
    }

    private static final String[] ANCHORS = {Styles.TEXT_ANCHOR_END, Styles.TEXT_ANCHOR_MIDDLE, Styles.TEXT_ANCHOR_START};
    private static final String[] BASELINES = {Styles.ALIGN_BASELINE_BASELINE, Styles.ALIGN_BASELINE_MIDDLE, Styles.ALIGN_BASELINE_HANGING};

    @Action
    public void addIcon() {
        Point2D pt = randomPoint();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                AnchoredIcon icon = new AnchoredIcon(pt.getX()+i*50, pt.getY()+j*50, randomIcon());
                PrimitiveGraphic bp = JGraphics.icon(icon);
                bp.setStyle(AttributeSet.of(Styles.TEXT_ANCHOR, ANCHORS[i], Styles.ALIGN_BASELINE, BASELINES[j]));
                bp.setDefaultTooltip("<html><b>Icon</b>: <i> " + pt + "</i>");
                bp.setDragEnabled(true);
                root1.addGraphic(bp);
                
                root1.addGraphic(JGraphics.path(new Line2D.Double(icon.getX()-20, icon.getY(), icon.getX()+20, icon.getY()),
                        Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
                root1.addGraphic(JGraphics.path(new Line2D.Double(icon.getX(), icon.getY()-20, icon.getX(), icon.getY()+20),
                        Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
            }
        }
    }
    
    private Icon randomIcon() {
        boolean img = Math.random() > .5;
        if (img) {
            URL iconUrl = SvgImage.class.getResource("resources/cherries.png");
            return new ImageIcon(iconUrl);
        } else {
            return new Icon() {
                @Override
                public int getIconWidth() {
                    return 30;
                }
                @Override
                public int getIconHeight() {
                    return 30;
                }
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.red);
                    g2.draw(new Line2D.Double(x, y, x+30, y+30));
                    g2.draw(new Line2D.Double(x+30, y, x, y+30));
                }
            };
        }
    }
    
    @Action
    public void addPointSet() {      
        final BasicPointSetGraphic bp = new BasicPointSetGraphic(new Point2D[]{randomPoint(), randomPoint(), randomPoint()},
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
        ed.addPropertyChangeListener("style", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                canvas1.repaint();
            }
        });
        JOptionPane.showMessageDialog(getMainFrame(), ed);
    }
    
    //endregion

    //region GRAPHICS WITH DELEGATORS
        
    @Action
    public void addDelegatingPointSet() {
        Set<String> list = new HashSet<String>(Arrays.asList(
                "Africa", "Indiana Jones", "Micah Andrew Peterson", "Chrysanthemum", 
                "Sequoia", "Asher Matthew Peterson", "Elisha", "Bob the Builder"));
        Map<String,Point2D.Double> crds = Maps.newLinkedHashMap();
        for (String s : list) {
            crds.put(s, new Point2D.Double(10*s.length(), 50 + 10*s.indexOf(" ")));
        }
        DelegatingPointSetGraphic<String,Graphics2D> bp = new DelegatingPointSetGraphic<String,Graphics2D>(
                MarkerRenderer.getInstance(), TextRenderer.getInstance());
        bp.addObjects(crds);
        bp.setDragEnabled(true);
        bp.getStyler().setLabelDelegate(Functions.toStringFunction());
        bp.getStyler().setLabelStyle(Styles.DEFAULT_TEXT_STYLE);
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
        bp.setPointSelectionEnabled(true);
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingPointSet2() {
        Map<Integer,Point2D.Double> points2 = Maps.newLinkedHashMap();
        for (int i = 1; i <= 10; i++) {
            points2.put(i, randomPoint());
        }
        final DelegatingPointSetGraphic<Integer,Graphics2D> bp = new DelegatingPointSetGraphic<Integer,Graphics2D>(
                MarkerRenderer.getInstance(), TextRenderer.getInstance());
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
                r.put(Styles.STROKE, Colors.lighterThan(r.getColor(Styles.FILL)));
                return r;
            }
        });
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingGraph() {
        // initialize graph object
        final Map<Integer,Point2D.Double> pts = Maps.newLinkedHashMap();
        for (int i = 0; i < 15; i++) {
            pts.put(i, randomPoint());
        }         
        Set<EndpointPair<Integer>> edges = new HashSet<>();
        for (int i = 0; i < pts.size(); i++) {
            int n = (int) (Math.random()*6);
            for (int j = 0; j < n; j++) {
                edges.add(EndpointPair.ordered(i, (int)(Math.random()*pts.size())));
            }
        }
        // create graphic
        DelegatingNodeLinkGraphic<Integer,EndpointPair<Integer>,Graphics2D> gr = JGraphics.nodeLink();
        gr.setDragEnabled(true);
        gr.setNodeLocations(pts);
        gr.getNodeStyler().setStyleDelegate(src -> {
            Point2D pt = pts.get(src);
            int yy = (int) Math.min(pt.getX()/3, 255);
            return AttributeSet.of(Styles.FILL, new Color(yy, 0, 255-yy))
                    .and(Styles.MARKER_RADIUS, (float) Math.sqrt(pt.getY()));
        });
        gr.getNodeStyler().setLabelDelegate(src -> {
            Point2D pt = pts.get(src);
            return String.format("(%.1f,%.1f)", pt.getX(), pt.getY());
        });
        gr.getNodeStyler().setLabelStyleDelegate(new Function<Integer, AttributeSet>(){
            AttributeSet bss = Styles.DEFAULT_TEXT_STYLE;
            public AttributeSet apply(Integer src) {
                return bss;                
            }
        });
        gr.setEdgeSet(edges);
        gr.getEdgeStyler().setStyleDelegate(src -> {
            Point2D src0 = pts.get(src.nodeU()), src1 = pts.get(src.nodeV());
            int dx = (int) (src0.getX() - src1.getX());
            dx = Math.min(Math.abs(dx/2), 255);
            int dy = (int) (src0.getY() - src1.getY());
            dy = Math.min(Math.abs(dy/3), 255);

            return AttributeSet.of(Styles.STROKE, new Color(dx, dy, 255-dy))
                    .and(Styles.STROKE_WIDTH, (float) Math.sqrt(dx*dx+dy*dy)/50);
        });
        root1.addGraphic(gr);
    }
    
    //endregion
    
    //region COMPOSITES
    
    @Action
    public void addLabeledShape() {
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal(randomPoint(), randomPoint());
        LabeledShapeGraphic gfc = new LabeledShapeGraphic();
        gfc.setPrimitive(rect);
        gfc.setDragEnabled(true);
        gfc.getObjectStyler().setStyle(RandomStyles.shape());
        gfc.getObjectStyler().setLabel("this is a long label for a rectangle that should get wrapped, "
                + "since it needs to be really big so we can adequately test something with a long label\n"
                + "and new line characters");
        gfc.getObjectStyler().setLabelStyle(RandomStyles.anchoredString());
        root1.addGraphic(gfc);
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
    
    //region COOL STUFF USING SPECIAL STYLES
    
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
