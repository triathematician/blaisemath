/*
 * Euclidean.java
 * Created on Sep 14, 2007, 8:10:03 AM
 */

package specto.visometry;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import specto.PlotPanel;
import specto.Visometry;
import scio.coordinate.R2;
import sequor.model.DoubleRangeModel;
import specto.plottable.Rectangle2D;

/**
 * This class handles coordinate transformations betwen standard 2D Cartesian coordinates
 * and the display window. The transformation is determined by two sets of parameters: a minimum
 * and maximum coordinate, and the window dimensions.
 * <br><br>
 * This would make the translation easy, except that the aspect ratio of the underlying plot may
 * not match the aspect of the containing window. Hence, the actual window min/max coordinates are
 * computed to ensure that the desired min/max are within that containing window.
 * <br><br>
 * @author Elisha Peterson
 */
public class Euclidean2 extends Visometry<R2> {
    
    // PROPERTIES
    
    /** The transformation between Cartesian coordinates and the window */
    private AffineTransform at;
    
    /** Aspect ratio for use in the transform. */
    private double aspectRatio=1;
    
    /** Actual min,max,aspect of the containing panel. */
    private R2 actualMin,actualMax;
    private double windowAspect;
    
    /** Desired min,max of the containing panel. The plot MUST include these points!! */
    private R2 desiredMin=new R2(-10,-10);
    private R2 desiredMax=new R2(10,10);
    
    
    // CONSTRUCTORS
    
    public Euclidean2(){super();at=new AffineTransform();}
    public Euclidean2(PlotPanel p){super(p);at=new AffineTransform();}
    
    
    // INITIALIZERS    
    
    /** Sets boundaries */
    @Override
    public void setBounds(R2 newMin,R2 newMax){setDesiredBounds(newMin,newMax);}
    /** Translates current boundaries by a certain amount.*/
    public void translateBounds(R2 change){setDesiredBounds(desiredMin.plus(change),desiredMax.plus(change));}
    /** Ensures min is really min and max is really max. */
    public void updateBounds(){
        if(desiredMin.x>desiredMax.x){double temp=desiredMin.x;desiredMin.x=desiredMax.x;desiredMax.x=temp;}
        if(desiredMin.y>desiredMax.y){double temp=desiredMin.y;desiredMin.y=desiredMax.y;desiredMax.y=temp;}
    }
    /** Snaps boundaries if x or y-axis is close to edge of screen. */
    public void snapBounds(){
        /** If actualmax.x is within a few pixels of x=0, or actualmin.x is, then reset the screen boundaries so that
         * the actualmax.x (or actualmin.x) actually IS at the origin. Do the same for y. */
    }
        
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public double getAspectRatio(){return aspectRatio;}
    public R2 getActualMin(){return actualMin;}
    public R2 getActualMax(){return actualMax;}
    public R2 getActualCenter(){return new R2(.5*(actualMin.x+actualMax.x),.5*(actualMin.y+actualMax.y));}
    public R2 getDesiredMin(){return desiredMin;}
    public R2 getDesiredMax(){return desiredMax;}
    public double getWindowAspect(){return windowAspect;}
    public double getDrawWidth(){return Math.abs(actualMax.x-actualMin.x);}
    public double getDrawHeight(){return Math.abs(actualMax.y-actualMin.y);}
    public double getDrawAspect(){return getDrawWidth()/getDrawHeight();}
    public AffineTransform getAffineTransformation(){return at;}
    
    public void setAspectRatio(final double newValue){
        if(newValue!=aspectRatio){
            final double oldValue=aspectRatio;
            final DoubleRangeModel drm=new DoubleRangeModel(oldValue,oldValue,newValue,(newValue-oldValue)/200);
            Thread runner=new Thread(new Runnable(){
                public void run() {
                    while(drm.increment(false,(newValue>oldValue)?1:-1)){
                        try{Thread.sleep(1);}catch(Exception e){}
                        aspectRatio=drm.getValue();computeTransformation();                             
                    }
                }            
            });
            runner.start();
        }
    }
    public void setDesiredMin(R2 newValue){setDesiredBounds(newValue,desiredMax);}
    public void setDesiredMax(R2 newValue){setDesiredBounds(desiredMin,newValue);}
    public void setDesiredBounds(R2 newMin,R2 newMax){
        boolean recompute=false;
        if(!newMin.equals(desiredMin)){
            desiredMin=newMin;
            recompute=true;
        }
        if(!newMax.equals(desiredMax)){
            desiredMax=newMax;
            recompute=true;
        }
        if(recompute){updateBounds();computeTransformation();}
    }
    public void setDesiredBounds(double minx,double miny,double maxx,double maxy){setDesiredBounds(new R2(minx,miny),new R2(maxx,maxy));}
    public void setWindowAspect(double newValue){if(newValue!=windowAspect){windowAspect=newValue;computeTransformation();}}
    
    
    // COMMANDS GENERATING RANGES OF VALUE WITHIN THE WINDOW
    
    /** Returns a range of x values, about one every two pixels.
     * @return vector of doubles containing x values within the range
     */
    public Vector<Double> getXRange(){
        Vector<Double> result=new Vector<Double>();
        double step=2*getDrawWidth()/(double)getWindowWidth();
        for(double d=actualMin.x;d<actualMax.x;d+=step){result.add(d);}
        return result;
    }
    
    /** Returns a range of y values, about one every two pixels.
     * @return vector of doubles containing x values within the range
     */
    public Vector<Double> getYRange(){
        Vector<Double> result=new Vector<Double>();
        double step=2*getDrawHeight()/(double)getWindowHeight();
        for(double d=actualMin.x;d<actualMax.x;d+=step){result.add(d);}
        return result;
    }
    
    /** Returns a range of x values, according to the given parameter.
     * @param factor    Inverse of number of samples per pixel.
     * @return          Vector of doubles containing x values within the range
     */
    public Vector<Double> getSparseXRange(double factor){
        Vector<Double> result=new Vector<Double>();
        double step=factor*getDrawWidth()/(double)getWindowWidth();
        for(double d=actualMin.x;d<actualMax.x;d+=step){result.add(d);}
        return result;
    }

    /** Returns a range of y values, according to the given parameter.
     * @param factor    Inverse of number of samples per pixel.
     * @return          Vector of doubles containing y values within the range
     */
    public Vector<Double> getSparseYRange(double factor){
        Vector<Double> result=new Vector<Double>();
        double step=factor*getDrawHeight()/(double)getWindowHeight();
        for(double d=actualMin.y;d<actualMax.y;d+=step){result.add(d);}
        return result;
    }
    
    /** Returns nice (rounds to nearest coordinates) range of x values.
     * @param factor    Determines number of samples per pixel.
     * @return          Vector of doubles containing the parameters in range.
     */
    public Vector<Double> getNiceXRange(double factor){
        // TODO implement this, based on the method in Grid2D
        return null;
    }
    
    /** Returns nice (rounds to nearest coordinates) range of y values.
     * @param factor    Determines number of samples per pixel.
     * @return          Vector of doubles containing the parameters in range.
     */
    public Vector<Double> getNiceYRange(double factor){
        // TODO implement this, based on the method in Grid2D
        return null;
    }
    
    
    // CONVERT GEOMETRY COORDINATES TO WINDOW COORDINATES
    
    public double toWindowX(double vx){return vx*at.getScaleX()+at.getTranslateX();}
    public double toWindowY(double vy){return vy*at.getScaleY()+at.getTranslateY();}
    public Point2D.Double toWindow(double vx,double vy){return new Point2D.Double(toWindowX(vx),toWindowY(vy));}
    public Point2D.Double toWindow(R2 vp){return new Point2D.Double(toWindowX(vp.x),toWindowY(vp.y));}
    
    // CONVERT WINDOW COORDINATES TO GEOMETRY COORDINATES
    
    public double toGeometryX(double wx){return (wx-at.getTranslateX())/at.getScaleX();}
    public double toGeometryY(double wy){return (wy-at.getTranslateY())/at.getScaleY();}
    public R2 toGeometry(int wx,int wy){return new R2(toGeometryX(wx),toGeometryY(wy));}
    public R2 toGeometry(Point wp){return new R2(toGeometryX(wp.x),toGeometryY(wp.y));}
    

    // METHODS TO HANDLE THE TRANSFORMATION COMPUTATION
    /** Computes the affine transform of the underlying window. */
    public double computeTransformation(){
        int windowWidth=container.getWidth();
        int windowHeight=container.getHeight();
        windowAspect=(double)windowWidth/(double)windowHeight;
        
        // Compute multiplier to determine how much the coordinates should be scaled.
        // This also computes the *actual* min and max points of the plot window.
        actualMin=(R2)desiredMin.clone();
        actualMax=(R2)desiredMax.clone();
        double multiplier=windowAspect/getDrawAspect()/aspectRatio;
        
        // Need to add space in the x-direction... getPlotWidth should increase
        if (multiplier>=1){
            double shift=0.5*(multiplier-1)*getDrawWidth();
            actualMin.x-=shift;
            actualMax.x+=shift;
            multiplier=(double)windowWidth/getDrawWidth()/aspectRatio;
        }
        // Need to add space in the y-direction... getPlotHeight should increase
        else{
            double shift=0.5*(1/multiplier-1)*getDrawHeight();
            actualMin.y-=shift;
            actualMax.y+=shift;
            multiplier=(double)windowHeight/getDrawHeight();
        }        
        //System.out.println("actual min "+actualMin.toString());
        //System.out.println("actual max "+actualMax.toString());
        
        at.setToIdentity();        
        at.scale(multiplier*aspectRatio,-multiplier);
        at.translate(-actualMin.x,-actualMax.y);
        fireStateChanged();
        return multiplier;
    }
    
    
    // EVENT HANDLING
    
    /** Press/release moves the plot window around */
    Point pressedAt=null;
    R2 oldMin=null;
    R2 oldMax=null;
    String mode=null;
    Rectangle2D zoomBox=null;
    
    @Override
    public void mousePressed(MouseEvent e){
        pressedAt=e.getPoint();
        mode=e.getModifiersExText(e.getModifiersEx());
        if(mode.equals("Alt+Button1")){
            zoomBox=new Rectangle2D(toGeometry(pressedAt),toGeometry(pressedAt));
            container.add(zoomBox);
        }else {
            oldMin=new R2(desiredMin);
            oldMax=new R2(desiredMax);
        }
    }
    @Override
    public void mouseReleased(MouseEvent e){
        mouseDragged(e);
        if(pressedAt!=null&&mode.equals("Alt+Button1")){
            container.remove(zoomBox);
            zoomBoxAnimated(zoomBox);
            zoomBox=null;
        }
        pressedAt=null;
        oldMin=null;
        oldMax=null;
        mode=null;
    }
    @Override
    public void mouseDragged(MouseEvent e){
        if(pressedAt!=null){
            if(mode.equals("Alt+Button1")){  
                zoomBox.setMax(toGeometry(e.getPoint()));
                container.repaint();
            }else{
                R2 delta=toGeometry(pressedAt).minus(toGeometry(e.getPoint()));
                setBounds(oldMin.plus(delta),oldMax.plus(delta));
            }
        }
    }
    /** Zoom operation */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        Point mouseLoc=e.getPoint();
        if(mouseLoc.x<0){mouseLoc.x=0;} else if (mouseLoc.x>getWindowWidth()) mouseLoc.x=getWindowWidth();
        if(mouseLoc.y<0){mouseLoc.y=0;} else if (mouseLoc.y>getWindowHeight()) mouseLoc.y=getWindowHeight();
        zoomPoint(toGeometry(mouseLoc),(e.getWheelRotation()>0)?1.05:0.95);
    }
    
    /** Sets based on zoom about a point (given in window units) */
    public void zoomPoint(R2 p,double factor){
        /** effective zoom point is between current center and mouse position...
        * close to center => 100% at the given point, close to edge => 10% at the given point. */
        double cx=.1*p.x+.9*getActualCenter().x;
        double cy=.1*p.y+.9*getActualCenter().y;
        setDesiredBounds(new R2(cx-factor*getDrawWidth()/2,cy-factor*getDrawHeight()/2),new R2(cx+factor*getDrawWidth()/2,cy+factor*getDrawHeight()/2));
    }
    public void zoomCenter(double factor){zoomPointAnimated(getActualCenter(),factor);}
    /** Creates an animating zoom using a particular timer. */
    public void zoomPointAnimated(R2 p,final double factor){
        final double cx=.1*p.x+.9*getActualCenter().x;
        final double cy=.1*p.y+.9*getActualCenter().y;
        final double xMultiplier=getDrawWidth()/2;
        final double yMultiplier=getDrawHeight()/2;
        final DoubleRangeModel drm=new DoubleRangeModel();
        drm.setRangeProperties(1.0,1.0,factor,(factor-1.0)/100);
        Thread runner=new Thread(new Runnable(){
            public void run() {
                while(drm.increment(false,(factor>1)?1:-1)){
                    try{Thread.sleep(1);}catch(Exception e){}
                    setDesiredBounds(new R2(cx-drm.getValue()*xMultiplier,cy-drm.getValue()*yMultiplier),
                        new R2(cx+drm.getValue()*xMultiplier,cy+drm.getValue()*yMultiplier));                                         
                }
            }            
        });
        runner.start();
    }
    /** Zooms to the boundaries of a particular box. */
    public void zoomBoxAnimated(final Rectangle2D boundary){
        final R2 min1=getActualMin();
        final R2 max1=getActualMax();
        final DoubleRangeModel drm=new DoubleRangeModel(0.0,0.0,1.0,.01);
        Thread runner=new Thread(new Runnable(){
            public void run() {
                while(drm.increment(false)){
                    try{Thread.sleep(1);}catch(Exception e){}
                    setDesiredBounds(
                            min1.x+(boundary.getMin().x-min1.x)*drm.getValue(),
                            min1.y+(boundary.getMin().y-min1.y)*drm.getValue(),
                            max1.x+(boundary.getMax().x-max1.x)*drm.getValue(),
                            max1.y+(boundary.getMax().y-max1.y)*drm.getValue());
                }
            }            
        });
        runner.start();
    }
        

    @Override
    public Vector<JMenuItem> getMenuItems() {
        Vector<JMenuItem> result=new Vector<JMenuItem>();
        JMenuItem mi;
        JMenu sub;
        sub=new JMenu("Zoom Settings");
        mi=new JMenuItem("Zoom In (2x)");        
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){zoomCenter(.5);}});
        sub.add(mi);
        mi=new JMenuItem("Zoom Out (2x)");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){zoomCenter(2);}});
        sub.add(mi);
        mi=new JMenuItem("Default Zoom");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){zoomBoxAnimated(new Rectangle2D(-10,-10,10,10));}});
        sub.add(mi);
        result.add(sub);
        
        sub=new JMenu("Aspect Ratio");
        ButtonGroup rbg=new ButtonGroup();
        mi=new JRadioButtonMenuItem("10:1");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){setAspectRatio(10);}});
        sub.add(mi);rbg.add(mi);
        mi=new JRadioButtonMenuItem("2:1");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){setAspectRatio(2);}});
        sub.add(mi);rbg.add(mi);
        mi=new JRadioButtonMenuItem("1:1");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){setAspectRatio(1);}});
        sub.add(mi);rbg.add(mi);mi.setSelected(true);
        mi=new JRadioButtonMenuItem("1:2");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){setAspectRatio(.5);}});
        sub.add(mi);rbg.add(mi);
        mi=new JRadioButtonMenuItem("1:10");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){setAspectRatio(.1);}});
        sub.add(mi);rbg.add(mi);
        mi=new JRadioButtonMenuItem("Custom");
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){}});
        sub.add(mi);rbg.add(mi);
        result.add(sub);
        
        sub=new JMenu("Centering");
        mi=new JMenuItem("Origin");              
        mi.addActionListener(this);
        sub.add(mi);
        mi=new JMenuItem("First Quadrant");      
        mi.addActionListener(this);
        sub.add(mi);
        mi=new JMenuItem("Upper Half Plane");    
        mi.addActionListener(this);
        sub.add(mi);
        result.add(sub);
        return result;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() instanceof JMenuItem){
            String s=e.getActionCommand();
            if(s.equals("Origin")){setDesiredBounds(desiredMin.minus(getActualCenter()),desiredMax.minus(getActualCenter()));
            }else if(s.equals("First Quadrant")){setDesiredMin(new R2(0,0));
            }else if(s.equals("Upper Half Plane")){setDesiredMin(new R2(desiredMin.x,0));
            }
        }
    }
    
    
    // SHAPE FACTORY METHODS
    
    /** Says whether a given point is contained within the visometry window. */
    public boolean contains(R2 point){
        return (point.x>=actualMin.x)&&(point.x<=actualMax.x)&&(point.y>=actualMin.y)&&(point.y<=actualMax.y);
    }
    
    /** Returns points at which the ray beginning at p1 and passing through p2 intersects the boundary of the window. */
    public R2 rayHit(R2 p1,R2 p2){
        if(p2.x>p1.x && p1.x<=actualMax.x){ // line goes to the right
            double slope=(p2.y-p1.y)/(p2.x-p1.x);
            double yRight=slope*(actualMax.x-p1.x)+p1.y;
            if(yRight<=actualMax.y&&yRight>=actualMin.y){ // point is on the right
                return new R2(actualMax.x,yRight);
            }else if(p2.y>p1.y && p1.y<=actualMax.y){ // line goes up
                return new R2((actualMax.y-p1.y)/slope+p1.x,actualMax.y);                
            }else if(p1.y>=actualMin.y){ // line goes down
                return new R2((actualMin.y-p1.y)/slope+p1.x,actualMin.y);                
            }
        }else if(p2.x<p1.x && p1.x>=actualMin.x){ // line goes to the left
            double slope=(p2.y-p1.y)/(p2.x-p1.x);
            double yLeft=slope*(actualMin.x-p1.x)+p1.y;
            if(yLeft<=actualMax.y&&yLeft>=actualMin.y){ // point is on the right
                return new R2(actualMin.x,yLeft);
            }else if(p2.y>p1.y && p1.y<=actualMax.y){ // line goes up
                return new R2((actualMax.y-p1.y)/slope+p1.x,actualMax.y);                
            }else if(p1.y>=actualMin.y){ // line goes down
                return new R2((actualMin.y-p1.y)/slope+p1.x,actualMin.y);                
            }
        }else if(p1.x==p2.x){ // line is vertical
            if(p2.y<p1.y && p1.y>=actualMin.y){ // line goes up
                return new R2(p1.x,actualMin.y);
            }else if(p1.y<=actualMax.y){
                return new R2(p1.x,actualMax.y);
            }
        }
        return null;
    }
    
    /** Returns a lineSegment in the current geometry. */
    public Shape lineSegment(R2 p1,R2 p2){
        return new java.awt.geom.Line2D.Double(toWindow(p1),toWindow(p2));
    }
    public Shape lineSegment(double x1,double y1,double x2,double y2){return lineSegment(new R2(x1,y1),new R2(x2,y2));}
    /** Returns the line that passes through the two points. */
    public Shape line(R2 p1,R2 p2){
        if(p1.x==p2.x){
            return lineSegment(p1.x,getActualMin().y,p2.x,getActualMax().y);
        }
        double slope=(p2.y-p1.y)/(p2.x-p1.x);
        return lineSegment(getActualMin().x,slope*(getActualMin().x-p1.x)+p1.y,getActualMax().x,slope*(getActualMax().x-p1.x)+p1.y);
    }
    /** Returns the ray that points from the first point through the second point. */
    public Shape ray(R2 p1,R2 p2,double arrowSize){        
        return arrow(p1,rayHit(p1,p2),arrowSize);
    }
    /** Returns arrow shape... the angles of the arrow must be done with respect to window geometry.
     * @param start the starting point for the arrow
     * @param end the ending point for the arrow
     * @param arrowSize multiplier for the arrow's size; if negative, it represents a multiple of the arrow's length; if positive, size in pixels
     * @return Path2D.Double containing the arrow
     */
    public Shape arrow(R2 start,R2 end,double arrowSize){
        if (start==null||end==null){return new Path2D.Double();}
        Point2D.Double winStart=toWindow(start);
        Point2D.Double winEnd=toWindow(end);
        double arrLength=arrowSize<=0?(-winStart.distance(winEnd)*arrowSize/5.0):(arrowSize);
        double winAngle=Math.atan2(winEnd.y-winStart.y,winEnd.x-winStart.x);
        double angle=Math.PI/6;
        Point2D.Double arrowEnd1=getPointOffsetAngle(winEnd, Math.PI+winAngle+angle, arrLength);
        Point2D.Double arrowEnd2=getPointOffsetAngle(winEnd, Math.PI+winAngle-angle, arrLength);
        Path2D.Double result=new Path2D.Double();
        result.moveTo(winStart.x,winStart.y);
        result.lineTo(winEnd.x,winEnd.y);
        result.lineTo(arrowEnd1.x,arrowEnd1.y);
        result.lineTo(arrowEnd2.x,arrowEnd2.y);
        result.lineTo(winEnd.x,winEnd.y);
        return result;
    }
    
    /** Returns a rectangle in the current geometry */
    public Shape rectangle(double x1,double y1,double x2,double y2){
        double wx1=toWindowX(x1);
        double wx2=toWindowX(x2);
        double wy1=toWindowY(y1);
        double wy2=toWindowY(y2);
        double x=(wx1<wx2?wx1:wx2);
        double y=(wy1<wy2?wy1:wy2);        
        return new java.awt.geom.Rectangle2D.Double(x,y,Math.abs(wx1-wx2),Math.abs(wy1-wy2));
    }
    /** Returns a rectangle in the current geometry */
    public Shape rectangle(R2 p1,R2 p2){return rectangle(p1.x,p1.y,p2.x,p2.y);}
    
    /** Returns a rectangle in the current geometry */
    public Shape trapezoid(double x1,double y1,double x2,double y2,double x3,double y3,double x4,double y4){   
        java.awt.geom.Path2D.Double path=new java.awt.geom.Path2D.Double();
        path.moveTo(x1,y1);
        path.lineTo(x2,y2);
        path.lineTo(x3,y3);
        path.lineTo(x4,y4);
        path.lineTo(x1,y1);
        path.transform(getAffineTransformation());
        return path;
    }
    
    /** Returns an ellipse in the current geometry */
    public Shape ellipse(double x1,double y1,double x2,double y2){
        double wx1=toWindowX(x1);
        double wx2=toWindowX(x2);
        double wy1=toWindowY(y1);
        double wy2=toWindowY(y2);
        double x=(wx1<wx2?wx1:wx2);
        double y=(wy1<wy2?wy1:wy2);        
        return new java.awt.geom.Ellipse2D.Double(x,y,Math.abs(wx1-wx2),Math.abs(wy1-wy2));      
    }    
    /** Returns an ellipse in the current geometry */
    public Shape ellipse(R2 p1,R2 p2){return ellipse(p1.x,p1.y,p2.x,p2.y);}
    /** Returns an ellipse in the current geometry */
    public Shape ellipse(R2 ctr,double wid,double height){return ellipse(new R2(ctr.x-wid,ctr.y-height),new R2(ctr.x+wid,ctr.y+height));
    }    
    /** Returns a circle in the current geometry. */
    public Shape circle(R2 ctr,double rad){return ellipse(ctr,rad,rad);}
    /** Returns path containing given list of points. */
    public Shape path(Vector<R2> points){
        java.awt.geom.Path2D.Double path=new java.awt.geom.Path2D.Double(java.awt.geom.Path2D.Double.WIND_NON_ZERO,points.size()+1);
        path.moveTo(points.firstElement().x, points.firstElement().y);
        for(R2 p:points){path.lineTo(p.x,p.y);}
        path.transform(getAffineTransformation());
        return path;
    }
    /** Returns closed path containing given list of points. */
    public Shape closedPath(Vector<R2> points){
        java.awt.geom.Path2D.Double path=new java.awt.geom.Path2D.Double(java.awt.geom.Path2D.Double.WIND_NON_ZERO,points.size()+1);
        path.moveTo(points.firstElement().x, points.firstElement().y);
        for(R2 p:points){path.lineTo(p.x,p.y);}
        path.closePath();
        path.transform(getAffineTransformation());
        return path;
    }
    
    
    
    
    // ABSOLUTE DISTANCE METHODS
    // REFER TO THE ABSOLUTE COORDINATES OF THE WINDOW
    
    /** Returns window coordinate point at an offset of given distance and angle from a particular one. */
    public java.awt.geom.Point2D.Double getPointOffsetAngle(java.awt.geom.Point2D.Double point,double angle,double distance){
        return new java.awt.geom.Point2D.Double(point.x+distance*Math.cos(angle),point.y+distance*Math.sin(angle));
    }
    
    /** Return a dot, whose size is independent of the geometry. */
    public Shape dot(R2 ctr,double winRad){
        return new java.awt.geom.Ellipse2D.Double(toWindowX(ctr.x)-winRad,toWindowY(ctr.y)-winRad,2*winRad,2*winRad);
    }
    /** Returns square dot with size independent of geometry. */
    public Shape squareDot(R2 ctr,double winRad){
        return new java.awt.geom.Rectangle2D.Double(toWindowX(ctr.x)-winRad,toWindowY(ctr.y)-winRad,2*winRad,2*winRad);
    }
    /** Returns arrow at given angle and given length in window geometry. */
    public Shape winArrow(R2 start,double angle,double length,double winArrowSize){
        Point2D.Double winStart=toWindow(start);
        Point2D.Double winEnd=getPointOffsetAngle(winStart,angle,length);
        double tipAngle=Math.PI/6;
        Point2D.Double arrowEnd1=getPointOffsetAngle(winEnd, Math.PI+angle+tipAngle, winArrowSize);
        Point2D.Double arrowEnd2=getPointOffsetAngle(winEnd, Math.PI+angle-tipAngle, winArrowSize);
        Path2D.Double result=new Path2D.Double();
        result.moveTo(winStart.x,winStart.y);
        result.lineTo(winEnd.x,winEnd.y);
        result.lineTo(arrowEnd1.x,arrowEnd1.y);
        result.lineTo(arrowEnd2.x,arrowEnd2.y);
        result.lineTo(winEnd.x,winEnd.y);
        return result;
    }
    /** Returns line at given angle and between given lengths in window geometry. */
    public Shape winLineAtRadius(R2 start,double angle,double length1,double length2){        
        return new java.awt.geom.Line2D.Double(getPointOffsetAngle(toWindow(start),angle,length1),getPointOffsetAngle(toWindow(start),angle,length2));
    }
    
    // DRAW METHODS
    
    /** Draws a bunch of solid dots. */
    public void fillDots(Graphics2D g,Vector<R2> points,double rad){
        for(R2 point:points){g.fill(dot(point,rad));}
    }
}