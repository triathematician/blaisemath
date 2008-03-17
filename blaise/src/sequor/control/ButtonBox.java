/*
 * ButtonBox.java
 * Created on Mar 16, 2008
 */

package sequor.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.EventListenerList;
import sequor.VisualControl;
import sequor.model.ComboBoxRangeModel;

/**
 * ButtonBox is a collection of VisualButton'label which can be displayed in a variety of formats.
 * @author Elisha Peterson
 */
public class ButtonBox extends VisualControlGroup {

    double buttonSize=15;
    double spacing=2;
    
    
    // CONSTRUCTORS
    
    /** Intializes to size which depends on given button size. */
    public ButtonBox(double x,double y,double size){
        super(x,y,10,10);
        setButtonSize(size);
        initStyle();
    }
    
    /** Initialize with bounding box. */
    public ButtonBox(double x,double y,double wid,double ht){
        super(x,y,wid,ht);
        initStyle();
    }
    
    
    // BEAN PATTERNS
    
    /** Returns a specific element, cast to a VisualButton */
    public VisualButton getElement(int i){return (VisualButton)elements.get(i);}
    
    /** Sets the button size. Also changes the bounding box. */
    public void setButtonSize(double newSize){
        if(newSize!=buttonSize){
            buttonSize=newSize;
            adjustBounds();
        }
    }

    /** Returns number of columns */
    public int getNumCols(){
        switch(layoutType.getValue()){
            case LAYOUT_HLINE:
                return elements.size();
            case LAYOUT_VLINE:
                return 1;
            case LAYOUT_BOX:
            default:
                int nCols=(int)((getWidth()-spacing)/(buttonSize+spacing));
                return (nCols<1)?1:nCols;
        }        
    }
    
    /** Returns number of rows */
    public int getNumRows(){
        switch(layoutType.getValue()){
            case LAYOUT_HLINE:
                return 1;
            case LAYOUT_VLINE:
                return elements.size();
            case LAYOUT_BOX:
            default:
                int nRows=(int)((getHeight()-spacing)/(buttonSize+spacing));
                return (nRows<1)?1:nRows;
        }        
    }

    
    // ADD/REMOVE METHODS

    @Override
    public void add(VisualControl vc) {
        if(!(vc instanceof VisualButton)){return;}
        VisualButton vb=(VisualButton)vc;
        vb.setSize(buttonSize);
        super.add(vc);
    }    
    
    
    // LAYOUT METHODS
    
    public static final int MIN_BUTTON_SIZE=5;
    
    /** Adjusts the buttonSize to fit within the boundary of this component. */
    public void adjustButtonSize(){
        int nRows=getNumRows();
        int nCols=getNumCols();
        // now set the button size to accomodate the width/height of the box.
        double desiredButtonWidth=(getWidth()-nCols*(spacing+1))/nCols;
        double desiredButtonHeight=(getHeight()-nRows*(spacing+1))/nRows;
        buttonSize=(desiredButtonWidth<desiredButtonHeight)?desiredButtonWidth:desiredButtonHeight;
        buttonSize=(buttonSize>MIN_BUTTON_SIZE)?buttonSize:MIN_BUTTON_SIZE;
    }
    
    /** Adjusts the width/height of the box to accomodate the current buttonSize and spacing */
    public void adjustBounds(){
        boundingBox.setRect(getX(),getY(),getNumCols()*(buttonSize+spacing)+spacing,getNumRows()*(buttonSize+spacing)+spacing);
    }
    
    /** Sets the positions of all the buttons. */
    public void performLayout(){
        int n=elements.size();
        int nCols=getNumCols();
        // go through the buttons and lay them out
        for(int i=0;i<n;i++){
            ((VisualButton)elements.get(i)).setPosition(
                    spacing+(i%nCols)*(buttonSize+spacing),
                    spacing+((int)(i/nCols))*(buttonSize+spacing),
                    buttonSize);
        }            
    }
    
    
    // LAYOUT OPTIONS
    
    ComboBoxRangeModel layoutType;
    public static final int LAYOUT_BOX=0;
    public static final int LAYOUT_HLINE=1;
    public static final int LAYOUT_VLINE=2;
    public static String[] layoutStrings={"Box","Horizontal","Vertical"};
    public ComboBoxRangeModel getLayoutModel(){return layoutType;}
    
    
    // STYLE OPTIONS
    
    ComboBoxRangeModel buttonStyle;
    public static final int STYLE_CIRCLE=VisualButton.STYLE_CIRCLE;
    public static final int STYLE_BOX=VisualButton.STYLE_BOX;
    public static final int STYLE_RBOX=VisualButton.STYLE_RBOX;
    public static String[] styleStrings=VisualButton.styleStrings;
    public ComboBoxRangeModel getButtonStyle(){return buttonStyle;}

    private void initStyle() {
        layoutType=new ComboBoxRangeModel(layoutStrings,0,0,2);
        buttonStyle=new ComboBoxRangeModel(styleStrings,0,0,2);
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
