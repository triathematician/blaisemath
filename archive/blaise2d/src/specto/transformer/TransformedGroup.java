/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * TransformedGroup.java
 * Created on Mar 7, 2008
 */

package specto.transformer;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import sequor.component.RangeTimer;
import specto.Animatable;
import specto.Plottable;
import specto.PlottableGroup;
import specto.Visometry;

/**
 * Represents a group of plottables of type V1 which are placed on a visometry of type V2.
 * This is accomplished by storing a V1 visometry within this class. It is expected that this method
 * may override the standard visometry in order to specify a subset of a window, etc. rather than the
 * entire window. Thus, the way in which the transformation is computed within V1 may be quite different.
 * <br><br>
 * To make this class work, the user should therefore override the visometry V1 with specified instructions
 * on how to handle coordinates to/from the window.
 * 
 * @author Elisha Peterson
 */
public class TransformedGroup <V1 extends Visometry, V2 extends Visometry> extends PlottableGroup<V2> implements Animatable<V2>, ChangeListener {
    
    /** The actual elements. */
    protected PlottableGroup<V1> transforming;
    /** The visometry used to plot the elements. */
    V1 drawVisometry;

    
    // CONSTRUCTORS
    
    public TransformedGroup(V1 drawVisometry) {
        transforming = new PlottableGroup<V1>();
        this.drawVisometry = drawVisometry;
    }
    
    
    // METHODS FOR HANDLING PLOTTABLE ELEMENTS
    
    @Override
    public void clear(){
        super.clear();
        transforming.clear();
    }   
    
    public void addTransforming(Plottable<V1> p){
        transforming.add(p);
        p.addChangeListener(this);
    }
    public void removeTransforming(Plottable<V1> p){
        p.removeChangeListener(this);
        transforming.remove(p);
    }
    
    
    // BEANS
    
    public PlottableGroup<V1> getTransformingElements(){return transforming;}
    @Override
    public void setColor(Color newValue) {transforming.setColor(newValue);}
    
    
    // COMPUTATIONS
    
    @Override
    public void recompute(V2 v2,boolean recomputeAll){
        transforming.recompute(drawVisometry,recomputeAll);
        super.recompute(v2,recomputeAll);
    }
    @Override
    public void recompute(V2 v2) {
        super.recompute(v2);
        transforming.recompute(drawVisometry);
    }
    
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g,V2 v) {
        super.paintComponent(g, v);
        transforming.paintComponent(g, drawVisometry);
    }
    @Override
    public void paintComponent(Graphics2D g,V2 v,RangeTimer t) {
        super.paintComponent(g, v, t);
        transforming.paintComponent(g, drawVisometry, t);
    }

    @Override
    public JMenu getOptionsMenu(){return transforming.getOptionsMenu();}
    @Override
    public int getAnimatingSteps(){return transforming.getAnimatingSteps();}
}