/*
 * PointSet2D.java
 * Created on Sep 27, 2007, 12:38:05 PM
 */

package specto.euclidean2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import specto.Animatable;
import scio.coordinate.R2;
import sequor.component.RangeTimer;
import sequor.model.StringRangeModel;
import sequor.model.PointRangeModel;
import specto.Plottable;

/**
 *
 * @author Elisha Peterson
 */
public class PointSet2D extends Plottable<Euclidean2> implements Animatable<Euclidean2>,ChangeListener{
    Vector<R2> points;
    public PointSet2D(){this(new Vector<R2>(),Color.BLACK);}
    public PointSet2D(Color c){this(new Vector<R2>(),c);}
    public PointSet2D(Vector<R2> points,Color c){
        initStyle();
        setColor(c);
        this.points=points;
    }
    
    public Vector<R2> getPath(){return points;}
    public void setPath(Vector<R2> path){points=path;}
    
    
    // DRAW METHODS

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        if(points.size()==0){return;}
        g.setStroke(strokes[style.getValue()]);
        g.draw(drawPath(v,0,points.size()));
    }
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,RangeTimer t){
        if(points.size()==0){return;}
        int curVal=t.getCurrentIntValue();
        g.setStroke(strokes[style.getValue()]);
        switch(animateStyle.getValue()){
            case ANIMATE_DRAW:
                g.draw(drawPath(v,t.getFirstIntValue(),curVal));
                break;
            case ANIMATE_DOT:
                g.fill(drawDot(v,curVal));
                break;
            case ANIMATE_TRACE:
                g.draw(drawPath(v,0,points.size()));
                g.fill(drawDot(v,curVal));
                break;
            case ANIMATE_TRAIL:
            default:
                g.draw(drawPath(v,0,curVal));
                g.draw(drawPath(v,curVal-5,curVal));
                g.fill(drawDot(v,curVal));
                break;
        }        
    }
    
    public Shape drawDot(Euclidean2 v,int pos){
        int posB=pos<0?0:(pos>=points.size()?points.size()-1:pos);
        return v.dot(points.get(posB),3.0);
    }
    
    public Path2D.Double drawPath(Euclidean2 v,int start,int end){
        Path2D.Double result=new Path2D.Double();
        int startB=start<0?0:(start>=points.size()?points.size()-1:start);
        int endB=end<0?0:(end>=points.size()?points.size()-1:end);
        result.moveTo(points.get(startB).x,points.get(startB).y);
        for(int i=startB;i<=endB;i++){result.lineTo(points.get(i).x,points.get(i).y);}
        v.transform(result);
        return result;
    }
    
    
    // STYLE SETTINGS

    public static final int REGULAR=0;
    public static final int THIN=1;
    public static final int MEDIUM=2;
    public static final int THICK=3;
    public static final int DOTTED=4;
    public static final int SKETCH=5;    

    public static final float[] dash2={1.0f,4.0f};
    public static final float[] dash3={2.0f,6.0f};
    public static final Stroke[] strokes={
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(3.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(4.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL),
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL,10.0f,dash2,0.0f),
        new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL,10.0f,dash3,0.0f)};
    
    public static final String[] styleStrings={"Regular","Thin","Medium","Thick","Dotted","Sketch"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    
    public static final int ANIMATE_DRAW=0;
    public static final int ANIMATE_DOT=1;
    public static final int ANIMATE_TRACE=2;
    public static final int ANIMATE_TRAIL=3;
    
    static final String[] animateStyleStrings={"Draw path","Moving dot","Trace path","Draw with trail"};
    public StringRangeModel animateStyle;

    @Override
    public void initStyle(){
        super.initStyle();
        animateStyle=new StringRangeModel(animateStyleStrings,ANIMATE_TRAIL,0,3);
        animateStyle.addChangeListener(this);
    }    
    @Override
    public String toString(){return "Path or Set of Points";}

    
    // EVENT HANDLING
    
    @Override
    public JMenu getOptionsMenu() {return animateStyle.appendToMenu(super.getOptionsMenu());}
    
    
    // ANIMATION ELEMENTS

    public int getAnimatingSteps() {return (points==null)?0:points.size();}


    // POINT MODELS
        
    public PointRangeModel getConstraintModel() {return new PathPointModel();}
    public Point2D getConstrainedPoint() {return new Point2D(getConstraintModel());}
  
    /** Gets closest point out of the computed path */
    public R2 getClosestPoint(double x0,double y0) {
        R2 closestPoint=new R2(Double.MAX_VALUE,Double.MAX_VALUE);
        double closestDistance=Double.MAX_VALUE;
        try{
            for(R2 pt:points){
                if(pt.distance(x0,y0)<closestDistance){
                    closestPoint=pt;
                    closestDistance=pt.distance(x0, y0);
                }
            }
        }catch(NullPointerException e){}
        return closestPoint;
    }
    
    
    // INNER CLASSES

    class PathPointModel extends PointRangeModel {
        public PathPointModel(){super.setTo(0,0);}
        @Override
        public void setTo(R2 point) {
            R2 closest = getClosestPoint(point.x,point.y);
            super.setTo(closest.x,closest.y);
        }
        @Override
        public void setTo(double x0, double y0) {
            R2 closest=getClosestPoint(x0,y0);
            super.setTo(closest.x,closest.y);
        }        
    } // class Function2D.ParametricPointModel
}
