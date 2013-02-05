/**
 * DelegatingPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import com.google.common.base.Functions;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.util.CoordinateChangeEvent;
import org.blaise.util.CoordinateListener;
import org.blaise.util.CoordinateManager;

/**
 * Manages a collection of points that are maintained as separate {@link Graphic}s,
 * and therefore fully customizable. Point locations are handled by a {@link CoordinateManager}.
 *
 * @param <Src> the type of object being displayed
 *
 * @see PointStyle
 * @see BasicPointSetGraphic
 *
 * @author Elisha Peterson
 */
public class DelegatingPointSetGraphic<Src> extends GraphicComposite implements ChangeListener, CoordinateListener {

    /** Manages locations of points */
    protected final CoordinateManager<Src,Point2D> manager = new CoordinateManager<Src,Point2D>();
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
     * Construct with source objects and locations as a map
     * @param map locations
     */
    public DelegatingPointSetGraphic(Map<Src, Point2D> map) {
        styler.setTipDelegate(Functions.toStringFunction());
        manager.addCoordinateListener(this);
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

    boolean updatingPoint = false;
    
    public synchronized void stateChanged(ChangeEvent e) {
        if (!updatingPoint && e.getSource() instanceof DelegatingPointGraphic) {
            DelegatingPointGraphic<Src> dpg = (DelegatingPointGraphic<Src>) e.getSource();
            manager.put(dpg.getSourceObject(), dpg.getPoint());
        }
    }

    public synchronized void coordinatesChanged(CoordinateChangeEvent evt) {
        // when coordinate manager changes
        Map added = evt.getAdded();
        List<Graphic> addMe = new ArrayList<Graphic>();
        if (added != null) {
            for (Entry<Src, Point2D> en : ((Map<Src,Point2D>)added).entrySet()) {
                Src src = en.getKey();
                DelegatingPointGraphic<Src> dpg = points.get(src);
                if (dpg == null) {
                    points.put(src, dpg = new DelegatingPointGraphic<Src>(en.getKey(), en.getValue()));
                    dpg.setStyler(styler);
                    dpg.addChangeListener(this);
                    addMe.add(dpg);
                } else {
                    // this should not result in manager changing
                    updatingPoint = true;
                    dpg.setPoint(en.getValue());
                    updatingPoint = false;
                }
            }
        }
        Set removed = evt.getRemoved();
        Set<DelegatingPointGraphic<Src>> removeMe = new HashSet<DelegatingPointGraphic<Src>>();
        if (removed != null) {
            for (Src s : (Set<Src>) removed) {
                removeMe.add(points.get(s));
                points.remove(s);
            }
        }
        replaceGraphics(removeMe, addMe);
    }

    //</editor-fold>

    /**
     * Manager responsible for tracking point locations
     * @return manager
     */
    public CoordinateManager<Src, Point2D> getCoordinateManager() {
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
     * @param obj objects to put
     */
    public synchronized void addObjects(Map<Src, Point2D> obj) {
        manager.putAll(obj);
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

    @Override
    public void initialize(JPopupMenu menu, Point point, Object focus, Set<Graphic> selection) {
        // provide additional info for context menu
        Graphic gfc = graphicAt(point);
        super.initialize(menu, point, gfc instanceof DelegatingPointGraphic ? ((DelegatingPointGraphic)gfc).getSourceObject() : focus, selection);
    }
    
}
