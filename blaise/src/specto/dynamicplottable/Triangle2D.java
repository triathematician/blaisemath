/*
 * Triangle2D.java
 * Created on Mar 6, 2008
 */

package specto.dynamicplottable;

import sequor.model.PointRangeModel;

/**
 * <p>
 * Triangle2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class Triangle2D extends DynamicPointSet2D {
    PointRangeModel p1,p2,p3;
    public Triangle2D(){this(1,0,Math.cos(2*Math.PI/3),Math.sin(2*Math.PI/3),Math.cos(4*Math.PI/3),Math.sin(4*Math.PI/3));}
    public Triangle2D(double x1,double y1,double x2,double y2,double x3,double y3){
        this(new PointRangeModel(x1,y1),new PointRangeModel(x2,y2),new PointRangeModel(x3,y3));
        style.setValue(STYLE_CYCLIC);
    }
    public Triangle2D(PointRangeModel p1,PointRangeModel p2,PointRangeModel p3){
        this.p1=p1;
        this.p2=p2;
        this.p3=p3;
        add(new Point2D(p1));
        add(new Point2D(p2));
        add(new Point2D(p3));
    }
    public double getSegment1(){return p2.getPoint().distance(p3.getPoint());}
    public double getSegment2(){return p1.getPoint().distance(p3.getPoint());}
    public double getSegment3(){return p1.getPoint().distance(p2.getPoint());}
    public double getAngle1(){return cosFormula(getSegment2(),getSegment3(),getSegment1());}
    public double getAngle2(){return cosFormula(getSegment3(),getSegment1(),getSegment2());}
    public double getAngle3(){return cosFormula(getSegment1(),getSegment2(),getSegment3());}
    
    public double cosFormula(double a,double b,double c){
        return Math.acos((a*a+b*b-c*c)/(2*a*b));
    }     
    
    // STYLE ELEMENTS    

    @Override
    public String toString(){return "Triangle";}
}
