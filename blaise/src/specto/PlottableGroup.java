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
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.component.RangeTimer;
import sequor.event.MouseVisometryEvent;
import sequor.event.MouseVisometryListener;
import specto.euclidean2.Euclidean2;

/**
 *
 * @author Elisha Peterson
 */
public class PlottableGroup<V extends Visometry> extends DynamicPlottable<V> implements Animatable<V>,ChangeListener {
    /** Elements in the group. */
    protected Vector<Plottable> plottables;
    /** Name of the group. */
   protected String name;


    public PlottableGroup() {plottables=new Vector<Plottable>();}
    
    public void clear(){
        for(Plottable p : plottables) {
            p.removeChangeListener(this);
        }
        plottables.clear();
    }    
    public void add(Plottable<V> p){
        plottables.add(p);
        p.addChangeListener(this);
    }
    public void remove(Plottable<V> p){
        plottables.remove(p);
        p.removeChangeListener(this);
    }
    
    
    // BEANS
    
    public Collection<Plottable> getElements(){return plottables;}

    @Override
    public void setColor(Color newValue) {
        super.setColor(newValue);
        for(Plottable p:plottables){p.setColor(newValue);}
    }
    
    /** Return name. */
    @Override
    public String toString() { return name; }
    /** Sets the display string. */
    public void setName(String name) { this.name = name; }
    
    
    public void recompute(V v,boolean recomputeAll){
        if(recomputeAll){
            recompute(v);
        }else{
            for(Plottable p:plottables){
                if(p instanceof Animatable){p.recompute(v);}
            }
        }
    }
    @Override
    public void recompute(V v) {for (Plottable p:plottables){p.recompute(v);}}
    @Override
    public void paintComponent(Graphics2D g,V v) {
        for (Plottable p:plottables){
            g.setColor(p.getColor());
            p.paintComponent(g,v);
        }
    }
    @Override
    public void paintComponent(Graphics2D g,V v,RangeTimer t) {
        for (Plottable p:plottables){
            g.setColor(p.getColor());
            if(p instanceof Animatable){
                ((Animatable)p).paintComponent(g,v,t);
            }else{
                p.paintComponent(g,v);
            }
        }
    }

    @Override
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu(toString() + " Options");   
        result.setForeground(getColor());
        result.add(getColorMenuItem());
        color.addChangeListener(this);
        for(Plottable p:plottables){
            result.add(p.getOptionsMenu());
        }
        if(style==null){return result;}
        return style.appendToMenu(result);
    }

    @Override
    public String[] getStyleStrings() {return null;}

    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource().equals(color)){
            for(Plottable p:plottables){
                p.setColor(color.getValue());
            }
        }
        super.stateChanged(e);
    }
    
    
    // MOUSE HANDLING
    
    protected MouseVisometryListener<V> mover;
    
    @Override
    public boolean clicked(MouseVisometryEvent<V> e) {
        for (Plottable<V> dp:plottables){
            if (dp instanceof DynamicPlottable && ((DynamicPlottable<V>)dp).clicked(e)){
               return true;
            }
        }
        return false;
    }
            
    protected Vector<MouseVisometryListener<V>> getHits(MouseVisometryEvent<V> e){
        Vector<MouseVisometryListener<V>> result=new Vector<MouseVisometryListener<V>>();
        for (Plottable<V> dp:plottables){
            if (dp instanceof DynamicPlottable && ((DynamicPlottable<V>)dp).clicked(e)){
                result.add((DynamicPlottable<V>)dp);
            }
        }
        return result;
    }

    @Override
    public void mouseClicked(MouseVisometryEvent<V> e){
        Vector<MouseVisometryListener<V>> hits=getHits(e);
        if(hits.isEmpty()){e.getSourceVisometry().mouseClicked(e);}
        else{hits.get(0).mouseClicked(e);}
    }

    @Override
    public void mousePressed(MouseVisometryEvent<V> e){
        Vector<MouseVisometryListener<V>> hits=getHits(e);
        if(hits.isEmpty()){e.getSourceVisometry().mousePressed(e);}
        else{mover=hits.get(0);mover.mousePressed(e);}
    }

    @Override
    public void mouseDragged(MouseVisometryEvent<V> e){
        if(mover!=null){mover.mouseDragged(e);}
        else{e.getSourceVisometry().mouseDragged(e);}
    }

    @Override
    public void mouseReleased(MouseVisometryEvent<V> e){
        if(mover!=null){mover.mouseReleased(e);mover=null;}
        else{e.getSourceVisometry().mouseReleased(e);}
    }

    @Override
    public void mouseMoved(MouseVisometryEvent<V> e){e.getSourceVisometry().mouseMoved(e);}

    @Override
    public void mouseEntered(MouseVisometryEvent<V> e){e.getSourceVisometry().mouseEntered(e);}

    @Override
    public void mouseExited(MouseVisometryEvent<V> e){e.getSourceVisometry().mouseExited(e);}

    public int getAnimatingSteps() {
        int maxSteps=0;
        int curSteps;
        for(Plottable p:plottables){
            if(p instanceof Animatable){
                curSteps=((Animatable)p).getAnimatingSteps();
                if(curSteps>maxSteps){maxSteps=curSteps;}
            }
        }
        return maxSteps;
    }
}
