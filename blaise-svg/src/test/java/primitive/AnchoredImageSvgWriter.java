package primitive;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import com.googlecode.blaisemath.primitive.Anchor;
import com.googlecode.blaisemath.primitive.AnchoredImage;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.render.StyleWriter;
import com.googlecode.blaisemath.svg.render.SvgRenderException;
import com.googlecode.blaisemath.svg.xml.SvgImage;

import java.awt.geom.Point2D;

/**
 * Converts an anchored image primitive to an SVG image primitive.
 * @author Elisha Peterson
 */
public class AnchoredImageSvgWriter implements PrimitiveSvgWriter<AnchoredImage, SvgImage> {

    @Override
    public SvgImage write(String id, AnchoredImage i, AttributeSet style) throws SvgRenderException {
        if (i == null || i.getWidth() == 0 || i.getHeight() == 0) {
            throw new SvgRenderException("Missing or 0 width/height image cannot be converted.");
        }
        Anchor anchor = Styles.anchorOf(style, Anchor.NORTHWEST);
        Point2D offset = anchor.offsetForRectangle(i.getWidth(), i.getHeight());
        AnchoredImage image = new AnchoredImage(i.getX() + offset.getX(), i.getY() - i.getHeight()+ offset.getY(),
                i.getWidth(), i.getHeight(), i.getOriginalImage(), i.getReference());
        SvgImage res = SvgImage.create((float) image.getX(), (float) image.getY(), (float) image.getWidth(), (float) image.getHeight(), image.getReference());
        res.id = id;
        res.style = StyleWriter.toString(style);
        return res;
    }

}
