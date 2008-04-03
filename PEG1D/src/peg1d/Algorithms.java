/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Algorithms.java
 * Created on Feb 26, 2008
 */

package peg1d;

import java.util.Vector;

/**
 *
 * @author Elisha Peterson
 */
public class Algorithms {
    
    // EVADER ALGORITHMS
    /** Computes new evader positions... in this case they all head to the origin. */
    public static Vector<Double> evadersTowardOrigin(Vector<Double> pursuerPosition,Vector<Double> evaderPosition,Simulation sim,int curStep){
        Vector<Double> result=new Vector<Double>();
        // evading elements always head to origin
        for(int i=0;i<evaderPosition.size();i++) {
            if(Math.abs(evaderPosition.get(i)-sim.getGoal())<sim.getCaptureRange()){
                result.add(evaderPosition.get(i));
            }else if(evaderPosition.get(i)<sim.getGoal()){
                result.add(evaderPosition.get(i)+sim.getStepSize()*sim.getESpeed());
            }else{
                result.add(evaderPosition.get(i)-sim.getStepSize()*sim.getESpeed());
            }
        }
        return result;
    }
    
    
    // PURSUER ALGORITHMS      
    
    /** Decide pursuer directions */
    public static Vector<Double> pursuersTowardClosest(Vector<Double> pursuerPosition,Vector<Double> evaderPosition,Simulation sim,int curStep){
        // pursuing elements chase closest evader
        Vector<Double> result=new Vector<Double>();
        for(int i=0;i<pursuerPosition.size();i++){
            Double closestEvader=getClosestTo(evaderPosition,pursuerPosition.get(i));
            if(closestEvader<pursuerPosition.get(i)){
                result.add(pursuerPosition.get(i)-sim.getPSpeed()*sim.getStepSize());
            }else{
                result.add(pursuerPosition.get(i)+sim.getPSpeed()*sim.getStepSize());
            }
        }
        return result;
    }
    
    public static Vector<Double> pursuers_DJ (Vector<Double> pursuerPosition,Vector<Double> evaderPosition, Vector<Double> evaderDirection, Simulation sim, int curStep) {
        Vector<Double> result=new Vector<Double> ();
        //fill in with algorithm
        //System.out.println(result.toString());        
        return result;
    }
    
    
    // UTILITY METHODS

   /** Returns closest in a list of doubles to the double given. */
   public static Double getClosestTo(Vector<Double> opponentPositions,Double position){
        Double minDist=Double.POSITIVE_INFINITY;
        int minIndex=0;
        for(int j=0;j<opponentPositions.size();j++){
            if(Math.abs(position-opponentPositions.get(j))<minDist){
                minDist=Math.abs(position-opponentPositions.get(j));
                minIndex=j;
            }
        }
        return opponentPositions.get(minIndex);
    }
}
