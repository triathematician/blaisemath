/*
 * Plottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */

package specto;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sequor.control.NumberAdjuster;
import sequor.model.ColorModel;
import sequor.model.ComboBoxRangeModel;

/**
 * This abstract class includes basic functionality for the plotting of some object on
 * a plot panel. Adds in support for firing change events, if the underlying settings
 * have changed for some reason.
 * <p>
 * Plottable's store all computations before displaying, allowing for plotpanel's to
 * pick and choose which elements to recompute before drawing. This allows the program
 * to avoid redundant computations. The PlotPanel class handles the recomputation
 * method before painting.
 * </p>
 * @author Elisha Peterson
 */
public abstract class Plottable<V extends Visometry> implements ChangeListener {
    public Plottable() {initStyle();}


    public void recompute(){}
    public void redraw(){fireStateChanged();}
    public void stateChanged(ChangeEvent e){if(e.getSource().equals(this)){return;}changeEvent=e;redraw();}
    public abstract void paintComponent(Graphics2D g,V v);           
    
    
    // EVENT HANDLING
    
    /** Event handling code copied from DefaultBoundedRangeModel. */      
    protected ChangeEvent changeEvent=new ChangeEvent(this);
    protected EventListenerList listenerList=new EventListenerList();    
    public void addChangeListener(ChangeListener l){listenerList.add(ChangeListener.class,l);}
    public void removeChangeListener(ChangeListener l){listenerList.remove(ChangeListener.class,l);}
    protected void fireStateChanged(){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ChangeListener.class){
                if(changeEvent==null){return;}
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
    
    
    // VISUAL STYLE ELEMENTS
    
    /** Color used by the element. */
    protected ColorModel color;
    /** Style setting used by the element. */
    public ComboBoxRangeModel style;
    /** Style strings used to select a style. */
    public abstract String[] getStyleStrings();
    /** Returns adjuster which can be used to modify the style. */
    public NumberAdjuster getStyleAdjuster(double x,double y){
        if(style!=null){
            NumberAdjuster result=new NumberAdjuster(x,y,style);
            result.setStyle(NumberAdjuster.STYLE_LINE);
            return result;
        }else{
            return null;
        }
    }
    
    /** Initializes the style. */
    public void initStyle(){
        color=new ColorModel();
        color.addChangeListener(this);
        String[] styles=getStyleStrings();
        if(styles==null || styles.length==0){return;}
        style=new ComboBoxRangeModel(styles,0,0,styles.length-1);
        style.addChangeListener(this);
    }
    
    
    // BEAN PATTERNS (STYLE)
    
    /** Returns color of the element. */
    public Color getColor(){return color.getValue();}
    /** Sets the color of the element. */
    public void setColor(Color newValue){color.setValue(newValue);}
    
    
    // CONTEXT MENU
    
    /** Returns menu containing any desired options. By default, returns a color element, display style, and decoration items. */
    public JMenu getOptionsMenu(){
        JMenu result=new JMenu(toString()+" Options");       
        result.setForeground(getColor());
        result.add(getColorMenuItem());  
        if(style==null){return result;}
        return style.appendToMenu(result);
    }
    
    /** Returns button which when pressed opens a color palette to change the color of the given item. */
    public Component getColorMenuItem(){return color.getMenuItem();}
}
