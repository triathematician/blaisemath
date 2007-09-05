/*
 * TeamOptions.java
 * Created on Aug 30, 2007, 12:33:47 PM
 */

package agent;

import Model.DoubleRangeModel;
import Model.IntegerRangeModel;
import Model.SpinnerDoubleEditor;
import Model.SpinnerIntegerEditor;
import behavior.Behavior;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pursuitevasion.SimulationSettings;
import task.Goal;
import behavior.Tasking;

/**
 * @author Elisha Peterson
 * <br><br>
 * Team-wide initial settings for pursuit-evasion games. Many of these are default
 * settings for the agents, which can specialize by adjusting some of these.
 */
public class TeamSettings implements ChangeListener,PropertyChangeListener {
    
// PROPERTIES    
    
    /** Team size */
    private IntegerRangeModel size=new IntegerRangeModel(3,1,100);
    /** Starting positions to use */
    private IntegerRangeModel start=new IntegerRangeModel(START_RANDOM,0,4);
    
    /** Default sensor range [in ft]. */
    protected DoubleRangeModel sensorRange=new DoubleRangeModel(10,0,5000);
    /** Default communications range [in ft]. */
    protected DoubleRangeModel commRange=new DoubleRangeModel(20,0,5000);
    /** Default speed [in ft/s]. */
    protected DoubleRangeModel topSpeed=new DoubleRangeModel(5,0,50);
    /** Default stationary setting. */
    protected boolean stationary=false;
    
    /** Default color. */
    protected Color color=Color.BLUE;
    
    /** The team's goal */
    Goal goal=new Goal();
    /** The team's tasking algorithm default */
    int tasking=Tasking.AUTO_CLOSEST;
    /** Default behavioral setting */
    private int defaultBehavior=Behavior.SEEK;

    
// CONSTANTS
    
    /** Start all at zero */
    public static final int START_ZERO=0;
    /** Start all at random */
    public static final int START_RANDOM=1;
    /** Start along a line */
    public static final int START_LINE=2;
    /** Start along a circle */
    public static final int START_CIRCLE=3;
    /** Start along an arc */
    public static final int START_ARC=4;
    
    
// CONSTRUCTORS    
    
    /** Default constructor */
    public TeamSettings(){
        super();
        pcs=new PropertyChangeSupport(this);
        size.addChangeListener(this);
        start.addChangeListener(this);
        sensorRange.addChangeListener(this);
        commRange.addChangeListener(this);
        topSpeed.addChangeListener(this);
    }
    /** Constructs and adds support for change listening 
     * @param g     the team's goal
     * @param t     the team's tasking
     * @param b     the team's default agent behavior
     * @param s     whether the team is stationary
     * @param c     the team's color
     * @param ss    pointer to the governing simulation settings */
    public TeamSettings(Goal g,int t,int b,boolean s,Color c,SimulationSettings ss){
        this();
        pcs=new PropertyChangeSupport(this);
        setGoal(g);
        setTasking(t);
        setDefaultBehavior(b);
        setStationary(s);
        setColor(c);
        addPropertyChangeListener(ss);
    }
    
    
// GETTERS & SETTERS
    
    public int getSize(){return size.getValue();}
    public int getStart(){return start.getValue();}
    public double getSensorRange(){return sensorRange.getValue();}
    public double getCommRange(){return commRange.getValue();}
    public double getTopSpeed(){return topSpeed.getValue();}
    public boolean isStationary(){return stationary;}
    public Color getColor(){return color;}
    public Goal getGoal(){return goal;}
    public int getTasking(){return tasking;}
    public int getDefaultBehavior(){return defaultBehavior;}
    
    public void setSize(int newValue){size.setValue(newValue);}
    public void setStart(int newValue){start.setValue(newValue);}
    public void setSensorRange(double newValue){sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){topSpeed.setValue(newValue);}
    public void setColor(Color newValue){
        if(newValue!=color){
            color=newValue;
            pcs.firePropertyChange("teamColor",null,newValue);
        }
    }
    public void setStationary(boolean newValue){
        if(newValue!=stationary){
            stationary=newValue;
            pcs.firePropertyChange("teamStationary",null,newValue);
        }
    }    
    public void setGoal(Goal newValue){
        if(!newValue.equals(goal)){
            goal=newValue;
            pcs.firePropertyChange("teamGoal",null,newValue);
        }
    }
    public void setTasking(int newValue){
        if(!(newValue==tasking)){
            tasking=newValue;
            pcs.firePropertyChange("teamTasking",null,newValue);
        }
    }
    public void setDefaultBehavior(int newValue){
        if(!(newValue==defaultBehavior)){
            defaultBehavior=newValue;
            pcs.firePropertyChange("teamDefaultBehavior",null,newValue);
        }
    }
    
    
// EVENT HANDLING ROUTINES
    
    /** Utility class for handling bean property changes. */
    protected PropertyChangeSupport pcs;
    /**Add a property change listener for a specific property.
     * @param l the listener */
    public void addPropertyChangeListener(PropertyChangeListener l){pcs.addPropertyChangeListener(l);}
    /**Remove a property change listener for a specific property.
     * @param l the listener */
    public void removePropertyChangeListener(PropertyChangeListener l){pcs.removePropertyChangeListener(l);}    
    /** Handles change events by firing change events */
    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==size){pcs.firePropertyChange("teamSize",null,size.getValue());}
        if(e.getSource()==start){pcs.firePropertyChange("teamStart",null,start.getValue());}
        if(e.getSource()==sensorRange){pcs.firePropertyChange("teamSensorRange",null,sensorRange.getValue());}
        if(e.getSource()==commRange){pcs.firePropertyChange("teamCommRange",null,commRange.getValue());}
        if(e.getSource()==topSpeed){pcs.firePropertyChange("teamTopSpeed",null,topSpeed.getValue());}
    }
    /** Passes on property changes */
    public void propertyChange(PropertyChangeEvent evt){pcs.firePropertyChange(evt);}
    
    
// GUI ELEMENT GENERATING ROUTINES
// The routines below generate models for use with gui elements... these models also include step sizes for spinners */
    
    public SpinnerIntegerEditor getSizeSpinnerModel(){return new SpinnerIntegerEditor(size,1);}
    public SpinnerIntegerEditor getStartSpinnerModel(){return new SpinnerIntegerEditor(start,1);}
    public SpinnerDoubleEditor getSensorRangeSpinnerModel(){return new SpinnerDoubleEditor(sensorRange,.5);}    
    public SpinnerDoubleEditor getCommRangeSpinnerModel(){return new SpinnerDoubleEditor(commRange,.5);}    
    public SpinnerDoubleEditor getTopSpeedSpinnerModel(){return new SpinnerDoubleEditor(topSpeed,.05);}    
}
