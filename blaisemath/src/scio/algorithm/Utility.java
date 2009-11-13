/*
 * Utility.java
 * Created on Oct 18, 2007, 12:46:36 PM
 */

package scio.algorithm;

import java.util.Vector;

/**
 * Utility methods for plotting.
 * @author ae3263
 */
public class Utility {
    
    /** What I'm trying to get at in the methods below is a way to place the lengths of a vector field, for example, between a couple very natural lengths,
     * so that in looking at the plot the arrows aren't too long or too short. Something aesthetically pleasing and demonstrative of the important qualities of
     * the data.
     * 
     * Another thing... I want to be able to automatically determine optimal plot scales, so that plotting a function adjusts the scale automatically, perhaps not
     * including all of the data set in order to capture the more important parts of the data set.
     * 
     * It seems likely that in the latter task a log scale may be more appropriate to consider the true scale of the data. For example, when plotting x^3-3x between
     * -10 and 10, it seems best to limit the y-axis scale somewhat... otherwise the minima are lost in the big picture. Not really important, but something to work
     * toward.
     */
    
    
    /** Stores an average and standard deviation of a data set. */
    public static class NStat{
        public NStat(){norm=0;dev=0;}
        double norm;
        double dev;
    }
    /** Computes average and standard deviation of d vector of doubles. */
    public static NStat getStat(Vector<Double> normalData){
        NStat result=new NStat();
        for(Double d:normalData){result.norm+=d;}
        result.norm=result.norm/normalData.size();
        for(Double d:normalData){result.dev+=(d-result.norm)*(d-result.norm);}
        result.dev=Math.sqrt(result.dev/normalData.size());
        return result;
    }
    /** Given normal distribution, places most results between 0 and scale, with those within 2 standard deviations representing the bulk of the spread.
     * This might be used to compute optimal upper and lower bounds for a plot window. */
    public static double getNormed(double value, double scale, NStat data){
        return scale/(1+Math.exp(-2*(value-data.norm)/data.dev));
    }
    
    // TEST
    public static void main(String[] args) {
        Vector<Double> test=new Vector<Double>();
        for(int i=0;i<100;i++){
            test.add(.0001*i*i*i-.3*i);
        }
        System.out.println("in function: .01x^3-.3x");
        System.out.println("resulting norms:");
        NStat stat=getStat(test);
        System.out.println("  avg: "+stat.norm+", dev: "+stat.dev);
        for(int i=0;i<100;i++){
            System.out.println("for x="+i+", the normed value is "+getNormed(i,10,stat));
        }
    }
}
