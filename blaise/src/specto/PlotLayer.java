/*
 * PlotLayer.java
 * Created on Nov 12, 2007, 9:23:29 AM
 */

// TODO Maybe move this to inner PlotPanel class

package specto;

import sequor.component.RangeTimer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JLayeredPane;

/**
 * Represents a collection of plottable's implemented as a single layer.
 * Intended for use within the PlotPanel class only.
 * <br><br>
 * @author ae3263
 */
public class PlotLayer<V extends Visometry> extends JLayeredPane {
    
    
    // PROPERTIES
    
    /** The owning class. */
    private PlotPanel owner;
    /** Contains all the basic components. */    
    private Vector<Plottable<V>> plottables;
    /** Whether or not this window animates. */
    private boolean animates=false;
        
    // CONSTRUCTORS
    
    public PlotLayer(PlotPanel owner){
        super();
        this.owner=owner;
        plottables=new Vector<Plottable<V>>();
        setPreferredSize(owner.getSize());
        setOpaque(false);
        setInheritsPopupMenu(true);
    }
    
    
    // BEAN GETTERS/SETTERS
    
    public Vector<Plottable<V>> getPlottables(){return plottables;}
    public boolean isAnimate(){return animates;}
    public void setAnimates(boolean newValue){animates=newValue;}
    
    
    // COMPONENT HANDLING
    
    public void add(Plottable<V> pv){plottables.add(pv);}
    public <T extends Plottable<V>> void addAll(Collection<T> cpv){for(T pv:cpv){add(pv);}}
    public void remove(Plottable<V> pv){plottables.remove(pv);}
    public void clear(){plottables.clear();}
    
    // PAINT METHODS
    
    public void recompute(){for(Plottable<V> p:plottables){p.recompute();}}
    
    @Override
    public void paintComponent(Graphics gb){
        for(Plottable p:plottables){
            gb.setColor(p.getColor());
            p.paintDecorations((Graphics2D)gb,owner.getVisometry());
            gb.setColor(p.getColor());
            p.paintComponent((Graphics2D)gb,owner.getVisometry());
        }
    }
    
    public void paintComponent(Graphics gb,RangeTimer timer){
        if(!animates){return;}
        for(Plottable p:plottables){
            gb.setColor(p.getColor());
            p.paintDecorations((Graphics2D)gb,owner.getVisometry(),timer);
            gb.setColor(p.getColor());
            if(p instanceof Animatable){
                ((Animatable)p).paintComponent((Graphics2D)gb,owner.getVisometry(),timer);
            }else{
                p.paintComponent((Graphics2D)gb,owner.getVisometry());
            }
        }        
    }
}
