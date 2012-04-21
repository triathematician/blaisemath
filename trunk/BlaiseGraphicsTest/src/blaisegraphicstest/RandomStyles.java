/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blaisegraphicstest;

import org.bm.blaise.style.PointStyle;
import org.bm.blaise.style.PathStyle;
import org.bm.blaise.style.ShapeStyle;
import org.bm.blaise.style.StringStyle;
import java.awt.Color;
import org.bm.blaise.style.BasicPathStyle;
import org.bm.blaise.style.BasicPointStyle;
import org.bm.blaise.style.BasicShapeStyle;
import org.bm.blaise.style.BasicStringStyle;

/**
 *
 * @author elisha
 */
public class RandomStyles {

    public static PointStyle point() {
        return new BasicPointStyle()
                    .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .thickness((float)(2*Math.random()))
                    .radius((int)(25*Math.random()));
    }
    
    public static PathStyle path() {
        return new BasicPathStyle(
                    new Color((float)Math.random(),(float)Math.random(),(float)Math.random()),
                    (float)(12*Math.random())
                );
    }
    
    public static ShapeStyle shape() {
        return new BasicShapeStyle(
                    new Color((float)Math.random(),(float)Math.random(),(float)Math.random()),
                    new Color((float)Math.random(),(float)Math.random(),(float)Math.random()),
                    (float)Math.random()
                );
    }

    public static StringStyle string() {
        return new BasicStringStyle()
                .color(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                .fontSize((float)(5+10*Math.random()));
    }
    
}
