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
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 *
 * @author Elisha Peterson
 */
public class Algorithms {

    // EVADER ALGORITHMS
    /** Computes new evader positions... in this case they all head to the origin. */
    public static Vector<Double> evadersTowardOrigin(Vector<Double> pursuerPosition, Vector<Double> evaderPosition, Simulation sim, int curStep) {
        Vector<Double> result = new Vector<Double>();
        double testValue;
        double ePos;
        double unitStep = sim.getStepSize() * sim.getESpeed();
        // evading elements always head to origin
        for (int i = 0; i < evaderPosition.size(); i++) {
            ePos = evaderPosition.get(i);
            testValue = ePos - sim.getGoal();
            if (Math.abs(testValue) < sim.getCaptureRange()) { result.add(ePos);
            } else { result.add( ePos + ( (testValue < 0) ? unitStep : -unitStep ) ); }
        }
        return result;
    }

    // PURSUER ALGORITHMS      
    /** Decide pursuer directions */
    public static HashMap<Integer,Integer> pursuersTowardClosest(Vector<Double> pursuerPosition, Vector<Double> evaderPosition, Simulation sim, int curStep) {
        HashMap<Integer, Integer> assignment = new HashMap<Integer, Integer>();
        for (int i = 0; i < pursuerPosition.size(); i++) {
            assignment.put(i, getClosestTo(evaderPosition, pursuerPosition.get(i)));
        }
        return assignment;
    }

    public static HashMap<Integer,Integer> pursuers_DJ(Vector<Double> pursuerPosition, Vector<Double> evaderPosition, Vector<Double> evaderDirection, Simulation sim, int curStep) {
        Vector<Double> result = new Vector<Double>();
        /**Each Pursuer looks to see the directions of all the Evaders.  He then
         * generates a table with values representing every Evader that is 
         * moving towards him.  Once all tables are generated, solve so that 
         * each Pursuer pursues a different Evader.  In the case that 2 Pursuers
         * can go after 2 Evaders, assign Evaders in a way that minimizes the 
         * total distance between Pursuer and Evader.
         */
//        System.out.println(evaderDirection);

        HashMap<Integer, HashSet<Integer>> pursuerTable = new HashMap<Integer, HashSet<Integer>>();
        int p = sim.getNP();
        int e = sim.getNE();
        for (int i = 0; i < p; i++) {
            if (pursuerPosition.get(i).equals(Double.POSITIVE_INFINITY)) {
                continue;
            }
            HashSet<Integer> evaderList = new HashSet<Integer>();
            for (int j = 0; j < e; j++) {
                if (Math.signum(pursuerPosition.get(i) - evaderPosition.get(j)) == Math.signum(evaderDirection.get(j))) {
                    evaderList.add(j);
                }
            }
            pursuerTable.put(i, evaderList);
        }

//        System.out.println(" 1: " + pursuerTable.toString());

        try {

            HashMap<Integer, Integer> finalAssignment = new HashMap<Integer, Integer>();
            for (int i : ((HashMap<Integer, HashSet<Integer>>) pursuerTable.clone()).keySet()) {
                int j = pursuerTable.get(i).iterator().next();
                if (pursuerTable.get(i).size() == 1) {
                    finalAssignment.put(i, j);
                    pursuerTable.remove(i);
                    for (Integer k : pursuerTable.keySet()) {
                        pursuerTable.get(k).remove(j);
                    }
                }

            }

//            System.out.println(" 2: " + pursuerTable.toString() + " and " + finalAssignment.toString());

            for (int i = 0; i < e; i++) {
                if (getUniquePursuer(pursuerTable, i) != null) {
                    finalAssignment.put(getUniquePursuer(pursuerTable, i), i);
                    pursuerTable.remove(getUniquePursuer(pursuerTable, i));
                    for (int j : pursuerTable.keySet()) {
                        pursuerTable.get(j).remove(i);
                        
                    }
                }
            }

//            System.out.println(" 3: " + pursuerTable.toString() + " and " + finalAssignment.toString());


            finalAssignment.putAll(getBestDistance(pursuerTable, pursuerPosition, evaderPosition).currentTable);


//            System.out.println(" 4: " + pursuerTable.toString() + " and " + finalAssignment.toString());

            return finalAssignment;
        } catch (NoSuchElementException ex) {
//            System.out.println("No possible assignment!");
            return null;
        }
    }

    // UTILITY METHODS
    
    /** Returns closest in a list of doubles to the double given. */
    public static int getClosestTo(Vector<Double> opponentPositions, Double position) {
        Double minDist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int j = 0; j < opponentPositions.size(); j++) {
            if (Math.abs(position - opponentPositions.get(j)) < minDist) {
                minDist = Math.abs(position - opponentPositions.get(j));
                minIndex = j;
            }
        }
        return minIndex;
    }
    
    /** Assigns pursuers given list of pursuer-evader assignments. */
    public static Vector<Double> getNewPursuerPositions(HashMap<Integer, Integer> assignment,
            Vector<Double> pursuerPosition, Vector<Double> evaderPosition, Simulation sim) {
        Vector<Double> result = new Vector<Double>();
        
        if (assignment == null) { return null; }
        double unitStep = sim.getPSpeed() * sim.getStepSize();
        double pPos;
        double assignedEvaderPosition;
        for (int i = 0; i < pursuerPosition.size(); i++) {
            pPos = pursuerPosition.get(i);
            if (assignment.keySet().contains(i)) {
                 assignedEvaderPosition = evaderPosition.get(assignment.get(i));
                result.add ( pPos + ( (assignedEvaderPosition < pPos) ? -unitStep : unitStep) );
            }
            else{
                result.add( pPos );
            }
        }
        return result;
    }

    public static Integer getUniquePursuer(HashMap<Integer, HashSet<Integer>> pursuerTable, Integer evader) {
        Integer result = null;
        for (Integer i : pursuerTable.keySet()) {
            if (pursuerTable.get(i).contains(evader)) {
                if (result == null) {
                    result = i;
                } else {
                    return null;
                }
            }
        }

        return result;
    }

    static class TableData {

        Double distance = Double.MAX_VALUE;
        HashMap currentTable = new HashMap<Integer, Integer>();
    }

    /** Returns minimal pursuer-evader assignment possible with given pursuerTable. */
    private static TableData getBestDistance(HashMap<Integer, HashSet<Integer>> pursuerTable, Vector<Double> pursuerPosition, Vector<Double> evaderPosition)
            throws NoSuchElementException {
//        System.out.println("   table input to getBestDistance: " + pursuerTable);
        if (pursuerTable.size() == 0){
            return new TableData();
        }
        HashMap<Integer, HashSet<Integer>> tempPursuerTable = new HashMap<Integer, HashSet<Integer>>();
        if (pursuerTable.size() == 1) {
            TableData result = new TableData();
            int i = pursuerTable.keySet().iterator().next();
            for (Integer j : pursuerTable.get(i)) {
                result.currentTable.put(i, j);
                result.distance = Math.abs(pursuerPosition.get(i) - evaderPosition.get(j));
            }
//            System.out.println(result.currentTable);
            return result;
        }
        TableData bestYet = new TableData();
        TableData current = new TableData();
        int i = pursuerTable.keySet().iterator().next();
        for (Integer j : pursuerTable.get(i)) {
            for (Integer k : pursuerTable.keySet()) {
                tempPursuerTable.put(k, (HashSet<Integer>) pursuerTable.get(k).clone());
            }
            tempPursuerTable.remove(i);
            for (Integer k : tempPursuerTable.keySet()) {
                tempPursuerTable.get(k).remove(j);
            }
            current = getBestDistance(tempPursuerTable, pursuerPosition, evaderPosition);
            current.distance += Math.abs(pursuerPosition.get(i) - evaderPosition.get(j));
            current.currentTable.put(i, j);
            if (current.distance < bestYet.distance) {
                bestYet = current;
            }

        }
//        System.out.println(bestYet.currentTable);
        return bestYet;
    }
}
