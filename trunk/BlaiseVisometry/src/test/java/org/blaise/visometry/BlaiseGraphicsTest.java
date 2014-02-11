/*
 * BlaiseGraphicsTest.java
 */

package org.blaise.visometry;

import org.blaise.util.ContextMenuInitializer;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.blaise.graphics.*;
import org.blaise.style.*;
import org.blaise.util.Edge;
import org.blaise.visometry.plane.PlanePlotComponent;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class BlaiseGraphicsTest extends SingleFrameApplication {

    GraphicRoot root1;
    VGraphicRoot<Point2D.Double> root2;
    GraphicComponent canvas1;
    PlanePlotComponent canvas2;
    PointStyleBasic bps = (PointStyleBasic) RandomStyles.point();

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
        final BasicPointGraphic bp = new BasicPointGraphic(pt);
        bp.setStyle(RandomStyles.point());
        bp.setDefaultTooltip("<html><b>Point</b>: <i> " + pt + "</i>");
        root1.addGraphic(bp);
    }

    @Action
    public void addPointSet() {
        BasicPointSetGraphic bp = new BasicPointSetGraphic(
                new Point2D[]{randomPoint(), randomPoint(), randomPoint()},
                this.bps);
        bp.addContextMenuInitializer(new ContextMenuInitializer<Graphic>(){
            public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
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
        BasicShapeGraphic bs = new BasicShapeGraphic(line, RandomStyles.path());
        bs.setDefaultTooltip("<html><b>Segment</b>: <i>" + line + "</i>");
        bs.addMouseListener(new GraphicHighlightHandler());
        root1.addGraphic(bs);
    }

    @Action
    public void addRectangle() {
        Rectangle2D.Double rect = new Rectangle2D.Double(Math.random()*canvas1.getWidth(), Math.random()*canvas1.getHeight(), 100*Math.random(), 100*Math.random());
        BasicShapeGraphic bs = new BasicShapeGraphic(rect, RandomStyles.shape());
        bs.addMouseListener(new GraphicHighlightHandler());
        bs.setDefaultTooltip("<html><b>Rectangle</b>: <i>" + rect + "</i>");
        root1.addGraphic(bs);
    }

    @Action
    public void addString() {
        Point2D pt = randomPoint();
        BasicTextGraphic gs = new BasicTextGraphic(pt, String.format("[%.4f, %.4f]", pt.getX(), pt.getY()));
        gs.setStyle(RandomStyles.string());
        gs.addMouseListener(new GraphicHighlightHandler());
        root1.addGraphic(gs);

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
        final DelegatingPointSetGraphic<String> bp = new DelegatingPointSetGraphic<String>();
        bp.getCoordinateManager().setCoordinateMap(crds);
        bp.getStyler().setLabelDelegate(new Function<String,String>(){ public String apply(String src) { return src; } });
        bp.getStyler().setStyleDelegate(new Function<String,PointStyle>(){
            PointStyleBasic r = new PointStyleBasic();
            PointStyleLabeled lps = new PointStyleLabeled(r);
            public PointStyle apply(String src) {
                int i1 = src.indexOf("a"), i2 = src.indexOf("e"), i3 = src.indexOf("i"), i4 = src.indexOf("o");
                r.setMarkerRadius(i1+5);
                r.setMarker(MarkerLibrary.getAvailableMarkers().get(i2+3));
                r.setStrokeWidth(2+i3/3f);
                r.setFill(new Color((i4*10+10) % 255, (i4*20+25) % 255, (i4*30+50) % 255));
                ((TextStyleBasic)lps.getLabelStyle()).fill(r.getFill());
                return lps;
            }
        });
        bp.addMouseListener(new GraphicHighlightHandler());
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingPointSet2() {
        Map<Integer,Point2D> points2 = Maps.newLinkedHashMap();
        for (int i = 1; i <= 10; i++) {
            points2.put(i, randomPoint());
        }
        final DelegatingPointSetGraphic<Integer> bp = new DelegatingPointSetGraphic<Integer>();
        bp.getCoordinateManager().setCoordinateMap(points2);
        bp.getStyler().setLabelDelegate(Functions.toStringFunction());
        bp.getStyler().setStyleDelegate(new Function<Integer,PointStyle>(){
            PointStyleBasic r = new PointStyleBasic();
            PointStyleLabeled lps = new PointStyleLabeled(r);
            { ((TextStyleBasic)lps.getLabelStyle()).setTextAnchor(Anchor.CENTER); }
            public PointStyle apply(Integer src) {
                r.setMarkerRadius(src+2);
                r.setFill(new Color((src*10+10) % 255, (src*20+25) % 255, (src*30+50) % 255));
                ((TextStyleBasic)lps.getLabelStyle())
                        .fill(r.getFill().brighter().brighter())
                        .fontSize(5+src.floatValue());
                return lps;
            }
        });
        bp.addMouseListener(new GraphicHighlightHandler());
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
        DelegatingNodeLinkGraphic<Point2D,Edge<Point2D>> gr = new DelegatingNodeLinkGraphic<Point2D,Edge<Point2D>>();
        ImmutableMap<Point2D, Point2D> idx = Maps.uniqueIndex(Arrays.asList(pts), Functions.<Point2D>identity());
        gr.setNodeLocations(idx);
        gr.getNodeStyler().setStyleDelegate(new Function<Point2D,PointStyle>(){
            public PointStyle apply(Point2D src) {
                int yy = (int) Math.min(src.getX()/3, 255);
                return new PointStyleBasic()
                        .markerRadius((float) Math.sqrt(src.getY()))
                        .fill(new Color(yy,0,255-yy));
            }
        });
        gr.getNodeStyler().setLabelDelegate(new Function<Point2D,String>(){ public String apply(Point2D src) { return String.format("(%.1f,%.1f)", src.getX(), src.getY()); } });     
        gr.getNodeStyler().setLabelStyleDelegate(new Function<Point2D,TextStyle>(){
            TextStyleBasic bss = new TextStyleBasic();
            public TextStyle apply(Point2D src) {
                return bss;                
            }
        });
        gr.setEdgeSet(edges);
        gr.getEdgeStyler().setStyleDelegate(new Function<Edge<Point2D>,PathStyle>(){
            public PathStyle apply(Edge<Point2D> src) {
                Point2D src0 = src.getNode1(), src1 = src.getNode2();
                int dx = (int) (src0.getX() - src1.getX());
                dx = Math.min(Math.abs(dx/2), 255);
                int dy = (int) (src0.getY() - src1.getY());
                dy = Math.min(Math.abs(dy/3), 255);
                
                return new PathStyleTapered()
                        .stroke(new Color(dx, dy, 255-dy))
                        .strokeWidth((float) Math.sqrt(dx*dx+dy*dy)/50);
            }
        });
        gr.addMouseListener(new GraphicHighlightHandler());
        root1.addGraphic(gr);
    }
    
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="COMPOSITES">

//    @Action
//    public void addArrow() {
//        Point2D p1 = randomPoint(), p2 = randomPoint();
//        SegmentGraphic ag = new SegmentGraphic(p1, p2);
//        ag.setDefaultTooltip("<html><b>Segment</b>: <i>" + p1 + ", " + p2 + "</i>");
//        ag.addMouseListener(new GraphicHighlightHandler());
//        root1.addGraphic(ag);
//    }

//    @Action
//    public void addLabeledPoint() {
//         Point2D p1 = randomPoint();
//        LabeledPointGraphic lpg = new LabeledPointGraphic(
//                p1,
//                String.format("(%.2f,%.2f)", p1.getX(), p1.getY()),
//                RandomStyles.point()
//                );
//        lpg.setDefaultTooltip("<html><b>Labeled Point</b>: <i> " + p1 + "</i>");
//        lpg.addMouseListener(new GraphicHighlightHandler());
//        root1.addGraphic(lpg);
//    }

//    @Action
//    public void addRuler() {
//         Point2D p1 = randomPoint(), p2 = randomPoint();
//        RulerGraphic lg = new RulerGraphic(p1, p2);
//
//        lg.setTickPositions(new float[]{(float)Math.random(), (float)Math.random(), (float)Math.random()});
//        lg.setTickLabels(new String[]{"A", "B", "C"});
//        lg.setRuleLeft((int)(10*Math.random()));
//        lg.setRuleRight((int)(-10*Math.random()));
//
//        lg.addMouseListener(new GraphicHighlightHandler());
//        root1.addGraphic(lg);
//    }

//    @Action
//    public void add2Point() {
//      Point2D p1 = randomPoint(), p2 = randomPoint();
//        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
//        ag.setDefaultTooltip("<html><b>Two Points</b>: <i>" + p1 + ", " + p2 + "</i>");
//        ag.addMouseListener(new GraphicHighlightHandler());
//        root1.addGraphic(ag);
//    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="COOL STUFF USING SPECIAL STYLES">

    @Action
    public void addLabeledPointSet() {
        BasicPointSetGraphic bp = new BasicPointSetGraphic(new Point2D[]{randomPoint(), randomPoint(), randomPoint(), randomPoint()});
        bp.setStyle(new PointStyleLabeled());
        bp.addMouseListener(new GraphicHighlightHandler());
        root1.addGraphic(bp);
    }

//    @Action
//    public void addRay() {
//      Point2D p1 = randomPoint(), p2 = randomPoint();
//        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
//        InfinitePointStyle ips = new InfinitePointStyle();
//        ips.setRayStyle(new ArrowPathStyle());
//        ag.setEndPointStyle(ips);
//        ag.setDefaultTooltip("<html><b>Ray</b>: <i>" + p1 + ", " + p2 + "</i>");
//        ag.addMouseListener(new GraphicHighlightHandler());
//        root1.addGraphic(ag);
//    }

//    @Action
//    public void addLine() {
//      Point2D p1 = randomPoint(), p2 = randomPoint();
//        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
//        InfinitePointStyle ips = new InfinitePointStyle();
//        ips.setRayStyle(new ArrowPathStyle());
//        ips.setExtendBoth(true);
//        ag.setEndPointStyle(ips);
//        ag.setDefaultTooltip("<html><b>Line</b>: <i>" + p1 + ", " + p2 + "</i>");
//        ag.addMouseListener(new GraphicHighlightHandler());
//        root1.addGraphic(ag);
//    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="PLANE - BASIC">

    @Action
    public void addPlanePoint() {
        VBasicPoint<Point2D.Double> bp = new VBasicPoint<Point2D.Double>(new Point2D.Double(1+Math.random(), 1+Math.random()));
        bp.getWindowEntry().addMouseListener(new GraphicHighlightHandler());
        root2.addGraphic(bp);
    }

    @Action
    public void addPlanePointSet() {
        final Point2D.Double[] arr = new Point2D.Double[50];
        for (int i = 0; i < arr.length; i++)
            arr[i] = new Point2D.Double(-Math.random(), -Math.random());
        VBasicPointSet<Point2D.Double> vps = new VBasicPointSet<Point2D.Double>(arr);
        vps.setStyle(new PointStyleBasic().fill(Color.blue).markerRadius(3f));
        vps.getWindowEntry().addMouseListener(new GraphicHighlightHandler());
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
        VBasicPolygonalPath<Double> vps = new VBasicPolygonalPath<Point2D.Double>(arr3);
        vps.getWindowEntry().addMouseListener(new GraphicHighlightHandler());
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
        VCustomPointSet<Point2D.Double, Integer> vps = new VCustomPointSet<Point2D.Double, Integer>(pts);
        vps.getStyler().setStyleDelegate(new Function<Integer, PointStyle>(){
            public PointStyle apply(Integer src) { 
                return new PointStyleBasic().fill(new Color(255-src*2,0,src*2)).stroke(null); 
            }
        });
        vps.getWindowEntry().addMouseListener(new GraphicHighlightHandler());
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
        VCustomGraph<Point2D.Double,Point2D,Edge<Point2D>> gr = new VCustomGraph<Point2D.Double,Point2D,Edge<Point2D>>(pp);
        Set<Edge<Point2D>> edges = new HashSet<Edge<Point2D>>();
        for (int i = 0; i < pts.length; i++) {
            int n = (int) (Math.random()*6);
            for (int j = 0; j < n; j++) {
                edges.add(new Edge<Point2D>(pts[i], pts[(int)(Math.random()*pts.length)]));
            }
        }
        gr.setEdges(edges);
        gr.getStyler().setStyleDelegate(new Function<Point2D,PointStyle>(){
            public PointStyle apply(Point2D src) {
                int yy = (int) Math.min(src.getX()/3, 255);
                return new PointStyleBasic()
                        .markerRadius((float) (5*Math.abs(src.getY())))
                        .fill(new Color(yy,0,255-yy));
            }
        });
        gr.getEdgeStyler().setStyleDelegate(new Function<Edge<Point2D>,PathStyle>(){
            public PathStyle apply(Edge<Point2D> src) {
                Point2D src0 = src.getNode1(), src1 = src.getNode2();
                int dx = (int) (src0.getX() - src1.getX());
                dx = Math.min(Math.abs(100*dx), 255);
                int dy = (int) (src0.getY() - src1.getY());
                dy = Math.min(Math.abs(200*dy), 255);

                return new PathStyleTapered()
                        .stroke(new Color(dx, dy, 255-dy))
                        .strokeWidth((float) Math.sqrt(dx*dx+dy*dy)/50);
            }
        });
        gr.getWindowEntry().addMouseListener(new GraphicHighlightHandler());
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
