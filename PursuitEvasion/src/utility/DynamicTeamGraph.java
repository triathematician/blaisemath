/*
 * DynamicTeamGraph.java
 * Created on Oct 16, 2007, 2:14:06 PM
 */

package utility;

import analysis.DataLog;
import metrics.CaptureCondition;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import simulation.Agent;
import simulation.Team;
import specto.Animatable;
import specto.Plottable;
import scio.coordinate.R2;
import sequor.component.RangeTimer;
import sequor.style.VisualStyle;
import specto.euclidean2.Euclidean2;

/**
 * Displays team communication graph at a given time.
 * Does this using the pre-computed paths in conjunction with comm distances.
 * <br><br>
 * @author ae3263
 */
public class DynamicTeamGraph extends Plottable<Euclidean2> implements Animatable<Euclidean2> {
    Team team;
    DataLog log;
    
    public DynamicTeamGraph(Team t,DataLog l){team=t;log=l;}
    
    public int pathSize(){
        return log.size();
    }
    
    public void paintComponent(Graphics2D g,Euclidean2 v) {}

    /** Draws graph corresponding to current step. */
    public void paintComponent(Graphics2D g,Euclidean2 v,RangeTimer t){
        if(pathSize()==0){return;}
        g.setColor(team.getColor().brighter().brighter());
        g.setStroke(VisualStyle.THIN_STROKE);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
        g.draw(getEdges(v,t.getCurrentIntValue()));
        for(CaptureCondition cc:team.capture){
            g.draw(getTargetEdges(v,cc,t.getCurrentIntValue()));
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }   
    
    
    /** Checks each pair of agents against team's range parameter, returns draw element. */    
    public Path2D.Double getEdges(Euclidean2 v,int time){
        int timeB=time<0?0:(time>=pathSize()?pathSize()-1:time);
        Path2D.Double result=new Path2D.Double();
        R2 p1;
        R2 p2;
        for(int i=0;i<team.size();i++){
            if(!team.getActiveAgents().contains(team.get(i))){ continue; }
            p1=log.agentAt(team.get(i),timeB);
            for(int j=i+1;j<team.size();j++){
                if(!team.getActiveAgents().contains(team.get(j))){ continue; }
                p2=log.agentAt(team.get(j),timeB);
                if(p1.distance(p2)<team.getCommRange()){
                    result.moveTo(v.toWindowX(p1.x),v.toWindowY(p1.y));
                    result.lineTo(v.toWindowX(p2.x),v.toWindowY(p2.y));
                }
            }
        }
        return result;
    } 
    
    /** Gets edges for each target in range. */
    public Path2D.Double getTargetEdges(Euclidean2 v,CaptureCondition cc,int time){
        if(cc.getTarget()==null){return new Path2D.Double();}
        int timeB=time<0?0:(time>=pathSize()?pathSize()-1:time);
        Path2D.Double result=new Path2D.Double();
        R2 p1;
        R2 p2;
        for(int i=0;i<team.size();i++){
            if(!team.getActiveAgents().contains(team.get(i))){ continue; }
            p1=log.agentAt(team.get(i),timeB);
            for(Agent a:cc.getTarget().getActiveAgents()){
                p2=log.agentAt(a,timeB);
                if(p1.distance(p2)<team.getSensorRange()){
                    result.moveTo(v.toWindowX(p1.x),v.toWindowY(p1.y));
                    result.lineTo(v.toWindowX(p2.x),v.toWindowY(p2.y));
                }
            }
        }
        return result;        
    }
    
    
    // BEANS

    public int getAnimatingSteps() {return 2;}

    @Override
    public String[] getStyleStrings() {return null;}
    
    @Override
    public String toString() { return "Sensor/Comm Graph"; }
}
