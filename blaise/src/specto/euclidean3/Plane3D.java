/**
 * Plane3D.java
 * Created on Nov 17, 2008
 */

package specto.euclidean3;

import java.awt.Graphics2D;
import java.util.Vector;
import scio.coordinate.R3;
import sequor.model.DoubleRangeModel;
import sequor.model.StringRangeModel;
import sequor.style.VisualStyle;

/**
 * <p>
 * Plots a plane through a specified point with specified direction vectors.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class Plane3D extends Point3D {
    
    /** One direction through the point. */
    R3 dir1;
    /** Second direction through point. */
    R3 dir2;
    /** Parameter values through point. */
    DoubleRangeModel d1;
    /** Parameter values through point. */
    DoubleRangeModel d2;

    /** Generates with specified point and directions. */
    public Plane3D(R3 value, R3 dir1, R3 dir2) {
        super(value);
        this.dir1 = dir1;
        this.dir2 = dir2;
        d1 = new DoubleRangeModel(0,-5,5);
        d2 = new DoubleRangeModel(0,-5,5);
    }
    /** Generates with specified point and directions. */
    public Plane3D(R3 value, R3 dir1, double min1, double max1, R3 dir2, double min2, double max2) {
        super(value);
        this.dir1 = dir1;
        this.dir2 = dir2;
        d1 = new DoubleRangeModel(min1,min1,max1);
        d1.setNumSteps(10, true);
        d2 = new DoubleRangeModel(min2,min2,max2);
        d2.setNumSteps(10, true);
    }
    /** Generates with specified point and directions. */
    public Plane3D(R3 value, R3 dir1, DoubleRangeModel range1, R3 dir2, DoubleRangeModel range2) {
        super(value);
        this.dir1 = dir1;
        this.dir2 = dir2;
        this.d1 = range1;
        this.d2 = range2;
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v) {
        Vector<R3> rectangle = new Vector<R3>();
        rectangle.add(prm.plus(dir1.times(d1.getMinimum())).plus(dir2.times(d2.getMinimum())));
        rectangle.add(prm.plus(dir1.times(d1.getMaximum())).plus(dir2.times(d2.getMinimum())));
        rectangle.add(prm.plus(dir1.times(d1.getMaximum())).plus(dir2.times(d2.getMaximum())));
        rectangle.add(prm.plus(dir1.times(d1.getMinimum())).plus(dir2.times(d2.getMaximum())));
        rectangle.add(rectangle.get(0));
        g.setColor(getColor());
        g.setComposite(VisualStyle.COMPOSITE05);
        v.fillPath(g, rectangle);
        g.setComposite(VisualStyle.COMPOSITE10);
        g.setStroke(VisualStyle.THIN_STROKE);
        switch(style2.getValue()) {
            case SOLID:
                v.drawPath(g, rectangle);
                break;
            case GRIDDED:
            default:
                for(Double d : d1.getValueRange(true, 0.0)) {
                    v.drawLineSegment(g, prm.plus(dir1.times(d)).plus(dir2.times(d2.getMinimum())), prm.plus(dir1.times(d)).plus(dir2.times(d2.getMaximum())));
                }
                for(Double d : d2.getValueRange(true, 0.0)) {
                    v.drawLineSegment(g, prm.plus(dir2.times(d)).plus(dir1.times(d1.getMinimum())), prm.plus(dir2.times(d)).plus(dir1.times(d1.getMaximum())));
                }
                break;                
        }
    }
    
    public static final int SOLID = 1;
    public static final int GRIDDED = 0;
    
    String[] styleStrings2 = { "Solid", "Gridded" };
    StringRangeModel style2 = new StringRangeModel(styleStrings2);
}
