/*
 * V2.java
 * Created on Oct 31, 2007, 8:45:24 AM
 */

package scio.coordinate;

/**
 * Represents a vector in R2 (with base poitn and length)
 * @author ae3263
 */
public class V2 extends R2{
    public R2 v;
    public V2(){super();v=new R2();}
    public V2(R2 point){super(point);v=new R2();}
    public V2(R2 point,R2 dir){super(point);v=dir;}
    public R2 getEnd(){return this.plus(v);}
    public R2 getStart(){return (R2)this;}
}
