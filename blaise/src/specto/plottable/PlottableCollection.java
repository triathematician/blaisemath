/*
 * PlottableCollection.java
 * Created on Feb 11, 2008
 */

package specto.plottable;

import specto.*;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;

/**
 * <p>
 * PlottableCollection implements a set of other plottables as a single plottable. The other
 * plottables should not be separately added to the visometry; that is handled by this class.
 * </p>
 * @author Elisha Peterson
 */
public class PlottableCollection<V extends Visometry> extends Plottable<V>{

    /** The plottables contained in the collection. */
    Vector<Plottable<V>> components;
    
    /** Default constructor */
    public PlottableCollection(){
        components=new Vector<Plottable<V>>();
    }
    
    /** Adds component to the collection */
    public void addPlottable(Plottable<V> p){
        components.add(p);
    }
    
    /** Removes component from the collection */
    public void removePlottable(Plottable<V> p){
        components.remove(p);
    }
    
    /** Clears the plottable collection */
    public void clear(){
        components.clear();
    }
    
    @Override
    public void recompute() {
        for(Plottable<V> p:components){
            p.recompute();
        }
    }

    @Override
    public void paintComponent(Graphics2D g) {
        for(Plottable<V> p:components){
            p.paintComponent(g);
        }
    }

    @Override
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu();
        for (Plottable<V> p:components){
            for (Component mi:p.getOptionsMenu().getMenuComponents()){
                result.add(mi);
            }
        }
        return result;
    }
}
