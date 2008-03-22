/**
 * ArcSlider.java
 * Created on Mar 18, 2008
 */

package sequor.control;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.VisualControl;
import sequor.VisualControlGroup;
import sequor.model.BoundedRangeModel;
import sequor.model.ComboBoxRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.StepControlledRangeModel;

/**
 * Contains a slider interfacing with an underlying RangeModel. This one can be displayed as an arc.
 * 
 * @author Elisha Peterson
 */
public class ArcSlider extends VisualControlGroup {
    /** Represents the button which can be moved around. */
    VisualControl handle;
    /** Represents the dimension of the handle. */
    double handleSize;
    /** Represents the underlying model. */
    BoundedRangeModel model;
    
    
    // CONSTRUCTORS
    
    public ArcSlider(Point position,BoundedRangeModel model){this(position.x,position.y,model);}
    public ArcSlider(int x,int y,BoundedRangeModel model){this(x,y,100,20,model);}
    public ArcSlider(int x,int y,int size,int handleSize,BoundedRangeModel model){
        super(x,y,size,size); 
        setHandleSize(handleSize);
        setModel(model);
        draggable=true;
        initStyle();
        setBackground(Color.LIGHT_GRAY);
    }    
    
    
    // BEAN PATTERNS
    
    public void setModel(BoundedRangeModel model){
        if(model!=null && !model.equals(this.model)){
            if(this.model!=null){this.model.removeChangeListener(this);}
            this.model=model;
            model.addChangeListener(this);
            initHandle();
        }
    }
    
    public void setHandleSize(double size){
        if(handleSize!=size){
            this.handleSize=size;
            fireStateChanged();
        }
    }
    
    
    // METHODS FOR THE HANDLE (SHAPE ON THE SLIDER WHICH CONTROLS THE UNDERLYING OBJECT)
    
    /** Adjusts the parameters for the line of available locations of the handle. Should be called whenever the component is resized or the model parameters change
     * (other than value). Should not be called when the handle simply moves.
     */
    protected void updateSnapCircle(){
        double theta=model.getValuePercent()*2*Math.PI;
        double radius=(getWidth()-handleSize)/2;
        ((SnapRule.PolarGrid)handle.getSnapRule()).setRing(
                getX()+radius,
                getY()+radius,
                radius,
                model.getNumSteps());
        handle.setBounds(
                (int)(getX()+radius+radius*Math.cos(theta)),
                (int)(getY()+radius-radius*Math.sin(theta)),
                (int)handleSize,(int)handleSize);
    }
    
    /** Sets the value of the underlying model. Should only be called if the handle position is changed. */
    void updateModelValue(){
        double theta=Math.atan2(getY()+getWidth()/2-handle.getY()-handleSize/2,handle.getX()+handleSize/2-getX()-getWidth()/2);
        if(theta<0){theta+=2*Math.PI;}
        model.setValuePercent(theta/(2*Math.PI));
    }
    
    /** Initializes the button which can be moved around the slider. The movement is constrained to a particular line, determined
     * by the size of the slider.
     */
    public void initHandle(){
        if(handle==null){handle=new VisualControl();}
        add(handle);
        if(handle.getSnapRule()==null){handle.setSnapRule(new SnapRule.PolarGrid());handle.getSnapRule().setForceSnap(true);}
        updateSnapCircle();
        handle.setDraggable(true); 
        addComponentListener(new SliderResizeListener());
        handle.addComponentListener(new HandleMoveListener());  
    }
    
    
    // PAINT METHOD
    
    @Override
    public void paintComponent(Graphics2D g) {
        super.paintComponent(g,0.5f);
        g.drawString(model.toString(),(float)handle.getX()+handle.getWidth()+5,(float)handle.getY()+handle.getHeight()-2);
    }
    
    
    // STYLE SETTINGS
    
    private void initStyle() {
        handle.setBackgroundShape(BoundedShape.Ellipse);
        setBackgroundShape(new BoundedWidthShape.Ring((0.5*getWidth()-handleSize)/(double)getWidth()));
    }
    private void updateStyle() {
        ((BoundedWidthShape.Ring)backgroundShape).setParameter((0.5*getWidth()-handleSize)/(double)getWidth());
    }
    
    // EVENT HANDLING
    
    /** Occurs when the underlying model is changed. */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==model){
            if(!handle.isAdjusting()){updateSnapCircle();}
            super.stateChanged(e);
        }
    }   
    
    /** Listener for when the control's handle is moved. */
    class HandleMoveListener implements ComponentListener{
        public void componentResized(ComponentEvent e) {}
        public void componentMoved(ComponentEvent e) {if(handle.isAdjusting()){updateModelValue();}}
        public void componentShown(ComponentEvent e) {}        
        public void componentHidden(ComponentEvent e) {} 
    }
    
    /** Listener for when the slider is resized. */    
    class SliderResizeListener implements ComponentListener{
        public void componentResized(ComponentEvent e) {updateSnapCircle();updateStyle();}
        public void componentMoved(ComponentEvent e) {updateSnapCircle();}
        public void componentShown(ComponentEvent e) {}        
        public void componentHidden(ComponentEvent e) {} 
    }
    
    
    // MOUSE EVENTS

    @Override
    public void clickAction(MouseEvent e) {
    }       

//    @Override
//    public boolean clicked(MouseEvent e) {return getBounds().contains(e.getPoint());}
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if(active==handle && model instanceof StepControlledRangeModel){
            adjusting=false;
            initialPoint=null;
            active=null;
            handle.setAdjusting(false);
            ((StepControlledRangeModel)model).reset();
        }else{
            super.mouseReleased(e);
        }
    }   
    
    
    // MODELS
    
    public IntegerRangeModel getSizeModel(){
        final IntegerRangeModel result=new IntegerRangeModel(getWidth(),10,500);
        result.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e) {
            setSize((int)result.getValue(),(int)result.getValue());
            updateSnapCircle();
            initStyle();
        }});
        return result;
    }
    public IntegerRangeModel getHandleSizeModel(){
        final IntegerRangeModel result=new IntegerRangeModel(getHeight(),10,200);
        result.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){
            setHandleSize(result.getValue());
            updateSnapCircle();
            initStyle();
        }});
        return result;
    }
}
