/*
 * Closest.java
 * Created on Sep 4, 2007, 2:46:01 PM
 */
package tasking;

import drasys.or.mp.Constraint;
import drasys.or.mp.ConstraintI;
import drasys.or.mp.DuplicateException;
import drasys.or.mp.NotFoundException;
import drasys.or.mp.Problem;
import drasys.or.mp.SizableProblemI;
import drasys.or.mp.Variable;
import drasys.or.mp.VariableI;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.Agent;
import java.util.Vector;
import simulation.Team;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 * <br><br>
 * This class
 */
public class ControlOptimal extends TaskGenerator {

    public ControlOptimal(Team target, int type) {
        super(target, type);
    }

    /** Assigns tasks (prey) to n/k pursuers. The algorithm uses the complete table
     * of distances between pursuers and evaders to map the closest
     * pursuer-prey pair. This process is repeated until there are no more
     * pursuers or prey available. If there are leftover pursuers, they are
     * assigned to the closest prey.
     * @param team pursuing team
     */
    @Override
    public void generate(Collection<Agent> team, DistanceTable table, double priority) {
        Vector<Agent> ps = new Vector<Agent>(team);
        Vector<Agent> es = new Vector<Agent>(target.getActiveAgents());
        DistanceTable dist = new DistanceTable(ps, es);
        int numberOfPursuers = ps.size();
        int numberOfEvaders = es.size();

        //  This defines (how many constaints, how many variables)      
        SizableProblemI prob = new Problem(numberOfPursuers + numberOfEvaders,
                numberOfPursuers * numberOfEvaders);
        prob.getMetadata().put("lp.isMinimize", "true");

        for (int i = 0; i < numberOfPursuers; i++) {
            for (int j = 0; j < numberOfEvaders; j++) {
                try {
                    prob.newVariable("x" + i + " " + j).setObjectiveCoefficient(dist.get(i, j));
                } catch (DuplicateException ex) {
                    Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        final byte LTE = Constraint.LESS;
        final byte GTE = Constraint.GREATER;
        final byte EQL = Constraint.EQUAL;

        if (numberOfPursuers >= numberOfEvaders) {
            for (int j = 0; j < numberOfEvaders; j++) {
                try {
                    prob.newConstraint(j + "is chased").setType(GTE).setRightHandSide(1.0);
                } catch (DuplicateException ex) {
                    Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (int i = 0; i < numberOfPursuers; i++) {
                try {
                    prob.newConstraint(i + "can only chase one evader").setType(LTE).setRightHandSide(1.0);
                } catch (DuplicateException ex) {
                    Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (int i = 0; i < numberOfPursuers; i++) {
                try {

                    prob.newConstraint(i + "is chasing").setType(EQL).setRightHandSide(1.0);
                } catch (DuplicateException ex) {
                    Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        for (int i = 0; i < numberOfPursuers; i++) {
            for (int j = 0; j < numberOfEvaders; j++) {
                try {
                    prob.setCoefficientAt(i + "can only chase one evader", "x" + i + " " + j, 1.0);
                    prob.setCoefficientAt(j + "is chased", "x" + i + " " + j, 1.0);                    
                } catch (NotFoundException ex) {
                    Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                }
                try{
                    prob.setCoefficientAt(i + "is chasing", "x" + i + " " + j, 1.0);                    
                } catch (NotFoundException ex) {
                    Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            
        }

    }
}


    
    
    