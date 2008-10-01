/**
 * PlaneFunctionVector2D.java
 * Created on Sep 22, 2008
 */

package specto.euclidean2;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.style.VisualStyle;
import specto.Decoration;

/**
 * This class represents a unit vector on a surface that is used to represent the directional derivative in the direction
 * of the vector.
 * @author Elisha Peterson
 */
public class PlaneFunctionVector2D extends Vector2D.Unit implements Decoration<Euclidean2,PlaneFunction2D> {
    double dirDer;
    PlaneFunction2D parent;
    
    public PlaneFunctionVector2D(PlaneFunction2D parent){
        setParent(parent);
    }    

    public void setParent(PlaneFunction2D parent) {
        if (parent!=null) {
            if (this.parent!=null) {
                this.parent.removeChangeListener(this);
            }
            this.parent=parent;
            parent.addChangeListener(this);
            fireStateChanged();
        }
    }

    public PlaneFunction2D getParent() {
        return parent;
    }
       
    @Override
    public void recompute(Euclidean2 v) {
        try {
            R2 gradient = parent.getGradientFunction().getValue(getPoint1());
            dirDer = gradient.dot(getVector());
        } catch (FunctionValueException ex) {
            System.out.println("error");
        }
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        super.paintComponent(g, v);
        g.setComposite(VisualStyle.COMPOSITE5);
        java.awt.geom.Point2D.Double winCenter = v.toWindow(getPoint2());
        g.drawString(""+dirDer,(float)winCenter.x+5,(float)winCenter.y+5);
        g.setComposite(AlphaComposite.SrcOver);
    }
}
