/*
 * RiemannSum2D.java
 * Created on Feb 27, 2008
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.decoration;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.component.RangeTimer;
import specto.Animatable;
import specto.Decoration;
import specto.plottable.Function2D;
import specto.visometry.Euclidean2;

/**
 * <p>
 * RiemannSum2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class RiemannSum2D extends Decoration<Euclidean2> implements Animatable,ChangeListener{

    public double leftPoint;
    public double rightPoint;
    public double numIntervals;
    
    public RiemannSum2D(Function2D parent){this(parent,-4.0,4.0,20);}
    public RiemannSum2D(Function2D parent,double leftPoint,double rightPoint,double numIntervals){
        super(parent);
        this.leftPoint=leftPoint;
        this.rightPoint=rightPoint;
        this.numIntervals=numIntervals;
        setColor(Color.RED);
    }

    Vector<R2> getSamplePoints(){
        Vector<R2> result=new Vector<R2>();
        double width=(rightPoint-leftPoint)/numIntervals;
        Function2D fnParent=(Function2D)parent;
        for(int i=0;i<numIntervals;i++){
            try {
                result.add(fnParent.getFunctionPoint(leftPoint + i * width + width / 2));
            } catch (FunctionValueException ex) {}
        }
        return result;
    }
    
    @Override
    public void paintComponent(Graphics2D g) {
        g.setColor(color);
        double width=(rightPoint-leftPoint)/numIntervals;
        for(R2 point:getSamplePoints()){
            g.fill(visometry.dot(point, 3));
            g.draw(visometry.rectangle(point.x-width/2,0.0,point.x+width/2,point.y));
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
        for(R2 point:getSamplePoints()){
            g.fill(visometry.rectangle(point.x-width/2,0.0,point.x+width/2,point.y));
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
    }
    @Override
    public void paintComponent(Graphics2D g, RangeTimer t) {
        paintComponent(g);
    }
    @Override
    public JMenu getOptionsMenu() {return null;}
    @Override
    public void recompute() {}
}
