/*
 * RelativePointBean.java
 * Created Jan 229, 2011
 */
package utils;

/**
 * Interface that can get and set a point
 */
public interface RelativePointBean<C> {

    /** @return the point */
    public C getPoint();
    /** Sets the point */
    public void setPoint(C point);
    /** Sets the point by movement from an initial point */
    public void setPoint(C initial, C dragStart, C dragFinish);
}
