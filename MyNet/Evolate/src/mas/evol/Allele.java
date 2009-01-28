/**
 * Allele.java
 * Created on Dec 11, 2008
 */

package mas.evol;

import mas.Parameter;

/**
 * <p> Represents a single parameter of numeric type that can be mutated or crossed
 * with another such value. </p>
 * @author Elisha Peterson
 */
public abstract class Allele<N extends Number> extends Parameter<N> {
        
    /** Initializes with particular values. */
    public Allele(N min, N max, N value) { super(min, max, value); }
    
    public abstract Allele copy();
    public abstract Allele mutation(double prob);
    /** Uses a coinflip to determine which value to use. */
    public Allele<N> cross(Allele<N> a2) {
        return coinToss() ? copy() : a2.copy();
    }
    
    /** Generates a random boolean. */
    public static boolean coinToss(){return Math.random()<=.5; }
    
    /** Integer typed allele. */
    public static class Int extends Allele<Integer> {
        public Int(Integer min, Integer max, Integer value) { super(min, max, value); }        
        /** Returns direct copy of the value. */
        @Override
        public Allele copy() { return new Int(min,max,value); }
        /** Mutation adds or subtracts 1 with even probability. */
        @Override
        public Allele mutation(double prob) {
            if (Math.random() <= prob) {
                if (coinToss()) {
                    return new Int(min, max, Math.max(min, value-1));
                } else {
                    return new Int(min, max, Math.min(max, value+1));
                }
            }
            return copy();
        }
    }
    
    /** Double typed allele. */
    public static class Doub extends Allele<Double> {
        /** Radius of addMutation by addition */
        public double addInterval = 0.1;
        /** Radius of addMutation by multiplication */
        public double multInterval = 0.1;
        /** Probability of addMutation by addition (vs. multiplication) */
        public double addMutationProb = 0.5;
        
        /** Constructor */
        public Doub(Double min, Double max, Double value) { super(min, max, value); }        
        /** Returns direct copy of the value. */
        @Override
        public Allele copy() { return new Doub(min,max,value); }
        /** Mutation adds or subtracts 1 with even probability. */
        @Override
        public Allele mutation(double prob) {
            if (Math.random() <= prob) {
                return (Math.random() <= addMutationProb) ? 
                    new Doub(min,max,addMutation()) : 
                    new Doub(min,max,prodMutation());
            }
            return copy();
        }
        /** Returns random additive mutation in interval. */
        public double addMutation() {
            return Math.min(max, Math.max(min, addInterval*(2*Math.random()-1)));
        }
        /** Returns random product mutation in interval. */
        public double prodMutation() {
            return Math.min(max, Math.max(min, value*(1+multInterval*(2*Math.random()-1)) ));
        }
        /** Overrides to average values. */
        @Override
        public Allele<Double> cross(Allele<Double> a2) {
            return new Doub(Math.min(min,a2.min), Math.max(max,a2.max), (value+a2.value)/2);
        }
    }
        
    
    /** Double typed allele. */
    public static class Flt extends Allele<Float> {
        /** Radius of addMutation by addition */
        public float addInterval = 0.1f;
        /** Radius of addMutation by multiplication */
        public float multInterval = 0.1f;
        /** Probability of addMutation by addition (vs. multiplication) */
        public float addMutationProb = 0.5f;
        
        /** Constructor */
        public Flt(Float min, Float max, Float value) { super(min, max, value); }        
        /** Returns direct copy of the value. */
        @Override
        public Allele copy() { return new Flt(min,max,value); }
        /** Mutation adds or subtracts 1 with even probability. */
        @Override
        public Allele mutation(double prob) {
            if (Math.random() <= prob) {
                return (Math.random() <= addMutationProb) ? 
                    new Flt(min,max,addMutation()) : 
                    new Flt(min,max,prodMutation());
            }
            return copy();
        }
        /** Returns random additive mutation in interval. */
        public float addMutation() {
            return Math.min(max, Math.max(min, addInterval*(2*(float)Math.random()-1)));
        }
        /** Returns random product mutation in interval. */
        public float prodMutation() {
            return Math.min(max, Math.max(min, value*(1+multInterval*(2*(float)Math.random()-1)) ));
        }
        /** Override to average values. */
        @Override
        public Allele<Float> cross(Allele<Float> a2) {
            return new Flt(Math.min(min,a2.min), Math.max(max,a2.max), (value+a2.value)/2);
        }
    }
}
