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
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import specto.PlotPanel;
import specto.Visometry;
import scio.coordinate.R2;
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
    
    public void setAspectRatio(double newValue){if(newValue!=aspectRatio){aspectRatio=newValue;computeTransformation();}}
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
            zoomBox=new Rectangle2D(this,toGeometryX(pressedAt.x),toGeometryY(pressedAt.y),toGeometryX(pressedAt.x),toGeometryY(pressedAt.y));
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
            setDesiredBounds(zoomBox.getMin(),zoomBox.getMax());
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
    public void zoomCenter(double factor){zoomPoint(getActualCenter(),factor);}
        

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
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){setDesiredBounds(-10,-10,10,10);}});
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
 
    /** Returns a lineSegment in the current geometry. */
    public Shape lineSegment(R2 p1,R2 p2){
        return new java.awt.geom.Line2D.Double(toWindow(p1),toWindow(p2));
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
    /** Return a dot, whose size is independent of the geometry. */
    public Shape dot(R2 ctr,double winRad){
        return new java.awt.geom.Ellipse2D.Double(toWindowX(ctr.x)-winRad,toWindowY(ctr.y)-winRad,2*winRad,2*winRad);
    }
    /** Returns square dot with size independent of geometry. */
    public Shape squareDot(R2 ctr,double winRad){
        return new java.awt.geom.Rectangle2D.Double(toWindowX(ctr.x)-winRad,toWindowY(ctr.y)-winRad,2*winRad,2*winRad);
    }
    /** Returns path containing given list of points. */
    public Shape path(Vector<R2> points){
        java.awt.geom.Path2D.Double path=new java.awt.geom.Path2D.Double();
        path.moveTo(points.firstElement().x, points.firstElement().y);
        for(R2 p:points){path.lineTo(p.x,p.y);}
        path.transform(getAffineTransformation());
        return path;
    }
    /** Returns closed path containing given list of points. */
    public Shape closedPath(Vector<R2> points){
        java.awt.geom.Path2D.Double path=new java.awt.geom.Path2D.Double();
        path.moveTo(points.firstElement().x, points.firstElement().y);
        for(R2 p:points){path.lineTo(p.x,p.y);}
        path.lineTo(points.firstElement().x,points.firstElement().y);
        path.transform(getAffineTransformation());
        return path;
    }
    // DRAW METHODS
    
    /** Draws a bunch of solid dots. */
    public void fillDots(Graphics2D g,Vector<R2> points,double rad){
        for(R2 point:points){g.fill(dot(point,rad));}
    }
}