/*
 * AnimationControl.java
 * Created on Mar 15, 2008
 */

package sequor.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import sequor.component.RangeTimer;

/**
 * <p>
 * AnimationControl is a collection of buttons that can be used to run the default
 * animation for a plot window.
 * </p>
 * @author Elisha Peterson
 */
public class AnimationControl extends ButtonBox {
    RangeTimer timer;

    public AnimationControl(int x,int y,RangeTimer t){this(x,y,20,t);}
    public AnimationControl(int x,int y,int buttonSize,RangeTimer t){this(x,y,buttonSize,LAYOUT_BOX,t);}
    public AnimationControl(int x, int y, RangeTimer timer,int orientation){
        this(x,y,timer);
        setOrientation(orientation);
    }
    public AnimationControl(int x,int y,int buttonSize,int layout,RangeTimer t){        
        super(x,y,80,22,layout);
        this.buttonSize=buttonSize;
        timer=t;
        timer.addChangeListener(this);
        timer.addActionListener(this);
        initButtons();
        stateChanged(new ChangeEvent(timer));
    }
    
    /** Changes the underlying timer. */
    public void setTimer(RangeTimer timer){
        if(this.timer != timer) {
            this.timer.removeChangeListener(this);
            this.timer.removeActionListener(this);
            for(int i = 0; i < 6; i++) {
                getElement(i).removeActionListener(this.timer);
                getElement(i).addActionListener(timer);
            }
            this.timer = timer;
            timer.addChangeListener(this);
            timer.addActionListener(this);
        }
    }
    
    /** Initializes buttons with shapes and the actions they perform. */
    void initButtons(){
        add(new VisualButton("restart",timer,BoundedShape.PLAY_RESTART));
        add(new VisualButton("slower",timer,BoundedShape.PLAY_SLOW));
        add(new VisualButton("play",timer,BoundedShape.PLAY_TRIANGLE));
        add(new VisualButton("faster",timer,BoundedShape.PLAY_FF));
        add(new ToggleButton("pause",timer,BoundedShape.PLAY_PAUSE));
        add(new VisualButton("stop",timer,BoundedShape.RECTANGLE));
        buttonStyle.setValue(STYLE_RBOX);
        adjustBounds();
        performLayout();
    }
    
    @Override
    public void stateChanged(ChangeEvent e){
        if(e.getSource().equals(timer)){
            getElement(2).setPressed(!timer.isStopped());
            getElement(4).setPressed(timer.isPaused());
            getElement(1).setForeground(timer.isStopped()?Color.LIGHT_GRAY:Color.BLACK);
            getElement(3).setForeground(timer.isPlaying()?Color.BLACK:Color.LIGHT_GRAY);
            getElement(4).setForeground(timer.isStopped()?Color.LIGHT_GRAY:Color.BLACK);
            getElement(5).setForeground(timer.isStopped()?Color.LIGHT_GRAY:Color.BLACK);
        }
        super.stateChanged(e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        getElement(2).setPressed(!timer.isStopped());
        getElement(4).setPressed(timer.isPaused());
    }
    
    
}
