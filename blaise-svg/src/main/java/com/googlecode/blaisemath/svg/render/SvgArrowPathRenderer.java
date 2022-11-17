package com.googlecode.blaisemath.svg.render;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
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

import com.googlecode.blaisemath.primitive.ArrowLocation;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.blaisemath.graphics.swing.render.ArrowPathRenderer.arrowShapes;

/**
 * Write SVG path with configurable arrows.
 * @author Elisha Peterson
 */
public class SvgArrowPathRenderer extends SvgRenderer<Shape> {

    private static final Logger LOG = Logger.getLogger(SvgArrowPathRenderer.class.getName());

    protected ArrowLocation arrowLoc = ArrowLocation.END;

    //region PROPERTIES

    public ArrowLocation getArrowLocation() {
        return arrowLoc;
    }

    public void setArrowLocation(ArrowLocation loc) {
        checkNotNull(loc);
        if (this.arrowLoc != loc) {
            this.arrowLoc = loc;
        }
    }

    //endregion

    @Override
    public void render(Shape s, AttributeSet style, SvgTreeBuilder canvas) {
        new SvgPathRenderer().render(s, style, canvas);

        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH);

        // can only draw if stroke is appropriate
        if (stroke == null || strokeWidth == null || strokeWidth <= 0) {
            return;
        }

        // arrow heads can only be drawn on certain shapes
        if (!(s instanceof Line2D || s instanceof GeneralPath)) {
            LOG.log(Level.WARNING, "Unable to draw arrowheads on this shape: {0}", s);
            return;
        }

        // create and draw arrowhead shape(s) at end of path
        GeneralPath arrowShapes = arrowShapes(s, arrowLoc, strokeWidth);
        new SvgShapeRenderer().render(arrowShapes, Styles.fillStroke(stroke, stroke, strokeWidth), canvas);
    }


}

