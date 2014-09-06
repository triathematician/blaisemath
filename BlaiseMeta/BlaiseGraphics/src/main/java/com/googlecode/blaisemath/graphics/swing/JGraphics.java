/**
 * JGraphicUtils.java
 * Created Aug 1, 2014
 */
package com.googlecode.blaisemath.graphics.swing;

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


import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.core.DelegatingPrimitiveGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.geom.OrientedPoint2D;
import com.googlecode.blaisemath.util.geom.LabeledPoint;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Factory methods for creating basic java2d-based graphics.
 * 
 * @author Elisha
 */
public class JGraphics {
    
    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE 
            = new BasicStroke(1.0f);
    /** Default composite */
    public static final Composite DEFAULT_COMPOSITE 
            = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

    // utilitiy class
    private JGraphics() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="FACTORY METHODS FOR SWING GRAPHICS">
    
    public static PrimitiveGraphic<Shape,Graphics2D> path(Shape primitive) {
        return new PrimitiveGraphic<Shape,Graphics2D>(primitive, Styles.defaultPathStyle(), PathRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<Shape,Graphics2D> path(Shape primitive, AttributeSet style) {
        return new PrimitiveGraphic<Shape,Graphics2D>(primitive, style, PathRenderer.getInstance());
    }
    
    public static <S> DelegatingPrimitiveGraphic<S,Shape,Graphics2D> path(S source, Shape primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<S,Shape,Graphics2D>(source, primitive, styler, PathRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<Shape,Graphics2D> shape(Shape primitive) {
        return new PrimitiveGraphic<Shape,Graphics2D>(primitive, Styles.defaultShapeStyle(), ShapeRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<Shape,Graphics2D> shape(Shape primitive, AttributeSet style) {
        return new PrimitiveGraphic<Shape,Graphics2D>(primitive, style, ShapeRenderer.getInstance());
    }
    
    public static <S> DelegatingPrimitiveGraphic<S,Shape,Graphics2D> shape(S source, Shape primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<S,Shape,Graphics2D>(source, primitive, styler, ShapeRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<Point2D,Graphics2D> point(Point2D primitive) {
        return new PrimitiveGraphic<Point2D,Graphics2D>(primitive, Styles.defaultPointStyle(), PointRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<Point2D,Graphics2D> point(Point2D primitive, AttributeSet style) {
        return new PrimitiveGraphic<Point2D,Graphics2D>(primitive, style, PointRenderer.getInstance());
    }
    
    public static <S> DelegatingPrimitiveGraphic<S,Point2D,Graphics2D> point(S source, Point2D primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<S,Point2D,Graphics2D>(source, primitive, styler, PointRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<OrientedPoint2D,Graphics2D> marker(OrientedPoint2D primitive) {
        return new PrimitiveGraphic<OrientedPoint2D,Graphics2D>(primitive, Styles.defaultPointStyle(), MarkerRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<OrientedPoint2D,Graphics2D> marker(OrientedPoint2D primitive, AttributeSet style) {
        return new PrimitiveGraphic<OrientedPoint2D,Graphics2D>(primitive, style, MarkerRenderer.getInstance());
    }
    
    public static <S> DelegatingPrimitiveGraphic<S,OrientedPoint2D,Graphics2D> marker(S source, OrientedPoint2D primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<S,OrientedPoint2D,Graphics2D>(source, primitive, styler, MarkerRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<LabeledPoint,Graphics2D> text(LabeledPoint primitive) {
        return new PrimitiveGraphic<LabeledPoint,Graphics2D>(primitive, Styles.defaultTextStyle(), TextRenderer.getInstance());
    }
    
    public static PrimitiveGraphic<LabeledPoint,Graphics2D> text(LabeledPoint primitive, AttributeSet style) {
        return new PrimitiveGraphic<LabeledPoint,Graphics2D>(primitive, style, TextRenderer.getInstance());
    }
    
    public static <S> DelegatingPrimitiveGraphic<S,LabeledPoint,Graphics2D> text(S source, LabeledPoint primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<S,LabeledPoint,Graphics2D>(source, primitive, styler, TextRenderer.getInstance());
    }

    public static <S> DelegatingNodeLinkGraphic<S, Edge<S>, Graphics2D> nodeLink() {
        return new DelegatingNodeLinkGraphic<S,Edge<S>,Graphics2D>(
                PointRenderer.getInstance(), TextRenderer.getInstance(), PathRenderer.getInstance());
    }
    
    //</editor-fold>
    
}
