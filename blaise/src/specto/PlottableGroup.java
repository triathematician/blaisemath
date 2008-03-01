/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * PlottableGroup.java
 * Created on Feb 25, 2008
 */

package specto;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.component.RangeTimer;

/**
 *
 * @author Elisha Peterson
 */
public class PlottableGroup<V extends Visometry> extends DynamicPlottable<V> implements Animatable,ChangeListener {
    protected Vector<Plottable<V>> plottables;

    public PlottableGroup(V v) {super(v);plottables=new Vector<Plottable<V>>();}
    
    public void clear(){plottables.clear();}    
    public void add(Plottable<V> p){plottables.add(p);p.addChangeListener(this);}
    public void remove(Plottable<V> p){plottables.remove(p);p.removeChangeListener(this);}
    public Collection<Plottable<V>> getElements(){return plottables;}

    @Override
    public void setColor(Color newValue) {
        super.setColor(newValue);
        for(Plottable p:plottables){p.setColor(newValue);}
    }
    @Override
    public void setVisometry(V newVis) {super.setVisometry(newVis);if(plottables!=null){for(Plottable p:plottables){p.setVisometry(newVis);}}}
    @Override
    public void recompute() {for (Plottable p:plottables){p.recompute();}}
    @Override
    public void paintComponent(Graphics2D g) {
        for (Plottable p:plottables){
            p.paintDecorations(g);
            p.paintComponent(g);
        }
    }
    @Override
    public void paintComponent(Graphics2D g, RangeTimer t) {
        for (Plottable p:plottables){
            p.paintDecorations(g,t);
            if(p instanceof Animatable){
                ((Animatable)p).paintComponent(g,t);
            }else{
                p.paintComponent(g);
            }
        }
    }

    @Override
    public JMenu getOptionsMenu() {
        if(!isOptionsMenuBuilding()){return null;}
        JMenu result=new JMenu();
        for(Plottable p:plottables){
            if(p.isOptionsMenuBuilding()){
                for(Component c:p.getOptionsMenu().getComponents()){
                    result.add(c);
                }
            }
        }
        return result;
    }

    
    // mouse event handling
    
    DynamicPlottable<V> mover;
    
    @Override
    public boolean clicked(MouseEvent e) {
        for (Plottable<V> dp:plottables){
            if (dp instanceof DynamicPlottable && ((DynamicPlottable<V>)dp).clicked(e)){
               return true;
            }
        }
        return false;
    }
            
    Vector<DynamicPlottable<V>> getHits(MouseEvent e){
        Vector<DynamicPlottable<V>> result=new Vector<DynamicPlottable<V>>();
        for (Plottable<V> dp:plottables){
            if (dp instanceof DynamicPlottable && ((DynamicPlottable<V>)dp).clicked(e)){
                result.add((DynamicPlottable<V>)dp);
            }
        }
        return result;
    }

    @Override
    public void mouseClicked(MouseEvent e){
        Vector<DynamicPlottable<V>> hits=getHits(e);
        if(hits.isEmpty()){visometry.mouseClicked(e);}
        else{hits.get(0).mouseClicked(e);}
    }

    @Override
    public void mousePressed(MouseEvent e){
        Vector<DynamicPlottable<V>> hits=getHits(e);
        if(hits.isEmpty()){visometry.mousePressed(e);}
        else{mover=hits.get(0);mover.mousePressed(e);}
    }

    @Override
    public void mouseDragged(MouseEvent e){
        if(mover!=null){mover.mouseDragged(e);}
        else{visometry.mouseDragged(e);}
    }

    @Override
    public void mouseReleased(MouseEvent e){
        if(mover!=null){mover.mouseReleased(e);mover=null;}
        else{visometry.mouseReleased(e);}
    }

    @Override
    public void mouseMoved(MouseEvent e){visometry.mouseMoved(e);}

    @Override
    public void mouseEntered(MouseEvent e){visometry.mouseEntered(e);}

    @Override
    public void mouseExited(MouseEvent e){visometry.mouseExited(e);}

    public void stateChanged(ChangeEvent e) {fireStateChanged();}
}
