/*
 * Grid2D.java
 * Created on Sep 14, 2007, 8:23:42 AM
 */

package specto.gridplottable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import specto.DynamicPlottable;
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
public class Grid2D extends DynamicPlottable<Euclidean2> implements ActionListener {
    
    // PROPERTIES
    
    private boolean gridOn=true;
    private boolean ticksOn=true;
    private boolean tickLabelsOn=true;
    private boolean axesOn=true;
    private boolean axisLabelsOn=true;
    
    private String xLabel="x";
    private String yLabel="y";
    
    private Vector<Double> xGrid;
    private Vector<Double> xGridWin;
    private Vector<Double> yGrid;
    private Vector<Double> yGridWin;
    // TODO think about whether to move the below to the Euclidean2 class
    
    /** Window values */
    private Point2D.Double origin;
    private Point2D.Double min;
    private Point2D.Double max;
    
    
    // CONSTANTS
       
    private static final int IDEAL_GRID_SPACE=40;
    private static final int TICK_HEIGHT=5;
    private static final DecimalFormat nf=new DecimalFormat("##0.#");
    
    
    // CONSTRUCTORS
    
    public Grid2D(){
        super();
        color.setValue(Color.getHSBColor(0.6f,0.5f,0.6f));
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public boolean isGridOn(){return gridOn;}
    public boolean isTicksOn(){return ticksOn;}
    public boolean isTickLabelsOn(){return tickLabelsOn;}
    public boolean isAxesOn(){return axesOn;}
    public boolean isAxisLabelsOn(){return axisLabelsOn;}
    public int getAxesType(){return style.getValue();}
    
    public void setGridOn(boolean newValue){if(newValue!=gridOn){gridOn=newValue;fireStateChanged();}}
    public void setTicksOn(boolean newValue){if(newValue!=ticksOn){ticksOn=newValue;fireStateChanged();}}
    public void setTickLabelsOn(boolean newValue){if(newValue!=tickLabelsOn){tickLabelsOn=newValue;fireStateChanged();}}
    public void setAxesOn(boolean newValue){if(newValue!=axesOn){axesOn=newValue;fireStateChanged();}}
    public void setAxisLabelsOn(boolean newValue){if(newValue!=axisLabelsOn){axisLabelsOn=newValue;fireStateChanged();}}
    public void setAxesType(int newValue){style.setValue(newValue);}
    
    
    // METHODS: COMPUTING THE GRID
    
    /** Sets the boundaries of the window */
    public void initMinMax(Euclidean2 v){
        origin=v.toWindow(0,0);
        min=v.getWindowMin();
        max=v.getWindowMax();
    }
    
    /** Sets the spacing of the grid using the underlying visometry */
    public void initGrid(Euclidean2 v){
        xGrid=niceRange(v.getActualMin().x,v.getActualMax().x,(double)IDEAL_GRID_SPACE*v.getDrawWidth()/v.getWindowWidth());
        xGridWin=new Vector<Double>();for(double d:xGrid){xGridWin.add(v.toWindowX(d));}
        yGrid=niceRange(v.getActualMin().y,v.getActualMax().y,(double)IDEAL_GRID_SPACE*v.getDrawHeight()/v.getWindowHeight());
        yGridWin=new Vector<Double>();for(double d:yGrid){yGridWin.add(v.toWindowY(d));}
    }
    
    /**
     * Takes in a range of values in min/max/step format and returns a "rounded" version of that range of doubles.
     * The range is in Euclidean2 coordinates (not window coordinates!)
     **/
    private Vector<Double> niceRange(double min,double max,double idealStep) {
        if(min>max){double temp=min;min=max;max=temp;}
        if(idealStep<0){idealStep=-idealStep;}
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

    // STYLES
    
    private static final Stroke BASIC_STROKE=new BasicStroke();
    private static final float[] dash1={6.0f,4.0f};
    private static final Stroke DASHED_STROKE=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash1,0.0f);
    
    // METHODS: DRAWING ALL ELEMENTS
    
    @Override
    public void recompute(){}
    
    /** Repaints the component on the given panel, using the given visometry. */
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        initMinMax(v);
        if(gridOn||ticksOn||tickLabelsOn){initGrid(v);}
        if(gridOn){
            g.setColor(color.getValue().brighter());
            g.setStroke(DASHED_STROKE);
            drawHLines(g,min.x,max.x);
            drawVLines(g,min.y,max.y);
        }
        if(axesOn){
            g.setColor(color.getValue());
            g.setStroke(BASIC_STROKE);
            paintAxes(g);
        }
    }
    
    /** Paints set of horizontal lines specified by yGrid between xMin and xMax */
    public void drawHLines(Graphics2D g,double xMin,double xMax){for(double y:yGridWin){g.draw(new Line2D.Double(xMin,y,xMax,y));}}
    /** Paints set of vertical lines specified by xGrid between yMin and yMax */
    public void drawVLines(Graphics2D g,double yMin,double yMax){for(double x:xGridWin){g.draw(new Line2D.Double(x,yMin,x,yMax));}}
    /** Paints a string at a *relative* position */
    public void drawString(Graphics2D g,String s,double x,double y,double dx,double dy){g.drawString(s,(float)(x+dx),(float)(y-dy));}
    
    /** Paints the axes, ticks, and labels on the axes. */
    public void paintAxes(Graphics2D g){
        switch(style.getValue()){
        case AXES_CROSS:
            Point2D.Double origin=new Point2D.Double(this.origin.x,this.origin.y);
            boolean xRight=false;
            boolean yTop=false;
            if(origin.x<min.x){origin.x=min.x;}else if(origin.x>max.x){origin.x=max.x;xRight=true;}
            if(origin.y>min.y){origin.y=min.y;}else if(origin.y<max.y){origin.y=max.y;yTop=true;}
            g.draw(new Line2D.Double(min.x,origin.y,max.x,origin.y));
            g.draw(new Line2D.Double(origin.x,min.y,origin.x,max.y));
            if(axisLabelsOn){
                drawString(g,xLabel,max.x,origin.y,-10,15);
                drawString(g,yLabel,origin.x,max.y,10,-15);
            }
            if(ticksOn){drawHLines(g,origin.x-TICK_HEIGHT,origin.x+TICK_HEIGHT);drawVLines(g,origin.y-TICK_HEIGHT,origin.y+TICK_HEIGHT);}
            if(tickLabelsOn){
                for(int i=0;i<xGrid.size();i++){drawString(g,nf.format(xGrid.get(i)),xGridWin.get(i),origin.y,4,yTop?-20:4);}
                for(int i=0;i<yGrid.size();i++){drawString(g,nf.format(yGrid.get(i)),origin.x,yGridWin.get(i),xRight?-20:4,4);}
            }
            break;
        case AXES_BOX:
            g.draw(new Rectangle2D.Double(0,0,max.x-1,min.y-1));
            if(axisLabelsOn){
                drawString(g,xLabel,min.x,min.y,80,5);
                drawString(g,yLabel,min.x,min.y,5,80);
            }
            if(ticksOn){
                drawHLines(g,min.x,2*TICK_HEIGHT);drawVLines(g,min.y,min.y-2*TICK_HEIGHT);
                drawHLines(g,max.x-2*TICK_HEIGHT,max.x);drawVLines(g,max.y,2*TICK_HEIGHT);
            }
            if(tickLabelsOn){
                for(int i=0;i<xGrid.size();i++){
                    drawString(g,nf.format(xGrid.get(i)),xGridWin.get(i),min.y,-2,20);
                    drawString(g,nf.format(xGrid.get(i)),xGridWin.get(i),max.y,-2,-20);
                }
                for(int i=0;i<yGrid.size();i++){
                    drawString(g,nf.format(yGrid.get(i)),min.x,yGridWin.get(i),20,2);
                    drawString(g,nf.format(yGrid.get(i)),max.x,yGridWin.get(i),-20,2);
                }
            }
            break;
        case AXES_L:
            g.draw(new Line2D.Double(this.origin.x,this.origin.y,max.x,this.origin.y));
            g.draw(new Line2D.Double(this.origin.x,this.origin.y,this.origin.x,max.y));
            if(axisLabelsOn){
                drawString(g,xLabel,max.x,this.origin.y,-10,15);
                drawString(g,yLabel,this.origin.x,max.y,10,-15);
            }
            if(ticksOn){drawHLines(g,this.origin.x,this.origin.x+TICK_HEIGHT);drawVLines(g,this.origin.y,this.origin.y-TICK_HEIGHT);}
            if(tickLabelsOn){
                for(int i=0;i<xGrid.size();i++){
                    if(xGrid.get(i)<0){continue;}
                    drawString(g,nf.format(xGrid.get(i)),xGridWin.get(i),this.origin.y,4,4);
                }
                for(int i=0;i<yGrid.size();i++){
                    if(yGrid.get(i)<0){continue;}
                    drawString(g,nf.format(yGrid.get(i)),this.origin.x,yGridWin.get(i),4,4);
                }
            }
            break;
        case AXES_T:
            g.draw(new Line2D.Double(min.x,this.origin.y,max.x,this.origin.y));
            g.draw(new Line2D.Double(this.origin.x,this.origin.y,this.origin.x,max.y));
            if(axisLabelsOn){
                drawString(g,xLabel,max.x,this.origin.y,-10,15);
                drawString(g,yLabel,this.origin.x,max.y,10,-15);
            }
            if(ticksOn){drawHLines(g,this.origin.x,this.origin.x+TICK_HEIGHT);drawVLines(g,this.origin.y,this.origin.y-TICK_HEIGHT);}
            if(tickLabelsOn){
                for(int i=0;i<xGrid.size();i++){
                    if(xGrid.get(i)<0){continue;}
                    drawString(g,nf.format(xGrid.get(i)),xGridWin.get(i),this.origin.y,4,4);
                }
                for(int i=0;i<yGrid.size();i++){
                    if(yGrid.get(i)<0){continue;}
                    drawString(g,nf.format(yGrid.get(i)),this.origin.x,yGridWin.get(i),4,4);
                }
            }
            break;
        }
    }    
    
    
    // GUI ELEMENT SUPPORT
    
    public JToolBar generateToolBar(){return null;}
    
    @Override
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Grid Options");
        result.setForeground(getColor());
        JMenuItem mi;
        
        result.add(color.getMenuItem());
        result.addSeparator();
        
        mi=new JCheckBoxMenuItem("Show Grid");          mi.setSelected(true);mi.addActionListener(this);result.add(mi);
        mi=new JCheckBoxMenuItem("Show Axes");          mi.setSelected(true);mi.addActionListener(this);result.add(mi);
        result.addSeparator();
        
        mi=new JCheckBoxMenuItem("Show Axis Labels");   mi.setSelected(true);mi.addActionListener(this);result.add(mi);
        mi=new JCheckBoxMenuItem("Show Ticks");         mi.setSelected(true);mi.addActionListener(this);result.add(mi);
        mi=new JCheckBoxMenuItem("Show Tick Labels");   mi.setSelected(true);mi.addActionListener(this);result.add(mi);
        result.addSeparator();
        
        for(JMenuItem mi2:style.getMenuItems()){result.add(mi2);}
        
        return result;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() instanceof JMenuItem){
            String s=e.getActionCommand();
            if(s.equals("Show Ticks")){ticksOn=!ticksOn;
            }else if(s.equals("Show Tick Labels")){tickLabelsOn=!tickLabelsOn;
            }else if(s.equals("Show Axes")){axesOn=!axesOn;
            }else if(s.equals("Show Axis Labels")){axisLabelsOn=!axisLabelsOn;
            }else if(s.equals("Show Grid")){gridOn=!gridOn;
            }
            fireStateChanged();
        }
    }
    
    
    // STYLE PARAMETERS
    
    public static final int AXES_CROSS=0;
    public static final int AXES_BOX=1;
    public static final int AXES_L=2;
    public static final int AXES_T=3;
    final static String[] styleStrings={"Cross-Style Axes","Box-Style Axes","L-Style Axes","Inverted T-Style Axes"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
}
