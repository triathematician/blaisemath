/*
 * ArcGroup.java
 * Created on May 3, 2008
 */

package specto.specialty.grid.plottable;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import scio.coordinate.I2;
import sequor.event.MouseVisometryEvent;
import sequor.event.MouseVisometryListener;
import specto.Plottable;
import specto.PlottableGroup;
import specto.specialty.grid.Grid2;

/**
 * <p>
 * ArcGroup represents a collection of arcs.
 * </p>
 * @author Elisha Peterson
 */
public class ArcGroup extends PlottableGroup<Grid2> {

    public ArcGroup(){
        super();
    } 
    
    @Override
    public void add(Plottable<Grid2> p){
        if (p instanceof GridArc){
            super.add(p);
        }
    }
    
    
    // PAINT METHODS

    @Override
    public void paintComponent(Graphics2D g, Grid2 v) {
        if(adder != null) {
            adder.setColor(getColor());
            adder.paintComponent(g, v);        
        }
        super.paintComponent(g, v);
    }
    
    @Override
    public String toString() { return "ArcGroup"; }
    
    
    // EVENT HANDLING
    
    GridArc adder = null;

    @Override
    public boolean clicked(MouseVisometryEvent<Grid2> e) {
        if(super.clicked(e)) {
            return true;
        }
        return ((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) == InputEvent.SHIFT_DOWN_MASK);
    }    

    String mode=null;
    
    @Override
    public void mousePressed(MouseVisometryEvent<Grid2> e) {
        Vector<MouseVisometryListener<Grid2>> hits=getHits(e);
        if(hits.isEmpty()){    
            if((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) == InputEvent.SHIFT_DOWN_MASK) {
                I2 point = (I2) e.getCoordinate();
                mover = null;
                adder = new GridArc(point.x,point.y,45,point.x,point.y,-45);
                fireStateChanged();
            }
        }
        else{
            mover = hits.get(0);
            mover.mousePressed(e);
        }
    }

    @Override
    public void mouseDragged(MouseVisometryEvent<Grid2> e) {
        if(adder != null){
            I2 point = (I2) e.getCoordinate();
            adder.setStop(point.x,point.y,-45);
            fireStateChanged();
        } else {
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseVisometryEvent<Grid2> e) {
        if(mover == null){
            add(adder);
            adder = null;
        } else {
            super.mouseReleased(e);
        }
        mode = null;
    }
}
