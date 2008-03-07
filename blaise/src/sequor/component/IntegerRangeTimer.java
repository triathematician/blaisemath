/*
 * IntegerRangeTimer.java
 * Created on Sep 14, 2007, 2:17:05 PM
 */

package sequor.component;

import specto.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.Timer;
import sequor.model.IntegerRangeModel;

/**
 * This class extends the timer to run off of a specified IntegerRangeModel... which contains the start/stop and step values. Essentially, this fires
 * events for each value in a particular range of Integer values. All that needs be done is pass this method a IntegerRangeModel. When a timer event
 * is fired, the IntegerRangeModel value is changed according to the particular settings of this timer. So to get the animation to work a class needs
 * to create a IntegerRangeModel, listen to changes in the IntegerRangeModel, then create a IntegerRangeTimer and start it.
 * 
 * @author Elisha Peterson
 */
public class IntegerRangeTimer extends Timer{
    IntegerRangeModel model;
    
    /** Whether the timer is currently paused. A pause stops the firing of action events, but does not reset the value of the model. */
    boolean paused=false;
    /** Whether the timer loops (goes several times). */
    boolean loops=true;
    
    public IntegerRangeTimer(int start,int stop,int step){this(new IntegerRangeModel(start,start,stop,step));}
    public IntegerRangeTimer(IntegerRangeModel model){        
        super(0,null);
        this.model=model;
        setRepeats(true);
    }

    public IntegerRangeModel getModel(){return model;}
    public void setModel(IntegerRangeModel drm){model=drm;}
    public void setLooping(boolean loops){this.loops=loops;}
    public boolean isLooping(){return loops;}
    public void setStep(int newStep){model.setStep(newStep);}
    public void setNumSteps(int n){model.setNumSteps(n, true);}
    
    public void pause(){if(isRunning()){paused=!paused;}}

    @Override
    public void start() {
        model.setValue(model.getMinimum());
        super.fireActionPerformed(new ActionEvent(this,0,"start"));
        super.start();
    }
    @Override
    public void stop() {
        super.fireActionPerformed(new ActionEvent(this,0,"stop"));
        super.stop();
    }

    @Override
    protected void fireActionPerformed(ActionEvent e) {
        // do nothing if the timer is not active
        if(paused||!isRunning()){return;}
        // first increment the value of the model
        if(model.increment(loops)&&!loops){stop();}
        // then fire the actions
        super.fireActionPerformed(e);
    }    
    
    private Integer initialStepSize;
    
    public void faster(){        
        if(getDelay()==0){ // if delay is bottomed out, change the stepsize of the model down to at fewest ten steps
            if(model.getNumSteps()>10){
                if(initialStepSize==null){initialStepSize=model.getStep();}
                if(model.getStep()==0){model.setStep(1);
                }else{model.setStep(model.getStep()*2);}
            }
        } else if(getDelay()<100){ // delay is small, divide by two each time
            setDelay(getDelay()/2);
        } else{ // if delay is large, increment by 50 clicks
            setDelay(getDelay()-50);
        }
    }
    public void slower(){
        if(getDelay()==0){ // if delay is bottomed out, create a default delay value.
            if(initialStepSize==null){
                setDelay(10);
            }else if(model.getStep()>2*initialStepSize){
                model.setStep(model.getStep()/2);
            }else if(model.getStep()>initialStepSize){
                model.setStep(initialStepSize);
            }else{
                setDelay(10);
            }
        }else if(getDelay()<50){ // if delay is small, Integer it
            setDelay(2*getDelay());
        }else{ // if delay is large, just increase by 50
            setDelay(getDelay()+50);
        }
    }

    public Vector<JMenuItem> getMenuItems() {
        final JMenu sub=new JMenu("Animation Settings");
        sub.add(new JCheckBoxMenuItem("Active",isRunning())).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(isRunning()){stop();}else{start();}
            }            
        });
        sub.addSeparator();
        sub.add(new JMenuItem("Restart Animation |<-")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){restart();}
        });
        sub.add(new JMenuItem("Pause Animation ||")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){if(isRunning()){pause();}}      
        });
        sub.add(new JMenuItem("Slower ||>")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){if(isRunning()){slower();}}            
        });
        sub.add(new JMenuItem("Faster >>")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){if(isRunning()){faster();}}     
        });
        Vector<JMenuItem> result=new Vector<JMenuItem>();
        result.add(sub);
        return result;
    }    
}
