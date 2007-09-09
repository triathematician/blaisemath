/*
 * Team.java
 * Created on Aug 28, 2007, 10:26:33 AM
 */

package agent;

import Blaise.BPlotPath2D;
import Euclidean.PPath;
import Euclidean.PPoint;
import Euclidean.PRandom;
import Model.PointRangeModel;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import behavior.Tasking;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import utility.DistanceTable;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Handles routines dealing with an entire team of players.
 * Includes cooperative/control algorithms, broadcast methods for instructing team's agents, etc.
 */
public class Team extends ArrayList<Agent> implements PropertyChangeListener {

    
// PROPERTIES  
    
    /** The team's settings */
    public TeamSettings ts;
    /** How the team assigns tasks to its agents */
    public Tasking tasking;
    
    
// CONSTRUCTORS    
    
    public Team(){this(new TeamSettings());}
    public Team(TeamSettings ts){
        super();
        this.ts=ts;
        ts.getGoal().setTeam(this);
        initTasking();
        for(int i=0;i<ts.getSize();i++){add(new Agent(this));}
        initStartingLocations();
    }
    public Team(Team team){
        ts=team.ts;
        ts.getGoal().setTeam(this);
        initTasking();
        for(Agent a:team){this.add(a);}
    }
    
    
// METHODS: HELP FOR INITIALIZAITON
    
    /** Sets tasking behavior given default settings */
    public void initTasking(){tasking=Tasking.getTasking(ts.getTasking());}
    
    /** Resets all agents to their initial positions; clears all paths */
    public void reset(){for(Agent a:this){a.reset();}}
    
    /** Re-initializes agent starting locations. */
    public void initStartingLocations(){
        switch(ts.getStart()){
            case TeamSettings.START_RANDOM: startRandom(50); break;
            case TeamSettings.START_LINE: startLine(new PPoint(-50,50),new PPoint(50,-50)); break;
            case TeamSettings.START_CIRCLE: startCircle(new PPoint(),50);break;
            case TeamSettings.START_ARC: startArc(new PPoint(),50,Math.PI/3,5*Math.PI/3);break;
            case TeamSettings.START_ZERO:
            default: startZero(); break;
        }
    }    
    
    
// BEAN PATTERNS: GETTERS & SETTERS
    
    /** Returns center of mass of the team
     * @return center of mass */
    public PPoint getCenterOfMass(){
        if (this.size()==0){return null;}
        PPoint center=new PPoint(0,0);
        for (Agent agent:this){
            center.translate(agent);
            //System.out.println("agent:"+agent.x+"+"+agent.y);
        }
        center.multiply(1.0/this.size());
        //System.out.println("center:"+center.x+"+"+center.y);
        return center;    
    }
    
    /** Sets team target.
     * @param target the target team */
    public void setTarget(Team target){ts.getGoal().setTarget(target);}
    
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
    
    /** Returns point models corresponding to initial positions
     * @return list of point models, suitable for creation of BClickablePoint's */
    public ArrayList<PointRangeModel> getPointModels(){
        ArrayList<PointRangeModel> result=new ArrayList<PointRangeModel>();
        for(Agent a:this){result.add(a.getPointModel());}
        return result;
    }
    
    /** Generates tree given list of agents */
    public DefaultMutableTreeNode getTreeNode(){
        DefaultMutableTreeNode result=new DefaultMutableTreeNode(ts);
        for(Agent agent:this){result.add(new DefaultMutableTreeNode(agent.as));}
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
        if(size()==1){get(0).setPoint(p1.translate(pn).multiply(.5));}
        else{
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
        if(size()==1){get(0).getPointModel().setTo(point.x+r*Math.cos((th1+th2)/2),point.y+r*Math.sin((th1+th2)/2));}
        else{
            for(int i=0;i<size();i++){
                get(i).getPointModel().setTo(point.x+r*Math.cos(th1+i*(th2-th1)/(size()-1.0)),point.y+r*Math.sin(th1+i*(th2-th1)/(size()-1.0)));
            }
        }
    }
       
    
// BROADCAST METHODS: PASS INSTRUCTIONS ONTO TEAM MEMBERS
    
    /** Instructs all agents to gather sensory data 
     * @param d The global table of distances */
    public void gatherSensoryData(DistanceTable d){for(Agent a:this){a.gatherSensoryData(d);}}
    /** Instructs all agents to generate communication events for other players
     * @param d The global table of distances */
    public void communicateSensoryData(DistanceTable d){for(Agent a:this){a.generateSensoryEvents(d);}}    
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
    public boolean goalAchieved(DistanceTable d){
        if(ts.getGoal()==null){return true;}
        return ts.getGoal().isAchieved(d);
    }
    
    
// HANDLE PROPERTY CHANGE EVENTS... BROADCAST TO TEAM!!
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName()=="Starting Loc"){initStartingLocations();}
        if(evt.getPropertyName()=="Color"){for(Agent a:this){a.as.setColor(ts.getAgentSettings().getColor());}}
    }
}
