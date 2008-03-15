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
import sequor.component.IntegerRangeTimer;
import specto.Animatable;
import specto.Plottable;
import specto.PlottableGroup;
import specto.Visometry;

/**
 * Represents a group of plottables of type V2 which are placed on a visometry of type V1.
 * The transformers "transformer" method converts from its natural coordinate type (V2) to the alternate coordinate type (V1).
 * 
 * @author Elisha Peterson
 */
public class TransformedGroup<V1 extends Visometry,V2 extends Visometry> extends PlottableGroup<V1> implements Animatable<V1>,ChangeListener {
    /** The actual elements. */
    protected PlottableGroup<V2> tPlottables;

    public TransformedGroup() {tPlottables=new PlottableGroup<V2>();}
    
    @Override
    public void clear(){tPlottables.clear();}    
    @Override
    public void add(Plottable<V1> p){}
    //public void add(Plottable<V2> p){tPlottables.add(p);}
    @Override
    public void remove(Plottable<V1> p){}
    //public void remove(Plottable<V2> p){tPlottables.remove(p);}
    public PlottableGroup<V2> getTransformableElements(){return tPlottables;}
    @Override
    public void setColor(Color newValue) {tPlottables.setColor(newValue);}
    @Override
    public void recompute(boolean recomputeAll){tPlottables.recompute(recomputeAll);}
    @Override
    public void recompute() {tPlottables.recompute();}
    @Override
    public void paintComponent(Graphics2D g,V1 v) {
        for (Plottable p:tPlottables.getElements()){
            g.setColor(p.getColor());
            p.paintComponent(g,v);
        }
    }
    @Override
    public void paintComponent(Graphics2D g,V1 v,IntegerRangeTimer t) {
        for (Plottable p:tPlottables.getElements()){
            g.setColor(p.getColor());
            if(p instanceof Animatable){
                ((Animatable)p).paintComponent(g,v,t);
            }else{
                p.paintComponent(g,v);
            }
        }
    }

    @Override
    public JMenu getOptionsMenu(){return tPlottables.getOptionsMenu();}
    @Override
    public int getAnimatingSteps(){return tPlottables.getAnimatingSteps();}
}