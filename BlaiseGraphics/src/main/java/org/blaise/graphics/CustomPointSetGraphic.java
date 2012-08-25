/**
 * CustomPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.util.Delegator;
import org.blaise.util.PointManager;

/**
 * Manages a collection of points that are maintained as separate {@link Graphic}s,
 * and therefore fully customizable. Point locations are handled by a {@link PointManager}.
 * 
 * @param <Src> the type of object being displayed
 *
 * @see PointStyle
 * @see BasicPointSetGraphic
 *
 * @author Elisha Peterson
 */
public class CustomPointSetGraphic<Src> extends GraphicComposite implements PropertyChangeListener {

    /** Manages points and their styles */
    protected ObjectStyler<Src, PointStyle> styler = new ObjectStyler<Src, PointStyle>();

    /** Point manager. Maintains objects and their locations, and enables mouse dragging. */
    protected final PointManager<Src, Point2D> pointManager =
        new PointManager<Src, Point2D>( 
                new Delegator<Src, Point2D>(){ 
                    public Point2D of(Src src) { return new Point2D.Double(); } 
                } 
            );

    /**
     * Construct with no points
     */
    public CustomPointSetGraphic() {
        this((Set<Src>) Collections.emptySet(), null);
    }

    /**
     * Construct with no style (will use the default)
     * @param objects the source objects
     * @param delegate used for point placement
     */
    public CustomPointSetGraphic(Set<? extends Src> objects, Delegator<Src, Point2D> delegate) {
        if (delegate != null) {
            pointManager.setInitialPointDelegate(delegate);
        }
        pointManager.setObjects(objects);
        pointManager.addPropertyChangeListener(this);
        initDragging();
    }
    
    protected void initDragging() {
        IndexedPointBeanDragger dragger = new IndexedPointBeanDragger(pointManager);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    @Override
    public String toString() {
        return "Point Set";
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equalsIgnoreCase("locationMap")) {
            Map<Src,Point2D> locs = (Map<Src,Point2D>) e.getNewValue();
            List<Graphic> newPts = new ArrayList<Graphic>();
            for (Entry<Src,Point2D> en : locs.entrySet()) {
                newPts.add(new DelegatingPointGraphic<Src>(en.getKey(), en.getValue()));
            }
        }
    }
    
    /**
     * Returns object that handles point locations
     * @return point location manager
     */
    public PointManager<Src, Point2D> getPointManager() {
        return pointManager;
    }

    /**
     * Return source objects
     * @return source objects
     */
    public Set<? extends Src> getObjects() {
        return pointManager.getObjects();
    }

    /**
     * Sets source objects
     * @param objects the objects
     */
    public void setObjects(Set<? extends Src> objects) {
        pointManager.setObjects(objects);
    }

    /**
     * Returns object used to style points
     * @return styler object styler
     */
    public ObjectStyler<Src, PointStyle> getStyler() {
        return styler;
    }

    /**
     * Sets object used to style points
     * @param styler object styler
     */
    public void setStyler(ObjectStyler<Src, PointStyle> styler) {
        this.styler = styler;
        fireGraphicChanged();
    }

}
