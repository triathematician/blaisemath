/*
 * Closest.java
 * Created on Sep 4, 2007, 2:46:01 PM
 */
package tasking;

import drasys.or.matrix.VectorI;
import drasys.or.mp.Constraint;
import drasys.or.mp.ConstraintI;
import drasys.or.mp.ConvergenceException;
import drasys.or.mp.DuplicateException;
import drasys.or.mp.InfeasibleException;
import drasys.or.mp.InvalidException;
import drasys.or.mp.NoSolutionException;
import drasys.or.mp.NotFoundException;
import drasys.or.mp.Problem;
import drasys.or.mp.ScaleException;
import drasys.or.mp.SizableProblemI;
import drasys.or.mp.UnboundedException;
import drasys.or.mp.Variable;
import drasys.or.mp.VariableI;
import drasys.or.mp.lp.DenseSimplex;
import drasys.or.mp.lp.LinearProgrammingI;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.Agent;
import java.util.Vector;
import simulation.Team;
import utility.DistanceTable;

/**
 * @author Lucas Gebhart w/ help of Dr. Elisha Peterson and the following package
 * drasys.or.mp.lp  Freeware, available at http://opsresearch.com
 *
 * <br><br>
 * This class 
 */
public class ControlOptimal extends TaskGenerator {

    public ControlOptimal(Team target, int type) {
        super(target, type);
    }

    /** Assigns tasks (prey) to n/k pursuers. The algorithm uses the complete table
     * of distances between pursuers and evaders to map the optimal set of pursuers and evaders. The simplex
     * method is used to generate the optimal pairing of pursuers to evaders based on minimizing overall distance
     * Each pursuer chases its assigned evader until all of the pursuers or evaders are inactive
     * @param team pursuing team
     */
    @Override
    public void generate(Collection<Agent> team, DistanceTable table, double priority) {
        try {
            Vector<Agent> ps = new Vector<Agent>(team);
            Vector<Agent> es = new Vector<Agent>(target.getActiveAgents());
//  Table has distances for every active pursuer to every active evader. 
  // this allows for the calculations in the algorithm to go quickly.
            DistanceTable dist = new DistanceTable(ps, es);
//   ps and es . size are defined elsewhere as the # of active members of the pursueing and evading teams
            int numberOfPursuers = ps.size();
            int numberOfEvaders = es.size();
//            This ends the program when there are no pursuers or evaders remaining. Fixes convergence issues.
            if(numberOfPursuers == 0 || numberOfEvaders == 0) { return; }
 //  This defines (how many constaints, how many variables)
            SizableProblemI prob = new Problem(numberOfPursuers + numberOfEvaders, numberOfPursuers * numberOfEvaders);
            prob.getMetadata().put("lp.isMaximize", "false");
// For every pursuer, iterate through every evader and create a variable
            //the variable takes the name x i j  where i and j are the number of the pursuer and
            // evader respectively. The coefficient in the objective constraint is equal to the distance from
            // the distance table discussed above. This will be minimized later.
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
// creates a constraint, in the case that there are more pursuers than evaders
            //that every evader is chased by exactly one pursuer. Extra pursuers are not given an assignment
            // in this algorithm, but perform another tasking in the more than one goal case.
            if (numberOfPursuers > numberOfEvaders) {
                for (int j = 0; j < numberOfEvaders; j++) {
                    try {
                        prob.newConstraint(j + "is chased").setType(GTE).setRightHandSide(1.0);
                    } catch (DuplicateException ex) {
                        Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
  // creates a constraint, in the case that there are more pursuers than evaders
  //that every pursuer can only chase, at the most, one evader.
                for (int i = 0; i < numberOfPursuers; i++) {
                    try {
                        prob.newConstraint(i + "can only chase one evader").setType(EQL).setRightHandSide(1.0);
                    } catch (DuplicateException ex) {
                        Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else if (numberOfPursuers == numberOfEvaders) {
                for (int j = 0; j < numberOfEvaders; j++) {
                    try {
                        prob.newConstraint(j + "is chased").setType(EQL).setRightHandSide(1.0);
                    } catch (DuplicateException ex) {
                        Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
  // creates a constraint, in the case that there are more pursuers than evaders
  //that every pursuer can only chase, at the most, one evader.
                for (int i = 0; i < numberOfPursuers; i++) {
                    try {
                        prob.newConstraint(i + "can only chase one evader").setType(EQL).setRightHandSide(1.0);
                    } catch (DuplicateException ex) {
                        Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
     // if there are less pursuers than evaders, the constraints are different.
   // every pursuer must chase someone, and every evader can only be chased by one pursuer.
   // this fixed an issue of under constrained problem, where pursuers would chase the same evader.
                else {
                for (int i = 0; i < numberOfPursuers; i++) {
                    try {

                        prob.newConstraint(i + "is chasing").setType(EQL).setRightHandSide(1.0);
                    } catch (DuplicateException ex) {
                        Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for (int j = 0; j < numberOfEvaders; j++) {
                    try {
                        prob.newConstraint(j + "only chased by one").setType(LTE).setRightHandSide(1.0);
                    } catch (DuplicateException ex) {
                        Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
// the following lines assign the coefficient to respective variables in their relevant constraints.
         
            for (int i = 0; i < numberOfPursuers; i++) {
                for (int j = 0; j < numberOfEvaders; j++) {
                    try {
                        prob.setCoefficientAt(i + "can only chase one evader", "x" + i + " " + j, 1.0);
                        prob.setCoefficientAt(j + "is chased", "x" + i + " " + j, 1.0);
                    } catch (NotFoundException ex) {
                       // Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        prob.setCoefficientAt(i + "is chasing", "x" + i + " " + j, 1.0);
                        prob.setCoefficientAt(j +"only chased by one", "x" + i + " " + j, 1.0);
                    } catch (NotFoundException ex) {
                       // Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
   // solves the above defined LP using the Simplex method
            LinearProgrammingI lp = new DenseSimplex(prob);
            double ans = lp.solve();
            VectorI solution = lp.getSolution();
            
// The solution vector is a series of 1's and 0's. 1's represent a given pursuer chasing a given evader.
// once assigned someone to chase, the evader moves out in the direction of that evader.
            
            for (int i = 0; i < numberOfPursuers; i++) {
                for (int j = 0; j < numberOfEvaders; j++) {
                    if (solution.elementAt(j + i * numberOfEvaders)==1) {
                        ps.get(i).assign(new Task(this, es.get(j).loc, goalType, priority));
                    }
                }
            }

            } catch  (NoSolutionException ex) {
            Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnboundedException ex) {
            Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InfeasibleException ex) {
            Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConvergenceException ex) {
            Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScaleException ex) {
            Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidException ex) {
            Logger.getLogger(ControlOptimal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}


    
    
    