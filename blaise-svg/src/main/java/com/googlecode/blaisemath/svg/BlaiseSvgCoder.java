package com.googlecode.blaisemath.svg;

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

import com.googlecode.blaisemath.graphics.svg.SvgCoder;
import com.googlecode.blaisemath.graphics.svg.SvgGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.svg.render.SvgRenderer;
import com.googlecode.blaisemath.svg.swing.SvgRootGraphic;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import java.io.IOException;

/**
 * Implements {@link SvgCoder} using Blaise libraries.
 * @author Elisha Peterson
 */
public class BlaiseSvgCoder extends SvgCoder {
    @Override
    public SvgGraphic graphicFrom(JGraphicComponent comp) {
        SvgRoot root = SvgRenderer.componentToSvg(comp);
        return SvgRootGraphic.create(root);
    }

    @Override
    public SvgGraphic decode(String str) {
        try {
            SvgRoot root = SvgIo.read(str);
            return SvgRootGraphic.create(root);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid SVG: "+str, e);
        }
    }

    @Override
    public String encode(SvgGraphic obj) {
        if (obj instanceof SvgRootGraphic) {
            try {
                return SvgIo.writeToString(((SvgRootGraphic) obj).getElement());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to write string: " + obj);
            }
        } else {
            throw new IllegalArgumentException("Unsupported content to encode: " + obj);
        }
    }
}
