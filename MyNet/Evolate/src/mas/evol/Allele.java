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

    /** Returns copy of this allele */
    @Override
    public abstract Allele copy();

    /** Returns a mutation of this allele
     * @param error determines how large the mutation will be
     * @return new Allele with mutation */
    public abstract Allele mutation(double error);

    /** By default use a coinflip to determine which value to use. */
    public Allele<N> cross(Allele<N> a2) { return Math.random()<=.5 ? copy() : a2.copy(); }

    // OVERRIDING METHODS TO ENSURE PROPER RETURN TYPE

    /** Converts parameter type to allele type. */
    public static Allele getAllele(Parameter pm) {
        if (pm.min instanceof Integer) { return new Int((Integer)pm.min,(Integer)pm.max,(Integer)pm.value);
        } else if (pm.min instanceof Double) { return new Doub((Double)pm.min,(Double)pm.max,(Double)pm.value);
        } else if (pm.min instanceof Float) { return new Flt((Float)pm.min,(Float)pm.max,(Float)pm.value);
        }
        return null;
    }

    /** Returns new instance */
    public static Parameter getInstance(Number val) { return getAllele(Parameter.getInstance(val)); }
    /** Returns random instance within values. */
    @Override
    public Parameter getRandom() { return getAllele(super.getRandom()); }
    
    /** Integer typed allele. */
    public static class Int extends Allele<Integer> {
        public Int(Integer min, Integer max, Integer value) { super(min, max, value); }        
        /** Returns direct copy of the value. */
        @Override
        public Allele copy() { return new Int(min,max,value); }
        /** Mutation adds or subtracts 1,...,error with even probability. */
        @Override
        public Allele mutation(double error) {
            Integer diff = (int) Math.ceil(Math.random()*error);
            if (Math.random()<.5) { return new Int(min, max, Math.max(min, value-diff)); }
            return new Int(min, max, Math.min(max, value+diff));
        }
    }
    
    /** Double typed allele. */
    public static class Doub extends Allele<Double> {
        /** Constructor */
        public Doub(Double min, Double max, Double value) { super(min, max, value); }        
        /** Returns direct copy of the value. */
        @Override
        public Allele copy() { return new Doub(min,max,value); }
        /** Default mutation is a combination of adding and multiplying. */
        @Override
        public Allele mutation(double err) { return comboMutation(err, err); }
        /** Returns combination of addition error and multiplicative error. */
        public Allele comboMutation(double mErr, double aErr) {
            return new Doub(min,max,
                    Math.min(max, Math.max(min, value*(1+mErr*(2*Math.random()-1))+aErr*(2*Math.random()-1))));
        }
        /** Returns random additive mutation in interval. */
        public Allele addMutation(double err){
            return new Doub(min,max,
                    Math.min(max, Math.max(min, value+err*(2*Math.random()-1))));
        }
        /** Returns random product mutation in interval. */
        public Allele prodMutation(double err){
            return new Doub(min,max,
                    Math.min(max, Math.max(min, value*(1+err*(2*Math.random()-1)))));
        }
        /** Overrides to average values. */
        @Override
        public Allele<Double> cross(Allele<Double> a2) {
            return new Doub(Math.min(min,a2.min), Math.max(max,a2.max), (value+a2.value)/2);
        }
    }
        
    
    /** Double typed allele. */
    public static class Flt extends Allele<Float> {
        /** Constructor */
        public Flt(Float min, Float max, Float value) { super(min, max, value); }
        /** Returns direct copy of the value. */
        @Override
        public Allele copy() { return new Flt(min,max,value); }
        /** Default mutation is a combination of adding and multiplying. */
        @Override
        public Allele mutation(double err) { return comboMutation(err, err); }
        /** Returns combination of addition error and multiplicative error. */
        public Allele comboMutation(double mErr, double aErr) {
            return new Flt(min, max, Math.min(max,Math.max(min,
                    (float)(value*(1+mErr*(2*Math.random()-1))+aErr*(2*Math.random()-1))
                    )));
        }
        /** Returns random additive mutation in interval. */
        public Allele addMutation(double err){
            return new Flt(min, max, Math.min(max,Math.max(min,
                    (float)(value+err*(2*Math.random()-1))
                    )));
        }
        /** Returns random product mutation in interval. */
        public Allele prodMutation(double err){
            return new Flt(min,max,
                    Math.min(max, Math.max(min, (float)(value*(1+err*(2*Math.random()-1))))));
        }
        /** Overrides to average values. */
        @Override
        public Allele<Float> cross(Allele<Float> a2) {
            return new Flt(Math.min(min,a2.min), Math.max(max,a2.max), (value+a2.value)/2);
        }
    }
}
