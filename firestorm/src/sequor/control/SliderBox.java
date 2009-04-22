/*
 * ButtonBox.java
 * Created on Mar 16, 2008
 */

package sequor.control;

import java.awt.event.MouseEvent;
import sequor.VisualControlGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import sequor.VisualControl;

/**
 * ButtonBox is a collection of VisualButton'label which can be displayed in a variety of formats.
 * @author Elisha Peterson
 */
public class SliderBox extends VisualControlGroup {

    protected int adjusterGirth=20;
    
    
    // CONSTRUCTORS
    
    /** Default constructor. */
    public SliderBox() {this(0,0,15);}
    /** Intializes to size which depends on given button size. */
    public SliderBox(int x,int y,int size){
        super(x,y,100,10);
        setAdjusterGirth(size);
    }    
    /** Initialize with bounding box. */
    public SliderBox(int x,int y,int wid,int ht){super(x,y,wid,ht);}
    
    
    // BEAN PATTERNS
    
    /** Returns a specific element, cast to a VisualButton */
    public NumberSlider getElement(int i){return (NumberSlider)elements.get(i);}    
    /** Sets the button size. Also changes the bounding box. */
    public void setAdjusterGirth(int newSize){
        if(newSize!=adjusterGirth){
            adjusterGirth=newSize;
            adjustBounds();
        }
    }

    
    // ADD/REMOVE METHODS

    @Override
    public void add(VisualControl vc) {
        if(!(vc instanceof NumberSlider)){return;}
        NumberSlider vb=(NumberSlider)vc;
        if(orientation==HORIZONTAL){
            vb.setSize(getWidth()-2*padding,adjusterGirth);
        }else{
            vb.setSize(adjusterGirth,getHeight()-2*padding);
        }
        super.add(vc);
        performLayout();
    }
    

    // LAYOUT METHODS
    
    public static final int HORIZONTAL=0;
    public static final int VERTICAL=1;
    
    protected int orientation=HORIZONTAL;
    
    public int getOrientation(){return orientation;}
    public void setOrientation(int newValue){
        if(orientation!=newValue){
            orientation=newValue;
            performLayout();
            fireStateChanged();
        }
    }    
    
    public static final int MIN_BUTTON_SIZE=5;
    
    /** Adjusts the adjusterSize to fit within the boundary of this component. */
    public void adjustAdjusterSize(){
        int adjusterLength=getWidth()-2*padding;
        int spaceAvailable=getHeight()-2*padding-(elements.size()-1)*spacing;
        adjusterGirth=spaceAvailable/elements.size();
        for(int i=0;i<elements.size();i++){
            getElement(i).setSize(adjusterLength,adjusterGirth);
        }
    }
    
    /** Adjusts the width/height of the box to accomodate the current size of adjusters within, and spacing.
     */
    public void adjustBounds(){
        switch(orientation){
            case HORIZONTAL:
                setSize(getWidth(),2*padding+(adjusterGirth+spacing)*elements.size()-spacing);
                break;
            case VERTICAL:
                setSize(2*padding+(adjusterGirth+spacing)*elements.size()-spacing,getHeight());
                break;
        }
    }
    
    /** Sets the positions of all the buttons. */
    public void performLayout(){
        adjustBounds();
        if(orientation==HORIZONTAL){
            int adjusterLength=getWidth()-2*padding;
            for(int i=0;i<elements.size();i++){
                elements.get(i).setBounds(
                        getX()+padding,
                        getY()+padding+i*(spacing+adjusterGirth),
                        adjusterLength,
                        adjusterGirth);
            }       
        }else{
            int adjusterLength=getHeight()-2*padding;
            for(int i=0;i<elements.size();i++){
                elements.get(i).setBounds(
                        getX()+padding+i*(spacing+adjusterGirth),
                        getY()+padding,
                        adjusterGirth,
                        adjusterLength);
            }       
        }
    }    
    
    
    // MOUSE EVENT HANDLING

    @Override
    public void clickAction(MouseEvent e) {
        if(e!=null){
            setOrientation(1-orientation);
        }
    }       

    
    // ACTION EVENT HANDLING
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e!=null){actionEvent=e;}
        fireActionPerformed(null);
    }
     
    protected ActionEvent actionEvent=null;
    protected EventListenerList actionListenerList=new EventListenerList();    
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class,l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class,l);}
    protected void fireActionPerformed(String s){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ActionListener.class){
                if(actionEvent==null){actionEvent=new ActionEvent(this,0,s);}
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }    
}
