/*
 * LabeledPointGraphic.java
 * Created Jan 22, 2011
 */

package com.googlecode.blaisemath.dev.compoundgraphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.geom.Point2D;
import com.googlecode.blaisemath.graphics.BasicPointGraphic;
import com.googlecode.blaisemath.graphics.BasicTextGraphic;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.style.TextStyle;
import com.googlecode.blaisemath.coordinate.PointBean;

/**
 * Displays a point together with a label.
 * 
 * TODO - align movement of string to movement of point
 *
 * @author Elisha
 */
public class LabeledPointGraphic extends GraphicComposite
        implements PointBean<Point2D> {

    /** Stores the point */
    private final BasicPointGraphic point;
    /** Stores the string */
    private final BasicTextGraphic string;

    //
    // CONSTRUCTORS
    //

    /** Construct labeled point with given primitive and default style */
    public LabeledPointGraphic(Point2D p, String s) { 
        this(p, s, null); 
    }

    /** Construct with given primitive and style. */
    public LabeledPointGraphic(Point2D p, String s, PointStyle style) {
        addGraphic(point = new BasicPointGraphic(p, style));
        addGraphic(string = new BasicTextGraphic(p, s));
    }

    
    //
    // PROPERTIES
    //

    public Point2D getPoint() { return point.getPoint(); }
    public void setPoint(Point2D p) { point.setPoint(p); fireGraphicChanged(); }

    public PointStyle getPointStyle() { return point.getStyle(); }
    public void setPointStyle(PointStyle style) { point.setStyle(style); }

    
    public String getString() { return string.getString(); }
    public void setString(String s) { string.setString(s); fireGraphicChanged(); }

    public TextStyle getStringStyle() { return string.getStyle(); }
    public void setStringStyle(TextStyle style) { string.setStyle(style); }

    
    //
    // EVENT HANDLING
    //

    @Override
    public void graphicChanged(Graphic source) {
        if (source == point) {
            string.setPoint(getPoint());
        }
        super.graphicChanged(source);
    }

    
    
    @Override
    public boolean contains(Point2D p) {
        // override to prevent dragging and tips on the string
        return this.point.contains(p);
   }    
    
    
}
