/**
 * Statistics.java
 * Created on Apr 23, 2008
 */

package peg1d;

import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JTextArea;

/**
 * This class maintains a set of data for many simulations of the pursuit and evasion game.
 * Currently, will log the winning team and time to win for multiple simulation runs.
 * 
 * @author Elisha Peterson
 */
public class Statistics {
    Simulation sim;
    
    int numRuns = -1;
    Vector<SingleRun> results;
    
    public Statistics(Simulation sim){
        this.sim = sim;
    }
    
    public void runSeveral(int num){
        numRuns=num;
        results=new Vector<SingleRun>(num);
        for (int i = 0; i < num; i++) {
            sim.randomizePositions();
            if(sim.run()){
                results.add(new SingleRun(sim.log.pursuersWin?+1:-1,sim.log.time));
            }else{
                results.add(new SingleRun(-1,0));
            }
        }
    }
    
    /** Outputs results to standard output. */
    public void output(JTextArea mainArea,JTextArea dataArea){
        int npWin=0;
        int neWin=0;
        int neEasyWin = 0;
        for(SingleRun sr:results){
            if(sr.winner==1){npWin++;}
            else if(sr.winner==-1){
                if(sr.time==0){
                    neEasyWin++;
                }else{
                    neWin++;
                }
            }
        }
        try {mainArea.getDocument().remove(0, mainArea.getDocument().getLength()-1);}catch(Exception e){}
        mainArea.append("--New Statistical Data--\n");
        mainArea.append("With "+numRuns+" runs, the pursuers won "+npWin+" times "+
                "and the evaders won "+neWin+" times. In "+neEasyWin+" cases, evaders automatically won.\n");
        
        try {dataArea.getDocument().remove(0, mainArea.getDocument().getLength()-1);}catch(Exception e){}
        dataArea.append("Team \tTime\n");
        for(SingleRun sr:results){
            dataArea.append(sr.toString());
        }
    }
    
    
    // INNER CLASSES
    
    /** Class to store information for a single run. */
    public class SingleRun{
        /** Has value of +1 for pursuers, -1 for evaders */
        int winner;
        double time;

        public SingleRun(int winner, double time) {
            this.winner = winner;
            this.time = time;
        }
        
        public String toString(){
            return winner+"\t"+time+"\n";
        }
    }
}
