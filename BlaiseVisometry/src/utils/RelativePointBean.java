/*
 * RelativePointBean.java
 * Created Jan 229, 2011
 */
package utils;

/**
 * Interface that can get and set a point
 * 
 * @author Elisha Peterson
 */
public interface RelativePointBean<C> {

    /** 
     * Return the point
     * @return the point 
     */
    public C getPoint();
    /** 
     * Sets the point 
     * @param point new point value
     */
    public void setPoint(C point);
    /** 
     * Sets the point by movement from an initial point 
     * @param initial starting position
     * @param dragStart start of drag
     * @param dragFinish end of drag
     */
    public void setPoint(C initial, C dragStart, C dragFinish);
}
