/*
 * RangeTimer.java
 * Created on Sep 14, 2007, 2:17:05 PM
 * Completely Redesigned on Mar 16, 2008
 */

package sequor.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;
import sequor.model.BoundedRangeModel;
import sequor.model.StringRangeModel;
import specto.*;

/**
 * This class extends the timer class to permit firing a sequence of doubles OR a sequence
 * of integers, depending on user necessity. Timer's have three different states: playing, paused, or stopped; and
 * several different potential speeds.
 * 
 * @author Elisha Peterson
 */
public class RangeTimer<N extends Number> extends StringRangeModel implements ActionListener {
    /** Determines what values the timer contains. */
    BoundedRangeModel<N> rangeValues;
 
    public static final int PLAYING=0;
    public static final int PAUSED=1;
    public static final int STOPPED=2;    
    public static final String[] statusStrings={"Play","Pause","Stop"};
    
    /** Stores the actual timer. */
    Timer timer;
    /** Stores whether or not the timer loops. */
    boolean looping;
    /** Stores the speed of the timer. */
    int speed;
    /** Stores the initial step included in "rangeValues" */
    N initialStepSize;
    
    // CONSTRUCTOR
    
    public RangeTimer(){}
    public RangeTimer(BoundedRangeModel<N> rangeValues){
        super(statusStrings,STOPPED,0,2);
        speed=0;
        this.rangeValues=rangeValues;
        initialStepSize=(N)rangeValues.getStep();
        looping=true;
        timer=new Timer(10,this);
    }
    
    
    // BEAN PATTERNS
    
    public boolean isPlaying(){return getValue()==PLAYING;}
    public boolean isPaused(){return getValue()==PAUSED;}
    public boolean isStopped(){return getValue()==STOPPED;}
    public boolean isLooping(){return looping;}
    public int getStatus(){return getValue();}
    public int getSpeed(){return speed;}
    public Number getFirstValue(){return rangeValues.getMinimum();}
    public int getFirstIntValue(){
        return (rangeValues.getMinimum() instanceof Integer)?
            (Integer)rangeValues.getMinimum():
            (int)Math.floor((Double)rangeValues.getMinimum());
    }
    public Number getCurrentValue(){return rangeValues.getValue();}
    public int getCurrentIntValue(){
        return (rangeValues.getValue() instanceof Integer)?
            (Integer)rangeValues.getValue():
            (int)Math.floor((Double)rangeValues.getValue());
    }
    public Number getLastValue(){return rangeValues.getMaximum();}
    public int getLastIntValue(){
        return (rangeValues.getMaximum() instanceof Integer)?
            (Integer)rangeValues.getMaximum():
            (int)Math.floor((Double)rangeValues.getMaximum());
    }
    
    public void setLooping(boolean looping){this.looping=looping;}

    
    // TIMER METHODS
    
    /** Starts sending out timer events; turns off pause if on. */
    void start(){play();}
    void play(){
        if(getStatus()!=PLAYING){
            if(getStatus()==STOPPED){fireActionPerformed("restart");}
            setValue(PLAYING);
            timer.start();
            fireStateChanged();
        }
    }
    /** Pauses the timer if running; does nothing otherwise. */
    void pause(){
        if(getStatus()==PLAYING){
            setValue(PAUSED);
            timer.start();
            fireStateChanged();
        }
    }
    /** Pauses the timer if running; starts playing if paused. */
    void togglePause(){
        if(getStatus()==PLAYING){
            pause();
        }else if(getStatus()==PAUSED){
            play();
        }
    }
    /** Stops the timer completely. */
    void stop(){
        if(getStatus()!=STOPPED){
            setValue(STOPPED);
            timer.stop();
            fireStateChanged();
        }
    }
    /** Resets the animation to the beginning; starts if stopped or paused. */
    void restart(){
        stop();
        rangeValues.setValue(rangeValues.getMinimum());
        start();
    }
    /** Makes the animation run slower. */
    void slower(){
        speed--;
        if(timer.getDelay()==0){ // if delay is bottomed out, create a default delay value.
            if(initialStepSize==null){
                timer.setDelay(10);
            }else if(rangeValues.getStep().doubleValue()>2*initialStepSize.doubleValue()){
                rangeValues.halfStep();
            }else if(rangeValues.getStep().doubleValue()>initialStepSize.doubleValue()){
                rangeValues.setStep(initialStepSize);
            }else{
                timer.setDelay(10);
            }
        }else if(timer.getDelay()<50){ // if delay is small, Integer it
            timer.setDelay(2*timer.getDelay());
        }else{ // if delay is large, just increase by 50
            timer.setDelay(timer.getDelay()+50);
        }
    }
    /** Makes the animation run faster. */
    void faster(){     
        speed++;
        if(timer.getDelay()==0){ // if delay is bottomed out, change the stepsize of the model down to at fewest ten steps
            if(rangeValues.getNumSteps()>10){
                if(initialStepSize==null){
                    initialStepSize=rangeValues.getStep();
                }
                rangeValues.doubleStep();
            }else{
                speed--;
            }
        } else if(timer.getDelay()<100){ // delay is small, divide by two each time
            timer.setDelay(timer.getDelay()/2);
        } else{ // if delay is large, increment by 50 clicks
            timer.setDelay(timer.getDelay()-50);
        }
    }
   
    
    
    // EVENT HANDLING
    
    /** Actions fired to the timer; e.g. menu items selected or toggle buttons pressed. Significantly,
     * may also represent actions from the underlying timer.
     */
    public void actionPerformed(ActionEvent e) {
        if(e==null){return;}
        // an actual timer event
        if(e.getSource().equals(timer)){
            if(isPlaying()){
                // increment value unless set to not loop
                if(rangeValues.increment(looping)&&!looping){stop();}
                System.out.println("val: "+rangeValues.getValue());
                fireActionPerformed(e);
                return;
            }
        }else{
            String s=e.getActionCommand();
            if(s.equals("play")||s.equals("start")){play();
            }else if(s.equals("pause")){
                togglePause();
            }else if(s.equals("stop")){stop();
            }else if(s.equals("restart")){restart();
            }else if(s.equals("slower")){
                if(isPlaying()){slower();}else{ 
                    if(rangeValues.increment(looping)&&!looping){stop();}
                }
                fireActionPerformed(e);
            }else if(s.equals("faster")){if(isPlaying()){faster();}}
        }
    }
    
    
    // ACTION EVENT HANDLING
     
    protected ActionEvent actionEvent=null;
    protected EventListenerList actionListenerList=new EventListenerList();    
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class,l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class,l);}
    protected void fireActionPerformed(String s){fireActionPerformed(new ActionEvent(this,0,s));}
    protected void fireActionPerformed(ActionEvent e){
        if(e!=null){actionEvent=e;}
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ActionListener.class){
                if(actionEvent==null){actionEvent=new ActionEvent(this,0,"timer");}
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }    
}
