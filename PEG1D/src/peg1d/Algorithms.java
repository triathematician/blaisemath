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
        // evading elements always head to origin
        for (int i = 0; i < evaderPosition.size(); i++) {
            if (Math.abs(evaderPosition.get(i) - sim.getGoal()) < sim.getCaptureRange()) {
                result.add(evaderPosition.get(i));
            } else if (evaderPosition.get(i) < sim.getGoal()) {
                result.add(evaderPosition.get(i) + sim.getStepSize() * sim.getESpeed());
            } else {
                result.add(evaderPosition.get(i) - sim.getStepSize() * sim.getESpeed());
            }
        }
        return result;
    }

    // PURSUER ALGORITHMS      
    /** Decide pursuer directions */
    public static Vector<Double> pursuersTowardClosest(Vector<Double> pursuerPosition, Vector<Double> evaderPosition, Simulation sim, int curStep) {
        // pursuing elements chase closest evader
        Vector<Double> result = new Vector<Double>();
        for (int i = 0; i < pursuerPosition.size(); i++) {
            Double closestEvader = getClosestTo(evaderPosition, pursuerPosition.get(i));
            if (closestEvader < pursuerPosition.get(i)) {
                result.add(pursuerPosition.get(i) - sim.getPSpeed() * sim.getStepSize());
            } else {
                result.add(pursuerPosition.get(i) + sim.getPSpeed() * sim.getStepSize());
            }
        }
        return result;
    }

    public static Vector<Double> pursuers_DJ(Vector<Double> pursuerPosition, Vector<Double> evaderPosition, Vector<Double> evaderDirection, Simulation sim, int curStep) {
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

            Vector<Double> move = new Vector<Double>();
            for (int i = 0; i < p; i++) {
                if (finalAssignment.keySet().contains(i)) {
                    Double assignedEvaderPosition = evaderPosition.get(finalAssignment.get(i));
                    if (assignedEvaderPosition < pursuerPosition.get(i)) {
                        move.add(pursuerPosition.get(i) - sim.getPSpeed() * sim.getStepSize());
                    } else {
                        move.add(pursuerPosition.get(i) + sim.getPSpeed() * sim.getStepSize());
                    }
                }
                else{
                    move.add(pursuerPosition.get(i));
                }
            }

            return move;
        } catch (NoSuchElementException ex) {
//            System.out.println("No possible assignment!");
            return null;
        }
    }

    // UTILITY METHODS
    /** Returns closest in a list of doubles to the double given. */
    public static Double getClosestTo(Vector<Double> opponentPositions, Double position) {
        Double minDist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int j = 0; j < opponentPositions.size(); j++) {
            if (Math.abs(position - opponentPositions.get(j)) < minDist) {
                minDist = Math.abs(position - opponentPositions.get(j));
                minIndex = j;
            }
        }
        return opponentPositions.get(minIndex);
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

    private static TableData getBestDistance(HashMap<Integer, HashSet<Integer>> pursuerTable, Vector<Double> pursuerPosition, Vector<Double> evaderPosition)
            throws NoSuchElementException {
//        System.out.println("   table input to getBestDistance: " + pursuerTable);
        if (pursuerTable.size() == 0){
            return new TableData();
        }
        HashMap<Integer, HashSet<Integer>> otherTable = new HashMap<Integer, HashSet<Integer>>();
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
                otherTable.put(k, (HashSet<Integer>) pursuerTable.get(k).clone());
            }
            otherTable.remove(i);
            for (Integer k : otherTable.keySet()) {
                otherTable.get(k).remove(j);
            }
            current = getBestDistance(otherTable, pursuerPosition, evaderPosition);
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
