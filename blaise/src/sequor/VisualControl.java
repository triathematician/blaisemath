/**
 * VisualControl.java
 * Created on Mar 12, 2008
 */

package sequor;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sequor.control.BoundedShape;
import sequor.control.BoundedWidthShape;
import sequor.control.SnapRule;

/**
 * Superclass for visual elements on PlotPanel's (or elsewhere) which may be used to control underlying DataModel's, such as IntegerRangeModel
 * or DoubleRangeModel. A typical example might be a slider that can be displayed on a plot and used to change the value of a parameter.
 * 
 * @author Elisha Peterson
 */
public class VisualControl extends java.awt.Component implements ChangeListener,MouseListener,MouseMotionListener {
    
    /** Specifies the shape of the object. */
    protected BoundedShape backgroundShape;
    /** Specifies "sticky" locations where the control will prefer to locate itself. */
    protected SnapRule snapRule;
    /** If true, the snap location according to snapRule will not change if things are resized. */
    protected boolean stickySnap=false;
    /** If true, component can be dragged around the screen. */
    protected boolean draggable=false;
        
    
    // CONSTRUCTORS
    
    public VisualControl(){this(0,0);}
    public VisualControl(int x,int y){this(x,y,10,10);}
    public VisualControl(int x,int y,int wid,int ht){
        super.setBounds(x,y,wid,ht);
        backgroundShape=new BoundedWidthShape.RoundRectangle(8);
    }
    
    // BEAN PATTERNS
    
    public SnapRule getSnapRule(){return snapRule;}
    
    public void setDraggable(boolean draggable){this.draggable=draggable;}
    public void setSnappingOff(){snapRule=null;}
    public void setSnapRule(SnapRule rule){snapRule=rule;}
    public void setStickySnap(boolean sticky){this.stickySnap=sticky;}
    public void enableSnapping(final JPanel p,int padding,int stickyPoint){
        SnapRule.enableSnapping(this,p,padding,stickyPoint);
    }
    public void enableSnapping(final JPanel p){
        SnapRule.enableSnapping(this,p,5);
    }
    
    // BEAN PATTERNS (POSITION/SIZE)
    
    public void translate(int dx,int dy){if(dx!=0 || dy!=0){setLocation(getX()+dx,getY()+dy);}}
        
    public Point getControlW(){return new Point(getX(),getY()+getHeight()/2);}
    public Point getControlE(){return new Point(getX()+getWidth(),getY()+getHeight()/2);}
    public Point getControlN(){return new Point(getX()+getWidth()/2,getY());}
    public Point getControlS(){return new Point(getX()+getWidth()/2,getY()+getHeight());}
    public Point getControlNW(){return new Point(getX(),getY());}
    public Point getControlNE(){return new Point(getX()+getWidth(),getY());}
    public Point getControlSW(){return new Point(getX(),getY()+getHeight());}
    public Point getControlSE(){return new Point(getX()+getWidth(),getY()+getHeight());}
    
    public BoundedShape getBackgroundShape(){return backgroundShape;}    
    public void setBackgroundShape(BoundedShape shape){this.backgroundShape=shape;}
    
    @Override
    public void setLocation(int x,int y){
        if(snapRule!=null){
            Point snapped=null;
            if(stickySnap){snapped=snapRule.snappedPoint();}
            if(snapped==null){
                snapped=snapRule.snappedPoint(x,y);
            }
            if(snapped!=null){
                super.setLocation(snapped.x,snapped.y);
                return;
            }            
        }
        super.setLocation(x,y);
    }
    
    // PAINTING
       
    public static final Stroke DEFAULT_STROKE=new BasicStroke(1.0f);
    protected boolean paintBorder = true;
    protected boolean paintBackground = true;
    
    public void paintComponent(Graphics2D g){
        paintComponent(g,0.5f);
    }
    public void paintComponent(Graphics2D g,float opacity){
        g.setColor(getBackground());
        g.setStroke(DEFAULT_STROKE);
        Shape s=backgroundShape.getBoundedShape(getBounds(),0);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));
        Paint p=g.getPaint();
        g.setPaint(new GradientPaint(0,0,getBackground()==null?Color.BLACK:getBackground(),100,0,Color.GRAY));
        if(paintBackground){g.fill(s);}
        g.setPaint(p);
        g.setColor(getForeground());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        if(paintBorder){g.draw(s);}
    }
    public void paintComponent(Graphics2D g,int x,int y){
        setLocation(x,y);
        paintComponent(g);
    }
    
    // EVENT HANDLING

    public void stateChanged(ChangeEvent e) {changeEvent=e;fireStateChanged();}
    
    
    // CHANGEEVENT HANDLING   
    
    protected ChangeEvent changeEvent=null;
    protected EventListenerList listenerList=new EventListenerList();    
    public void addChangeListener(ChangeListener l){listenerList.add(ChangeListener.class,l);}
    public void removeChangeListener(ChangeListener l){listenerList.remove(ChangeListener.class,l);}
    protected void fireStateChanged(){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ChangeListener.class){
                if(changeEvent==null){changeEvent=new ChangeEvent(this);}
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
    
    
    // MOUSE HANDLING 
    
    protected boolean adjusting;
    protected Point initialPoint;
    
    public boolean isAdjusting(){return adjusting;}
    public void setAdjusting(boolean newValue){adjusting=newValue;}

    public boolean clicked(MouseEvent e) {
        return(backgroundShape==null || !backgroundShape.isClickable())?
            getBounds().contains(e.getPoint()):
            backgroundShape.getBoundedShape(getBounds(),0).contains(e.getPoint());        
    }
    
    @Override
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}

    @Override
    public void mousePressed(MouseEvent e) {
        if(draggable && clicked(e)){
            adjusting=true;
            initialPoint=new Point(getX()-e.getX(),getY()-e.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(adjusting && initialPoint!=null){
            setLocation(e.getX()+initialPoint.x,e.getY()+initialPoint.y);
            fireStateChanged();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(adjusting){
            Point current=e.getPoint();
            setLocation(current.x+initialPoint.x,current.y+initialPoint.y);
            initialPoint=null;
            adjusting=false;
        }
    }
}
