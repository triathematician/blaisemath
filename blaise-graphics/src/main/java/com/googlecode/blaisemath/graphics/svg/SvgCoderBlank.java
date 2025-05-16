package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.annotations.Beta;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Placeholder to decode SVG as an empty string.
 * @author Elisha Peterson
 */
@Beta
public class SvgCoderBlank extends SvgCoder {
    
    /** String produced when unable to export SVG */
    public static final String UNSUPPORTED_SVG = "<!-- Unsupported><svg></svg>";

    @Override
    public String encode(SvgGraphic obj) {
        return UNSUPPORTED_SVG;
    }

    @Override
    public SvgGraphic decode(String str) {
        return new EmptySvgGraphic();
    }

    @Override
    public SvgGraphic graphicFrom(JGraphicComponent comp) {
        return new EmptySvgGraphic();
    }
    
    /** Graphic for empty/missing SVG content. */
    private static class EmptySvgGraphic extends SvgGraphic {
        @Override
        public AttributeSet getStyle() {
            return AttributeSet.EMPTY;
        }

        @Override
        public void renderTo(Graphics2D canvas) {
            // do nothing
        }

        @Override
        public @Nullable Rectangle2D boundingBox(Graphics2D canvas) {
            return null;
        }

        @Override
        public boolean contains(Point2D point, Graphics2D canvas) {
            return false;
        }

        @Override
        public boolean intersects(Rectangle2D box, Graphics2D canvas) {
            return false;
        }        
    }

}
