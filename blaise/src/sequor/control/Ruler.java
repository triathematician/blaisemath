/**
 * Ruler.java
 * Created on Mar 25, 2008
 */

package sequor.control;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.print.attribute.Size2DSyntax;

/**
 * This class generates shapes which can be used to draw rulers. The shape may contain
 * large and small ticks, of specified size, as well as an underlying line.
 * 
 * @author Elisha Peterson
 */
public class Ruler {
    /** Returns line with the ticks. */
    public static Shape getTickLine(double x1,double y1,double x2,double y2,int n,double tickSize){
        Path2D.Double result=new Path2D.Double();
        result.append(new Line2D.Double(x1,y1,x2,y2),false);
        result.append(getTicks(x1, y1, x2, y2, n, tickSize),false);
        return result;
    }
    /** Returns ticks given a line. */
    public static Shape getTicks(Line2D.Double line,int n,double tickSize){return getTicks(line.x1,line.y1,line.x2,line.y2,n,tickSize);}
    /** Returns shape consisting of n lines at specified points along the given line. */
    public static Shape getTicks(double x1,double y1,double x2,double y2,int n,double tickSize){
        Path2D.Double result=new Path2D.Double();
        // slope of the ticks, perpendicular to that of the line
        double lineAngle=Math.atan2(y2-y1,x2-x1);        
        double dx=tickSize*Math.cos(lineAngle+Math.PI/2);
        double dy=tickSize*Math.sin(lineAngle+Math.PI/2);
        double x,y;
        for(int i=0;i<=n;i++){
            x=x1+(x2-x1)*i/n;
            y=y1+(y2-y1)*i/n;
            result.moveTo(x+dx,y+dy);
            result.lineTo(x-dx,y-dy);
        }
        return result;
    }
    /** Returns set of horizontal lines at specified y-values of given length. */
    public static Line2D.Double[] getHorizontalLines(double x,Vector<Double> ys,double length){
        Line2D.Double[] result=new Line2D.Double[ys.size()];
        for(int i=0;i<ys.size();i++){result[i]=new Line2D.Double(x,ys.get(i),x+length,ys.get(i));}
        return result;
    }
    /** Returns set of vertiocal lines at specified x-values of given length. */
    public static Line2D.Double[] getVerticalLines(Vector<Double> xs,double y,double length){
        Line2D.Double[] result=new Line2D.Double[xs.size()];
        for(int i=0;i<xs.size();i++){result[i]=new Line2D.Double(xs.get(i),y,xs.get(i),y+length);}
        return result;
    }
    
    /** Draws labeled line with ticks. */
    public static void drawLabeledHorizontalLines(Graphics2D g,double x,Vector<Double> ys,double length,Vector<String> ss,Point2D offset){
        for(int i=0;i<Math.min(ys.size(),ss.size());i++){
            g.draw(new Line2D.Double(x,ys.get(i),x+length,ys.get(i)));
            g.drawString(ss.get(i),(float)(x+offset.getX()),(float)(ys.get(i)+offset.getY()));
        }
    }    
    /** Draws labeled line with ticks. */
    public static void drawLabeledVerticalLines(Graphics2D g,Vector<Double> xs,double y,double length,Vector<String> ss,Point2D offset){
        for(int i=0;i<Math.min(xs.size(),ss.size());i++){
            g.draw(new Line2D.Double(xs.get(i),y,xs.get(i),y+length));
            g.drawString(ss.get(i),(float)(xs.get(i)+offset.getX()),(float)(y+offset.getY()));
        }
    }
}
