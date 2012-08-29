/**
 * DelegatingPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.util.Delegator;
import org.blaise.util.Delegators;
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
public class DelegatingPointSetGraphic<Src> extends GraphicComposite implements ChangeListener, PropertyChangeListener {

    /** Manages locations of points */
    protected final PointManager<Src,Point2D> manager = new PointManager<Src,Point2D>();
    /** Objects for individual points */
    protected final Map<Src, DelegatingPointGraphic<Src>> points = new HashMap<Src, DelegatingPointGraphic<Src>>();
    /** Generates styles for graphics */
    protected ObjectStyler<Src, PointStyle> styler = new ObjectStyler<Src, PointStyle>();

    /**
     * Construct with no points
     */
    public DelegatingPointSetGraphic() {
        this(Collections.EMPTY_MAP);
    }

    /**
     * Construct with no style (will use the default)
     * @param objects the source objects
     * @param delegate used for point placement
     */
    public DelegatingPointSetGraphic(Set<? extends Src> objects, Delegator<Src, Point2D> delegate) {
        this(Delegators.apply(delegate, objects));
    }
    

    /**
     * Construct with source objects and locations as a map
     * @param map locations
     */
    public DelegatingPointSetGraphic(Map<Src, Point2D> map) {
        styler.setTipDelegate(new Delegator<Src,String>(){
            public String of(Src src) {
                return src+"";
            }
        });
        manager.addPropertyChangeListener(this);
        addObjects(map);
    }

    @Override
    public String toString() {
        return "DelegatingPointSet";
    }
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    //
    // EVENT HANDLERS
    //

    public synchronized void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof DelegatingPointGraphic) {
            DelegatingPointGraphic<Src> dpg = (DelegatingPointGraphic<Src>) e.getSource();
            manager.update(dpg.getSourceObject(), dpg.getPoint());
        }
    }
    
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        // when the point manager moves points
        if (evt.getSource() == manager) {
            if ("add".equals(evt.getPropertyName())) {
                add((Map<Src,Point2D>) evt.getNewValue());
            } else if ("cache".equals(evt.getPropertyName())) {
                Map<Src,Point2D> toRemove = (Map<Src,Point2D>) evt.getNewValue();
                remove(toRemove.keySet());
            } else if ("remove".equals(evt.getPropertyName())) {
                remove((Set<Src>) evt.getNewValue());
            }
        }
    }
    
    /** Only execute when manager has changed */
    private synchronized void add(Map<Src,Point2D> map) {
        List<Graphic> addMe = new ArrayList<Graphic>();
        for (Entry<Src, Point2D> en : map.entrySet()) {
            Src src = en.getKey();
            DelegatingPointGraphic<Src> dpg = points.get(src);
            if (dpg == null) {
                points.put(src, dpg = new DelegatingPointGraphic<Src>(en.getKey(), en.getValue()));
                dpg.setStyler(styler);
                dpg.addChangeListener(this);
                addMe.add(dpg);
            } else {
                dpg.setPoint(en.getValue());
            }
        }
        addGraphics(addMe);
    }
    
    /** Only execute when manager has changed */
    private synchronized void remove(Set<Src> set) {
        Set<DelegatingPointGraphic<Src>> removeMe = new HashSet<DelegatingPointGraphic<Src>>();
        for (Src s : set) {
            removeMe.add(points.get(s));
            points.remove(s);
        }
        removeGraphics(removeMe);
    }
    
    //</editor-fold>
    

    /**
     * Manager responsible for tracking point locations
     * @return manager
     */
    public PointManager<Src, Point2D> getPointManager() {
        return manager;
    }
    
    /**
     * Return source objects
     * @return source objects
     */
    public synchronized Set<? extends Src> getObjects() {
        return manager.getObjects();
    }
    
    /**
     * Adds objects to the graphic
     * @param obj objects to add
     */
    public synchronized void addObjects(Map<Src, Point2D> obj) {
        manager.add(obj);
    }

    /**
     * Returns object used to style points
     * @return styler object styler
     */
    public synchronized ObjectStyler<Src, PointStyle> getStyler() {
        return styler;
    }

    /**
     * Sets object used to style points
     * @param styler object styler
     */
    public synchronized void setStyler(ObjectStyler<Src, PointStyle> styler) {
        this.styler = styler;
        fireGraphicChanged();
    }

}
