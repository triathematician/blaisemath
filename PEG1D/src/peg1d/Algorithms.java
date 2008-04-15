/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Algorithms.java
 * Created on Feb 26, 2008
 */

package peg1d;

import java.util.HashMap;
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
        /**Each Pursuer looks to see the directions of all the Evaders.  He then
         * generates a table with values representing every Evader that is 
         * moving towards him.  Once all tables are generated, solve so that 
         * each Pursuer pursues a different Evader.  In the case that 2 Pursuers
         * can go after 2 Evaders, assign Evaders in a way that minimizes the 
         * total distance between Pursuer and Evader.
         */
        System.out.println(evaderDirection);
        
           HashMap<Integer,Vector<Integer>> pursuerTable = new HashMap<Integer,Vector<Integer>> ();
           int p = sim.getNP();
           int e = sim.getNE();
           for (int i = 0; i < p; i++) {
            Vector<Integer> evaderList = new Vector<Integer>();
               for (int j = 0; j < e; j++) {
                   if(Math.signum(pursuerPosition.get(i) - evaderPosition.get(j)) == Math.signum(evaderDirection.get(j))) {
                       evaderList.add (j);
                   }
               }
           pursuerTable.put(i,evaderList);
           }
          HashMap<Integer,Integer> finalAssignment = new HashMap<Integer,Integer> (); 
          for (int i = 0; i < p; i++) {
              for (int j = 0; j < e; j++) {
                  if(pursuerTable.get(i).size() == 1) {
                      finalAssignment.put(i,j);
                      for (int k = 0; k < p; k++) {
                          pursuerTable.get(k).remove(j);
                      }
                  }
              }
          }
          
          for (int i = 0; i < e; i++) {
            if(getUniquePursuer(pursuerTable,i) != null){
                finalAssignment.put(getUniquePursuer(pursuerTable,i),i);
                for (int j = 0; j < p; j++) {
                    pursuerTable.get(j).remove(i);
                }
            }
        }
          
        


        System.out.println(pursuerTable.toString());
        System.out.println(finalAssignment.toString());
        return pursuersTowardClosest(pursuerPosition, evaderPosition, sim, curStep);
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

    public static Integer getUniquePursuer(HashMap<Integer,Vector<Integer>> pursuerTable, Integer evader){
        Integer result = null;
        for(Integer i:pursuerTable.keySet()) {
            if(pursuerTable.get(i).contains(evader)){
                if(result==null){
                    result = i;
                }
                else{
                    return null;
                }
            }
        }

        return result;
    }
    class TableData {
        Double distance = Double.MAX_VALUE;
        HashMap currentTable = new HashMap<Integer,Integer> ();
    }
    
    public static TableData getBestDistance(HashMap<Integer,Vector<Integer>> pursuerTable, Vector<Double> pursuerPosition, Vector<Double> evaderPosition) {
        TableData result = null;
        HashMap<Integer,Vector<Integer>> otherTable = new HashMap<Integer,Vector<Integer>> ();
        HashMap<Integer,Integer> bestYet = new HashMap<Integer,Integer> ();
        HashMap<Integer,Integer> current = new HashMap<Integer,Integer> ();
        otherTable.putAll(pursuerTable);
        for(Integer i:otherTable.keySet()) {
            current.put(i,otherTable.get(i).firstElement());
            otherTable.remove(i);
        }
        return result;
    }
}