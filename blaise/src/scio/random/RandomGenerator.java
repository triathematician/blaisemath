/**
 * RandomGenerator.java
 * Created on Nov 25, 2008
 */

package scio.random;

/**
 * <p>
 * This interface specifies a class which generates random numbers of some coordinate type.
 * </p>
 * @author Elisha Peterson
 */
public interface RandomGenerator<C> {
    /** Generate a random number of the specified type. */
    public C getRValue();
    
    /** Represents the uniform distribution between 0 and 1. */
    public static RandomGenerator<Double> BASIC = new RandomGenerator<Double>(){
        public Double getRValue() { return Math.random(); }
    };
    
    /** Represents the standard Gaussian distribution. */
    public static RandomGenerator<Double> GAUSSIAN = new RandomGenerator<Double>(){
        public Double getRValue() {
            return (new java.util.Random()).nextGaussian();            
        }        
    };
    
    
    // SPECIALIZED CLASSES
        
    /** Represents a uniform distribution between two specified numbers. */
    public static class Uniform implements RandomGenerator<Double> {
        public double low=0.0;
        public double high=1.0;
        public Uniform(double low, double high) { this.low = low; this.high = high; }
        public Double getRValue() { return (high-low)*Math.random()+low; }
        public static Double getValue(double low, double high) { return (high-low)*Math.random()+low; }
    }
    
    /** Represents a normal distribution with specified mean and standard deviation. */
    public static class Normal implements RandomGenerator<Double> {
        public double mean=0.0;
        public double std=1.0;
        public Normal(double mean, double std) { this.mean = mean; this.std = std; }
        public Double getRValue() { return std*(new java.util.Random()).nextGaussian()+mean; }     
        public static double getValue(double mean, double std) { return std*(new java.util.Random()).nextGaussian()+mean; }
    }
}
