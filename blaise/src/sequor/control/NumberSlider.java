/**
 * NumberSlider.java
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
import sequor.model.StringRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.StepControlledRangeModel;

/**
 * <p>
 * Contains a slider interfacing with an underlying RangeModel.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class NumberSlider extends VisualControlGroup {
    /** Represents the button which can be moved around. */
    VisualControl handle;
    /** Represents the underlying model. */
    BoundedRangeModel model;
    
    
    // CONSTRUCTORS
    
    public NumberSlider(BoundedRangeModel model){this(0,0,model);}
    public NumberSlider(Point position,BoundedRangeModel model){this(position.x,position.y,model);}
    public NumberSlider(int x,int y,BoundedRangeModel model){this(x,y,100,15,STYLE_CIRCLE,model);}
    public NumberSlider(int x,int y,int width,int height,int style,BoundedRangeModel model){
        super(x,y,width,height); 
        setModel(model);
        draggable=true;
        initStyle();
        this.style.setValue(style);
        setBackground(Color.WHITE);
    }    
    
    
    // BEAN PATTERNS
    
    /** Gets model for the slider. */
    public BoundedRangeModel getModel() { return this.model; }
    /** Sets model for the slider. */
    public void setModel(BoundedRangeModel model){
        if(model!=null && !model.equals(this.model)){
            if(this.model!=null){this.model.removeChangeListener(this);}
            this.model=model;
            model.addChangeListener(this);
            initHandle();
        }
    }
    
    public int getLength(){return isHorizontal()?getWidth():getHeight();}
    public int getGirth(){return isHorizontal()?getHeight():getWidth();}
    public boolean isHorizontal(){return getWidth()>getHeight();}
    
    
    // METHODS FOR THE HANDLE (SHAPE ON THE SLIDER WHICH CONTROLS THE UNDERLYING OBJECT)
    
    /** Adjusts the parameters for the line of available locations of the handle. Should be called whenever the component is resized or the model parameters change
     * (other than value). Should not be called when the handle simply moves.
     */
    protected void updateSnapLine(){
        if(isHorizontal()){
            ((SnapRule.Grid)handle.getSnapRule()).setHorizontal(getX(),getY(),getWidth()-getHeight(),model.getNumSteps());
        }else{
            ((SnapRule.Grid)handle.getSnapRule()).setVertical(getX(),getY(),getHeight()-getWidth(),model.getNumSteps());
        }
    }
    
    /** Sets the proper location of the handle. */
    public void updateHandleLocation(){
        double percent=model.getValuePercent();
        if(isHorizontal()){
            handle.setBounds((int)(getX()+percent*(getWidth()-getHeight())),getY(),getGirth(),getGirth());
        }else{
            handle.setBounds(getX(),(int)(getY()+percent*(getHeight()-getWidth())),getGirth(),getGirth());
        }
    }
    
    /** Sets the value of the underlying model. Should only be called if the handle position is changed. */
    void updateModelValue(){
        if(isHorizontal()){
            model.setValuePercent((handle.getX()-getX())/(double)Math.abs(getWidth()-getHeight()));
        }else{
            model.setValuePercent((handle.getY()-getY())/(double)Math.abs(getWidth()-getHeight()));
        }
    }
    
    /** Initializes the button which can be moved around the slider. The movement is constrained to a particular line, determined
     * by the size of the slider.
     */
    public void initHandle(){
        if(handle==null){handle=new VisualControl();}
        add(handle);
        if(handle.getSnapRule()==null){
            handle.setSnapRule(new SnapRule.Grid());
            handle.getSnapRule().setForceSnap(true);
        }
        updateSnapLine();
        handle.setDraggable(true);    
        updateHandleLocation();
        addComponentListener(new SliderResizeListener());
        handle.addComponentListener(new HandleMoveListener());  
    }
    
    
    // PAINT METHOD
    
    @Override
    public void paintComponent(Graphics2D g) {  
        if(adjusting){updateHandleLocation();}
        super.paintComponent(g,0.5f);
        g.drawString((getName()==null?"":getName()+": ")+model.toString(),(float)handle.getX()+handle.getWidth()+5,(float)handle.getY()+handle.getHeight()-2);
    }
    
    
    // STYLE SETTINGS
    
    StringRangeModel style;
    public static final int STYLE_LINE=0;
    public static final int STYLE_CIRCLE=1;
    public static final int STYLE_BOX=2;
    public static final int STYLE_RBOX=3;
    public static final int STYLE_BOWTIE=4;
    public static final int STYLE_DIAMOND=5;
    public static final int STYLE_DOTS=6;
    
    public static String[] styleStrings={"Line","Circular","Rectangular","Rounded","Bowtie","Diamond","Dots"};
    public StringRangeModel getStyle(){return style;}

    private void initStyle() {
        style=new StringRangeModel(styleStrings,STYLE_CIRCLE,0,6);
        updateStyle();
        style.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){updateStyle();fireStateChanged();}});
    }
    
    public void updateStyle(){
        switch(style.getValue()){
            case STYLE_LINE:
                handle.setBackgroundShape(BoundedShape.ELLIPSE); 
                setBackgroundShape(isHorizontal()?BoundedShape.LINE_HORIZONTAL:BoundedShape.LINE_VERTICAL);
                break;
            case STYLE_BOX:
                handle.setBackgroundShape(BoundedShape.RECTANGLE);
                setBackgroundShape(BoundedShape.RECTANGLE);
                break;
            case STYLE_RBOX:
                handle.setBackgroundShape(new BoundedWidthShape.RoundRectangle(8));
                setBackgroundShape(new BoundedWidthShape.RoundRectangle(8));
                break;
            case STYLE_CIRCLE:
                handle.setBackgroundShape(BoundedShape.ELLIPSE);
                setBackgroundShape(new BoundedWidthShape.RoundRectangle(handle.getWidth()));
                break;
            case STYLE_DIAMOND:
                handle.setBackgroundShape(BoundedShape.ELLIPSE);
                setBackgroundShape(BoundedShape.DIAMOND);
                break;
            case STYLE_BOWTIE:
                handle.setBackgroundShape(BoundedShape.ELLIPSE); 
                setBackgroundShape(BoundedShape.BOWTIE);
                break;
            case STYLE_DOTS:
            default:
                handle.setBackgroundShape(BoundedShape.ELLIPSE); 
                setBackgroundShape(new BoundedWidthShape.DotDotDot(1));
                break;
        }
    }    
    
    // EVENT HANDLING
    
    /** Occurs when the underlying model is changed. */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==model){
            if(!handle.isAdjusting()){
                updateSnapLine();
                updateHandleLocation();
            }
            super.stateChanged(e);
        }else if(e.getSource()==style){
            fireStateChanged();
        }
    }   
    
    /** Listener for when the control's handle is moved. */
    class HandleMoveListener implements ComponentListener{
        public void componentResized(ComponentEvent e) {}
        public void componentMoved(ComponentEvent e) {
            if(handle.isAdjusting()){
                updateModelValue();
            }
        }
        public void componentShown(ComponentEvent e) {}        
        public void componentHidden(ComponentEvent e) {} 
    }
    
    /** Listener for when the slider is resized. */    
    class SliderResizeListener implements ComponentListener{
        public void componentResized(ComponentEvent e) {
            updateSnapLine();
            updateHandleLocation();
        }
        public void componentMoved(ComponentEvent e) {
            updateSnapLine();
            updateHandleLocation();
        }
        public void componentShown(ComponentEvent e) {}        
        public void componentHidden(ComponentEvent e) {} 
    }
    
    
    // MOUSE EVENTS

    @Override
    public void clickAction(MouseEvent e) {
        if(e!=null){
            style.cycle();
        }
    }       

    @Override
    public boolean clicked(MouseEvent e) {
        return getBounds().contains(e.getPoint());
    }
        
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
    
    public IntegerRangeModel getWidthModel(){
        final IntegerRangeModel result=new IntegerRangeModel(getWidth(),10,500);
        result.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e) {setSize((int)result.getValue(),getHeight());}});
        return result;
    }
    public IntegerRangeModel getHeightModel(){
        final IntegerRangeModel result=new IntegerRangeModel(getHeight(),10,500);
        result.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e) {setSize(getWidth(),result.getValue());}});
        return result;
    }
}
