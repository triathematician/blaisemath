package Blaise;

import Interface.BPlotPanel;
import Function.VectorField;
import Euclidean.PPath;
import Euclidean.PPoint;
import Model.PointRangeModel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

/**
 * BPlot2D.java<br>
 * Author: Elisha Peterson<br>
 * Created on February 13, 2007, 10:18 AM<br><br>
 *
 * This class implements a swing component for drawing elements on a Cartesian
 *   coordinate space. It has support for drawing a background grid and axes,
 *   and for drawing points, paths, and vectors on the background. Under the
 *   current implementation, these elements must paintComponent themselves using the
 *   specified affine transform belonging to this class.<br><br>
 *
 * The graphics elements to be drawn are stored in three vectors according
 *   to type: path, point, and phase. These are currently public variables,
 *   allowing them to be added/deleted externally.<br><br>
 *
 * The PlotBounds class helps out with drawing the grid and maintaining the
 *   correct aspect ratio. Currently the aspect ratio is required to be 1.
 *   Grid lines are scaled automatically based on the size of the window and
 *   the desired coordinate range. They may occur 1, 2, 2.5, or 5 units apart,
 *   depending on which fits the best within the window.<br><br>
 *
 * The scale is computed to ensure that prespecified coordinates are contained
 *   within the window... the window may be larger or taller.<br><br>
 *
 * Almost all the work is farmed out to subclasses responsible for coordinating
 *   the window bounds, the grid, and the axes.<br><br>
 *
 * <b>DESIRED FEATURES LIST:</b><br>
 *    1. Export options to BPlot2DOptions class
 *    2. Create bean patterns for plot class
 *    3. Allow change of aspect ratio
 *    4. Implement bounds adjustment controls
 *                           CLICK/DRAG      MOUSEWHEEL      BUTTON      CONTEXTMENU
 *       a. Pan:             normal          --              yes        `center(0,0)
 *       b. Zoom:            alt[rectangle]  normal          yes         automatic settings
 *       c. Aspect:          shift[arrow]    shift           yes         automatic settings
 *    5. Implement axis clicks for single-dimension adjustments, including single bound adjustments
 *    6. Implement "snap to integer" for view adjustments
 *    7. Implement "snap to quadrants" for four basic quadrants
 *    8. Choose better names than leftX,lowY,etc. Use x0,y0,x1,y1 or something else.
 *    9. Display coordinates at mouse cursor
 *   10. Use icons and/or small buttons for basic plot settings.
 *   11. Create plot settings panel
 *   12. Allow grid class to generate coordiante lists, both 1d and 2d.
 *   13. Write universal interface for plot components.
 *      a. Paint class
 *      b. Positioning getters/setters
 *      c. Click checking
 *      d. Ability to fire ChangeEvent's
 *      e. Animation settings
 *      f. Ability to respond to changes in geometry
 *   14. Implement color options/editing in BPlot2DOptions and corresponding panel/editor
 *   15. Place Bounds and Grid in separate class files
 *      a. Bounds: controls the range of values displayed within the window, coordinate transformations
 *      b. Grid: controls the spacing of lines, ticks, etc. within the window, and generates lists of points
 *      c. BPlot2D: swing component, draws elements, controls animation
 *   16. Have Bounds implement the BGeometry interface
 *   17. Introduce event handling into Bounds and Grid classes
 *   18. Ability to edit strings corresponding to the plot axes
 *   19. Ability to "pull" the axes off the chart
 *   20. Implement "L" axis mode as well as Box and Cross
 *   21. Write BVectorField2D class (display a vector field 2 inputs->2 outputs on the plot)
 *   22. Write BColorMap2D class (display a color map 2 inputs->1 output on the plot)
 */

public class BPlot2D extends JPanel implements BPlotPanel {
    
    /** Constants */
    private final int IDEAL_GRID_SPACE=40;
    
    /** Stores bounds elements & settings. Class definition below. */
    private Bounds bounds;
    /** Stores affine transform for Cartesian->window coords */
    private AffineTransform at;
    
    /** Stores grid elements & settings. **/
    private Grid grid;
    private Timer animator;
    private int animateTime;
    private int animateCycle=1000;
    private JCheckBox gridCheck;
    private JCheckBox axesCheck;
    private JCheckBox crossCheck;
    private JCheckBox ticksCheck;
    private JCheckBox labelsCheck;
    private JCheckBox fieldCheck;
    private JCheckBox animateCheck;
    
    /** A vector of paths stored for painting */
    private Vector<BPlotPath2D> path;
    /** A vector of points stored for painting */
    private Vector<BClickablePoint> point;
    /** Unchangeable points */
    private Vector<PPoint> fixedPoints;
    /** A vector of vectors stored for painting */
    private VectorField vectorField;
    //public Vector<PVector> phase;
    
    /** Creates a new instance, with assumed bounds. */
    public BPlot2D(){
        initComponents();
        setBackground(Color.WHITE);
        setOpaque(true);
        initUI();
    }
    /** Creates a new instance of BPlot2D with given bounds. */
    public BPlot2D(Dimension dimension,double lx,double ly,double rx,double uy) {
        initComponents();
        bounds.setBounds(lx,ly,rx,uy);
        
        setPreferredSize(dimension);
        setBackground(Color.WHITE);
        setOpaque(true);
        initUI();
    }
    /** Creates new instance around a fixed square. */
    public BPlot2D(Dimension dimension,double range){this(dimension,-range,-range,range,range);}
    
    // Getters & Setters
    public void setVectorField(VectorField vf){vectorField=vf;}
    public VectorField getVectorField(){return vectorField;}
    
    // Getters & Setters
    public void setBounds(double b){bounds.setBounds(-b,-b,b,b);}
    public double getLeftX(){return bounds.drawLeftX;}
    public void setLeftX(double leftX){bounds.setLeftX(leftX);}
    public double getLowY(){return bounds.drawLowY;}
    public void setLowY(double lowY){bounds.setLowY(lowY);}
    public double getRightX(){return bounds.drawRightX;}
    public void setRightX(double rightX){bounds.setRightX(rightX);}
    public double getUpY(){return bounds.drawUpY;}
    public void setUpY(double upY){bounds.setUpY(upY);}
    
    /** Animation parameters */
    public int getAnimateDelay(){return animator.getDelay();}
    public void setAnimateDelay(int ad){animator.setDelay(ad);}
    
    /** Sets up the interface buttons. */
    private void initUI() {
        this.addMouseWheelListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        // sets up & adds buttons
        gridCheck=new JCheckBox("Grid",grid.showGrid);
        axesCheck=new JCheckBox("Axes",grid.showAxes);
        crossCheck=new JCheckBox("Cross",grid.crossAxes);
        ticksCheck=new JCheckBox("Ticks",grid.showTicks);
        labelsCheck=new JCheckBox("Labels",grid.showLabels);
        fieldCheck=new JCheckBox("Field",grid.showField);
        animateCheck=new JCheckBox("Animate",grid.animate);
        gridCheck.addActionListener(grid);
        axesCheck.addActionListener(grid);
        crossCheck.addActionListener(grid);
        ticksCheck.addActionListener(grid);
        labelsCheck.addActionListener(grid);
        fieldCheck.addActionListener(grid);
        animateCheck.addActionListener(grid);
        gridCheck.setContentAreaFilled(false);
        axesCheck.setContentAreaFilled(false);
        crossCheck.setContentAreaFilled(false);
        ticksCheck.setContentAreaFilled(false);
        labelsCheck.setContentAreaFilled(false);
        fieldCheck.setContentAreaFilled(false);
        animateCheck.setContentAreaFilled(false);
        
        // performs layout... code generated by NetBeans graphical form
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(327, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(gridCheck)
                .addComponent(axesCheck)
                .addComponent(crossCheck)
                .addComponent(ticksCheck)
                .addComponent(labelsCheck)
                .addComponent(fieldCheck)
                .addComponent(animateCheck)))
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(201, Short.MAX_VALUE)
                .addComponent(gridCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axesCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crossCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ticksCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelsCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(animateCheck)));
    }
    
    /** Initializes components of the class. */
    public void initComponents(){
        grid=new Grid();
        bounds=new Bounds();
        at=new AffineTransform();
        path=new Vector<BPlotPath2D>();
        point=new Vector<BClickablePoint>();
        fixedPoints=new Vector<PPoint>();
        //phase=new Vector<PVector>();
        animator=new Timer(100,new ActionListener(){
            public void actionPerformed(ActionEvent e){
                for(BPlotPath2D p:path){p.animate(0,animateTime);}
                repaint();
                animateTime++;
                if(animateTime>animateCycle){animateTime=0;}
            }
        });
        animator.stop();
    }
    
    /**
     * Methods to add/delete points...
     */
    public void addPoint(PointRangeModel prmodel){
        point.add(new BClickablePoint(prmodel,this));
    }
    public void addPoint(PointRangeModel prmodel,Color color){
        point.add(new BClickablePoint(prmodel,this,color));
    }
    public void addPoints(Vector<PointRangeModel> prmodel){
        for (PointRangeModel prm:prmodel){addPoint(prm);}
    }
    public void addPoints(Vector<PointRangeModel> prmodel,Color color){
        for (PointRangeModel prm:prmodel){addPoint(prm,color);}
    }
    public void deletePoints(Vector<PointRangeModel> prmodel){
        point.removeAll(prmodel);
    }
    public void addFixedPoints(Vector<PPoint> points){fixedPoints.addAll(points);}
    public void deleteFixedPoints(){fixedPoints.removeAllElements();}
    public void deletePoints(){point.removeAllElements();}
    public void addPath(PPath path){this.path.add((BPlotPath2D)path);}
    public void addPaths(Vector<PPath> paths){
        for(PPath path:paths){this.path.add((BPlotPath2D)path);}
    }
    public void deletePaths(){path.removeAllElements();}
    
    /** Sets the animation cycle. */
    public void setAnimateCycle(int c){animateCycle=c;}
    /** Initiate animation cycle. */
    protected void turnAnimationOn(){
        animateTime=0;
        grid.animate=true;
        animator.start();
    }
    /** Terminate animation cycle. */
    protected void turnAnimationOff(){
        for(BPlotPath2D p:path){p.animateOff();}
        grid.animate=false;
        animator.stop();
    }
    
    /** Draw the panel. */
    protected void paintComponent(Graphics gb){
        super.paintComponent(gb);
        Graphics2D g=(Graphics2D)gb;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Paint background if we're opaque.
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Compute the affine transform, paintComponent grid and axes.
        bounds.computeAffineTransform(at,getWidth(),getHeight());
        grid.drawComponent(g);
        
        // Paints points, lines, etc. Uses the colors stored within these
        //   elements.
        // Also pass in the affine transform to allow them to render using the
        //   proper sizing.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        for(BPlotPath2D p:path){p.paintComponent(g,at);}
        if(!animator.isRunning()){
            for(BClickablePoint p:point){p.paintComponent(g);}
            g.setColor(Color.BLACK);
            for(PPoint p:fixedPoints){g.draw(new Ellipse2D.Double(toWindowX(p.getX())-10,toWindowY(p.getY())-10,20,20));}
        }
    }
    
    //Methods required by ChangeListener interface
    public void stateChanged(ChangeEvent e){repaint();}
    
    /** Return the Cartesian coordinates of the given window coordinates. */
    public PPoint toCartesian(int windowX,int windowY){return bounds.toCartesian(windowX,windowY);}
    public double toCartesianX(int windowX){return bounds.toCartesianX(windowX);}
    public double toCartesianY(int windowY){return bounds.toCartesianY(windowY);}
    
    /** Return the window coordinates of the given Cartesian coordiantes. */
    public PPoint toWindow(PPoint cPoint){return bounds.toWindow(cPoint);}
    public int toWindowX(double cartesianX){return bounds.toWindowX(cartesianX);}
    public int toWindowY(double cartesianY){return bounds.toWindowY(cartesianY);}
    
    /** Draws solution dots on the grid vertices. */
    public void drawPoint(PPoint point,Color c){
        BClickablePoint drawnPoint=new BClickablePoint(new PointRangeModel(point,
                bounds.getLeftX(),bounds.getLowY(),bounds.getRightX(),bounds.getUpY()),
                this,BClickablePoint.CONCENTRIC);
        drawnPoint.setColor(c);
        drawnPoint.paintComponent(getGraphics());
    }
    
// Event handling    
    
    /** Pass actions onto grid */
    public void actionPerformed(ActionEvent e){grid.actionPerformed(e);}
    /** Zoom operation */
    public void mouseWheelMoved(MouseWheelEvent e){
        Point mouseLoc = e.getPoint();
        if (mouseLoc.x < 0) mouseLoc.x = 0;
        else if (mouseLoc.x > getWidth())mouseLoc.x = getWidth();
        if (mouseLoc.y < 0) mouseLoc.y = 0;
        else if (mouseLoc.y > getHeight())mouseLoc.y=getHeight();
        bounds.zoomPoint(mouseLoc.x,mouseLoc.y,(e.getWheelRotation()>0)?1.05:0.95);
        repaint();
    }
    /** Determines which points underly a given mouse event */
    public BClickablePoint getFirstClicked(MouseEvent e){
        for(BClickablePoint p:point){if(p.clicked(e)){return p;}}
        return null;
    }
    /** Press/release moves the plot window around */
    BClickablePoint clickedPoint;
    Point pressed;
    double oldLX,oldLY,oldRX,oldUY;
    public void mousePressed(MouseEvent e){
        clickedPoint=getFirstClicked(e);
        // if null, do the window drag
        if(clickedPoint==null){
            pressed=e.getPoint();
            oldLX=bounds.leftX;oldLY=bounds.lowY;oldRX=bounds.rightX;oldUY=bounds.upY;
        }
        // if not null, let the point drag
        else{clickedPoint.mousePressed(e);addMouseListener(clickedPoint);addMouseMotionListener(clickedPoint);}
    }
    public void mouseReleased(MouseEvent e){
        if(clickedPoint==null&&pressed!=null){
            bounds.translate(oldLX,oldLY,oldRX,oldUY,pressed,e.getPoint());
            repaint();
            pressed=null;
        } else{removeMouseListener(clickedPoint);removeMouseMotionListener(clickedPoint);}
    }
    public void mouseDragged(MouseEvent e){
        if(clickedPoint==null&&pressed!=null){
            bounds.translate(oldLX,oldLY,oldRX,oldUY,pressed,e.getPoint());
            repaint();
        }
    }
    public void mouseMoved(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    
// Event generating   
    
    /** Event handling code modified from DefaultBoundedRangeModel. */
    protected EventListenerList listenerList=new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class,l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class,l);}
    protected void fireActionPerformed(String code){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ActionListener.class){
                ((ActionListener)listeners[i+1]).actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,code));
            }
        }
    }
    
// Utility classes
    
    /**
     * BPlot2D.Bounds class
     *
     * Utility class which deals with the Cartesian coordinate space of the
     *   plot window. It has support for maintaining a certain aspect ratio
     *   and for keeping a certain plot range within the window, no matter
     *   what the getPlotWidth/getPlotHeight of the external window. It also supports
     *   the computation of grid spacing and of the affine transform.
     */
    class Bounds {
        /**
         * Stores the plot range which must be contained within the display
         *   window. This range never changes internally, but only through
         *   an external call.
         */
        private double leftX,lowY,rightX,upY;
        /** Stores the actual plot range displayed **/
        private double drawLeftX,drawLowY,drawRightX,drawUpY;
        /** Stores the aspect ratio of pixels to be used in plotting. **/
        private double boundsAspectRatio;
        /** Stores the aspect ratio of the containing window. **/
        private double drawAspectRatio;
        
        /** Basic Constructor */
        Bounds(){this(-10,-10,10,10);}
        
        // Getters & Setters
        public double getDrawAspectRatio(){return drawAspectRatio;}
        public void setDrawAspectRatio(double drawAspectRatio){this.drawAspectRatio=drawAspectRatio;}
        public double getBoundsAspectRatio(){return boundsAspectRatio;}
        private void setBoundsAspectRatio(double boundsAspectRatio){this.boundsAspectRatio=boundsAspectRatio;}
        
        public double getLeftX(){return leftX;}
        public void setLeftX(double leftX){this.leftX=leftX;}
        public double getLowY(){return lowY;}
        public void setLowY(double lowY){this.lowY=lowY;}
        public double getRightX(){return rightX;}
        public void setRightX(double rightX){this.rightX=rightX;}
        public double getUpY(){return upY;}
        public void setUpY(double upY){this.upY=upY;}
        public PPoint getCenter(){return new PPoint((rightX+leftX)*.5,(upY+lowY)*.5);}
        public double getCenterX(){return (rightX+leftX)*.5;}
        public double getCenterY(){return (upY+lowY)*.5;}
        
        public double getPlotWidth(){return (rightX-leftX);}
        public double getPlotHeight(){return (upY-lowY);}
        public double getDrawWidth(){return (drawRightX-drawLeftX);}
        public double getDrawHeight(){return (drawUpY-drawLowY);}
        
        /**
         * Class constructor sets these values. Okay for rightX<leftX and upY<lowY. *
         */
        Bounds(double lx,double ly,double rx,double uy){
            setBounds(lx,ly,rx,uy);
            boundsAspectRatio=getPlotWidth()/getPlotHeight();
            drawAspectRatio=1;
            computeNewBounds();
        }
        
        /** Resets window bounds **/
        public void setBounds(double lx,double ly,double rx,double uy){
            this.leftX=lx;
            this.lowY=ly;
            this.rightX=rx;
            this.upY=uy;
        }
        /** Translates based on two integer values */
        public void translate(double lx,double ly,double rx,double uy,Point pressed,Point end){
            double dx=toCartesianX(pressed.x)-toCartesianX(end.x);
            double dy=toCartesianY(pressed.y)-toCartesianY(end.y);
            setBounds(lx+dx,ly+dy,rx+dx,uy+dy);
        }
        /** Sets based on zoom about a point (given in window units) */
        public void zoomPoint(int x,int y,double factor){
            /** effective zoom point is between current center and mouse position...
             * close to center => 100% at the given point, close to edge => 10% at the given point.
             */
            double cx=toCartesianX(x);
            double cy=toCartesianY(y);
            double weightX=.99;
            double weightY=.99;
            cx+=weightX*(getCenterX()-cx);
            cy+=weightY*(getCenterY()-cy);
            setBounds(cx-factor*getPlotWidth()/2,cy-factor*getPlotHeight()/2,cx+factor*getPlotWidth()/2,cy+factor*getPlotHeight()/2);
        }
        
        /** Return the Cartesian coordinates of the given window coordinates. */
        protected PPoint toCartesian(int windowX,int windowY){
            return new PPoint(toCartesianX(windowX),toCartesianY(windowY));
        }
        protected double toCartesianX(int windowX){
            return (windowX-at.getTranslateX())/at.getScaleX();
        }
        protected double toCartesianY(int windowY){
            return (windowY-at.getTranslateY())/at.getScaleY();
        }
        
        /** Return the window coordinates of the given Cartesian coordinates. */
        public PPoint toWindow(PPoint cPoint){
            return new PPoint((double)toWindowX(cPoint.x),(double)toWindowY(cPoint.y));
        }
        public int toWindowX(double cartesianX){
            return (int)(cartesianX*at.getScaleX()+at.getTranslateX());
        }
        public int toWindowY(double cartesianY){
            return (int)(cartesianY*at.getScaleY()+at.getTranslateY());
        }
        
        /**
         * Computes the plot range based on the current drawAspectRatio,
         *   the desired AspectRatio, and the required plot range.
         */
        private double computeNewBounds(){
            // Multiplier which determines how much the coordinates should be scaled.
            double multiplier=drawAspectRatio/boundsAspectRatio;
            
            // Need to add space in the x-direction... getPlotWidth should increase from
            //   getPlotWidth() to multiplier*getPlotWidth()
            if (multiplier>=1){
                drawLeftX=leftX-0.5*(multiplier-1)*getPlotWidth();
                drawRightX=rightX+0.5*(multiplier-1)*getPlotWidth();
                drawLowY=lowY;
                drawUpY=upY;
            }
            // Need to add space in the y-direction... getPlotHeight should increase from
            //  getPlotHeight() to getPlotHeight()/multiplier
            else if (multiplier<1){
                drawLeftX=leftX;
                drawRightX=rightX;
                drawLowY=lowY-0.5*(1/multiplier-1)*getPlotHeight();
                drawUpY=upY+0.5*(1/multiplier-1)*getPlotHeight();
            }
            return multiplier;
        }
        
        /** Computes the affine transform. **/
        public double computeAffineTransform(AffineTransform at,int windowWidth,int windowHeight){
            setDrawAspectRatio((double)windowWidth/(double)windowHeight);
            double multiplier=computeNewBounds();
            double scale;
            at.setToIdentity();
            
            // If window is too wide, set size based on y-direction...
            //   else based on x-direction
            if (multiplier>1){scale=(double)getWidth()/(drawRightX-drawLeftX);} else{scale=(double)getHeight()/(drawUpY-drawLowY);}
            at.scale(scale,-scale);
            at.translate(-drawLeftX,-drawUpY);
            
            // fire event indicating that changes have been made, and the plot can be redrawn
            fireActionPerformed("bounds");
            return scale;
        }
    }
    
    /**
     * BPlot2D.Grid class
     *
     * Handles the drawing and storage of grid elements. Uses the current
     *   AffineTransform to determine appropriate grid spcaing, and computes
     *   collections of doubles which represent where the horizontal and
     *   vertical grid elements should be drawn. Also handles the drawing
     *   of the grid on a given Graphics2D object.
     */
    private class Grid implements ActionListener {
        /** Determines whether grid is drawn. */
        protected boolean showGrid;
        /** Determines whether tick marks are drawn. */
        protected boolean showTicks;
        /** Determines whether labels are drawn. */
        protected boolean showLabels;
        /** Determines whether axes are drawn. */
        protected boolean showAxes;
        /** Determines style of axes: cross or box. */
        protected boolean crossAxes;
        /** Determines whether vector field is drawn. */
        protected boolean showField;
        /** Determines animation setting. */
        protected boolean animate;
        /** Stores the color of various elements */
        protected Color gridColor,axesColor,labelsColor,ticksColor,fieldColor;
        /** Stores the stroke style for the grid */
        protected Stroke gridStroke;
        /** Stores the coordinates of the horizontal and vertical grids. */
        protected Vector<Double> gridX,gridY;
        /** Stores the position of the axes. */
        protected double x,y;
        
        
        /** Initializes elements */
        Grid(){
            showGrid=true;
            gridColor=Color.LIGHT_GRAY;
            float[] dash1={6.0f,4.0f};
            gridStroke=new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash1,0.0f);
            gridX=new Vector<Double>();
            gridY=new Vector<Double>();
            
            showTicks=true;
            ticksColor=Color.GRAY;
            
            showLabels=true;
            labelsColor=Color.GRAY;
            
            showAxes=true;
            crossAxes=true;
            axesColor=Color.DARK_GRAY;
            x=0;y=0;
            
            showField=false;
            fieldColor=Color.GRAY;
        }
        
        /**
         * Determines grid parameters given a getPlotWidth (getPlotHeight) and a range...
         *   and returns a vector of doubles corresponding to grid elements.
         */
        private void setSpacing() {
            gridX=niceRange(bounds.drawLeftX,bounds.drawRightX,bounds.getDrawWidth()*(double)IDEAL_GRID_SPACE/(double)getWidth());
            gridY=niceRange(bounds.drawLowY,bounds.drawUpY,bounds.getDrawHeight()*(double)IDEAL_GRID_SPACE/(double)getHeight());
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
        
        /** Draws all of the grid/axis elements. */
        private void drawComponent(Graphics2D g) {
            if(showGrid||showLabels||showTicks){setSpacing();}
            if(showGrid){drawGrid(g);}
            if(showTicks){drawTicks(g);}
            if(showAxes){drawAxes(g);}
            if(showLabels){drawLabels(g);}
            if(showField){drawField(g);}
        }
        
        /** Draws the grid. */
        private void drawGrid(Graphics2D g){
            g.setColor(gridColor);
            g.setStroke(gridStroke);
            // Draws a line at every point in gridX and gridY
            for (double xValue:gridX){
                g.draw(new Line2D.Double(toWindowX(xValue),0,toWindowX(xValue),getHeight()));
            }
            for (double yValue:gridY){
                g.draw(new Line2D.Double(0.,toWindowY(yValue),getWidth(),toWindowY(yValue)));
            }
            g.setStroke(new BasicStroke());
        }
        
        /** Draws the axes. */
        private void drawAxes(Graphics2D g){
            g.setColor(axesColor);
            if(crossAxes){
                g.draw(new Line2D.Double(0,at.getTranslateY(),getWidth(),at.getTranslateY()));
                g.draw(new Line2D.Double(at.getTranslateX(),0,at.getTranslateX(),getHeight()));
                g.drawString("x",getWidth()-10,toWindowY(0)+15);
                g.drawString("y",toWindowX(0)-10,15);
            } else{
                g.setStroke(new BasicStroke(3.0f));
                g.draw(new Rectangle2D.Double(1,0,getWidth()-2,getHeight()-2));
                g.drawString("x",getWidth()-80,getHeight()-10);
                g.drawString("y",10,30);
                g.setStroke(new BasicStroke());
            }
        }
        
        /** Draws the ticks. */
        private void drawTicks(Graphics2D g){
            g.setColor(ticksColor);
            g.setStroke(new BasicStroke(2.0f));
            if(crossAxes){
                // Draws a line at every point in gridX and gridY
                for (double xValue:gridX){
                    g.draw(new Line2D.Double(toWindowX(xValue),toWindowY(0)-5,toWindowX(xValue),toWindowY(0)+5));
                }
                for (double yValue:gridY){
                    g.draw(new Line2D.Double(toWindowX(0)-5,toWindowY(yValue),toWindowX(0)+5,toWindowY(yValue)));
                }
            } else {
                // Draws a line at every point in gridX and gridY
                for (double xValue:gridX){
                    g.draw(new Line2D.Double(toWindowX(xValue),0,toWindowX(xValue),9));
                    g.draw(new Line2D.Double(toWindowX(xValue),getHeight()-8,toWindowX(xValue),getHeight()));
                }
                for (double yValue:gridY){
                    g.draw(new Line2D.Double(0,toWindowY(yValue),9,toWindowY(yValue)));
                    g.draw(new Line2D.Double(getWidth()-8,toWindowY(yValue),getWidth(),toWindowY(yValue)));
                }
            }
            g.setStroke(new BasicStroke());
        }
        
        
        /** Draws the labels. */
        private void drawLabels(Graphics2D g){
            g.setColor(labelsColor);
            DecimalFormat nf=new DecimalFormat("##0.#");
            if(crossAxes){
                for (double xValue:gridX){
                    g.drawString(nf.format(xValue),toWindowX(xValue)+4,toWindowY(0)-4);
                }
                for (double yValue:gridY){
                    g.drawString(nf.format(yValue),toWindowX(0)+4,toWindowY(yValue)-4);
                }
            } else {
                for (double xValue:gridX){
                    g.drawString(nf.format(xValue),toWindowX(xValue)-2,20);
                    g.drawString(nf.format(xValue),toWindowX(xValue)-2,getHeight()-11);
                }
                for (double yValue:gridY){
                    g.drawString(nf.format(yValue),10,toWindowY(yValue)-2);
                    g.drawString(nf.format(yValue),getWidth()-24,toWindowY(yValue)-2);
                }
            }
        }
        
        /** Draws the field at grid components. */
        private void drawField(Graphics2D g){
            g.setColor(fieldColor);
            double optimalLength=.9*(gridX.get(1)-gridX.get(0));
            PPoint drawnVector;
            for (double xValue:gridX){
                for (double yValue:gridY){
                    drawnVector=new PPoint(xValue,yValue);
                    drawnVector.setPoint(vectorField.get(drawnVector));
                    drawnVector.scale(optimalLength);
                    g.draw(new Line2D.Double(toWindowX(xValue),toWindowY(yValue),
                            toWindowX(xValue+drawnVector.x),toWindowY(yValue+drawnVector.y)));
                }
            }
        }
        
        /** Listens for button toggle */
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==gridCheck){showGrid=!showGrid;}
            if(e.getSource()==axesCheck){showAxes=!showAxes;}
            if(e.getSource()==crossCheck){crossAxes=!crossAxes;}
            if(e.getSource()==ticksCheck){showTicks=!showTicks;}
            if(e.getSource()==labelsCheck){showLabels=!showLabels;}
            if(e.getSource()==fieldCheck){showField=!showField;}
            if(e.getSource()==animateCheck){if(animate){turnAnimationOff();}else{turnAnimationOn();}}
            repaint();
        }
    }
    /* END BPlot2D.Grid class */
    
}
/* END BPlot2D class */

