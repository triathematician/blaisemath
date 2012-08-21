/**
 * Derivative.java
 * Created on Mar 11, 2008
 */

package scio.function;

import scio.coordinate.R2;
import scio.coordinate.R3;

/**
 *
 * @author Elisha Peterson
 */
public class Derivative {
    public static Double approximateDerivative(Function <Double,Double> function,double input,double tolerance) throws FunctionValueException{
        double step1=0.1;
        double value=function.getValue(input);
        double result=Double.MAX_VALUE;
        double leftResult=(function.getValue(input+step1)-value)/step1;
        while(Math.abs(leftResult-result)>tolerance){
            result=leftResult;
            step1=step1/10.;
            leftResult=(function.getValue(input+step1)-value)/step1;
        }
        
        step1=0.1;
        result=Double.MAX_VALUE;        
        double rightResult=(function.getValue(input+step1)-value)/step1;
        while(Math.abs(rightResult-result)>tolerance){
            result=rightResult;
            step1=step1/10.;
            rightResult=(function.getValue(input+step1)-value)/step1;
        }
        
        if(Math.abs(leftResult-rightResult)<tolerance){
            return leftResult;
        }
        return null;
    }
    
    public static R2 approximateDerivative(Function <Double,R2> function,double input,double tolerance) throws FunctionValueException{
        double step1=0.1;
        R2 value=function.getValue(input);
        R2 result=new R2(Double.MAX_VALUE,Double.MAX_VALUE);
        R2 leftResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        while(result.distance(leftResult)>tolerance){
            result=leftResult;
            step1=step1/10.;
            leftResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        }
        
        step1=0.1; 
        result=new R2(Double.MAX_VALUE,Double.MAX_VALUE);
        R2 rightResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        while(result.distance(rightResult)>tolerance){
            result=rightResult;
            step1=step1/10.;
            rightResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        }
        
        if(leftResult.distance(rightResult)<tolerance){
            return leftResult;
        }
        return null;
    }
    
    public static R3 approximateDerivative(Function <Double,R3> function,double input,double tolerance) throws FunctionValueException{
        double step1=0.01;
        R3 value=function.getValue(input);
        R3 result=new R3(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
        R3 leftResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        while(result.distanceTo(leftResult)>tolerance/2){
            result=leftResult;
            step1=step1/10.;
            leftResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        }
        
        step1=0.01; 
        result=new R3(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
        R3 rightResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        while(result.distanceTo(rightResult)>tolerance/2){
            result=rightResult;
            step1=step1/10.;
            rightResult=function.getValue(input+step1).minus(value).multipliedBy(1/step1);
        }
        
        if(leftResult.distanceTo(rightResult)<tolerance){
            return leftResult;
        }
        return null;
    }

    public static R2 approximateDerivativeTwo(Function<Double, R2> function, double input, double tolerance) throws FunctionValueException {
        double step1=0.01;
        R2 value=Derivative.approximateDerivative(function,input,tolerance);
        R2 result=new R2(Double.MAX_VALUE,Double.MAX_VALUE);
        R2 leftResult=Derivative.approximateDerivative(function,input+step1,tolerance).minus(value).multipliedBy(1/step1);
        while(result.distance(leftResult)>tolerance/2){
            result=leftResult;
            step1=step1/10.;
            leftResult=Derivative.approximateDerivative(function,input+step1,tolerance).minus(value).multipliedBy(1/step1);
        }
        
        step1=0.01; 
        result=new R2(Double.MAX_VALUE,Double.MAX_VALUE);
        R2 rightResult=Derivative.approximateDerivative(function,input+step1,tolerance).minus(value).multipliedBy(1/step1);
        while(result.distance(rightResult)>tolerance/2){
            result=rightResult;
            step1=step1/10.;
            rightResult=Derivative.approximateDerivative(function,input+step1,tolerance).minus(value).multipliedBy(1/step1);
        }
        
        if(leftResult.distance(rightResult)<tolerance){
            return leftResult;
        }
        return null;
    }
}
