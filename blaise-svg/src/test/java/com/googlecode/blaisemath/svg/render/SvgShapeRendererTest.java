package com.googlecode.blaisemath.svg.render;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.svg.xml.*;
import org.junit.Test;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SvgShapeRendererTest {

    @Test
    public void svgElement() {
        assertSvgRenderException(() -> SvgShapeRenderer.svgElement(null));

        assertEqualsEllipse(SvgEllipse.create(2.5, 3, 1.5, 1), SvgShapeRenderer.svgElement(new Ellipse2D.Float(1, 2, 3, 2)));
        assertEqualsCircle(SvgCircle.create(2, 3, 1), SvgShapeRenderer.svgElement(new Ellipse2D.Float(1, 2, 2, 2)));
        assertEqualsLine(SvgLine.create(1, 2, 3, 4), SvgShapeRenderer.svgElement(new Line2D.Float(1, 2, 3, 4)));
        assertEqualsRect(SvgRect.create(1, 2, 3, 4), SvgShapeRenderer.svgElement(new Rectangle2D.Float(1, 2, 3, 4)));
        assertEqualsRect(SvgRect.create(1, 2, 3, 4, 1f, 2f), SvgShapeRenderer.svgElement(new RoundRectangle2D.Float(1, 2, 3, 4, 1, 2)));

        // TODO - test special cases for polygon/polyline?
    }

    public static void assertEqualsEllipse(SvgEllipse e1, SvgElement e) {
        assertEquals(SvgEllipse.class, e.getClass());
        SvgEllipse e2 = (SvgEllipse) e;
        assertEquals(e1.cx, e2.cx, .01f);
        assertEquals(e1.cy, e2.cy, .01f);
        assertEquals(e1.rx, e2.rx, .01f);
        assertEquals(e1.ry, e2.ry, .01f);
    }

    public static void assertEqualsCircle(SvgCircle c1, SvgElement e) {
        assertEquals(SvgCircle.class, e.getClass());
        SvgCircle c2 = (SvgCircle) e;
        assertEquals(c1.cx, c2.cx, .01f);
        assertEquals(c1.cy, c2.cy, .01f);
        assertEquals(c1.r, c2.r, .01f);
    }

    public static void assertEqualsLine(SvgLine l1, SvgElement e) {
        assertEquals(SvgLine.class, e.getClass());
        SvgLine l2 = (SvgLine) e;
        assertEquals(l1.x1, l2.x1, .01f);
        assertEquals(l1.y1, l2.y1, .01f);
        assertEquals(l1.x2, l2.x2, .01f);
        assertEquals(l1.y2, l2.y2, .01f);
    }

    public static void assertEqualsRect(SvgRect r1, SvgElement e) {
        assertEquals(SvgRect.class, e.getClass());
        SvgRect r2 = (SvgRect) e;
        assertEquals(r1.x, r2.x, .01f);
        assertEquals(r1.y, r2.y, .01f);
        assertEquals(r1.width, r2.width, .01f);
        assertEquals(r1.height, r2.height, .01f);
        assertEquals(r1.rx, r2.rx);
        assertEquals(r1.ry, r2.ry);
    }

    private void assertSvgRenderException(Runnable r) {
        try {
            r.run();
            fail("Expected a read exception");
        } catch (SvgRenderException x) {
            // expected
        }
    }

}
