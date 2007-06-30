package Euclidean;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.Vector;

/**
 * PPath.java<br>
 * Author: Elisha Peterson<br>
 * Created circa February 5, 2007<br><br>
 * 
 * This class represents an ordered collection of points, and supports drawing
 *   such points using a Path2D.Double object on a Graphics2D object.<br><br>
 * 
 * TODO Create booleans for drawing as points and paths.
 */

public class PPath extends Vector<PPoint> implements Serializable{

    // Default constructor
    public PPath(){super();}
    
    // More constructors
    public PPath(double x0,double y0){super();add(new PPoint(x0,y0));}
    public PPath(PPoint point){super();add(point);}
    
    /** Converts the path to a Path2D.Double type, which is convenient for drawing. */
    public Path2D.Double getPath2D(){
        return getPath2D(0,size());
    }
    
    /** Converts path to a Path2D.Double type, using the specified subset range. */
    public Path2D.Double getPath2D(int start,int end){
        if(start<0){start=0;}else if(start>=size()){start=size()-1;}
        if(end<0){end=0;}else if(end>=size()){end=size()-1;}
        Path2D.Double path=new Path2D.Double();
        path.moveTo(get(start).x,get(start).y);
        for(int i=start+1;i<end;i++){path.lineTo(get(i).x,get(i).y);}
        return path;
    }
}