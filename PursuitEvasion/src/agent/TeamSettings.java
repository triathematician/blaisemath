/*
 * TeamOptions.java
 * Created on Aug 30, 2007, 12:33:47 PM
 */

package agent;

import Model.ComboBoxRangeModel;
import Model.IntegerRangeModel;
import Model.Settings;
import Model.SpringUtilities;
import behavior.Behavior;
import java.awt.Color;
import javax.swing.event.ChangeEvent;
import pursuitevasion.SimulationSettings;
import task.Goal;
import behavior.Tasking;
import java.awt.Component;
import javax.swing.JPanel;

/**
 * @author Elisha Peterson
 * <br><br>
 * Team-wide initial settings for pursuit-evasion games. Many of these are default
 * settings for the agents, which can specialize by adjusting some of these.
 */
public class TeamSettings extends Settings {
    
// PROPERTIES    
    
    /** Team size */
    private IntegerRangeModel size=new IntegerRangeModel(3,1,100);
    /** Starting positions to use */
    private ComboBoxRangeModel start=new ComboBoxRangeModel(START_STRINGS,START_RANDOM,0,4);
    /** The team's tasking algorithm default */
    private ComboBoxRangeModel tasking=new ComboBoxRangeModel(Tasking.TASKING_STRINGS,Tasking.AUTO_CLOSEST,Tasking.FIRST,Tasking.LAST);
    /** The team's goal */
    private Goal goal=new Goal();
    /** Options that carry over to agents */
    private AgentSettings as=new AgentSettings();    

    
// CONSTANTS
    
    public static final int START_ZERO=0;
    public static final int START_RANDOM=1;
    public static final int START_LINE=2;
    public static final int START_CIRCLE=3;
    public static final int START_ARC=4;
    public static final String[] START_STRINGS={"All at Zero","Random Positions","Along a Line","Around a Circle","Along a Circular Arc"};
    
    
// CONSTRUCTORS & INITIALIZERS   
    
    public TeamSettings(){this(1,START_ZERO,new Goal(),Tasking.NO_TASKING,Behavior.FLEE,Color.LIGHT_GRAY,new SimulationSettings());}
    public TeamSettings(int n,int st,Goal g,int t,int b,Color c,SimulationSettings ss){
        addProperty("# Agents",size,Settings.EDIT_INTEGER);
        addProperty("Starting Loc",start,Settings.EDIT_COMBO);
        addProperty("Tasking",tasking,Settings.EDIT_COMBO);
        setSize(n);setStart(st);setGoal(g);setTasking(t);as.setBehavior(b);as.setColor(c);
        as.setString("Team");
        initEventListening();
    }
    
    
// GETTERS & SETTERS
    
    public int getSize(){return size.getValue();}
    public int getStart(){return start.getValue();}
    public int getTasking(){return tasking.getValue();}
    public Goal getGoal(){return goal;}
    public AgentSettings getSubSettings(){return as;}
    public String toString(){return as.toString();}
    
    public void setSize(int newValue){size.setValue(newValue);}
    public void setStart(int newValue){start.setValue(newValue);}
    public void setTasking(int newValue){tasking.setValue(newValue);}
    public void setGoal(Goal newValue){if(!newValue.equals(goal)){goal=newValue;}}
    public void setSubSettings(AgentSettings newValue){if(!newValue.equals(as)){as=newValue;}}
    

// METHODS TO GENERATE GUI ELEMENTS
    
    /** Overrides super method to add in goal and subsettings as well */
    public JPanel getPanel(){
        JPanel result=super.getPanel();
        for(Component c:goal.getPanel().getComponents()){result.add(c);}
        for(Component c:as.getPanel().getComponents()){result.add(c);}
        SpringUtilities.makeCompactGrid(result,result.getComponentCount()/2,2,5,5,5,5);
        return result;
    }
}
