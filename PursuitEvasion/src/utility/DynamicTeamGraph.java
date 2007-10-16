/*
 * DynamicTeamGraph.java
 * Created on Oct 16, 2007, 2:14:06 PM
 */

package utility;

import Euclidean.PPoint;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import simulation.Agent;
import simulation.Team;
import specto.Animatable;
import specto.Plottable;
import specto.RangeTimer;
import specto.dynamicplottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 * Displays team communication graph at a given time.
 * Does this using the pre-computed paths in conjunction with comm distances.
 * <br><br>
 * @author ae3263
 */
public class DynamicTeamGraph extends Plottable<Euclidean2> implements Animatable {
    Team team;
    
    public DynamicTeamGraph(Team t){team=t;}
    
    public int pathSize(){
        if(team==null||team.size()==0){return 0;}
        return team.get(0).getPath().size();
    }
    
    public void paintComponent(Graphics2D g) {
    }

    /** Draws graph corresponding to current step. */
    public void paintComponent(Graphics2D g, RangeTimer t){
        if(pathSize()==0){return;}
        g.setStroke(PointSet2D.VERY_DOTTED_STROKE);
        g.setColor(team.getColor().brighter().brighter());
        g.draw(getEdges(t.getCurrentStep()));
        g.draw(getTargetEdges(t.getCurrentStep()));
    }   
    
    /** Checks each pair of agents against team's range parameter, returns draw element. */    
    public Path2D.Double getEdges(int time){
        int timeB=time<0?0:(time>=pathSize()?pathSize()-1:time);
        Path2D.Double result=new Path2D.Double();
        PPoint p1;
        PPoint p2;
        for(int i=0;i<team.size();i++){
            p1=team.get(i).getPath().get(timeB);
            for(int j=i+1;j<team.size();j++){
                p2=team.get(j).getPath().get(timeB);
                if(p1.distanceTo(p2)<team.getCommRange()){
                    result.moveTo(visometry.toWindowX(p1.x),visometry.toWindowY(p1.y));
                    result.lineTo(visometry.toWindowX(p2.x),visometry.toWindowY(p2.y));
                }
            }
        }
        return result;
    } 
    
    /** Gets edges for each target in range. */
    public Path2D.Double getTargetEdges(int time){
        int timeB=time<0?0:(time>=pathSize()?pathSize()-1:time);
        Path2D.Double result=new Path2D.Double();
        PPoint p1;
        PPoint p2;
        for(int i=0;i<team.size();i++){
            p1=team.get(i).getPath().get(timeB);
            for(Agent a:team.getGoal().getTarget()){
                p2=a.getPath().get(timeB);
                if(p1.distanceTo(p2)<team.getSensorRange()){
                    result.moveTo(visometry.toWindowX(p1.x),visometry.toWindowY(p1.y));
                    result.lineTo(visometry.toWindowX(p2.x),visometry.toWindowY(p2.y));
                }
            }
        }
        return result;        
    }
}
