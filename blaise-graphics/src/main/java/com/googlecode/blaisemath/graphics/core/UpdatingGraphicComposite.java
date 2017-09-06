package com.googlecode.blaisemath.graphics.core;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import static com.googlecode.blaisemath.util.Preconditions.checkNotNull;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;

/**
 * Encapsulates a set of graphics as a composite, along with elements used
 * to create/update the graphics.
 * 
 * @author petereb1
 * @param <T>
 */
public class UpdatingGraphicComposite<T> {

    /** Contains the graphic elements */
    private final GraphicComposite<Graphics2D> composite = new GraphicComposite<Graphics2D>();
    /** Index for the graphics, based on source object */
    private final BiMap<T, Graphic<Graphics2D>> index = HashBiMap.create();
    /** Creates/updates the graphics */
    private GraphicUpdater<T> updater;

    public UpdatingGraphicComposite(GraphicUpdater<T> updater) {
        this.updater = checkNotNull(updater);
    }
    
    public static <T> UpdatingGraphicComposite<T> create(GraphicUpdater<T> updater) {
        return new UpdatingGraphicComposite<T>(updater);
    }

    public void setObjects(Iterable<T> data, Function<T,Rectangle2D> locs) {
        Set<Graphic<Graphics2D>> toRemove = Sets.newHashSet(composite.getGraphics());
        for (T t : data) {
            if (index.containsKey(t)) {
                toRemove.remove(index.get(t));
            }
        }
        composite.removeGraphics(toRemove);
        index.keySet().retainAll(data instanceof Collection ? (Collection) data : Lists.newArrayList(data));
        
        for (T obj : data) {
            Graphic<Graphics2D> existing = index.get(obj);
            Rectangle2D loc = locs.apply(obj);
            if (loc == null && existing != null) {
                composite.removeGraphic(existing);
                index.remove(obj);
            } else if (loc != null) {
                Graphic gfc = updater.update(obj, loc, existing);
                if (existing == null) {
                    index.put(obj, gfc);
                    composite.addGraphic(gfc);
                }
            }
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public GraphicComposite<Graphics2D> getGraphic() {
        return composite;
    }

    public GraphicUpdater<T> getUpdater() {
        return updater;
    }

    public void setUpdater(GraphicUpdater<T> updater) {
        this.updater = updater;
        // TODO rebuild if different
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LOOKUPS">
    
    public T objectOf(Graphic<Graphics2D> gfc) {
        return index.inverse().get(gfc);
    }

    public Graphic<Graphics2D> graphicOf(T obj) {
        return index.get(obj);
    }

    //</editor-fold>
    
}
