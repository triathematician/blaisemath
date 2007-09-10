/*
 * AgentSettings.java
 * Created on Aug 28, 2007, 11:26:02 AM
 */

package agent;

import Model.ColorModel;
import Model.ComboBoxRangeModel;
import Model.DoubleRangeModel;
import Model.IntegerRangeModel;
import Model.Settings;
import Model.SpinnerDoubleEditor;
import behavior.Behavior;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Initial settings for particular agents in pursuit-evasion games. Agents can assume
 * the values of the team, or they can specialize. The default is to assume the
 * team values. Hence, when a team is passed with the constructor, the corresponding
 * settings are simply borrowed from the team.
 * <br><br>
 * @author Elisha Peterson
 */
public class AgentSettings extends Settings {
    
    
// PROPERTIES    
    
    /** Default sensor range [in ft]. */
    private DoubleRangeModel sensorRange=new DoubleRangeModel(10,0,5000);
    /** Default communications range [in ft]. */
    private DoubleRangeModel commRange=new DoubleRangeModel(20,0,5000);
    /** Default speed [in ft/s]. */
    private DoubleRangeModel topSpeed=new DoubleRangeModel(5,0,50,.05);
    /** Default behavioral setting */
    private ComboBoxRangeModel behavior=Behavior.getComboBoxModel();
    
    /** Default color. */
    private ColorModel color=new ColorModel(Color.BLUE);
    /** Display string */
    private String s="Agent";
   
    
// CONSTRUCTORS & INITIALIZERS
    
    public AgentSettings(){
        addProperty("Speed",topSpeed,Settings.EDIT_DOUBLE);
        addProperty("Sensor Range",sensorRange,Settings.EDIT_DOUBLE);
        addProperty("Comm Range",commRange,Settings.EDIT_DOUBLE);
        addProperty("Behavior",behavior,Settings.EDIT_COMBO);
        addProperty("Color",color,Settings.EDIT_COLOR);
        initEventListening();
    }
    public AgentSettings(TeamSettings ts){this();copyFrom(ts.getSubSettings());}
    public void copyFrom(AgentSettings as){setSensorRange(as.getSensorRange());setCommRange(as.getCommRange());setTopSpeed(as.getTopSpeed());setColor(as.getColor());setBehavior(as.getBehavior());}
    
    
// GETTERS AND SETTERS
    
    public double getSensorRange(){return sensorRange.getValue();}
    public double getCommRange(){return commRange.getValue();}
    public double getTopSpeed(){return topSpeed.getValue();}
    public int getBehavior(){return behavior.getValue();}
    public Color getColor(){return color.getValue();}
    public String toString(){return s;}
    
    public void setSensorRange(double newValue){sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){topSpeed.setValue(newValue);}
    public void setBehavior(int newValue){behavior.setValue(newValue);}
    public void setColor(Color newValue){color.setValue(newValue);}
    public void setString(String newValue){s=newValue;}
}
