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
import java.util.Collection;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import sequor.component.IntegerRangeTimer;
import sequor.event.MouseVisometryEvent;
import sequor.event.MouseVisometryListener;

/**
 *
 * @author Elisha Peterson
 */
public class PlottableGroup<V extends Visometry> extends DynamicPlottable<V> implements Animatable<V>,ChangeListener {
    protected Vector<Plottable<V>> plottables;

    public PlottableGroup() {plottables=new Vector<Plottable<V>>();}
    
    public void clear(){plottables.clear();}    
    public void add(Plottable<V> p){
        plottables.add(p);
        p.addChangeListener(this);
    }
    public void remove(Plottable<V> p){
        plottables.remove(p);
        p.removeChangeListener(this);
    }
    public Collection<Plottable<V>> getElements(){return plottables;}

    @Override
    public void setColor(Color newValue) {
        super.setColor(newValue);
        for(Plottable p:plottables){p.setColor(newValue);}
    }
    public void recompute(boolean recomputeAll){
        if(recomputeAll){
            recompute();
        }else{
            for(Plottable p:plottables){
                if(p instanceof Animatable){p.recompute();}
            }
        }
    }
    @Override
    public void recompute() {for (Plottable p:plottables){p.recompute();}}
    @Override
    public void paintComponent(Graphics2D g,V v) {
        for (Plottable p:plottables){
            g.setColor(p.getColor());
            p.paintComponent(g,v);
        }
    }
    @Override
    public void paintComponent(Graphics2D g,V v,IntegerRangeTimer t) {
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
        JMenu result=new JMenu();
        for(Plottable p:plottables){
            try{
                for(Component c:p.getOptionsMenu().getComponents()){
                    result.add(c);                    
                }
            }catch(NullPointerException e){}
        }
        if(result.getComponentCount()==0){return null;}
        return result;
    }

    @Override
    public String[] getStyleStrings() {return null;}
    
    // mouse event handling
    
    MouseVisometryListener<V> mover;
    
    @Override
    public boolean clicked(MouseVisometryEvent<V> e) {
        for (Plottable<V> dp:plottables){
            if (dp instanceof DynamicPlottable && ((DynamicPlottable<V>)dp).clicked(e)){
               return true;
            }
        }
        return false;
    }
            
    Vector<MouseVisometryListener<V>> getHits(MouseVisometryEvent<V> e){
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
