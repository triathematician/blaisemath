package specto.integer2;

/*
 * ArcZ2D.java
 * Created on May 3, 2008
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import sequor.event.MouseVisometryEvent;
import sequor.style.VisualStyle;
import specto.PlottableGroup;

/**
 * <p>
 * ArcZ2D represents a path between two points on the grid. Both points have an (optional) angle
 * argument specifying the tangent angle of departure from the endpoint.
 * </p>
 * @author Elisha Peterson
 */
public class ArcZ2D extends PlottableGroup<Integer2> {
    VertexZ2D start;
    VertexZ2D stop;
    
    public ArcZ2D(){
        this(0,0,90,1,0,90);
    }    
    public ArcZ2D(int x1,int y1,double angleStart,int x2,int y2,double angleStop){
        start = new VertexZ2D (x1, y1, angleStart);
        stop = new VertexZ2D (x2, y2, angleStop);
        start.style.setValue(VertexZ2D.EDGE_CILIATED);
        stop.style.setValue(VertexZ2D.EDGE_CILIATED);
        add(start);
        add(stop);
        setColor(Color.RED);
    }
    
    
    // PROPERTY PATTERNS

    void setStart(int x, int y, double angle) { start.setTo(x,y,angle); }
    void setStop(int x, int y, double angle) { stop.setTo(x,y,angle); }
    
    
    // PAINT METHODS

    @Override
    public void paintComponent(Graphics2D g, Integer2 v) {
        start.paintComponent(g, v);
        stop.paintComponent(g, v);
        Point2D.Double p1 = v.toWindow(start.coordinate);
        Point2D.Double p2 = v.toWindow(stop.coordinate);
        Path2D.Double result=new Path2D.Double();
        result.moveTo(p1.x,p1.y);
        result.curveTo(
                p1.x+50*Math.cos(start.angle*Math.PI/180.),p1.y-50*Math.sin(start.angle*Math.PI/180.),
                p2.x+50*Math.cos(stop.angle*Math.PI/180.),p2.y-50*Math.sin(stop.angle*Math.PI/180.),
                p2.x,p2.y);
        g.setColor(getColor());
        switch(style.getValue()){
            case PLAIN:
                g.setStroke(VisualStyle.THICK_STROKE);
                break;
            case THICK:
                g.setStroke(VisualStyle.VERY_THICK_STROKE);
                break;
        }
        g.draw(result);
    }

    
    // STYLE METHODS
    
    public static final int PLAIN = 0;
    public static final int DIRECTED = 1;
    public static final int REVERSE_DIRECTED = 2;
    public static final int LABELED = 3;
    public static final int THICK = 4;
    public static final int GROUP_LABELED = 5;
    
    final static String[] styleStrings = { "Plain", "Directed", "Reverse Directed", "Labeled", "Thick", "Group Labeled" };
    
    @Override
    public String[] getStyleStrings() {return styleStrings;}   
    
    @Override
    public String toString() { return "Arc"; }

    @Override
    public void mouseClicked(MouseVisometryEvent<Integer2> e) {
        System.out.println(toPGF());
        super.mouseClicked(e);        
    }
    
    
    // MOUSE HANDLING
    
    
    
    // OUTPUT
    
    public String toPGF() {
        return "\\draw[thick]("+start.coordinate+")to[out="+start.angle+",in="+stop.angle+"]("+stop.coordinate+");";
    }
    
    public String toSVG() {
        return "nonfunctional!!";
    }
}
