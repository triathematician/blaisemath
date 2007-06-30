package Blaise;

import Interface.BPlottable;
import Model.FollowerRangeModel;
import Model.PointRangeModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <b>BClickablePoint.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 27, 2007, 10:14 AM</i><br><br>
 * 
 * A GUI component which both displays a point on a plot and allows the point to
 *   be clicked... the plot feeds mouse input to this class to be handled. If the
 *   point is determined to be clicked on/dragged, then the class determines where
 *   the point ends up, and then makes a change request to the underlying data
 *   model. Note that the point is not stored here exactly, but only in that model.<br><br>
 * 
 * A note on the underlying model: requires a PointRangeModel to initialize and
 *   store the point. All changes MUST pass through the model.<br><br>
 * 
 * The first implementation likely include requests for whenever the mouse button
 *   is down... although this seems only necessary for when the button is released,
 *   so later versions might accomodate that.<br><br>
 * 
 * As for events, the change is initialized when the button is pressed in the
 *   vicinity of the point. A special method (clicked) determines whether or not
 *   the mouse point is determined to be on the point.<br><br>
 * 
 * The underlying model stores Cartesian coordinates, whereas this class must convert
 *   to integer coordinates. For these purposes, two FollowerRangeModels are
 *   implemented, which store the integer values in terms of the GUI window. That's
 *   where all the translation occurs.<br><br>
 *
 * <b>DESIRED FEATURES LIST:</b><br>
 *    1. Export options to BPlotPointStyle
 *    2. Expand number of display options
 *    3. Write corresponding class for vectors
 *    4. Write display options panel
 *    5. Right click menu
 *    6. When click-moving, maintain "ghost" at point where it started
 *    7. Snap to integers? starting point? other points?
 *    8. Option to display coordinates (especially when moving!)
 */
public class BClickablePoint implements BPlottable {
    
    /** Constants */
    public static final int SMALL=0;
    public static final int BIG=1;
    public static final int CONCENTRIC=2;
    public static final int[] SIZE={1,3,6};
    
    
    /** The controlling class. */
    private BPlot2D controller;
    /** This model stores the data. */
    private PointRangeModel pointModel;
    /** These models control adjustments of x,y values */
    private FollowerRangeModel xModel,yModel;
    
    /** When the value is adjusting using the mouse. */
    private boolean on;
    
    /** Display parameters */
    private Color color;
    private int style;
    
    /** When the value is adjusting using the mouse. */
    private boolean adjusting;
    
    /** Default constructor */
    BClickablePoint(){
        initializeModels(new PointRangeModel());
        setStyle(CONCENTRIC);
        setColor(Color.GREEN);
    }
      
    /** Extra constructors */
    BClickablePoint(PointRangeModel pointModel,BPlot2D controller){
        this.controller=controller;
        initializeModels(pointModel);
        setStyle(BIG);
        setColor(Color.BLACK);
    }
    BClickablePoint(PointRangeModel pointModel,BPlot2D controller,int style){
        this.controller=controller;
        addChangeListener(controller);
        initializeModels(pointModel);
        setStyle(style);
        setColor(Color.BLACK);
    }
    BClickablePoint(PointRangeModel pointModel,BPlot2D controller,Color color){
        this.controller=controller;
        addChangeListener(controller);
        initializeModels(pointModel);
        setStyle(BIG);
        setColor(color);
    }

    public void initializeModels(){initializeModels(new PointRangeModel());}
 
    /** Initializes the two follower models */
    private void initializeModels(PointRangeModel pointModel){
        this.pointModel=pointModel;
        pointModel.addChangeListener(controller);
        xModel=new FollowerRangeModel(pointModel.xModel);
        yModel=new FollowerRangeModel(pointModel.yModel);
        updateDimensions();
    }
       
     
    // Getters & setters
    public Color getColor(){return color;}
    public void setColor(Color color){this.color=color;}
    public int getStyle(){return style;}
    public void setStyle(int style){this.style=style;}
    public void cycleStyle(){style++;if(style==3){style=0;}}
    public boolean getOn(){return on;}
    public void setOn(boolean on){this.on=on;}
    
    /**
     * These get values from the underlying point model, adjusted by
     * the affine transform corresponding to the window.
     */
    public double getX(){return xModel.getDoubleValue();}
    public void setX(double x){xModel.setDoubleValue(x);}
    public void setX(int windowX){xModel.setDoubleValue(controller.toCartesianX(windowX));}
    public double getY(){return yModel.getDoubleValue();}
    public void setY(double y){yModel.setDoubleValue(y);}
    public void setY(int windowY){yModel.setDoubleValue(controller.toCartesianY(windowY));}
    
    // Sets the dimensions to use
    public void updateDimensions(){
        xModel.setMaximum(controller.getWidth());
        yModel.setMaximum(controller.getHeight());
    }
    
    public void paintComponent(Graphics gb){
        updateDimensions();
        Graphics2D g=(Graphics2D)gb;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(color);
        int winX=controller.toWindowX(getX());
        int winY=controller.toWindowY(getY());
        switch(style){
            case SMALL:
                g.fill(new Ellipse2D.Double(winX-1,winY-1,2,2));
                break;
            case BIG:
                g.fill(new Ellipse2D.Double(winX-3,winY-3,6,6));
                break;
            case CONCENTRIC:
                g.draw(new Ellipse2D.Double(winX-6,winY-6,12,12));
                g.fill(new Ellipse2D.Double(winX-3,winY-3,6,6));
                break;
            default:
                g.fill(new Ellipse2D.Double(winX-1,winY-1,2,2));
                break;
        }
    }
    public void paintComponent(Graphics2D g, AffineTransform at) {
    }
    
    /** Determines if the point was clicked on, given a mouse event. */
    public boolean clicked(MouseEvent e){
        return Math.abs(e.getX()-controller.toWindowX(getX()))+Math.abs(e.getY()-controller.toWindowY(getY()))<CLICK_EDIT_RANGE;
    }
    
    // sets parameters based on a mouse event
    private void setUsingMouseEvent(MouseEvent e){setX(e.getX());setY(e.getY());}    
    //Methods required by the MouseInputListener interface.
    public void mousePressed(MouseEvent e){if(clicked(e)){adjusting=true;e=null;}}
    public void mouseDragged(MouseEvent e){if(adjusting){setUsingMouseEvent(e);}}
    public void mouseReleased(MouseEvent e){if(adjusting){setUsingMouseEvent(e);}adjusting=false;}
    public void mouseClicked(MouseEvent e){if(clicked(e)){cycleStyle();}}
    public void mouseMoved(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}

    public void stateChanged(ChangeEvent e){}


    /** Event handlers */
    protected ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    
    /*
     * The rest of this is event handling code copied from
     * DefaultBoundedRangeModel.
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {}
}