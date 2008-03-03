/*
 * RangeTimer.java
 * 
 * Created on Sep 14, 2007, 2:17:05 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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

/**
 * This class extends the timer class to permit firing a sequence of doubles OR a sequence
 * of integers, depending on user necessity. The events correspond to a discrete integer set,
 * while the doubles are translated based on a shift (shift/startStep occur at the same time),
 * and a step.
 * <br><br>
 * @author Elisha Peterson
 */
public class RangeTimer extends Timer implements ActionListener{
    int startStep=0;
    int currentStep=0;
    int pauseSteps=0;
    int stopStep=400;
    double step=1;
    double shift=0;
    boolean paused=false;
    boolean activated=false;
    
    public RangeTimer(){this(null);}
    public RangeTimer(double start,double stop,double step){
        this(null);
        setRange(start,stop,step);
    }
    public RangeTimer(ActionListener al){
        super(0,al);
        addActionListener(this);
    }
    
    public int getStartStep(){return startStep;}
    public int getCurrentStep(){return currentStep;}
    public int getStopStep(){return stopStep;}
    public int getPauseSteps(){return pauseSteps;}
    public double getStart(){return shift;}
    public double getCurrent(){
        if(currentStep>stopStep){return getStop();}
        return shift+step*(currentStep-startStep);
    }
    public double getStep(){return step;}
    public double getStop(){return shift+step*(stopStep-startStep);}

    public void setStart(double start){shift=start;}
    public void setStep(double step){this.step=step;}
    public void setStop(double stop){setNumSteps((int)((stop-shift)/step));}
    public void setRange(double start,double stop,double step){setStart(start);setStop(stop);setStep(step);}
    public void setStartStep(int ss){startStep=ss;}
    public void setStopStep(int ss){stopStep=ss;}
    public void setNumSteps(int n){stopStep=startStep+n;}
    public void setPauseSteps(int newValue){pauseSteps=newValue;}
   
    public boolean isNotStopped(){return isRunning()||paused;}
    
    @Override
    public void start(){
        activated=true;
        super.start();
    }
    @Override
    public void restart(){
        currentStep=startStep;
        start();
    }
    @Override
    public void stop(){
        paused=false;
        activated=false;
        super.stop();
        //fireActionPerformed(new ActionEvent(this,0,"stop"));
    }
    public void pause(){
        if(isRunning()){paused=!paused;}
    }
    public void iterate(){
        if(!paused){
            currentStep++;
            if(currentStep+pauseSteps>stopStep){
                stop();
                currentStep=startStep;
            }
        }
    }
    
    public void faster(){
        if(getDelay()<100){
            setDelay(getDelay()/2);
        }else{
            setDelay(getDelay()-50);
        }
    }
    public void slower(){
        if(getDelay()==0){
            setDelay(10);
        }else if(getDelay()<50){
            setDelay(2*getDelay());
        }else{
            setDelay(getDelay()+50);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){iterate();}

    public Vector<JMenuItem> getMenuItems() {
        final JMenu sub=new JMenu("Animation Settings");
        sub.add(new JCheckBoxMenuItem("Active",false)).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(isNotStopped()){stop();
                }else{start();}
            }            
        });
        sub.addSeparator();
        sub.add(new JMenuItem("Restart Animation |<-")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){if(activated){restart();}}
        });
        sub.add(new JMenuItem("Pause Animation ||")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){if(activated){pause();}}            
        });
        sub.add(new JMenuItem("Slower ||>")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){if(activated){slower();}}            
        });
        sub.add(new JMenuItem("Faster >>")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){if(activated){faster();}}     
        });
        Vector<JMenuItem> result=new Vector<JMenuItem>();
        result.add(sub);
        return result;
    }    
}
