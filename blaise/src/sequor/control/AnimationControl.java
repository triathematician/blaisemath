/*
 * AnimationControl.java
 * Created on Mar 15, 2008
 */

package sequor.control;

import javax.swing.event.ChangeEvent;
import sequor.component.RangeTimer;

/**
 * AnimationControl is ...
 * @author Elisha Peterson
 */
public class AnimationControl extends ButtonBox {
    RangeTimer timer;

    public AnimationControl(double x,double y,RangeTimer t){        
        super(x,y,62,22);
        timer=t;
        timer.addChangeListener(this);
        timer.addActionListener(this);
        initButtons();
    }
    
    void initButtons(){
        double x=getX();
        double y=getY();
        add(new VisualButton("restart",this,new ShapeLibrary.PlayRestart(buttonSize,4)));
        add(new VisualButton("slower",this,new ShapeLibrary.PlaySlower(buttonSize,4)));
        add(new VisualButton("play",this,new ShapeLibrary.PlayTriangle(buttonSize,4)));
        add(new VisualButton("faster",this,new ShapeLibrary.PlayFaster(buttonSize,4)));        
        add(new ToggleButton("pause",this,new ShapeLibrary.PlayPause(buttonSize,4)));
        add(new VisualButton("stop",this,new ShapeLibrary.PlayStop(buttonSize,4)));
        super.adjustBounds();
        super.performLayout();
    }
    
    @Override
    public void stateChanged(ChangeEvent e){
        // only possible change coming from the timer which we care about is the "paused" state which turns on and off
        if(e.getSource().equals(timer)){
            getElement(4).setPressed(timer.isPaused());
        }
    }
}
