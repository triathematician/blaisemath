/*
 * AgentSettings.java
 * Created on Aug 28, 2007, 11:26:02 AM
 */

package agent;

import Model.DoubleRangeModel;
import Model.SpinnerDoubleEditor;
import behavior.Behavior;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Elisha Peterson
 * <br><br>
 * Initial settings for particular agents in pursuit-evasion games. Agents can assume
 * the values of the team, or they can specialize. The default is to assume the
 * team values. Hence, when a team is passed with the constructor, the corresponding
 * settings are simply borrowed from the team.
 */
public class AgentSettings implements ChangeListener {
    
    
// PROPERTIES    
    
    /** Default sensor range [in ft]. */
    private DoubleRangeModel sensorRange=new DoubleRangeModel(10,0,5000);
    /** Default communications range [in ft]. */
    private DoubleRangeModel commRange=new DoubleRangeModel(20,0,5000);
    /** Default speed [in ft/s]. */
    private DoubleRangeModel topSpeed=new DoubleRangeModel(5,0,50);
    /** Default stationary setting. */
    private boolean stationary=false;
    
    /** Default color. */
    private Color color=Color.BLUE;
    
    /** Default behavioral setting */
    private int defaultBehavior=Behavior.SEEK;
   
    
// CONSTRUCTORS    
    
    /** Default constructor */
    public AgentSettings(){
        pcs=new PropertyChangeSupport(this);
        sensorRange.addChangeListener(this);
        commRange.addChangeListener(this);
        topSpeed.addChangeListener(this);
    }
    /** Constructs and adds support for change listening 
     * @param ts the settings for the agent's team
     */
    public AgentSettings(TeamSettings ts){
        pcs=new PropertyChangeSupport(this);
        sensorRange=ts.sensorRange;
        commRange=ts.commRange;
        topSpeed=ts.topSpeed;
        stationary=ts.isStationary();
        color=ts.getColor();
        defaultBehavior=ts.getDefaultBehavior();
        pcs.addPropertyChangeListener(ts);
    }
    
    
// GETTERS AND SETTERS
    
    public double getSensorRange(){return sensorRange.getValue();}
    public double getCommRange(){return commRange.getValue();}
    public double getTopSpeed(){return topSpeed.getValue();}
    public boolean isStationary(){return stationary;}
    public Color getColor(){return color;}
    public int getDefaultBehavior(){return defaultBehavior;}
    
    public void setSensorRange(double newValue){sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){topSpeed.setValue(newValue);}
    public void setColor(Color newValue){
        if(newValue!=color){
            color=newValue;
            pcs.firePropertyChange("agentColor",null,newValue);
        }
    }
    public void setStationary(boolean newValue){
        if(newValue!=stationary){
            stationary=newValue;
            pcs.firePropertyChange("agentStationary",null,newValue);
        }
    }
    public void setDefaultBehavior(int newValue){
        if(!(newValue==defaultBehavior)){
            defaultBehavior=newValue;
            pcs.firePropertyChange("agentDefaultBehavior",null,newValue);
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
        if(e.getSource()==sensorRange){pcs.firePropertyChange("agentSensorRange",null,sensorRange.getValue());}
        if(e.getSource()==commRange){pcs.firePropertyChange("agentCommRange",null,commRange.getValue());}
        if(e.getSource()==topSpeed){pcs.firePropertyChange("agentTopSpeed",null,topSpeed.getValue());}
    }
    
    
// GUI ELEMENT GENERATING ROUTINES
// The routines below generate models for use with gui elements... these models also include step sizes for spinners */
    
    public SpinnerDoubleEditor getSensorRangeSpinnerModel(){return new SpinnerDoubleEditor(sensorRange,.5);}    
    public SpinnerDoubleEditor getCommRangeSpinnerModel(){return new SpinnerDoubleEditor(commRange,.5);}    
    public SpinnerDoubleEditor getTopSpeedSpinnerModel(){return new SpinnerDoubleEditor(topSpeed,.05);}    
}
