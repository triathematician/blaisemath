/*
 * Grid2D.java
 * Created on Sep 14, 2007, 8:23:42 AM
 */

package specto.dynamicplottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JToolBar;
import specto.PlotPanel;
import specto.Visometry;
import specto.DynamicPlottable;
import specto.coordinate.R2;
import specto.plotpanel.Plot2D;
import specto.visometry.Euclidean2;

/**
 * Draws a grid on a 2-dimensional plot. Also has support for drawing axes, tick marks,
 * labels on the axes, and for editing aspect-ratio/zooming/etc. In short, handles all
 * visual aids drawn on the graph, as well as almost all mouse events relavent to the plot window.
 * <br><br>
 * Thus, in a sense, this class is a visual editor for the AffineTransform of the
 * corresponding visometry, Euclidean2.
 * <br><br>
 * The primary component here is the grid, or set of x-values plus set of y-values. These
 * are stored as two vectors which are updated whenever the underlying affine transform
 * changes.
 * <br><br>
 * @author Elisha Peterson
 */
public class Grid2D extends DynamicPlottable<Euclidean2> implements MouseWheelListener {
    
    // PROPERTIES
    
    private boolean gridOn;
    private boolean ticksOn;
    private boolean labelsOn;
    private boolean axesOn;
    private boolean coordsOn;
    private int axesType;
    
    private Color axesColor;
    private Color gridColor;
    
    private Vector<Integer> xGrid;
    private Vector<Integer> yGrid;
    // TODO think about whether to move the below to the Euclidean2 class
    private Point origin;
    private Point min;
    private Point max;
    
    
    // CONSTANTS
    
    public static final int AXES_BOX=0;
    public static final int AXES_CROSS=1;
    public static final int AXES_L=2;
    
    private static final int IDEAL_GRID_SPACE=40;
    private static final int TICK_HEIGHT=5;
    private static final DecimalFormat nf=new DecimalFormat("##0.#");
    
    
    // CONSTRUCTORS
    
    public Grid2D(){}
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public boolean isGridOn(){return gridOn;}
    public boolean isTicksOn(){return ticksOn;}
    public boolean isLabelsOn(){return labelsOn;}
    public boolean isAxesOn(){return axesOn;}
    public boolean isCoordsOn(){return coordsOn;}
    public int getAxesType(){return axesType;}
    
    public void setGridOn(boolean newValue){if(newValue!=gridOn){gridOn=newValue;fireStateChanged();}}
    public void setTicksOn(boolean newValue){if(newValue!=ticksOn){ticksOn=newValue;fireStateChanged();}}
    public void setLabelsOn(boolean newValue){if(newValue!=labelsOn){labelsOn=newValue;fireStateChanged();}}
    public void setAxesOn(boolean newValue){if(newValue!=axesOn){axesOn=newValue;fireStateChanged();}}
    public void setCoordsOn(boolean newValue){if(newValue!=coordsOn){coordsOn=newValue;fireStateChanged();}}
    public void setAxesType(int newValue){if(newValue!=axesType){axesType=newValue;fireStateChanged();}}
    
    
    // METHODS: COMPUTING THE GRID
    
    /** Sets the boundaries of the window */
    public void initMinMax(Euclidean2 v){
        origin=v.toWindow(0,0);
        min=v.getWindowMin();
        max=v.getWindowMax();
    }
    
    /** Sets the spacing of the grid using the underlying visometry */
    public void initGrid(Euclidean2 v){
        throw new UnsupportedOperationException("Not supported yet.");
        // gridX=niceRange(bounds.drawLeftX,bounds.drawRightX,bounds.getDrawWidth()*(double)IDEAL_GRID_SPACE/(double)getWidth());
        //  gridY=niceRange(bounds.drawLowY,bounds.drawUpY,bounds.getDrawHeight()*(double)IDEAL_GRID_SPACE/(double)getHeight());
    }
    
    /**
     * Takes in a range of values in min/max/step format and returns a
     *   "rounded" version of that range of doubles.
     **/
    private Vector<Double> niceRange(double min,double max,double idealStep) {
        if(idealStep==0){return new Vector<Double>();}
        // first, put the range in between 1 and 10
        double tempx=idealStep;
        while(tempx>10){tempx/=10;}
        while(tempx<1){tempx*=10;}
        
        // initialize factor to be the power of 10 just computed
        double factor=idealStep/tempx;
        // now set the factor as that times 1,2,2.5, or 5
        if(tempx<1.8){}
        if(tempx<2.2){factor*=2;} else if(tempx<3.5){factor*=2.5;} else if(tempx<7.5){factor*=5;} else{factor*=10;}
        
        double minx=factor*Math.ceil(min/factor);
        double maxx=factor*Math.floor(max/factor);
        Vector<Double> elements=new Vector<Double>();
        do{
            elements.add(minx);
            minx+=factor;
        }while(minx<maxx+factor);
        
        return elements;
    }

    
    // METHODS: DRAWING ALL ELEMENTS
    
    /** Repaints the component on the given panel, using the given visometry. */
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        initMinMax(v);
        if(gridOn||ticksOn||labelsOn){initGrid(v);}
        if(gridOn){
            g.setColor(gridColor);
            drawHLines(g,min.x,max.x);
            drawVLines(g,min.y,max.y);
        }
        if(axesOn){paintAxes(g);}
    }
    
    /** Paints set of horizontal lines specified by yGrid between xMin and xMax */
    public void drawHLines(Graphics2D g,int xMin,int xMax){for(int y:yGrid){g.draw(new Line2D.Double(xMin,y,xMax,y));}}
    /** Paints set of vertical lines specified by xGrid between yMin and yMax */
    public void drawVLines(Graphics2D g,int yMin,int yMax){for(int x:xGrid){g.draw(new Line2D.Double(x,yMin,x,yMax));}}
    /** Paints a string at a *relative* position */
    public void drawString(Graphics2D g,String s,int x,int y,int dx,int dy){g.drawString(s,x+dx,y-dy);}
    
    /** Paints the axes, ticks, and labels on the axes. */
    public void paintAxes(Graphics2D g){
        g.setColor(axesColor);
        double temp;
        Point2D.Double tempPoint;
        switch(axesType){
        case AXES_CROSS:
            g.draw(new Line2D.Double(min.x,origin.y,max.x,origin.y));
            g.draw(new Line2D.Double(origin.x,min.y,origin.x,max.y));
            drawString(g,"x",max.x,origin.y,-10,15);
            drawString(g,"y",origin.x,max.y,10,-15);
            if(ticksOn){drawHLines(g,-TICK_HEIGHT,TICK_HEIGHT);drawVLines(g,-TICK_HEIGHT,TICK_HEIGHT);}
            if(labelsOn){
                for(int x:xGrid){drawString(g,nf.format(x),x,origin.y,4,4);}
                for(int y:yGrid){drawString(g,nf.format(y),origin.x,y,4,4);}
            }
            break;
        case AXES_BOX:
            g.draw(new Rectangle2D.Double(min.x+1,min.y,max.x-2,max.y-2));
            drawString(g,"x",max.x,max.y,-80,-10);
            drawString(g,"y",min.x,max.y,10,-80);
            if(ticksOn){
                drawHLines(g,min.x,2*TICK_HEIGHT);drawVLines(g,min.y,2*TICK_HEIGHT);
                drawHLines(g,max.x-2*TICK_HEIGHT,max.x);drawVLines(g,max.y-2*TICK_HEIGHT,max.y);
            }
            if(labelsOn){
                for(int x:xGrid){
                    drawString(g,nf.format(x),x,min.y,-2,20);
                    drawString(g,nf.format(x),x,max.y,-2,-20);
                }
                for(int y:yGrid){
                    drawString(g,nf.format(y),min.x,y,20,2);
                    drawString(g,nf.format(y),max.x,y,-20,2);
                }
            }
            break;
        case AXES_L:
            g.draw(new Line2D.Double(origin.x,origin.y,max.x,origin.y));
            g.draw(new Line2D.Double(origin.x,origin.y,origin.x,max.y));
            drawString(g,"x",max.x,origin.y,-10,15);
            drawString(g,"y",origin.x,max.y,10,-15);
            if(ticksOn){drawHLines(g,-TICK_HEIGHT,TICK_HEIGHT);drawVLines(g,-TICK_HEIGHT,TICK_HEIGHT);}
            if(labelsOn){
                for(int x:xGrid){drawString(g,nf.format(x),x,origin.y,4,4);}
                for(int y:yGrid){drawString(g,nf.format(y),origin.x,y,4,4);}
            }
            break;
        }
    }
        
    
    // EVENT HANDLING
    
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // GUI ELEMENT SUPPORT
    
    public JToolBar generateToolBar(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
