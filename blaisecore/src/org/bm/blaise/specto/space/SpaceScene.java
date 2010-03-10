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
import scio.coordinate.Point3D;

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
public class SpaceScene {

    TreeMap<Point3D[], PrimitiveStyle> objects;
    SpaceProjection proj;

    public SpaceScene(SpaceProjection proj) {
        this.proj = proj;
        objects = new TreeMap<Point3D[], PrimitiveStyle>(proj.getPolygonZOrderComparator());
    }

    public void clear() {
        objects.clear();
    }

    public void addObject(Point3D[] arr) {
        objects.put(arr, null);
    }

    public void addObject(Point3D[] arr, PrimitiveStyle style) {
        objects.put(arr, style);
    }

    public void addObjects(Collection<Point3D[]> arr) {
        for (Point3D[] a : arr) {
            objects.put(a, null);
        }
    }
    
    public void addObjects(Point3D[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            objects.put(arr[i], null);
        }
    }

    public void addObjects(Point3D[][] arr, PrimitiveStyle style) {
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].length < 2) {
                System.out.println("adding + " + Arrays.toString(arr[i]));
            }
            objects.put(arr[i], style);
        }
    }

    public void draw(SpaceGraphics sg) {
//        sg.getShapeStyle().setFillOpacity(0.6f);
        sg.getShapeStyle().setStroke(new BasicStroke(0.5f));
        //sg.getShapeStyle().setStroke(null);
        sg.getPathStyle().setStroke(new BasicStroke(2.0f));
        sg.getPathStyle().setColor(BlaisePalette.STANDARD.func1());
        int r = sg.getPointStyle().getRadius();
        double dist;
        for (Entry<Point3D[],PrimitiveStyle> entry : objects.entrySet()) {
            dist = proj.getAverageDist(entry.getKey());
            if (dist < proj.clipDist)
                continue;
            PrimitiveStyle style = entry.getValue();
            if (style instanceof ArrowStyle && entry.getKey().length == 2)
                sg.drawArrow(entry.getKey()[0], entry.getKey()[1]);
            else if (style instanceof PointStyle && entry.getKey().length == 1) {
                PointStyle oldStyle = sg.getPointStyle();
                int r2 = ((PointStyle) style).getRadius();
                ((PointStyle) style).setRadius((int) (r2 * proj.viewDist / dist));
                sg.setPointStyle((PointStyle) style);
                sg.drawPoint(entry.getKey()[0]);
                ((PointStyle) style).setRadius(r2);
                sg.setPointStyle(oldStyle);
            } else { // style and class determined by length of array
                switch (entry.getKey().length) {
                    case 1:
                        break;
                    case 2:
                        sg.drawSegment(entry.getKey()[0], entry.getKey()[1]);
                        break;
                    default:
                        sg.drawShape(entry.getKey());
                }
            }
        }
        sg.getPointStyle().setRadius(r);
    }
}
