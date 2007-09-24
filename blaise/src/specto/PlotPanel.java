/*
 * PlotPanel.java
 * Created on Sep 14, 2007, 7:50:15 AM
 */

package specto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import specto.visometry.Euclidean2;

/**
 * This is a superclass for plot windows. It implements component handling and drawing
 * functions, where components are Plottable's or DynamicPlottable's.
 * <br><br>
 * @author Elisha Peterson
 */
public class PlotPanel<V extends Visometry> extends JPanel implements ChangeListener {
    
    
    // PROPERTIES
    
    private Vector<Plottable<V>> pComponents;
    private V visometry;
    private RangeTimer timer;
    
        
    // CONSTRUCTORS
    
    public PlotPanel(){super();initComponents();}
    public PlotPanel(V visometry){super();initComponents();setVisometry(visometry);}
    
    
    // INITIALIZERS
    
    private void initComponents(){
        pComponents=new Vector<Plottable<V>>();
        timer=null;
        visometry=null;
        setBackground(Color.WHITE);
        setOpaque(true);        
    }
    
    protected void resetAnimation(){
        if(timer==null){timer=new RangeTimer(
                new ActionListener(){public void actionPerformed(ActionEvent e){repaint();}}
        );}
        timer.restart();
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public V getVisometry(){return visometry;}
    public RangeTimer getTimer(){return timer;}
    
    public void setVisometry(V newValue){visometry=newValue;visometry.initContainer(this);}
    
    
    // COMPONENT HANDLING
    
    public void add(Plottable<V> pv){
        pComponents.add(pv);
        pv.addChangeListener(this);
        if(pv instanceof DynamicPlottable){
            addMouseListener((DynamicPlottable)pv);
            addMouseMotionListener((DynamicPlottable)pv);
        }
    }
    public <T extends Plottable<V>> void addAll(Collection<T> cpv){for(T pv:cpv){add(pv);}}
    public void remove(Plottable<V> pv){
        pComponents.remove(pv);
        pv.removeChangeListener(this);
        if(pv instanceof DynamicPlottable){
            removeMouseListener((DynamicPlottable)pv);
            removeMouseMotionListener((DynamicPlottable)pv);
        }
    }
    public <T extends Plottable<V>> void removeAll(Collection<T> cpv){for(T pv:cpv){remove(pv);}}
    
    
    // PAINT METHODS
    
    public void paintComponent(Graphics gb){
        super.paintComponent(gb);
        if(visometry==null){return;}
        if (isOpaque()) {
            gb.setColor(getBackground());
            gb.fillRect(0, 0, getWidth(), getHeight());
        }
        Graphics2D g=(Graphics2D)gb;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        if(timer!=null&&timer.isRunning()){
            for(Plottable<V> p:pComponents){
                if(p instanceof Animatable){((Animatable)p).paintComponent(g,visometry,timer);} else{p.paintComponent(g,visometry);}
            }
        }else{for(Plottable<V> p:pComponents){p.paintComponent(g,visometry);}}
    }
    
    
    // EVENT HANDLING
    
    public void stateChanged(ChangeEvent e){repaint();}
}
