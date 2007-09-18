/*
 * Team.java
 * Created on Aug 28, 2007, 10:26:33 AM
 */

package simulation;

import Blaise.BPlot2D;
import Blaise.BPlotPath2D;
import Euclidean.PPath;
import Euclidean.PPoint;
import Euclidean.PRandom;
import Model.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import behavior.*;
import utility.DistanceTable;

/**
 * @author Elisha Peterson<br><br>
 *
 * Handles routines dealing with an entire team of players.
 * Includes cooperative/control algorithms, broadcast methods for instructing team's agents, etc.
 */
public class Team extends ArrayList<Agent> implements ActionListener,PropertyChangeListener {
    
    
    // PROPERTIES
    
    /** The team's settings */
    public TeamSettings tes;
    /** How the team assigns tasks to its agents */
    public Tasking tasking;
    
    /** Whether to forward action events */
    private boolean editing=false;
    
    
    
    // CONSTRUCTORS
    public Team(){super();tes=new TeamSettings();}
    public Team(TeamSettings tes){
        super();
        this.tes=tes;
        getGoal().setTeam(this);
        initTasking();
        for(int i=0;i<getSize();i++){add(new Agent(this));}
        initStartingLocations();
        for(Agent a:this){a.addActionListener(this);}
    }
    public Team(Team team){
        tes=team.tes;
        initTasking();
        for(Agent a:team){this.add(a);}
    }
    
    public Team(int size,int start,int goalType,int goalNum,double goalDist,int tasking,int behavior,Color color){
        super();
        editing=true;
        tes=new TeamSettings();
        setSize(size);
        setStart(start);
        setGoal(new Goal(goalType,goalNum,goalDist));
        setTasking(tasking);
        setBehavior(behavior);
        setColor(color);
        getGoal().setTeam(this);
        this.clear();
        for(int i=0;i<getSize();i++){add(new Agent(this));}
        initTasking();
        initStartingLocations();
        for(Agent a:this){a.addActionListener(this);} 
        editing=false;
    }
    
    
    // METHODS: HELP FOR INITIALIZAITON
    
    /** Sets tasking behavior given default settings */
    public void initTasking(){tasking=Tasking.getTasking(getTasking());}
    
    /** Resets all agents to their initial positions; clears all paths */
    public void reset(){for(Agent a:this){a.reset();}getGoal().setAchieved(false);}
    
    /** Changes the number of agents, resets starting locations. */
    public void initAgentNumber(){
        editing=true;
        while(size()>getSize()){get(size()-1).removeActionListener(this);this.remove(size()-1);}
        while(size()<getSize()){add(new Agent(this));get(size()-1).addActionListener(this);}
        initStartingLocations();
    }
    
    /** Re-initializes agent starting locations. */
    public void initStartingLocations(){
        editing=true;
        switch(getStart()){
        case START_RANDOM: startRandom(50); break;
        case START_LINE: startLine(new PPoint(-50,50),new PPoint(50,-50)); break;
        case START_CIRCLE: startCircle(new PPoint(),50);break;
        case START_ARC: startArc(new PPoint(),50,Math.PI/3,5*Math.PI/3);break;
        case START_ZERO:
        default: startZero(); break;
        }
        editing=false;
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    /** Returns center of mass of the team
     * @return center of mass */
    public PPoint getCenterOfMass(){
        if (this.size()==0){return null;}
        PPoint center=new PPoint(0,0);
        for (Agent agent:this){
            center.translate(agent.getPosition());
            //System.out.println("agent:"+agent.x+"+"+agent.y);
        }
        center.multiply(1.0/this.size());
        //System.out.println("center:"+center.x+"+"+center.y);
        return center;
    }
    
    /** Sets team target.
     * @param target the target team */
    public void setTarget(Team target){getGoal().setTarget(target);}
    
    /** Returns paths taken by team members.
     * @return collection of paths associated to the team */
    public ArrayList<PPath> getPaths(){
        ArrayList<PPath> result=new ArrayList<PPath>();
        for(Agent a:this){result.add(a.getPath());}
        return result;
    }
    
    /** Returns plot path taken by team members.
     * @return collection of plottable paths */
    public ArrayList<BPlotPath2D> getPlotPaths(){
        ArrayList<BPlotPath2D> result=new ArrayList<BPlotPath2D>();
        for(Agent a:this){result.add(a.getPlotPath());}
        return result;
    }
    
    /** Adds initial point models to plot, and adds change listening to this team. */
    public void placeInitialPointsOn(BPlot2D p){for(Agent a:this){p.addPoint(a.getPointModel(),a.getColor());}}
    
    /** Generates tree given list of agents */
    public DefaultMutableTreeNode getTreeNode(){
        DefaultMutableTreeNode result=new DefaultMutableTreeNode(this);
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
    public void startLine(PPoint p1,PPoint pn){
        if(size()==1){get(0).setPosition(p1.translate(pn).multiply(.5));} else{
            PPoint step=p1.toward(pn).multiply(1/(size()-1.0));
            for(int i=0;i<size();i++){
                get(i).getPointModel().setTo(p1.x+step.x*i,p1.y+step.y*i);
            }
        }
    }
    
    /** Places team members in a circle.
     * @param point center of the circle
     * @param r     radius of the circle */
    public void startCircle(PPoint point,double r){
        for(int i=0;i<size();i++){
            get(i).getPointModel().setTo(point.x+r*Math.cos(2*Math.PI*i/(double)size()),point.y+r*Math.sin(2*Math.PI*i/(double)size()));
        }
    }
    
    /** Places team members in a circular arc.
     * @param point center of the arc
     * @param r     radius of the arc
     * @param th1   starting angle of the arc
     * @param th2   ending angle of the arc */
    public void startArc(PPoint point, double r,double th1,double th2){
        if(size()==1){get(0).getPointModel().setTo(point.x+r*Math.cos((th1+th2)/2),point.y+r*Math.sin((th1+th2)/2));} else{
            for(int i=0;i<size();i++){
                get(i).getPointModel().setTo(point.x+r*Math.cos(th1+i*(th2-th1)/(size()-1.0)),point.y+r*Math.sin(th1+i*(th2-th1)/(size()-1.0)));
            }
        }
    }
    
    
    // BROADCAST METHODS: CHANGE SETTINGS OF AGENTS ON TEAM
    
    public void copySpeedtoTeam(){for(Agent a:this){a.setTopSpeed(getTopSpeed());}}
    public void copySensorRangetoTeam(){for(Agent a:this){a.setSensorRange(getSensorRange());}}
    public void copyCommRangetoTeam(){for(Agent a:this){a.setCommRange(getCommRange());}}
    public void copyBehaviortoTeam(){for(Agent a:this){a.setBehavior(getBehavior());}}
    public void copyColortoTeam(){for(Agent a:this){a.setColor(getColor());}}
    public void copyLeadFactortoTeam(){for(Agent a:this){a.setLeadFactor(getLeadFactor());}}
    
    
    // BROADCAST METHODS: PASS INSTRUCTIONS ONTO TEAM MEMBERS
    
    /** Instructs all agents to gather sensory data
     * @param d The global table of distances */
    public void gatherSensoryData(DistanceTable d){for(Agent a:this){a.gatherSensoryData(d);}}
    /** Instructs all agents to generate communication events for other players
     * @param d The global table of distances */
    public void communicateSensoryData(DistanceTable d){for(Agent a:this){a.generateSensoryEvents(this,d);}}
    /** Tells each agent to form their belief regarding the playing field. */
    public void fuseAgentPOV(){for(Agent a:this){a.fusePOV();}}
    /** Generates directions for each team member based on their task and behavior.
     * @param time      the current time stamp
     * @param stepTime  the time between iterations */
    public void planPaths(double time,double stepTime){for(Agent a:this){a.planPath(time,stepTime);}}
    /** Moves all agents on the team using their assigned directions. */
    public void move(){for(Agent a:this){a.move();a.remember();}}
    
    
    // BOOLEAN-GENERATING METHODS TESTING FOR WHETHER GOAL HAS BEEN REACHED
    
    /**
     * Checks to see if goal has been achieved
     * @param d     the global distance table
     * @return      true if the team's goal has been achieved, otherwise false
     */
    public void checkGoal(DistanceTable d,double time){
        if(getGoal()==null){return;}
        if(getGoal().justAchieved(d)){
            fireActionPerformed("goalmet");
        }
    }
    
    
    // EVENT LISTENING
    
    /** Receives and re-broadcasts action events from agents on the team */
    public void actionPerformed(ActionEvent evt){if(editing){return;}fireActionPerformed(evt.getActionCommand());}
    
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
    public int getTasking(){return tes.tasking.getValue();}
    public Goal getGoal(){return tes.goal;}
    public String toString(){return tes.s;}
    
    public void setSize(int newValue){tes.size.setValue(newValue);}
    public void setStart(int newValue){tes.start.setValue(newValue);}
    public void setTasking(int newValue){tes.tasking.setValue(newValue);}
    public void setGoal(Goal newValue){if(!newValue.equals(tes.goal)){tes.goal=newValue;}}
    
    public double getSensorRange(){return tes.sensorRange.getValue();}
    public double getCommRange(){return tes.commRange.getValue();}
    public double getTopSpeed(){return tes.topSpeed.getValue();}
    public int getBehavior(){return tes.behavior.getValue();}
    public double getLeadFactor(){return tes.leadFactor.getValue();}
    public Color getColor(){return tes.color.getValue();}
    public PPoint getPositionTime(double t){return tes.pm.getValue(t);}
    
    public void setSensorRange(double newValue){tes.sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){tes.commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){tes.topSpeed.setValue(newValue);}
    public void setBehavior(int newValue){tes.behavior.setValue(newValue);}
    public void setLeadFactor(double newValue){tes.leadFactor.setValue(newValue);}
    public void setColor(Color newValue){tes.color.setValue(newValue);}
    public void setString(String newValue){tes.s=newValue;}
    public void setFixedPath(String xt,String yt){tes.pm.setXString(xt);tes.pm.setYString(yt);}
    
    public JPanel getPanel(){return tes.getPanel();}   
    
    
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
        /** The team's tasking algorithm default */
        private ComboBoxRangeModel tasking=new ComboBoxRangeModel(Tasking.TASKING_STRINGS,Tasking.AUTO_CLOSEST,Tasking.FIRST,Tasking.LAST);
        /** The team's goal */
        private Goal goal=new Goal();
        
        /** Default sensor range [in ft]. */
        private DoubleRangeModel sensorRange=new DoubleRangeModel(10,0,5000);
        /** Default communications range [in ft]. */
        private DoubleRangeModel commRange=new DoubleRangeModel(20,0,5000);
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
            addProperty("Tasking",tasking,Settings.EDIT_COMBO);
            addProperty("Speed",topSpeed,Settings.EDIT_DOUBLE);
            addProperty("Sensor Range",sensorRange,Settings.EDIT_DOUBLE);
            addProperty("Comm Range",commRange,Settings.EDIT_DOUBLE);
            addProperty("Behavior",behavior,Settings.EDIT_COMBO);
            addProperty("Lead Factor",leadFactor,Settings.EDIT_DOUBLE);
            addProperty("Position(t)",pm,Settings.EDIT_PARAMETRIC);
            addProperty("Color",color,Settings.EDIT_COLOR);
            initEventListening();
        }
        
        public JPanel getPanel(){
            JPanel result=super.getPanel();
            for(Component c:goal.getPanel().getComponents()){result.add(c);}
            SpringUtilities.makeCompactGrid(result,result.getComponentCount()/2,2,5,5,5,5);
            return result;
        }
        
        /** Listens for changes to settings */
        public void propertyChange(PropertyChangeEvent evt) {
            editing=true;
            String ac=null;
            if(evt.getPropertyName()=="# Agents"){            initAgentNumber();      ac="teamAgentsChange";
            } else if(evt.getPropertyName()=="Starting Loc"){ initStartingLocations();ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Tasking"){      initTasking();          ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Goal Type"){                            ac="teamSetupChange";
            } else if(evt.getPropertyName()=="One or All"){                           ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Capture Distance"){                     ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Speed"){        copySpeedtoTeam();      ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Sensor Range"){ copySensorRangetoTeam();ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Comm Range"){   copyCommRangetoTeam();  ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Behavior"){     copyBehaviortoTeam();   ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Lead Factor"){  copyLeadFactortoTeam(); ac="teamSetupChange";
            } else if(evt.getPropertyName()=="Color"){        copyColortoTeam();      ac="teamDisplayChange";}
            editing=false;
            fireActionPerformed(ac);
        }
    }
}
