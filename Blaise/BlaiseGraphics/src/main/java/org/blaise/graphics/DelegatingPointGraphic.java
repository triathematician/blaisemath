/**
 * DelegatingPointGraphic.java
 * Created Aug 21, 2012
 */
package org.blaise.graphics;

/*
 * #%L
 * BlaiseGraphics
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

import static com.google.common.base.Preconditions.*;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import javax.annotation.Nonnull;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.style.TextStyle;

/**
 * Uses an {@link ObjectStyler} and a source object to draw a labeled point on a canvas.
 *
 * @param <S> source object type
 * @author Elisha
 */
public class DelegatingPointGraphic<S> extends AbstractPointGraphic {

    /** Source object */
    protected S src;
    /** Manages delegators */
    @Nonnull protected ObjectStyler<S, PointStyle> styler = ObjectStyler.create();

    public DelegatingPointGraphic() {
        this(null, new Point2D.Double());
    }

    public DelegatingPointGraphic(S src, Point2D pt) {
        super(pt);
        setSourceObject(src);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public S getSourceObject() {
        return src;
    }

    public final void setSourceObject(S src) {
        if (this.src != src) {
            this.src = src;
            setDefaultTooltip(styler.getTipDelegate().apply(src));
            fireGraphicChanged();
        }
    }

    public ObjectStyler<S, PointStyle> getStyler() {
        return styler;
    }

    public void setStyler(ObjectStyler<S, PointStyle> styler) {
        if (this.styler != checkNotNull(styler)) {
            this.styler = styler;
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>

    
    @Override
    public String getTooltip(Point2D p) {
        if (tipEnabled) {
            String txt = styler.getTipDelegate() == null ? null : styler.getTipDelegate().apply(src);
            return txt == null ? defaultTooltip : txt;
        } else {
            return null;
        }
    }

    @Override
    @Nonnull 
    public PointStyle drawStyle() {
        PointStyle style = null;
        if (styler.getStyleDelegate() != null) {
            style = styler.getStyleDelegate().apply(src);
        }
        if (style == null) {
            style = parent.getStyleContext().getPointStyle(this);
        }
        return style;
    }

    public void draw(Graphics2D canvas) {
        PointStyle ps = drawStyle();
        ps.draw(point, canvas, styleHints);

        if (styler.getLabelDelegate() != null) {
            String label = styler.getLabelDelegate().apply(src);
            if (!Strings.isNullOrEmpty(label)) {
                Function<? super S,TextStyle> lStyler = styler.getLabelStyleDelegate();
                if (lStyler != null) {
                    TextStyle style = lStyler.apply(src);
                    if (style != null) {
                        style.draw(point, label, canvas, styleHints);
                    }
                }
            }
        }
    }


}
