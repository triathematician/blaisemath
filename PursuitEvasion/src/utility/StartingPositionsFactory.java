/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

import java.util.Vector;
import scio.coordinate.R2;
import scio.random.PRandom;
import simulation.Agent;

/**
 *
 * @author ae3263
 */
public class StartingPositionsFactory {

        // METHODS FOR SETTING INITIAL POSITIONS OF TEAM MEMBERS
    
    /** Places all team members at zero. */
    public static void startZero(Vector<Agent> team){for(Agent agent:team){agent.getPointModel().setTo(0,0);}}
    
    /** Places team members at random within a rectangle.
     * @param spread sets the rectangle to [-spread,-spread]->[spread,spread] */
    public static void startRandom(Vector<Agent> team,double spread){for(Agent agent:team){agent.getPointModel().setTo(PRandom.rectangle(spread));}}
    
    /** Places team members along a line.
     * @param p1   position of the first agent
     * @param pn   position of the last agent */
    public static void startLine(Vector<Agent> team,R2 p1,R2 pn){
        if(team.size()==1){
            team.get(0).setPosition(p1.plus(pn).multipliedBy(.5));
        }else{
            R2 step=pn.minus(p1).multipliedBy(1.0/(team.size()-1.0));
            for(int i=0;i<team.size();i++){
                team.get(i).getPointModel().setTo(p1.x+step.x*i,p1.y+step.y*i);
            }
        }
    }
    
    /** Places team members in a circle.
     * @param point center of the circle
     * @param r     radius of the circle */
    public static void startCircle(Vector<Agent> team,R2 point,double r){
        for(int i=0;i<team.size();i++){
            team.get(i).getPointModel().setTo(point.x+r*Math.cos(2*Math.PI*i/(double)team.size()),point.y+r*Math.sin(2*Math.PI*i/(double)team.size()));
        }
    }
    
    /** Places team members in a circular arc.
     * @param point center of the arc
     * @param r     radius of the arc
     * @param th1   starting angle of the arc
     * @param th2   ending angle of the arc */
    public static void startArc(Vector<Agent> team,R2 point, double r,double th1,double th2){
        if(team.size()==1){
            team.get(0).getPointModel().setTo(point.x+r*Math.cos((th1+th2)/2),point.y+r*Math.sin((th1+th2)/2));
        }else{
            for(int i=0;i<team.size();i++){
                team.get(i).getPointModel().setTo(point.x+r*Math.cos(th1+i*(th2-th1)/(team.size()-1.0)),point.y+r*Math.sin(th1+i*(th2-th1)/(team.size()-1.0)));
            }
        }
    }    
}
