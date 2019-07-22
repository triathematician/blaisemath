/**
 * TestBean.java
 * Created on Jun 29, 2009
 */

package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *   <code>TestBean</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class PointBean {
    
    protected Dimension dim = new Dimension(20,500);
    public Dimension getDim() { return dim; }
    public void setDim(Dimension dim) { this.dim = dim; }

    protected Insets insets = new Insets(2,34,4,-10);
    public Insets getInsets() { return insets; }
    public void setInsets(Insets insets) { this.insets = insets; }

    protected Rectangle rect = new Rectangle(5,-3,30,50);
    public Rectangle getRect() { return rect; }
    public void setRect(Rectangle rect) { this.rect = rect; }

    protected Rectangle2D.Double rect2 = new Rectangle2D.Double(5,-3,30,50);
    public Rectangle2D.Double getRect2() { return rect2; }
    public void setRect2(Rectangle2D.Double rect2) { this.rect2 = rect2; }

    protected Point point=new Point(4,5);
    public Point getPoint() { return point; }
    public void setPoint(Point point) { this.point = point; }

    protected Point2D.Double p2d = new Point2D.Double(3.0,5.0);
    public Point2D.Double getP2d() { return p2d; }
    public void setP2d(Point2D.Double p2d) { this.p2d = p2d; }

}
