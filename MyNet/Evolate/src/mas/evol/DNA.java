/**
 * DNA.java
 * Created on Dec 9, 2008
 */

package mas.evol;

import mas.Agent;
import mas.Parameter;
import mas.ParameterSpace;

/**
 * <p>Extension of parameter space with variables that can be recombined.
 * Allele's added to the space will be recombined; other types will not.</p>
 * @author Elisha Peterson
 */
public class DNA extends ParameterSpace {
 
    /** Default constructor */
    public DNA(){}

    /** Override to return class of this type. */
    public static DNA getInstance() { return new DNA(); }

    /** Returns agent with this DNA and a certain amount of error introduced.
     * @param pointProbab the probability that each allele will be mutated
     * @param error the error to use in mutation
     * @return a DNA strand with the mutated result
     */
    public DNA mutation(float pointProb, float err){
        DNA result = (DNA) copy();
        for (Parameter pm : result.p.values()) {
            if (Math.random() < pointProb) { ((Allele)pm).mutate(err); }
        }
        return result;
    }

    /** Returns DNA cross of two different agents. */
    public static DNA cross(DNA dna1, DNA dna2, float err){
        DNA result = new DNA();
        for (String s : dna1.p.keySet()) {
            result.set(s, ((Allele)dna1.get(s)).cross((Allele)dna2.get(s)).mutation(err));
        }
        return result;
    }    

    // Override methods to ensure classes have proper types

    /** Sets parameter. */
    @Override
    public Parameter set(String key, Number val) {
        Allele pm = (Allele) Allele.getInstance(val);
        p.put(key, pm);
        return pm;
    }
    
    /** Returns copy */
    @Override
    public ParameterSpace copy() {
        DNA ps2 = new DNA();
        for(String s:p.keySet()) { ps2.p.put(s,((Allele)get(s)).copy()); }
        return ps2;
    }

    /** Returns randomized parameters. */
    @Override
    public ParameterSpace getRandom() {
        DNA ps2 = new DNA();
        for(String s:p.keySet()) { ps2.p.put(s,((Allele)get(s)).getRandom()); }
        return ps2;
    }
}
