/*
 * ArcGroupZ2D.java
 * Created on May 3, 2008
 */

package specto.integer2;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import scio.coordinate.Z2;
import sequor.event.MouseVisometryEvent;
import sequor.event.MouseVisometryListener;
import specto.Plottable;
import specto.PlottableGroup;

/**
 * <p>
 * ArcGroupZ2D represents a collection of arcs.
 * </p>
 * @author Elisha Peterson
 */
public class ArcGroupZ2D extends PlottableGroup<Integer2> {

    public ArcGroupZ2D(){
        super();
    } 
    
    @Override
    public void add(Plottable<Integer2> p){
        if (p instanceof ArcZ2D){
            super.add(p);
        }
    }
    
    
    // PAINT METHODS

    @Override
    public void paintComponent(Graphics2D g, Integer2 v) {
        if(adder != null) {
            adder.setColor(getColor());
            adder.paintComponent(g, v);        
        }
        super.paintComponent(g, v);
    }
    
    @Override
    public String toString() { return "ArcGroup"; }
    
    
    // EVENT HANDLING
    
    ArcZ2D adder = null;

    @Override
    public boolean clicked(MouseVisometryEvent<Integer2> e) {
        if(super.clicked(e)) {
            return true;
        }
        return ((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) == InputEvent.SHIFT_DOWN_MASK);
    }    

    String mode=null;
    
    @Override
    public void mousePressed(MouseVisometryEvent<Integer2> e) {
        Vector<MouseVisometryListener<Integer2>> hits=getHits(e);
        if(hits.isEmpty()){    
            if((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) == InputEvent.SHIFT_DOWN_MASK) {
                Z2 point = (Z2) e.getCoordinate();
                mover = null;
                adder = new ArcZ2D(point.x,point.y,45,point.x,point.y,-45);
                fireStateChanged();
            }
        }
        else{
            mover = hits.get(0);
            mover.mousePressed(e);
        }
    }

    @Override
    public void mouseDragged(MouseVisometryEvent<Integer2> e) {
        if(adder != null){
            Z2 point = (Z2) e.getCoordinate();
            adder.setStop(point.x,point.y,-45);
            fireStateChanged();
        } else {
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseVisometryEvent<Integer2> e) {
        if(mover == null){
            add(adder);
            adder = null;
        } else {
            super.mouseReleased(e);
        }
        mode = null;
    }
}
