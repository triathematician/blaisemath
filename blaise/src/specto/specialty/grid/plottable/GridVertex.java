/*
 * GridVertex.java
 * Created on May 3, 2008
 */

package specto.specialty.grid.plottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import scio.coordinate.Z2;
import scio.coordinate.R2;
import sequor.event.MouseVisometryEvent;
import specto.DynamicPlottable;
import specto.specialty.grid.Grid2;
import specto.style.LineStyle;

/**
 * <p>
 * GridVertex is a simple dot on the grid... represents a <i>ciliated</i> node.
 * </p>
 * @author Elisha Peterson
 */
public class GridVertex extends DynamicPlottable<Grid2> {
    
    Z2 coordinate;
    Double angle = null;
    int dist = 1;
    
    public GridVertex(){this(new Z2());}    
    public GridVertex(Z2 coordinate){this(coordinate,null);}
    GridVertex(int x, int y, double angle) {this(new Z2(x,y),angle);}
    public GridVertex(Z2 coordinate,Double angle){
        this.coordinate = coordinate;
        this.angle = angle;
        setColor(Color.GREEN);
        style.setValue(DOT_CILIATED);
    }
    

    // PROPERTY PATTERNS
    
    void setTo(int x, int y) { setTo(x,y,angle); }
    void setTo(int x, int y, Double angle) {
        if (coordinate.x != x || coordinate.y != y || !this.angle.equals(angle)) {
            coordinate.setLocation(x, y);
            this.angle = angle;
            fireStateChanged();
        }
    }
    
    
    // PAINT METHODS

    @Override
    public void paintComponent(Graphics2D g, Grid2 v) {
        Point2D.Double center=v.toWindow(coordinate);
        double radius=6;
        g.fill(new Ellipse2D.Double(center.x-radius,center.y-radius,2*radius,2*radius));        
        switch(style.getValue()){
            case PLAIN :
                break;
            case DOT_CILIATED : 
                g.fill(new Ellipse2D.Double(
                        center.x+3*radius*Math.cos(angle*Math.PI/180)-.5*radius,
                        center.y-3*radius*Math.sin(angle*Math.PI/180)-.5*radius,
                        radius,radius));
                break;
            case EDGE_CILIATED :
                g.setStroke(LineStyle.THIN_STROKE);
                g.draw(new Line2D.Double(
                        center.x,center.y,
                        center.x+3*radius*Math.cos(angle*Math.PI/180),
                        center.y-3*radius*Math.sin(angle*Math.PI/180)));
                break;
        }
    }

    
    // STYLE METHODS
    
    public static final int PLAIN = 0;
    public static final int DOT_CILIATED = 1;
    public static final int EDGE_CILIATED = 2;
    
    final static String[] styleStrings = { "Plain", "Dot Ciliated", "Edge Ciliated" };
    
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    
    @Override
    public String toString(){return "Node";}
    
    
    // EVENT HANDLING
    
    int mode = 0;
      
    @Override
    public boolean clicked(MouseVisometryEvent<Grid2> e) {return withinClickRange(e,coordinate);}

    @Override
    public void mouseClicked(MouseVisometryEvent<Grid2> e) {
        style.cycle();
    }
    
    @Override
    public void mousePressed(MouseVisometryEvent<Grid2> e) {
        super.mousePressed(e);
        if((e.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK)) == InputEvent.CTRL_DOWN_MASK) {
             mode = 1;
        }       
    }

    @Override
    public void mouseDragged(MouseVisometryEvent<Grid2> e) {
        if(adjusting){
            if(mode == 1){
                Point2D.Double center = e.getSourceVisometry().toWindow(coordinate);
                if(Math.abs(center.x-e.getX())+Math.abs(center.y-e.getY()) > 15) {
                    angle = 180*R2.angle(-center.x+e.getX(),center.y-e.getY())/Math.PI;
                    fireStateChanged();
                }
            } else {
                setTo(((Z2)e.getCoordinate()).x,((Z2)e.getCoordinate()).y);
            }
        }        
    }

    @Override
    public void mouseReleased(MouseVisometryEvent<Grid2> e) {
        super.mouseReleased(e);
        mode = 0;
        System.out.println(toPGF());
    }    
    
    
    
    // OUTPUT
    
    public String toPGF() {
        return "\\draw[draw=none,fill=black]("+coordinate+")node{};\n"+
                "\\draw[draw=none,fill=black,shift={("+angle+":.2)}]("+coordinate+")node{};";
    }
    
    public String toSVG() {
        return "nonfunctional!!";
    }    
}
