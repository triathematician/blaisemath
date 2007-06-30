/*
 * BGeometry.java
 *
 * Created on March 8, 2007, 4:50 PM
 *
 * Abstract class for working with geometries... the window geometry is stored
 *   in a "Point" class while the actual geometry is referenced by an unknown
 *   type class "T".
 *
 * Event handling: listens/generates actions
 */

package Interface;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.Component;

public interface BGeometry<T> extends ActionListener {
    
// Patterns for window coordinates    
    
    public Point getWinMin();
    public Point getWinCtr();
    public Point getWinMax();
    public int getWinWidth();
    public int getWinHeight();
    
// Patterns for geometry coordinates
    
    public T setBounds(T min,T max);
    public T getGeoMin();
    public T getGeoCtr();
    public T getGeoMax();
    
// Coordinate transformation methods
    
    public Point2D.Double toWindow(T t);
    public T toGeometry(Point p);
    
// Methods for generating the transformation
    
    public void computeTransformation();
    
// Event handling
    
    public void addActionListener(ActionListener l);
    public void removeActionListener(ActionListener l);
    public void fireActionPerformed(String code);
}
