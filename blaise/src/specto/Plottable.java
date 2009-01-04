/*
 * Plottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */

package specto;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sequor.control.NumberSlider;
import sequor.model.ColorModel;
import sequor.model.StringRangeModel;

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
    
    boolean visible = true;
    
    public Plottable() {initStyle();}

    /** Used when the function must be recomputed, typically within paintComponent. */
    public void recompute(V v){}
    /** Called when the plottable is redrawn. */
    public void redraw(){fireStateChanged();}
    /** When the state is changed, should redraw this plottable. */
    public void stateChanged(ChangeEvent e){if(e.getSource().equals(this)){return;}changeEvent=e;redraw();}
    /** Method that paints the plottable. Uses either already-stored values computed by "recompute" or computes
     * new values if necessary.
     * @param g the graphics object
     * @param v the visometry */
    public abstract void paintComponent(Graphics2D g,V v);           
    
    
    // BEAN PATTERNS
    
    /** Sets whether plot is visible. */
    public void setVisible(boolean newValue){visible=newValue;}
    /** Returns visibility status. */
    public boolean isVisible(){return visible;}
    
    
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
    public StringRangeModel style;
    /** Style strings used to select a style. */
    public abstract String[] getStyleStrings();
    /** Returns adjuster which can be used to modify the style. */
    public NumberSlider getStyleSlider(int x,int y){
        if(style!=null){
            NumberSlider result=new NumberSlider(x,y,style);
            return result;
        }else{
            return null;
        }
    }
    /** Returns adjuster which can be used to modify the style, with string for name. */
    public NumberSlider getStyleSlider(String name,int x,int y){
        if(style!=null){
            NumberSlider result=new NumberSlider(name,x,y,style);
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
        style=new StringRangeModel(styles);
        style.addChangeListener(this);
    }
    
    
    // BEAN PATTERNS (STYLE)
    
    /** Returns color of the element. */
    public Color getColor(){return color.getValue();}
    /** Sets the color of the element. */
    public void setColor(Color newValue){color.setValue(newValue);}
    /** Sets the color model. */
    public void setColorModel(ColorModel model){this.color = model;}
    /** Returns the color model. */
    public ColorModel getColorModel(){return color;}
    /** Synchronizes color model with another plottable. */
    public void synchronizeColorsWith(Plottable p){setColorModel(p.getColorModel());}
    
    
    // CONTEXT MENU
    
    /** Returns menu containing any desired options. By default, returns a color element, display style, and decoration items. */
    public JMenu getOptionsMenu(){
        JMenu result=new JMenu(toString());       
        result.setForeground(getColor());
        result.add(getVisibleMenuItem());
        result.add(getColorMenuItem());  
        if(style==null){return result;}
        return style.appendToMenu(result);
    }
    
    /** Returns check box with label that shows/hides the plottable. */
    public Component getVisibleMenuItem(){
        JCheckBox result = new JCheckBox("Visible",visible);
        result.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                visible=!visible;
                fireStateChanged();
            }
        });
        return result;
    }
    
    /** Returns button which when pressed opens a color palette to change the color of the given item. */
    public Component getColorMenuItem(){return color.getMenuItem();}
    
    /** Returns "add" menu item. */
    public JMenuItem getAddMenuItem(final PlotPanel<V> panel){
        JMenuItem mi=new JMenuItem(toString());
        mi.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
                    panel.add(Plottable.this);
            }});
        return mi;
    }
}
