/**
 * RandomPoint2D.java
 * Created on Mar 14, 2008
 */

package specto.dynamicplottable;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.coordinate.R2;
import scio.random.PRandom;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
import specto.visometry.Euclidean2;

/**
 * Displays "n" random points within specified range of the center.
 * @author Elisha Peterson
 */
public class RandomPoint2D extends Point2D{
    Vector<R2> points;
    DoubleRangeModel parameter;
    IntegerRangeModel numPoints;

    public RandomPoint2D(){this(new PointRangeModel());}
    public RandomPoint2D(PointRangeModel prm){
        super(prm);
        points=new Vector<R2>();
        numPoints=new IntegerRangeModel(1000,0,5000,1);
        parameter=new DoubleRangeModel(1,0,10,.1);
        style.setValue(TYPE_NCIRCLE);
    }
    public RandomPoint2D(Point2D parent) {this(parent.prm);}

    
    // MODEL QUERY
    public DoubleRangeModel getParameterModel(){return parameter;}
    public IntegerRangeModel getNumPointsModel(){return numPoints;}
    
    // PAINTING
    
    @Override
    public void recompute() {
        points.clear();
        double x=getPoint().getX();
        double y=getPoint().getY();
        double size=parameter.getValue();
        switch(style.getValue()){
            case TYPE_UCIRCLE:
                for(int i=0;i<numPoints.getValue();i++){
                    points.add(PRandom.disk(getPoint(),size));
                }
                break;
            case TYPE_NCIRCLE:
                for(int i=0;i<numPoints.getValue();i++){
                    points.add(PRandom.diskNormal(getPoint(),size));
                }
                break;
            case TYPE_URECTANGLE:
                for(int i=0;i<numPoints.getValue();i++){
                    points.add(PRandom.rectangle(x-size,y-size,x+size,y+size));
                }
                break;
            case TYPE_NRECTANGLE:
            default:
                for(int i=0;i<numPoints.getValue();i++){
                    points.add(new R2(PRandom.normal(x,size),PRandom.normal(y,size)));
                }
                break;
        }
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        g.setColor(getColor());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.3f));
        for(R2 p:points){
            g.fill(v.dot(p,1));
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        super.paintComponent(g,v);
    }
    
    public static final int TYPE_UCIRCLE=0;
    public static final int TYPE_NCIRCLE=1;
    public static final int TYPE_URECTANGLE=2;
    public static final int TYPE_NRECTANGLE=3;
    public static String[] typeStrings={"Uniform Circle","Normal Circle","Uniform Rectangle","Normal Rectangle"};

    @Override
    public String[] getStyleStrings() {return typeStrings;}
    @Override
    public String toString() {return "Random Point";}    
}
