/**
 * VisualControl.java
 * Created on Mar 12, 2008
 */

package sequor;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Superclass for visual elements on PlotPanel's (or elsewhere) which may be used to control underlying DataModel's, such as IntegerRangeModel
 * or DoubleRangeModel. A typical example might be a slider that can be displayed on a plot and used to change the value of a parameter.
 * 
 * @author Elisha Peterson
 */
public abstract class VisualControl extends FiresChangeEvents implements ChangeListener,MouseListener,MouseMotionListener {
    
    // CLASSES
    
    protected java.awt.geom.Point2D.Double position;
    
    // BEAN PATTERNS
    
    public java.awt.geom.Point2D.Double getPosition(){return position;}
    public void setPosition(java.awt.geom.Point2D.Double position){this.position=position;}
    
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

    public boolean clickedPos(Point2D.Double position,MouseEvent e){return Math.abs(position.x-e.getX())+Math.abs(position.y-e.getY())<CLICK_EDIT_RANGE;}
    public boolean clicked(MouseEvent e) {return clickedPos(position,e);}
    
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e){if(clicked(e)){adjusting=true;}}
    public void mouseDragged(MouseEvent e){}
    public void mouseReleased(MouseEvent e){mouseDragged(e);adjusting=false;}
    public void mouseMoved(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
}
