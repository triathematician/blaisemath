/**
 * ShapeBean.java
 * Created on Aug 4, 2009
 */

package org.blaise.firestarter;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *   <code>ShapeBean</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class ShapeBean {

    protected Shape rect2d = new Rectangle2D.Double(0.0, 0.0, 10.0, 10.0);
    public Shape getRect2d() { return rect2d; }
    public void setRect2d(Shape rect2d) { this.rect2d = rect2d; }

    protected Ellipse2D.Double ellipse2d = new Ellipse2D.Double(0.0, 0.0, 10.0, 10.0);
    public Ellipse2D.Double getEllipse2d() { return ellipse2d; }
    public void setEllipse2d(Ellipse2D.Double ellipse2d) { this.ellipse2d = ellipse2d; }

    protected Line2D.Double line2d = new Line2D.Double(0.0, 0.0, 10.0, 10.0);
    public Line2D.Double getLine2d() { return line2d; }
    public void setLine2d(Line2D.Double line2d) { this.line2d = line2d; }

}
