/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.visometry;

import java.awt.Color;
import org.blaise.style.PathStyleBasic;
import org.blaise.style.PointStyleBasic;
import org.blaise.style.ShapeStyleBasic;
import org.blaise.style.TextStyleBasic;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.style.ShapeStyle;
import org.blaise.style.TextStyle;
import org.blaise.style.Styles;

/**
 *
 * @author elisha
 */
public class RandomStyles {

    public static PointStyle point() {
        return new PointStyleBasic()
                    .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .strokeWidth((float)(2*Math.random()))
                    .markerRadius((int)(25*Math.random()));
    }
    
    public static PathStyle path() {
        return new PathStyleBasic()
                    .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .strokeWidth((float)(12*Math.random()));
    }
    
    public static ShapeStyle shape() {
        return new ShapeStyleBasic()
                .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                .strokeWidth((float)Math.random());
    }

    public static TextStyle string() {
        return new TextStyleBasic()
                .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                .fontSize((float)(5+10*Math.random()));
    }
    
}
