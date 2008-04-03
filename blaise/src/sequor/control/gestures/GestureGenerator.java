/**
 * GestureGenerator.java
 * Created on Mar 25, 2008
 */

package sequor.control.gestures;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Vector;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;
import scio.matrix.HashMatrix;
import scio.random.Markov;

/**
 * Class contains code for implementing mouse gestures.
 * @author Elisha Peterson
 */
public abstract class GestureGenerator {
    
    public abstract  String[] getStates();
    public abstract HashMap<String,Double> getStartProb();
    public abstract HashMatrix<String,Double> getTransProb();
    public abstract HashMap<String,Function<R2,Double>> getEmitProb();
    
    public Vector<String> computePath(R2[] observations) throws FunctionValueException{
        return new Markov<String,R2>().forwardViterbi(
                observations, 
                getStates(), 
                getStartProb(), 
                getTransProb(), 
                getEmitProb()).vitPath;
    }
    
    // HELPER METHODS
    
    /** Returns probability of a point occurring given a movement. */
    public static Double getMoveProb(R2 pt){return pt.magnitude()/50;}
    /** Returns probability of an angle occurring given a movement. Zero if the angle difference is more than sensitivity. */
    public static Double getThetaProb(R2 pt,double theta,double sensitivity){
        theta=theta % (2*Math.PI);
        double ptheta=pt.angle() % (2*Math.PI);
        double dTheta=Math.min(Math.abs(ptheta-theta),Math.min(Math.abs(ptheta+2*Math.PI-theta),Math.abs(ptheta-2*Math.PI-theta)));
        if(dTheta>sensitivity){return 0.0;}
        if(sensitivity==0){return dTheta==0 ? 1 : 0.0;}
        return 1/sensitivity*(1-dTheta/sensitivity);
    }
    
    
    // POST-PROCESSING
    
    /** Removes duplicate elements in a vector of strings. */
    public Vector<Interval<String>> removeDuplicates(Vector<String> input){
        Vector<Interval<String>> result=new Vector<Interval<String>>();
        if(input.size()==0){return result;}
        result.add(new Interval<String>(input.firstElement(),0,0));
        for(int i=0;i<input.size();i++){
            if(!input.get(i).equals(result.lastElement().value)){
                result.lastElement().stop=i-1;
                result.add(new Interval(input.get(i),i,i));
            }
        }
        result.lastElement().stop=input.size()-1;
        return result;
    }
    
    /** Removes duplicate elements in a vector of strings; uses start/stop values from "intervals" */
    public Vector<Interval<String>> removeDuplicates(Vector<String> input,Vector<Interval<Integer>> intervals){
        if(input.size()==0 || intervals.size()==0){return null;}
        Vector<Interval<String>> result=new Vector<Interval<String>>();
        result.add(new Interval<String>(input.firstElement(),intervals.get(0).start,intervals.get(0).stop));
        for(int i=0;i<input.size();i++){
            if(!input.get(i).equals(result.lastElement().value)){
                result.lastElement().stop=intervals.get(i-1).stop;
                result.add(new Interval(input.get(i),intervals.get(i).start,intervals.get(i).stop));
            }
        }
        result.lastElement().stop=intervals.lastElement().stop;
        return result;
    }
    
    public class Interval<V>{
        public V value;
        public int start;
        public int stop;
        public Interval(V value,int start,int stop){this.value=value;this.start=start;this.stop=stop;}
        @Override
        public String toString(){return value+" ("+start+"-"+stop+")";}
    }
    
    public static Vector<Integer> getIntVector(Vector<Interval<Integer>> input){
        Vector result=new Vector();
        for(Interval iv:input){result.add(iv.value);}
        return result;
    }    
    public static Vector<String> getStrVector(Vector<Interval<String>> input){
        Vector result=new Vector();
        for(Interval iv:input){result.add(iv.value);}
        return result;
    }
    
    /** Clips path to just the essential elements. */
    public Vector<Interval<String>> processOutput(Vector<String> output,String noneState){
        if(output.size()==0){return null;}
        // compute differences (ignoring static states)      
        HashMap<String,Integer> map=getTransProb().getMap();
        Vector<Interval<Integer>> diffSet=new Vector<Interval<Integer>>();
        int last=0;
        while(output.get(last).equals(noneState)){last++;} // ignore the static state
        for(int next=last+1;next<output.size();next++){
            if(output.get(next).equals(noneState)){continue;} // ignore the static state
            int diff=map.get(output.get(next))-map.get(output.get(last));
            if(diff>getStates().length/2){diff-=(getStates().length-1);}
            if(-diff>getStates().length/2){diff+=(getStates().length-1);}
            diffSet.add(new Interval<Integer>(diff,last,next));
            last=next;
        }
        
        //System.out.println(" : "+diffSet);
        
        Vector<Interval<String>> arcOutput=removeDuplicates(ShapeGestureGenerator.compute(getIntVector(diffSet)),diffSet);
        if(arcOutput==null){return null;}
        
        // adjust endpoints of the arcs to correspond to the observed data
        arcOutput.get(0).stop++;
        for(int i=1;i<arcOutput.size();i++){            
            arcOutput.get(i).start++;
            arcOutput.get(i).stop++;
        }
        
        //System.out.println("  : "+arcOutput);
        
        return arcOutput;
    }
    
    public Shape getArcOutput(Vector<Interval<String>> arcInput,Vector<R2> observed){        
        if(arcInput==null){return null;}
        // split up large arcs (more than 6 points)
        Vector<Interval<String>> arcOutput=new Vector<Interval<String>>();
        for(Interval<String> iv:arcInput){
            if(iv.value.equals("Corner")){continue;}
            if(iv.value.equals("Line") || (iv.stop-iv.start)<5){
                arcOutput.add(new Interval<String>(iv.value,iv.start,iv.stop));
            }else{
                for(int i=0;i<(iv.stop-iv.start)/5;i++){
                    arcOutput.add(new Interval<String>(iv.value,iv.start+i*5,Math.min(iv.stop,iv.start+(i+1)*5)));
                }
            }
        }
        // draw curve
        Path2D.Double result=new Path2D.Double();
        result.moveTo(observed.firstElement().x,observed.firstElement().y);
        R2 start;
        R2 stop=null;
        R2 mid1,mid2;
        for(Interval<String> iv:arcOutput){
            start=observed.get(iv.start);
            stop=observed.get(iv.stop);
            mid1=start.plus(observed.get(iv.start+1).minus(observed.get(Math.max(0,iv.start-1))).scaledToLength(observed.get(iv.start+(iv.stop+1-iv.start)/3).minus(start).magnitude()));
            mid2=stop.plus(observed.get(iv.stop-1).minus(observed.get(Math.min(iv.stop+1,observed.size()-1))).scaledToLength(observed.get(iv.start+2*(iv.stop+1-iv.start)/3).minus(stop).magnitude()));
            if(iv.value.equals("Line")){
                result.lineTo(start.x,start.y);
                result.lineTo(stop.x,stop.y);
            }else if(iv.value.equals("ArcLeft") || iv.value.equals("ArcRight")){
                result.lineTo(start.x,start.y);
                result.curveTo(mid1.x,mid1.y,mid2.x,mid2.y,stop.x,stop.y);
            }
        }    
        if(observed.firstElement().distance(observed.lastElement())<20){result.closePath();}
        return result;
    }
    
    
    final static String[] ccwCircle = {"N","E","S","W"};    
    final static String[] cwCircle = {"N","W","S","E"};
    final static String[] zagRight = {"N","E","S","E"};
    final static String[] zagLeft = {"N","W","S","W"};
    final static String[] leftRight = {"W","E"};
    final static String[] leftRight2 = {"W","0","E"};
    final static String[] rightLeft = {"E","W"};
    final static String[] rightLeft2 = {"E","0","W"};
    
    public static HashMap<String[],String> getStringMap(){
        HashMap<String[],String> gMap=new HashMap<String[],String>();
        gMap.put(ccwCircle,"circle");
        gMap.put(cwCircle,"circle");
        gMap.put(zagRight,"zagright");
        gMap.put(zagLeft,"zagleft");
        gMap.put(leftRight,"leftright");
        gMap.put(rightLeft,"rightleft");
        gMap.put(leftRight2,"leftright");
        gMap.put(rightLeft2,"rightleft");
        return gMap;
    }
    
    public static HashMap<String[],Object> getShapeMap(){
        HashMap<String[],Object> gMap=new HashMap<String[],Object>();
        gMap.put(ccwCircle,new Ellipse2D.Double(10,10,50,50));
        gMap.put(cwCircle,new Ellipse2D.Double(20,20,30,30));
        gMap.put(zagRight,new Ellipse2D.Double(50,50,100,10));
        gMap.put(zagLeft,new Ellipse2D.Double(0,50,100,10));
        return gMap;
    }
    
    
    
    // In the remainder of this class we implement functions necessary for the computations.
    // Each function represents the probability that a given point is expressed given the underlying state.
    // Thus, the probability that a given point occurs given no movement approaches 0 as r increases, etc.

    /** Function representing no movement. */
    public static Function<R2,Double> staticFunction=new Function<R2,Double>(){
        public Double getValue(R2 pt) throws FunctionValueException {return Math.max(0.0,0.4*(1-pt.magnitude()/5));}
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
    };
    
    /** Returns function representing movement at a particular angle. */
    public static class AngleFunction implements Function<R2,Double> {
        double theta;
        double sens;
        public AngleFunction(double theta,double sens){this.theta=theta;this.sens=sens;}
        public Double getValue(R2 x) throws FunctionValueException {return getMoveProb(x)*getThetaProb(x,theta,sens);}
        public Vector<Double> getValue(Vector<R2> xx) throws FunctionValueException {return null;}
    }
}