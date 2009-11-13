/*
 * SpaceRendered.java
 * Created on Oct 22, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.BasicStroke;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.bm.blaise.specto.primitive.*;
import scio.coordinate.P3D;

/**
 * <p>
 *   This style holds 3-dimensional objects of a variety of types, together with their
 *   styles, and controls ordering of these objects.
 * </p>
 * <p>
 *   The standard data type is an array of 3d points (<code>P3D</code>s). These are stored
 *   as a sorted collection (sorted with respect to distance from camera). An array should
 *   contain 1 or more points. Single points are rendered as dots; double points are rendered
 *   as segments; three or more points are rendered as polygons, using the normal vector
 *   computed by the first three coordinates.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceRendered {

    TreeMap<P3D[], PrimitiveStyle> objects;
    SpaceProjection proj;

    public SpaceRendered(SpaceProjection proj) {
        this.proj = proj;
        objects = new TreeMap<P3D[], PrimitiveStyle>(proj.getPolyComparator());
    }

    public void clear() {
        objects.clear();
    }

    public void addObject(P3D[] arr) {
        objects.put(arr, null);
    }

    public void addObjects(Collection<P3D[]> arr) {
        for (P3D[] a : arr) {
            objects.put(a, null);
        }
    }
    
    public void addObjects(P3D[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            objects.put(arr[i], null);
        }
    }

    public void addObjects(P3D[][] arr, PrimitiveStyle style) {
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].length < 2) {
                System.out.println("adding + " + Arrays.toString(arr[i]));
            }
            objects.put(arr[i], style);
        }
    }

    public void draw(SpaceGraphics sg) {
        sg.getShapeStyle().setFillOpacity(0.2f);
        sg.getShapeStyle().setStroke(new BasicStroke(0.5f));
        //sg.getShapeStyle().setStroke(null);
        sg.getPathStyle().setStroke(new BasicStroke(2.0f));
        sg.getPathStyle().setColor(BlaisePalette.STANDARD.function());
        for (Entry<P3D[],PrimitiveStyle> entry : objects.entrySet()) {
            if (proj.getAverageDist(entry.getKey()) < proj.clipDist) {
                continue;
            }
            PrimitiveStyle style = entry.getValue();
            if (style instanceof ArrowStyle && entry.getKey().length == 2) {
                sg.drawArrow(entry.getKey()[0], entry.getKey()[1]);
            } else { // style and class determined by length of array
                switch (entry.getKey().length) {
                    case 1:
                        sg.drawPoint(entry.getKey()[0]);
                        break;
                    case 2:
                        sg.drawSegment(entry.getKey()[0], entry.getKey()[1]);
                        break;
                    default:
                        sg.drawClosedPath(entry.getKey());
                }
            }
        }
    }
}
