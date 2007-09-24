/*
 * RangeTimer.java
 * 
 * Created on Sep 14, 2007, 2:17:05 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package specto;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class RangeTimer extends Timer {

    int currentStep=0;
    int pauseSteps=0;
    double start=0;
    double step=1;
    double stop=100;
    
    public RangeTimer(){super(100,null);}
    public RangeTimer(ActionListener al){super(100,al);}
    
    public double getCurrentValue(){double cv=currentStep*step+start;return (cv>stop)?stop:cv;}
    public int getCurrentStep(){return currentStep;}
    public int getPauseSteps(){return pauseSteps;}
    public double getStart(){return start;}
    public double getStep(){return step;}
    public double getStop(){return stop;}

    public void setStart(double start){this.start=start;}
    public void setStep(double step){this.step=step;}
    public void setStop(double stop){this.stop=stop;}
    public void setRange(double start,double stop,double step){this.start=start;this.step=step;this.stop=stop;}
    public void setPauseSteps(int newValue){pauseSteps=newValue;}
   
    public void restart(){currentStep=0;start();}
    public void pause(){if(isRunning()){stop();}else{start();}}
    public void iterate(){currentStep++;if((currentStep+pauseSteps)*step+start>stop){currentStep=0;}}
    
    protected void fireActionPerformed(ActionEvent e){iterate();super.fireActionPerformed(e);
    }
}
