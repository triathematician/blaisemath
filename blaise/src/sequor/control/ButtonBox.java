/*
 * ButtonBox.java
 * Created on Mar 16, 2008
 */

package sequor.control;

import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import sequor.VisualControlGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sequor.VisualControl;
import sequor.model.StringRangeModel;

/**
 * ButtonBox is a collection of VisualButton'label which can be displayed in a variety of formats.
 * @author Elisha Peterson
 */
public class ButtonBox extends VisualControlGroup {

    protected int buttonSize=20;
    
    protected int desiredNumberColumns=2;
    
    
    // CONSTRUCTORS
    
    /** Intializes to size which depends on given button size. */
    public ButtonBox(int x,int y,int size){
        super(x,y,10,10);
        setButtonSize(size);
        initStyle();
    }    
    /** Initialize with bounding box. */
    public ButtonBox(int x,int y,int wid,int ht){
        super(x,y,wid,ht);
        initStyle();
    }
    public ButtonBox(int x,int y,int wid,int ht,int layout){
        super(x,y,wid,ht);        
        initStyle();
        layoutType.setValue(layout);
    }
    
    
    // BEAN PATTERNS
    
    /** Returns a specific element, cast to a VisualButton */
    public VisualButton getElement(int i){return (VisualButton)elements.get(i);}
    
    /** Sets the button size. Also changes the bounding box. */
    public void setButtonSize(int newSize){
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
                int nCols=(int)((getWidth()-2*padding+spacing)/(buttonSize+spacing));
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
                int nRows=(int)((getHeight()-2*padding+spacing)/(buttonSize+spacing));
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
        int desiredButtonWidth=(getWidth()-(nCols-1)*spacing-2*padding)/nCols;
        int desiredButtonHeight=(getHeight()-(nRows-1)*spacing-2*padding)/nRows;
        buttonSize=(desiredButtonWidth<desiredButtonHeight)?desiredButtonWidth:desiredButtonHeight;
        buttonSize=(buttonSize>MIN_BUTTON_SIZE)?buttonSize:MIN_BUTTON_SIZE;
        for(int i=0;i<elements.size();i++){
            getElement(i).setSize(buttonSize);
        }
    }
    
    /** Adjusts the width of the box if the current layout is a standard box, to accomodate at least a particular number of buttons per row. */
    public void adjustWidth(){
        if(layoutType.getValue()==LAYOUT_BOX){
            setSize(2*padding+desiredNumberColumns*(buttonSize+spacing)-spacing,2*padding+buttonSize+spacing);
        }
    }
    
    /** Adjusts the width/height of the box to accomodate the current buttonSize and spacing. Uses the currently specified width to control the number of
     * buttons horizontally, and adjusts the height of the box accordingly.
     */
    public void adjustBounds(){
        int nCols=getNumCols();
        int nRows=(int)Math.ceil(elements.size()/(double)nCols);
        setBounds(getX(),getY(),nCols*(buttonSize+spacing)+2*padding-spacing,nRows*(buttonSize+spacing)+2*padding-spacing);
    }
    
    /** Sets the positions of all the buttons. */
    public void performLayout(){
        int n=elements.size();
        int nCols=getNumCols();
        // go through the buttons and lay them out
        for(int i=0;i<n;i++){
            elements.get(i).setBounds(
                    getX()+padding+(i%nCols)*(buttonSize+spacing),
                    getY()+padding+((int)(i/nCols))*(buttonSize+spacing),
                    buttonSize,
                    buttonSize);
        }            
    }
    
    
    // LAYOUT OPTIONS
    
    StringRangeModel layoutType;
    public static final int LAYOUT_BOX=0;
    public static final int LAYOUT_HLINE=1;
    public static final int LAYOUT_VLINE=2;
    public static String[] layoutStrings={"Box","Horizontal","Vertical"};
    public StringRangeModel getLayoutModel(){return layoutType;}
    public void setOrientation(int newOrientation){layoutType.setValue(newOrientation);}
    
    // STYLE OPTIONS
    
    StringRangeModel buttonStyle;
    public static final int STYLE_CIRCLE=0;
    public static final int STYLE_BOX=1;
    public static final int STYLE_RBOX=2;
    public static String[] styleStrings={"Circular","Square","Rounded"};
    public StringRangeModel getButtonStyle(){return buttonStyle;}
    public BoundedShape getButtonShape(){
        switch(buttonStyle.getValue()){
            case STYLE_BOX:
                return BoundedShape.Rectangle;
            case STYLE_RBOX:
                return new BoundedWidthShape.RoundRectangle(8);
            case STYLE_CIRCLE:
            default:
                return BoundedShape.Ellipse;
                
        }
    }

    private void initStyle() {
        layoutType=new StringRangeModel(layoutStrings,LAYOUT_BOX,0,2);
        layoutType.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                adjustWidth();
                adjustBounds();
                performLayout();
                fireStateChanged();
            }            
        });
        buttonStyle=new StringRangeModel(styleStrings,STYLE_CIRCLE,0,2);
        buttonStyle.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                for(int i=0;i<elements.size();i++){
                    getElement(i).setBackgroundShape(getButtonShape());
                }
                switch(buttonStyle.getValue()){
                    case(STYLE_CIRCLE):
                    case(STYLE_RBOX):
                        backgroundShape=new BoundedWidthShape.RoundRectangle(8);
                        break;
                    case(STYLE_BOX):
                        backgroundShape=BoundedShape.Rectangle;
                        break;
                }
            }            
        });
        backgroundShape=new BoundedWidthShape.RoundRectangle(8);
    }
    
    
    // MOUSE EVENT HANDLING

    @Override
    public void clickAction(MouseEvent e) {
        if(e!=null){
            layoutType.cycle();
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
