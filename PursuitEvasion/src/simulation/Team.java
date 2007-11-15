/*
 * Team.java
 * Created on Aug 28, 2007, 10:26:33 AM
 */

// TODO Add team select box to goal settings panel

package simulation;

import sequor.model.DoubleRangeModel;
import goal.Goal;
import behavior.*;
import utility.DistanceTable;
import goal.TaskGenerator;
import specto.PlotPanel;
import specto.dynamicplottable.Point2D;
import specto.visometry.Euclidean2;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import sequor.component.Settings;
import sequor.model.ColorModel;
import sequor.model.ComboBoxRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.ParametricModel;
import scio.coordinate.R2;
import scio.random.PRandom;

/**
 * @author Elisha Peterson<br><br>
 *
 * Handles routines dealing with an entire team of players.
 * Includes cooperative/control algorithms, broadcast methods for instructing team's agents, etc.
 */
public class Team extends Vector<Agent> implements TaskGenerator,ActionListener,PropertyChangeListener {
    
    
    // PROPERTIES
    
    /** The team's settings */
    public TeamSettings tes;
    /** The team's goals */
    private Vector<Goal> goals=new Vector<Goal>();
    /** Active agents */
    private Vector<Agent> activeAgents=new Vector<Agent>();
    
    /** Whether to forward action events */
    private boolean editing=false;
    /** Result of the simulation if it should be recorded. Defaults to time at which goal is reached. */
    private double value;
    
    
    
    // CONSTRUCTORS
    public Team(){super();tes=new TeamSettings();}
    public Team(TeamSettings tes){
        super();
        value=Double.MAX_VALUE;
        this.tes=tes;
        for(int i=0;i<getSize();i++){add(new Agent(this));}
        initStartingLocations();
        for(Agent a:this){a.addActionListener(this);}
        activeAgents.addAll(this);
    }
    public Team(Team team){
        value=Double.MAX_VALUE;
        tes=team.tes;
        for(Agent a:team){this.add(a);}
        activeAgents.addAll(this);
    }
    
    public Team(int size,int start,int tasking,int behavior,Color color){
        super();
        editing=true;
        value=Double.MAX_VALUE;
        tes=new TeamSettings();
        setSize(size);
        setStart(start);
        setBehavior(behavior);
        setColor(color);
        this.clear();
        for(int i=0;i<getSize();i++){add(new Agent(this));}
        initStartingLocations();
        for(Agent a:this){a.addActionListener(this);} 
        editing=false;
        activeAgents.addAll(this);
    }
    
    
    // METHODS: HELP FOR INITIALIZAITON
    
    /** Adds a goal. */        
    public void addGoal(double weight,Team target,int type,double threshhold){
        goals.add(new Goal(weight,this,target,type,threshhold));
    }
        
    /** Resets all agents to their initial positions; clears all paths */
    public void reset(){
        value=Double.MAX_VALUE;
        for(Agent a:activeAgents){
            a.reset();
        }
        for(Goal g:goals){
            g.reset();
        }
    }
    
    /** Changes the number of agents, resets starting locations. */
    public void initAgentNumber(){
        editing=true;
        while(size()>getSize()){get(size()-1).removeActionListener(this);this.remove(size()-1);}
        while(size()<getSize()){add(new Agent(this));get(size()-1).addActionListener(this);}
        initStartingLocations();
        editing=false;
    }
    
    /** Re-initializes agent starting locations. */
    public void initStartingLocations(){
        editing=true;
        switch(getStart()){
        case START_RANDOM: startRandom(50); break;
        case START_LINE: startLine(new R2(-50,50),new R2(50,-50)); break;
        case START_CIRCLE: startCircle(new R2(),50);break;
        case START_ARC: startArc(new R2(),50,Math.PI/3,5*Math.PI/3);break;
        case START_ZERO:
        default: startZero(); break;
        }
        editing=false;
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    /** Returns center of mass of the team
     * @return center of mass */
    public R2 getCenterOfMass(){
        if (this.size()==0){return null;}
        R2 center=new R2(0,0);
        for(Agent agent:activeAgents){
            center.translate(agent.loc);
            //System.out.println("agent:"+agent.x+"+"+agent.y);
        }
        center.multiplyBy(1.0/this.size());
        //System.out.println("center:"+center.x+"+"+center.y);
        return center;
    }
    
    /** Adds initial point models to plot, and adds change listening to this team. */
    public void placeInitialPointsOn(PlotPanel<Euclidean2> p){
        for(Agent a:this){
            p.add(new Point2D(a.getPointModel(),a.getColor().brighter()));
        }
    }
    
    /** Generates tree given list of goals and agents */
    public DefaultMutableTreeNode getTreeNode(){
        DefaultMutableTreeNode result=new DefaultMutableTreeNode(this);
        for(Goal g:goals){result.add(new DefaultMutableTreeNode(g));}
        for(Agent agent:this){result.add(new DefaultMutableTreeNode(agent));}
        return result;
    }
    
    
    // METHODS FOR SETTING INITIAL POSITIONS OF TEAM MEMBERS
    
    /** Places all team members at zero. */
    public void startZero(){for(Agent agent:this){agent.getPointModel().setTo(0,0);}}
    
    /** Places team members at random within a rectangle.
     * @param spread sets the rectangle to [-spread,-spread]->[spread,spread] */
    public void startRandom(double spread){for(Agent agent:this){agent.getPointModel().setTo(PRandom.rectangle(spread));}}
    
    /** Places team members along a line.
     * @param p1   position of the first agent
     * @param pn   position of the last agent */
    public void startLine(R2 p1,R2 pn){
        if(size()==1){get(0).setPosition(p1.plus(pn).multipliedBy(.5));} else{
            R2 step=pn.minus(p1).multipliedBy(1.0/(size()-1.0));
            for(int i=0;i<size();i++){
                get(i).getPointModel().setTo(p1.x+step.x*i,p1.y+step.y*i);
            }
        }
    }
    
    /** Places team members in a circle.
     * @param point center of the circle
     * @param r     radius of the circle */
    public void startCircle(R2 point,double r){
        for(int i=0;i<size();i++){
            get(i).getPointModel().setTo(point.x+r*Math.cos(2*Math.PI*i/(double)size()),point.y+r*Math.sin(2*Math.PI*i/(double)size()));
        }
    }
    
    /** Places team members in a circular arc.
     * @param point center of the arc
     * @param r     radius of the arc
     * @param th1   starting angle of the arc
     * @param th2   ending angle of the arc */
    public void startArc(R2 point, double r,double th1,double th2){
        if(size()==1){get(0).getPointModel().setTo(point.x+r*Math.cos((th1+th2)/2),point.y+r*Math.sin((th1+th2)/2));} else{
            for(int i=0;i<size();i++){
                get(i).getPointModel().setTo(point.x+r*Math.cos(th1+i*(th2-th1)/(size()-1.0)),point.y+r*Math.sin(th1+i*(th2-th1)/(size()-1.0)));
            }
        }
    }
    
    
    
    // BROADCAST METHODS: PASS INSTRUCTIONS ONTO TEAM MEMBERS
    
    /** Instructs all agents to gather sensory data
     * @param d The global table of distances */
    public void gatherSensoryData(DistanceTable d){
        for(Agent a:activeAgents){
            a.gatherSensoryData(d);
        }
    }
    /** Instructs all agents to generate communication events for other players
     * @param d The global table of distances */
    public void communicateSensoryData(DistanceTable d){
        for(Agent a:activeAgents){
            a.generateSensoryEvents(this,d);
        }
    }
    /** Tells each agent to form their belief regarding the playing field. */
    public void fuseAgentPOV(){
        for(Agent a:activeAgents){
            a.fusePOV();
        }
    }
    /** Assigns tasks to the agents. */
    public void assignTasks(){
        for(Agent a:activeAgents){
            a.tasks.clear();
        }
        for(Goal g:goals){
            g.assignTasks();
        }
    }
    /** Generates directions for each team member based on their task and behavior.
     * @param time      the current time stamp
     * @param stepTime  the time between iterations */
    public void planPaths(double time,double stepTime){
        for(Agent a:activeAgents){
            a.planPath(time,stepTime);
        }
    }
    /** Moves all agents on the team using their assigned directions. */
    public void move(){
        for(Agent a:activeAgents){
            a.setPosition(a.loc.getEnd());        
        }
    }
    
    
    // BOOLEAN-GENERATING METHODS TESTING FOR WHETHER GOAL HAS BEEN REACHED
    
    /**
     * Checks to see if default value if goal has changed.
     * @param d     the global distance table
     */
    public void checkGoal(DistanceTable d,double time){  
        if(goals.isEmpty()){return;}
        double newValue=0;
        for(Goal g:goals){
            newValue+=g.getValue(d);
        }
        value=newValue;
    }
    
    
    // EVENT LISTENING
    
    /** Receives and re-broadcasts action events from agents on the team */
    public void actionPerformed(ActionEvent evt){
        if(!editing){
            fireActionPerformed(evt.getActionCommand());
        }
    }
    
    // Remaining code deals with action listening
    protected EventListenerList listenerList = new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(String s){
        if(editing){return;}
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2;i>=0;i-=2){
            if(listeners[i]==ActionListener.class){
                ((ActionListener)listeners[i+1]).actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // BEAN PATTERNS FOR INITIAL SETTINGS
    
    public int getSize(){return tes.size.getValue();}
    public int getStart(){return tes.start.getValue();}
    public Collection<Goal> getGoals(){return goals;}
    @Override
    public String toString(){return tes.s;}
    
    public void setSize(int newValue){tes.size.setValue(newValue);}
    public void setStart(int newValue){tes.start.setValue(newValue);}
    
    public double getSensorRange(){return tes.sensorRange.getValue();}
    public double getCommRange(){return tes.commRange.getValue();}
    public double getTopSpeed(){return tes.topSpeed.getValue();}
    public int getBehavior(){return tes.behavior.getValue();}
    public double getLeadFactor(){return tes.leadFactor.getValue();}
    public Color getColor(){return tes.color.getValue();}
    public R2 getPositionTime(double t){return new R2(tes.pm.getValue(t));}
    public Double getValue(){return value;}
    
    public void setSensorRange(double newValue){tes.sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){tes.commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){tes.topSpeed.setValue(newValue);}
    public void setBehavior(int newValue){tes.behavior.setValue(newValue);}
    public void setLeadFactor(double newValue){tes.leadFactor.setValue(newValue);}
    public void setColor(Color newValue){tes.color.setValue(newValue);}
    public void setString(String newValue){tes.s=newValue;}
    public void setFixedPath(String xt,String yt){tes.pm.setXString(xt);tes.pm.setYString(yt);}
    
    public JPanel getPanel(){return tes.getPanel();}   
        
    // BROADCAST METHODS: CHANGE SETTINGS OF AGENTS ON TEAM
    
    public void copySpeedtoTeam(){for(Agent a:this){a.setTopSpeed(getTopSpeed());}}
    public void copySensorRangetoTeam(){for(Agent a:this){a.setSensorRange(getSensorRange());}}
    public void copyCommRangetoTeam(){for(Agent a:this){a.setCommRange(getCommRange());}}
    public void copyBehaviortoTeam(){for(Agent a:this){a.setBehavior(getBehavior());}}
    public void copyColortoTeam(){for(Agent a:this){a.setColor(getColor());}}
    public void copyLeadFactortoTeam(){for(Agent a:this){a.setLeadFactor(getLeadFactor());}}
    public void copyPathtoTeam(){for(Agent a:this){a.setFixedPath(tes.pm.getStringX(),tes.pm.getStringY());}}
    
    // CONSTANTS FOR INITIAL SETTINGS
    
    public static final int START_ZERO=0;
    public static final int START_RANDOM=1;
    public static final int START_LINE=2;
    public static final int START_CIRCLE=3;
    public static final int START_ARC=4;
    public static final String[] START_STRINGS={"All at Zero","Random Positions","Along a Line","Around a Circle","Along a Circular Arc"};
    
    
    // SUBCLASSES
    
    /** Contains all the initial settings for the simulation. Everything else is used while the simulation is running. */
    private class TeamSettings extends Settings {
        
        /** Team size */
        private IntegerRangeModel size=new IntegerRangeModel(3,1,100);
        /** Starting positions to use */
        private ComboBoxRangeModel start=new ComboBoxRangeModel(START_STRINGS,START_RANDOM,0,4);
        
        /** Default sensor range [in ft]. */
        private DoubleRangeModel sensorRange=new DoubleRangeModel(20,0,5000);
        /** Default communications range [in ft]. */
        private DoubleRangeModel commRange=new DoubleRangeModel(50,0,5000);
        /** Default speed [in ft/s]. */
        private DoubleRangeModel topSpeed=new DoubleRangeModel(5,0,50,.05);
        /** Default behavioral setting */
        private ComboBoxRangeModel behavior=Behavior.getComboBoxModel();
        /** Lead factor if required for behavior */
        private DoubleRangeModel leadFactor=new DoubleRangeModel(0,0,5,.01);
        /** Position function if required for behavior */
        private ParametricModel pm=new ParametricModel();
        /** Default color. */
        private ColorModel color=new ColorModel(Color.BLUE);
        /** Display string */
        private String s="Team";
        
        public TeamSettings(){
            addProperty("# Agents",size,Settings.EDIT_INTEGER);
            addProperty("Starting Loc",start,Settings.EDIT_COMBO);
            addProperty("Speed",topSpeed,Settings.EDIT_DOUBLE);
            addProperty("Sensor Range",sensorRange,Settings.EDIT_DOUBLE);
            addProperty("Comm Range",commRange,Settings.EDIT_DOUBLE);
            addProperty("Behavior",behavior,Settings.EDIT_COMBO);
            addProperty("Lead Factor",leadFactor,Settings.NO_EDIT);
            addProperty("Position(t)",pm,Settings.NO_EDIT);
            addProperty("Color",color,Settings.EDIT_COLOR);
            initEventListening();
        }
        
        /**public JPanel getPanel(){
            JPanel result=new JPanel();
            for(Component c:super.getPanel().getComponents()){result.add(c);}
            SpringUtilities.makeCompactGrid(result,result.getComponentCount()/2,2,5,5,5,5);
            return result;
        }*/
        
        /** Listens for changes to settings */
        public void propertyChange(PropertyChangeEvent evt) {
            editing=true;
            String ac=null;
            if(evt.getSource()==behavior){
                copyBehaviortoTeam();
                if(behavior.getValue()==Behavior.PURSUIT_LEADING){
                    setPropertyEditor("Lead Factor",Settings.EDIT_DOUBLE);
                    setPropertyEditor("Position(t)",Settings.NO_EDIT);
                }else if(behavior.getValue()==Behavior.FIXEDPATH){
                    setPropertyEditor("Position(t)",Settings.EDIT_PARAMETRIC);
                    setPropertyEditor("Lead Factor",Settings.NO_EDIT);
                }else{
                    setPropertyEditor("Position(t)",Settings.NO_EDIT);
                    setPropertyEditor("Lead Factor",Settings.NO_EDIT);
                }
                ac="teamSetupChange";
            } else if(evt.getSource()==size){       initAgentNumber();      ac="teamAgentsChange";
            } else if(evt.getSource()==start){      initStartingLocations();ac="teamSetupChange";
            } else if(evt.getSource()==goals){                              ac="teamSetupChange";
            } else if(evt.getSource()==topSpeed){   copySpeedtoTeam();      ac="teamSetupChange";
            } else if(evt.getSource()==sensorRange){copySensorRangetoTeam();ac="teamSetupChange";
            } else if(evt.getSource()==commRange){  copyCommRangetoTeam();  ac="teamSetupChange";
            } else if(evt.getSource()==leadFactor){ copyLeadFactortoTeam(); ac="teamSetupChange";
            } else if(evt.getSource()==pm){         copyPathtoTeam();       ac="teamSetupChange";
            } else if(evt.getSource()==color){      copyColortoTeam();      ac="teamDisplayChange";}
            editing=false;
            fireActionPerformed(ac);
        }
    }
}
