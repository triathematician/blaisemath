/**
 * VisualControl.java
 * Created on Mar 12, 2008
 */

package sequor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.ColorModel;

/**
 * Superclass for visual elements on PlotPanel's (or elsewhere) which may be used to control underlying DataModel's, such as IntegerRangeModel
 * or DoubleRangeModel. A typical example might be a slider that can be displayed on a plot and used to change the value of a parameter.
 * 
 * @author Elisha Peterson
 */
public abstract class VisualControl extends FiresChangeEvents implements ChangeListener,MouseListener,MouseMotionListener {
    
    protected ColorModel color;
    
    public VisualControl(){this(0,0);}
    public VisualControl(double x,double y){this(x,y,10,10);}
    public VisualControl(double x,double y,double wid,double ht){
        color=new ColorModel(Color.BLACK);
        boundingBox=new Rectangle2D.Double(x,y,wid,ht);
    }
    
    // CLASSES
    
    protected Rectangle2D.Double boundingBox;
    
    // BEAN PATTERNS
    
    public Point2D.Double getPosition(){return new Point2D.Double(boundingBox.getX(),boundingBox.getY());}
    public void setPosition(Point2D.Double position){boundingBox.setRect(position.x,position.y,getWidth(),getHeight());}    
    public void setPosition(double x,double y, double squareSize){boundingBox.setRect(x,y,squareSize,squareSize);}
    public void setSize(double width, double ht) {boundingBox.setRect(getX(),getY(),width,ht);}
    
    public double getX(){return boundingBox.getX();}
    public double getY(){return boundingBox.getY();}
    public double getWidth(){return boundingBox.getWidth();}
    public double getHeight(){return boundingBox.getHeight();}
    
    public Point2D.Double getControlW(){return new Point2D.Double(getX(),getY()+getHeight()/2);}
    public Point2D.Double getControlE(){return new Point2D.Double(getX()+getWidth(),getY()+getHeight()/2);}
    public Point2D.Double getControlN(){return new Point2D.Double(getX()+getWidth()/2,getY());}
    public Point2D.Double getControlS(){return new Point2D.Double(getX()+getWidth()/2,getY()+getHeight());}
    public Point2D.Double getControlNW(){return new Point2D.Double(getX(),getY());}
    public Point2D.Double getControlNE(){return new Point2D.Double(getX()+getWidth(),getY());}
    public Point2D.Double getControlSW(){return new Point2D.Double(getX(),getY()+getHeight());}
    public Point2D.Double getControlSE(){return new Point2D.Double(getX()+getWidth(),getY()+getHeight());}
    
    // PAINTING
       
    public abstract void paintComponent(Graphics2D g);
    
    // EVENT HANDLING

    public void stateChanged(ChangeEvent e) {fireStateChanged();}
    
    
    // FiresChangeEvents METHODS
    
    @Override
    public FiresChangeEvents clone() {throw new UnsupportedOperationException("Not supported yet.");}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {throw new UnsupportedOperationException("Not supported yet.");}
    @Override
    public PropertyChangeEvent getChangeEvent(String s) {return new PropertyChangeEvent(this, s, null,null);}
    @Override
    public void setValue(String s) {throw new UnsupportedOperationException("Not supported yet.");}
    
    
    // MOUSE HANDLING 
    
    public static final int CLICK_EDIT_RANGE=8;
    protected boolean adjusting;

    public boolean clickedPos(Point2D.Double position,MouseEvent e,int range){return Math.abs(position.x-e.getX())+Math.abs(position.y-e.getY())<range;}
    public boolean clicked(MouseEvent e) {return boundingBox.contains(e.getPoint());}
    
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e){if(clicked(e)){adjusting=true;}}
    public void mouseDragged(MouseEvent e){}
    public void mouseReleased(MouseEvent e){mouseDragged(e);adjusting=false;}
    public void mouseMoved(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
}
