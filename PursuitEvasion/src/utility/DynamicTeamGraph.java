/*
 * DynamicTeamGraph.java
 * Created on Oct 16, 2007, 2:14:06 PM
 */

package utility;

import goal.Goal;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.JMenu;
import simulation.Agent;
import simulation.Team;
import specto.Animatable;
import specto.Plottable;
import sequor.component.RangeTimer;
import scio.coordinate.R2;
import specto.plottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 * Displays team communication graph at a given time.
 * Does this using the pre-computed paths in conjunction with comm distances.
 * <br><br>
 * @author ae3263
 */
public class DynamicTeamGraph extends Plottable<Euclidean2> implements Animatable {
    Team team;
    DataLog log;
    
    public DynamicTeamGraph(Euclidean2 vis,Team t,DataLog l){super(vis);team=t;log=l;}
    
    public int pathSize(){
        return log.size();
    }
    
    public void paintComponent(Graphics2D g) {
    }

    /** Draws graph corresponding to current step. */
    public void paintComponent(Graphics2D g, RangeTimer t){
        if(pathSize()==0){return;}
        g.setStroke(PointSet2D.VERY_DOTTED_STROKE);
        g.setColor(team.getColor().brighter().brighter());
        g.draw(getEdges(t.getCurrentStep()));
        for(Goal goal:team.getGoals()){
            g.draw(getTargetEdges(goal,t.getCurrentStep()));
        }
    }   
    
    
    /** Checks each pair of agents against team's range parameter, returns draw element. */    
    public Path2D.Double getEdges(int time){
        int timeB=time<0?0:(time>=pathSize()?pathSize()-1:time);
        Path2D.Double result=new Path2D.Double();
        R2 p1;
        R2 p2;
        for(int i=0;i<team.size();i++){
            p1=log.agentAt(team.get(i),timeB);
            for(int j=i+1;j<team.size();j++){
                p2=log.agentAt(team.get(j),timeB);
                if(p1.distance(p2)<team.getCommRange()){
                    result.moveTo(visometry.toWindowX(p1.x),visometry.toWindowY(p1.y));
                    result.lineTo(visometry.toWindowX(p2.x),visometry.toWindowY(p2.y));
                }
            }
        }
        return result;
    } 
    
    /** Gets edges for each target in range. */
    public Path2D.Double getTargetEdges(Goal g,int time){
        if(g.getTarget()==null){return new Path2D.Double();}
        int timeB=time<0?0:(time>=pathSize()?pathSize()-1:time);
        Path2D.Double result=new Path2D.Double();
        R2 p1;
        R2 p2;
        for(int i=0;i<team.size();i++){
            p1=log.agentAt(team.get(i),timeB);
            for(Agent a:g.getTarget()){
                p2=log.agentAt(a,timeB);
                if(p1.distance(p2)<team.getSensorRange()){
                    result.moveTo(visometry.toWindowX(p1.x),visometry.toWindowY(p1.y));
                    result.lineTo(visometry.toWindowX(p2.x),visometry.toWindowY(p2.y));
                }
            }
        }
        return result;        
    }

    public JMenu getOptionsMenu() {
        return new JMenu("Network ");
    }

    public void recompute() {
    }
}
