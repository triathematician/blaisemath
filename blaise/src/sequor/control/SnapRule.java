/**
 * SnapRule.java
 * Created on Mar 17, 2008
 */

package sequor.control;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JPanel;
import scio.coordinate.R2;
import sequor.VisualControl;

/**
 * Represents an object with the ability to "filter" a coordinate via a set of rules. This is intended to be used for "snapping" an object's
 * position to a particular point or set of points.
 * 
 * @author Elisha Peterson
 */
public abstract class SnapRule {
    
    // STATIC METHODS
    
    /** Returns rectangle containing snap points of a given VisualControl. */
    public static Rectangle2D.Double getSnapBox(VisualControl vc,JPanel p,double padding){
        return new Rectangle2D.Double(
                padding,
                padding,
                p.getWidth()-2*padding-vc.getWidth(),
                p.getHeight()-2*padding-vc.getHeight());
    }
    /** Tells a VisualControl to snap to a particular JPanel. */
    public static void enableSnapping(final VisualControl vc,final JPanel p,final double padding){enableSnapping(vc,p,padding,-1);}
    public static void enableSnapping(final VisualControl vc,final JPanel p,final double padding, int stickyPoint){
        final RectangleControlPoints sr=new RectangleControlPoints(getSnapBox(vc,p,padding));
        sr.setStickyPoint(stickyPoint);
        vc.setSnapRule(sr);        
        ComponentListener resnap=new ComponentListener(){
            public void componentResized(ComponentEvent e){
                sr.setControlBox(getSnapBox(vc,p,padding));
                if(sr.stickyPoint!=-1){
                    vc.setStickySnap(true);
                    vc.setLocation(vc.getX(),vc.getY());
                    vc.setStickySnap(false);
                }
            }
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}
            public void componentHidden(ComponentEvent e) {}
        };
        vc.addComponentListener(resnap);
        p.addComponentListener(resnap);
    }    
    
    
    // SnapRule CLASS ELEMENTS
    
    /** Whether to force the snap to the closest possible point. */
    protected boolean forceSnap=false;
    /** Range within which points will snap. */
    protected int snapRange=50;
    /** Whether snapping should be to a particular point. */
    protected int stickyPoint=-1;
    
    public int getStickyPoint(){return stickyPoint;}
    public int getSnapRange(){return snapRange;}
    public boolean isForceSnap(){return forceSnap;}
    
    public void setStickyPoint(int n){stickyPoint=n;}
    public void setSnapRange(int n){snapRange=n;}
    public void setForceSnap(boolean val){forceSnap=val;}
    
    /** The only required method is one which "translates" a particular input to a particular output. */
    public abstract Point snappedPoint(java.awt.Point input); 
    public abstract Point snappedPoint();
    public Point snappedPoint(int x,int y){return snappedPoint(new Point(x,y));}
    public Point snappedPoint(double x,double y){return snappedPoint(new Point((int)x,(int)y));}
    
    
    
    // INNER CLASSES
    
    /** Rule with a single point. */
    public static class SinglePoint extends SnapRule {
        java.awt.Point point;
        public SinglePoint(java.awt.Point point){this.point=point;}
        private SinglePoint(double x, double y){this((int)x,(int)y);}
        public SinglePoint(int x,int y){point=new java.awt.Point(x,y);}
        public Point getPoint() {return point;}
        public void setPoint(Point point) {this.point = point;}        
        public Point snappedPoint(){return point;}
        public Point snappedPoint(Point input) {
            if(forceSnap || point.distance(input)<snapRange){
                input.setLocation(point);
            }
            return input;
        }
    } // INNER CLASS SnapRule.SinglePoint
    
    
    /** Rule with a line. Any input point will snap to a point along the line. */
    public static class Line extends SnapRule {
        Line2D.Double line;
        public Line(Line2D.Double line){this.line=line;}
        public Line(double x1,double y1,double x2,double y2){line=new Line2D.Double(x1,y1,x2,y2);}
        public Line2D.Double getLine() {return line;}
        public void setLine(double x1,double y1,double x2,double y2){line.setLine(x1, y1, x2, y2);}
        public void setLine(Line2D.Double line) {this.line = line;}
        /** By default return the center of the line. */
        public Point snappedPoint(){return null;}
        public Point snappedPoint(Point input) {
            R2 temp=new R2(input.x,input.y).closestOnSegment(new R2(line.x1,line.y1),new R2(line.x2,line.y2));
            input.setLocation((int)temp.x,(int)temp.y);
            return input;
        }
    } // INNER CLASS SnapRule.Line
    
    
    /** Snaps using multiple rules. */
    public static class Multiple extends SnapRule {
        Vector<SnapRule> rules;
        public Multiple(){rules=new Vector<SnapRule>();}
        public Multiple(SnapRule primary){this();rules.add(primary);}
        public Vector<SnapRule> getRules() {return rules;}
        public void setRules(Vector<SnapRule> rules) {this.rules = rules;}
        public void clearRules(){rules.clear();}
        public void addRule(SnapRule rule){rules.add(rule);}
        public void addPointRule(double x,double y){addPointRule((int)x,(int)y);}
        public void addPointRule(int x,int y){rules.add(new SinglePoint(x,y));}
        public void addPointRules(Collection<Point> points){for(Point p:points){rules.add(new SinglePoint(p));}}
        public Point snappedPoint(){
            try{
                return rules.get(stickyPoint).snappedPoint();
            }catch(ArrayIndexOutOfBoundsException e){
                return null;
            }
        }
        public Point snappedPoint(Point input){
            Point temp=new Point(input.x,input.y);
            if(forceSnap){
                // snap to closest rule
                double closestDist=Double.MAX_VALUE;
                int closestIndex=0;
                for(int i=0;i<rules.size();i++){
                    temp=(rules.get(i) instanceof Line)?
                        rules.get(i).snappedPoint(input.x,input.y):
                        rules.get(i).snappedPoint();
                    if(temp.distance(input)<closestDist){
                        closestDist=temp.distance(input);
                        closestIndex=i;
                    }
                }
                return (rules.get(closestIndex) instanceof Line)?
                    rules.get(closestIndex).snappedPoint(input.x,input.y):
                    rules.get(closestIndex).snappedPoint();
            }else{
                // snap to first available rule
                for(int i=0;i<rules.size();i++){
                    rules.get(i).snappedPoint(input);
                    if(!(temp.x==input.x&&temp.y==input.y)){
                        stickyPoint=i;
                        return input;
                    }
                }
                return input;
            }
        }        
    } // INNER CLASS SnapRule.Multiple
    
    
    /** Snaps to a Cartesian grid of points specified by x,y,dx,dy,nx,ny. Similar to Multiple but uses a different algorithm
     * to snap elements so that it's not necessary to go through every point to compute the closest one. */
    public static class Grid extends SnapRule {
        double x;
        double y;
        double dx;
        double dy;
        int nx;
        int ny;
        public Grid(){this(0,0,10,10,10,10);}
        public Grid(double x,double y,double dx,double dy,int nx,int ny){setParameters(x,y,dx,dy,nx,ny);}
        public void setParameters(double x,double y,double dx,double dy,int nx,int ny){
            this.x=x;
            this.y=y;
            this.dx=dx;
            this.dy=dy;
            this.nx=nx;
            this.ny=ny;
        }
        public void setHorizontal(double x,double y,double width,int nx){setBySize(x,y,width,1,nx,0);}
        public void setVertical(double x,double y,double height,int ny){setBySize(x,y,1,height,0,ny);}
        public void setBySize(double x,double y,double width,double height,int nx,int ny){setParameters(x,y,(nx==0)?0:width/nx,(ny==0)?0:height/ny,nx,ny);}
        /** Returns closest grid element, provided it is within specified range. */
        @Override
        public Point snappedPoint(Point input) {
            int ix=(int)Math.round((input.x-x)/dx);
            int iy=(int)Math.round((input.y-y)/dy);
            ix=(ix<0)?0:(ix>nx)?nx:ix;
            iy=(iy<0)?0:(iy>ny)?ny:iy;
            ix=(dx==0)?0:ix;
            iy=(dy==0)?0:iy;
//            System.out.println("input: "+input.toString());
//            System.out.println("values (x,y,dx,dy,nx,ny)=("+x+","+y+","+dx+","+dy+","+nx+","+ny+")");
//            System.out.println("values (ix,iy)=("+ix+","+iy+")");
            Point result=new Point((int)(x+ix*dx),(int)(y+iy*dy));
            if(forceSnap || result.distance(input)<snapRange){
                input.setLocation(result);
            }
            return input;
        }
        /** Returns first grid element. */
        @Override
        public Point snappedPoint() {return null;}
    } // INNER CLASS SnapRule.Grid
    
    
    

    /** Snaps to a polar grid of points specified by x,y,dx,dy,nx,ny. Similar to Multiple but uses a different algorithm
     * to snap elements so that it's not necessary to go through every point to compute the closest one. */
    public static class PolarGrid extends SnapRule {
        /** Here, (x,y) is the center of the circle. */
        double x;
        double y;
        /** (r,theta) is the "low end" of grid values. */
        double r;
        double theta;
        double dr;
        double dtheta;        
        int nr;
        int ntheta;
        public PolarGrid(){this(100,100,50,0,20,Math.PI/6,0,11);}
        public PolarGrid(double x,double y,double r,double theta,double dr,double dtheta,int nr,int ntheta){
            setParameters(x,y,r,theta,dr,dtheta,nr,ntheta);
        }
        public void setParameters(double x,double y,double r,double theta,double dr,double dtheta,int nr,int ntheta){
            this.x=x;
            this.y=y;
            this.r=r;
            this.theta=theta;
            this.dr=dr;
            this.dtheta=dtheta;
            this.nr=nr;
            this.ntheta=ntheta;
        }
        public void setRing(double x,double y,double r,int ntheta){
            setParameters(x,y,r,0,0,2*Math.PI/(double)ntheta,0,ntheta-1);
        }
        /** Returns closest grid element, provided it is within specified range. */
        @Override
        public Point snappedPoint(Point input) {
            int ir=(int)Math.round((input.distance(x,y)-r)/dr);
            double inputTheta=Math.atan2(y-input.y,input.x-x);
            if(inputTheta<0){inputTheta+=2*Math.PI;}
            int itheta=(int)Math.round((inputTheta-theta)/dtheta);
            ir=(ir<0)?0:(ir>nr)?nr:ir;
            itheta=(itheta<0)?0:(itheta>ntheta)?ntheta:itheta;
            ir=(dr==0)?0:ir;
            itheta=(dtheta==0)?0:itheta;
//            System.out.println("input: "+input.toString());
//            System.out.println("values (x,y,r,theta,dr,dtheta,nr,ntheta)=("+x+","+y+","+r+","+theta+","+dr+","+dtheta+","+nr+","+ntheta+")");
//            System.out.println("values (ir,itheta)=("+ir+","+itheta+")");
            Point result=new Point(
                    (int)(x+(r+ir*dr)*Math.cos(theta+itheta*dtheta)),
                    (int)(y-(r+ir*dr)*Math.sin(theta+itheta*dtheta)));
//            System.out.println("result: "+result.toString());
            setForceSnap(true);
            if(forceSnap || result.distance(input)<snapRange){
                input.setLocation(result);
            }
            return input;
        }
        /** Returns first grid element. */
        @Override
        public Point snappedPoint() {return null;}
    } // INNER CLASS SnapRule.Grid
    
    /** Rule with control points of a rectangle. */
    public static class RectangleControlPoints extends Multiple{
        public RectangleControlPoints(Rectangle2D.Double rectangle){
            super(new SinglePoint(rectangle.x,rectangle.y));
            setControlBox(rectangle);
        }
        public void setControlBox(Rectangle2D.Double rectangle){
            rules.clear();
            addPointRule(rectangle.x,rectangle.y);
            addPointRule(rectangle.x+rectangle.width/2,rectangle.y);
            addPointRule(rectangle.x+rectangle.width,rectangle.y);
            addPointRule(rectangle.x+rectangle.width,rectangle.y+rectangle.height/2);
            addPointRule(rectangle.x+rectangle.width,rectangle.y+rectangle.height);
            addPointRule(rectangle.x+rectangle.width/2,rectangle.y+rectangle.height);
            addPointRule(rectangle.x,rectangle.y+rectangle.height);
            addPointRule(rectangle.x,rectangle.y+rectangle.height/2);
        }
    } // INNER CLASS SnapRule.RectangleControlPoints
}
