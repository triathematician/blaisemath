package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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


import com.googlecode.blaisemath.graphics.swing.render.PathRenderer;
import com.googlecode.blaisemath.graphics.swing.render.ImageRenderer;
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer;
import com.googlecode.blaisemath.graphics.swing.render.MarkerRenderer;
import com.googlecode.blaisemath.graphics.swing.render.IconRenderer;
import com.googlecode.blaisemath.graphics.swing.render.TextRenderer;
import com.google.common.graph.EndpointPair;
import com.googlecode.blaisemath.graphics.impl.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.DelegatingPrimitiveGraphic;
import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.coordinate.OrientedPoint2D;
import com.googlecode.blaisemath.primitive.AnchoredIcon;
import com.googlecode.blaisemath.primitive.AnchoredImage;
import com.googlecode.blaisemath.primitive.AnchoredText;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Point2D;
import javax.swing.Icon;

/**
 * Factory methods for creating basic java2d-based graphics.
 * 
 * @author Elisha Peterson
 */
public class JGraphics {
    
    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);
    /** Default composite */
    public static final Composite DEFAULT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

    // utility class
    private JGraphics() {
    }
    
    //region FACTORIES
    
    public static PrimitiveGraphic<Shape, Graphics2D> path(Shape primitive) {
        return new PrimitiveGraphic<>(primitive, Styles.DEFAULT_PATH_STYLE.copy(), PathRenderer.getInstance());
    }

    public static PrimitiveGraphic<Shape, Graphics2D> path(Shape primitive, AttributeSet style) {
        return new PrimitiveGraphic<>(primitive, style, PathRenderer.getInstance());
    }

    public static <S> DelegatingPrimitiveGraphic<S, Shape, Graphics2D> path(S source, Shape primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<>(source, primitive, styler, PathRenderer.getInstance());
    }

    public static PrimitiveGraphic<Shape, Graphics2D> shape(Shape primitive) {
        return new PrimitiveGraphic<>(primitive, Styles.DEFAULT_SHAPE_STYLE.copy(), ShapeRenderer.getInstance());
    }

    public static PrimitiveGraphic<Shape, Graphics2D> shape(Shape primitive, AttributeSet style) {
        return new PrimitiveGraphic<>(primitive, style, ShapeRenderer.getInstance());
    }

    public static <S> DelegatingPrimitiveGraphic<S, Shape, Graphics2D> shape(S source, Shape primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<>(source, primitive, styler, ShapeRenderer.getInstance());
    }

    public static PrimitiveGraphic<Point2D, Graphics2D> point(Point2D primitive) {
        return new PrimitiveGraphic<>(primitive, Styles.DEFAULT_POINT_STYLE.copy(), MarkerRenderer.getInstance());
    }

    public static PrimitiveGraphic<Point2D, Graphics2D> point(Point2D primitive, AttributeSet style) {
        return new PrimitiveGraphic<>(primitive, style, MarkerRenderer.getInstance());
    }

    public static <S> DelegatingPrimitiveGraphic<S, Point2D, Graphics2D> point(S source, Point2D primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<>(source, primitive, styler, MarkerRenderer.getInstance());
    }

    public static PrimitiveGraphic<Point2D, Graphics2D> marker(OrientedPoint2D primitive) {
        return new PrimitiveGraphic<>(primitive, Styles.DEFAULT_POINT_STYLE.copy(), MarkerRenderer.getInstance());
    }

    public static PrimitiveGraphic<Point2D, Graphics2D> marker(OrientedPoint2D primitive, AttributeSet style) {
        return new PrimitiveGraphic<>(primitive, style, MarkerRenderer.getInstance());
    }
    
    public static <S> DelegatingPrimitiveGraphic<S, Point2D, Graphics2D> marker(S source, OrientedPoint2D primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<>(source, primitive, styler, MarkerRenderer.getInstance());
    }

    public static PrimitiveGraphic<AnchoredText, Graphics2D> text(AnchoredText primitive) {
        return new PrimitiveGraphic<>(primitive, Styles.DEFAULT_TEXT_STYLE.copy(), TextRenderer.getInstance());
    }

    public static PrimitiveGraphic<AnchoredText, Graphics2D> text(AnchoredText primitive, AttributeSet style) {
        return new PrimitiveGraphic<>(primitive, style, TextRenderer.getInstance());
    }

    public static <S> DelegatingPrimitiveGraphic<S, AnchoredText, Graphics2D> text(S source, AnchoredText primitive, ObjectStyler<S> styler) {
        return new DelegatingPrimitiveGraphic<>(source, primitive, styler, TextRenderer.getInstance());
    }

    public static PrimitiveGraphic<AnchoredImage, Graphics2D> image(AnchoredImage primitive) {
        return new PrimitiveGraphic<>(primitive, AttributeSet.EMPTY, ImageRenderer.getInstance());
    }

    public static PrimitiveGraphic<AnchoredImage, Graphics2D> image(double x, double y, double wid, double ht, Image image, String ref) {
        return new PrimitiveGraphic<>(new AnchoredImage(x, y, wid, ht, image, ref), AttributeSet.EMPTY, ImageRenderer.getInstance());
    }

    public static PrimitiveGraphic<AnchoredIcon, Graphics2D> icon(AnchoredIcon icon) {
        return new PrimitiveGraphic<>(icon, AttributeSet.EMPTY, IconRenderer.getInstance());
    }

    public static PrimitiveGraphic<AnchoredIcon, Graphics2D> icon(Icon icon, double x, double y) {
        return new PrimitiveGraphic<>(new AnchoredIcon(x, y, icon), AttributeSet.EMPTY, IconRenderer.getInstance());
    }

    public static <S> DelegatingNodeLinkGraphic<S, EndpointPair<S>, Graphics2D> nodeLink() {
        return new DelegatingNodeLinkGraphic<>(MarkerRenderer.getInstance(), TextRenderer.getInstance(), PathRenderer.getInstance());
    }
    
    //endregion
    
}
