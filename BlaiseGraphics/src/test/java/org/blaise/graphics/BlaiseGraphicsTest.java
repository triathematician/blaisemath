/*
 * BlaiseGraphicsTest.java
 */

package org.blaise.graphics;

import java.awt.Color;
import java.awt.Point;
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
import org.blaise.graphics.compound.LabeledPointGraphic;
import org.blaise.graphics.compound.RulerGraphic;
import org.blaise.graphics.compound.SegmentGraphic;
import org.blaise.graphics.compound.TwoPointGraphicSupport;
import org.blaise.style.Anchor;
import org.blaise.style.ArrowPathStyle;
import org.blaise.style.BasicPointStyle;
import org.blaise.style.BasicStringStyle;
import org.blaise.style.FancyPathStyle;
import org.blaise.style.InfinitePointStyle;
import org.blaise.style.LabeledPointStyle;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.style.ShapeLibrary;
import org.blaise.style.StringStyle;
import org.blaise.util.Delegator;
import org.blaise.util.Delegators;
import org.blaise.util.Edge;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class BlaiseGraphicsTest extends SingleFrameApplication {
    
    GraphicRoot root1;
    GraphicComponent canvas1;
    BasicPointStyle bps = (BasicPointStyle) RandomStyles.point();
    
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
        final BasicPointGraphic bp = new BasicPointGraphic(pt);
        bp.setStyle(RandomStyles.point());
        bp.setDefaultTooltip("<html><b>Point</b>: <i> " + pt + "</i>");
        root1.addGraphic(bp);
    }
    
    @Action
    public void addSegment() {        
        Line2D.Double line = new Line2D.Double(randomPoint(), randomPoint());
        BasicShapeGraphic bs = new BasicShapeGraphic(line, RandomStyles.path());
        bs.setDefaultTooltip("<html><b>Segment</b>: <i>" + line + "</i>");
        bs.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(bs);
    }
    
    @Action
    public void addRectangle() {        
        Rectangle2D.Double rect = new Rectangle2D.Double(Math.random()*canvas1.getWidth(), Math.random()*canvas1.getHeight(), 100*Math.random(), 100*Math.random());
        BasicShapeGraphic bs = new BasicShapeGraphic(rect, RandomStyles.shape());
        bs.addMouseListener(new GraphicHighlighter());
        bs.setDefaultTooltip("<html><b>Rectangle</b>: <i>" + rect + "</i>");
        root1.addGraphic(bs);
    }
    
    @Action
    public void addString() {        
        Point2D pt = randomPoint();
        BasicStringGraphic gs = new BasicStringGraphic(pt, String.format("[%.4f, %.4f]", pt.getX(), pt.getY()));
        gs.setStyle(RandomStyles.string());
        gs.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(gs);
    }
    
    @Action
    public void addPointSet() {      
        BasicPointSetGraphic bp = new BasicPointSetGraphic(
                new Point2D[]{randomPoint(), randomPoint(), randomPoint()},
                this.bps);
        bp.addContextMenuInitializer(new ContextMenuInitializer(){
            public void initialize(JPopupMenu menu, Point point) {
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
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="GRAPHICS WITH DELEGATORS">
        
    @Action
    public void addDelegatingPointSet() {
        Set<String> list = new HashSet<String>(Arrays.asList("Africa", "Indiana Jones", "Micah Andrew Peterson", "Chrysanthemum", "Sequoia", "Asher Matthew Peterson", "Elisha", "Bob the Builder"));
        final DelegatingPointSetGraphic<String> bp = new DelegatingPointSetGraphic<String>(
                list, 
                new Delegator<String,Point2D>(){ 
                    public Point2D of(String src) { return new Point(10*src.length(), 50 + 10*src.indexOf(" ")); } 
                });
        bp.getStyler().setLabelDelegate(new Delegator<String,String>(){ public String of(String src) { return src; } });
        bp.getStyler().setStyleDelegate(new Delegator<String,PointStyle>(){
            BasicPointStyle r = new BasicPointStyle();
            LabeledPointStyle lps = new LabeledPointStyle(r);
            public PointStyle of(String src) {
                int i1 = src.indexOf("a"), i2 = src.indexOf("e"), i3 = src.indexOf("i"), i4 = src.indexOf("o");
                r.setRadius(i1+5);
                r.setShape(ShapeLibrary.getAvailableShapers().get(i2+3));
                r.setThickness(2+i3/3f);
                r.setFill(new Color((i4*10+10) % 255, (i4*20+25) % 255, (i4*30+50) % 255));
                ((BasicStringStyle)lps.getLabelStyle()).color(r.getFill());
                return lps;
            }
        });
        bp.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(bp);        
    }
    
    @Action
    public void addDelegatingPointSet2() {
        final DelegatingPointSetGraphic<Integer> bp = new DelegatingPointSetGraphic<Integer>(
                new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10)),
                new Delegator<Integer,Point2D>(){ public Point2D of(Integer src) { return randomPoint(); } });
        bp.getStyler().setLabelDelegate(new Delegator<Integer,String>(){ public String of(Integer src) { return src.toString(); } });
        bp.getStyler().setStyleDelegate(new Delegator<Integer,PointStyle>(){
            BasicPointStyle r = new BasicPointStyle();
            LabeledPointStyle lps = new LabeledPointStyle(r);
            { ((BasicStringStyle)lps.getLabelStyle()).setAnchor(Anchor.Center); }
            public PointStyle of(Integer src) {
                r.setRadius(src+2);
                r.setFill(new Color((src*10+10) % 255, (src*20+25) % 255, (src*30+50) % 255));
                ((BasicStringStyle)lps.getLabelStyle())
                        .color(r.getFill().brighter().brighter())
                        .fontSize(5+src.floatValue());
                return lps;
            }
        });
        bp.addMouseListener(new GraphicHighlighter());
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
        Map<Point2D, Point2D> pMap = Delegators.apply(new Delegator<Point2D,Point2D>(){ public Point2D of(Point2D src) { return src; } }, new HashSet<Point2D>(Arrays.asList(pts)));
        gr.setPoints(pMap);
        gr.getStyler().setStyleDelegate(new Delegator<Point2D,PointStyle>(){
            public PointStyle of(Point2D src) {
                int yy = (int) Math.min(src.getX()/3, 255);
                return new BasicPointStyle()
                        .radius((float) Math.sqrt(src.getY()))
                        .fill(new Color(yy,0,255-yy));
            }
        });
        gr.getStyler().setLabelDelegate(new Delegator<Point2D,String>(){ public String of(Point2D src) { return String.format("(%.1f,%.1f)", src.getX(), src.getY()); } });     
        gr.getStyler().setLabelStyleDelegate(new Delegator<Point2D,StringStyle>(){
            BasicStringStyle bss = new BasicStringStyle();
            public StringStyle of(Point2D src) {
                return bss;                
            }
        });
        gr.setEdges(edges);
        gr.getEdgeStyler().setStyleDelegate(new Delegator<Edge<Point2D>,PathStyle>(){
            public PathStyle of(Edge<Point2D> src) {
                Point2D src0 = src.getNode1(), src1 = src.getNode2();
                int dx = (int) (src0.getX() - src1.getX());
                dx = Math.min(Math.abs(dx/2), 255);
                int dy = (int) (src0.getY() - src1.getY());
                dy = Math.min(Math.abs(dy/3), 255);
                
                return new FancyPathStyle()
                        .color(new Color(dx, dy, 255-dy))
                        .width((float) Math.sqrt(dx*dx+dy*dy)/50);
            }
        });
        gr.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(gr);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="COMPOSITES">
    
    @Action
    public void addArrow() {      
        Point2D p1 = randomPoint(), p2 = randomPoint();
        SegmentGraphic ag = new SegmentGraphic(p1, p2);
        ag.setDefaultTooltip("<html><b>Segment</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(ag);
    }
    
    @Action
    public void addLabeledPoint() {
         Point2D p1 = randomPoint();
        LabeledPointGraphic lpg = new LabeledPointGraphic(
                p1,
                String.format("(%.2f,%.2f)", p1.getX(), p1.getY()),
                RandomStyles.point()
                );
        lpg.setDefaultTooltip("<html><b>Labeled Point</b>: <i> " + p1 + "</i>");
        lpg.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(lpg);        
    }
    
    @Action
    public void addRuler() {
         Point2D p1 = randomPoint(), p2 = randomPoint();
        RulerGraphic lg = new RulerGraphic(p1, p2);
        
        lg.setTickPositions(new float[]{(float)Math.random(), (float)Math.random(), (float)Math.random()});
        lg.setTickLabels(new String[]{"A", "B", "C"});
        lg.setRuleLeft((int)(10*Math.random()));
        lg.setRuleRight((int)(-10*Math.random()));
        
        lg.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(lg);        
    }
    
    @Action
    public void add2Point() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
        ag.setDefaultTooltip("<html><b>Two Points</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(ag);        
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="COOL STUFF USING SPECIAL STYLES">
    
    @Action
    public void addLabeledPointSet() {   
        BasicPointSetGraphic bp = new BasicPointSetGraphic(new Point2D[]{randomPoint(), randomPoint(), randomPoint(), randomPoint()});
        bp.setStyle(new LabeledPointStyle());
        bp.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(bp);
    }
    
    @Action
    public void addRay() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
        InfinitePointStyle ips = new InfinitePointStyle();
        ips.setRayStyle(new ArrowPathStyle());
        ag.setEndPointStyle(ips);
        ag.setDefaultTooltip("<html><b>Ray</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(ag);        
    }
    
    @Action
    public void addLine() {
      Point2D p1 = randomPoint(), p2 = randomPoint();
        TwoPointGraphicSupport ag = new TwoPointGraphicSupport(p1, p2);
        InfinitePointStyle ips = new InfinitePointStyle();
        ips.setRayStyle(new ArrowPathStyle());
        ips.setExtendBoth(true);
        ag.setEndPointStyle(ips);
        ag.setDefaultTooltip("<html><b>Line</b>: <i>" + p1 + ", " + p2 + "</i>");
        ag.addMouseListener(new GraphicHighlighter());
        root1.addGraphic(ag);        
    }
    
    //</editor-fold>
    
        
    //<editor-fold defaultstate="collapsed" desc="APP CODE">

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        BlaiseGraphicsTestView view = new BlaiseGraphicsTestView(this);
        canvas1 = view.canvas1;
        root1 = view.canvas1.getGraphicRoot();
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
