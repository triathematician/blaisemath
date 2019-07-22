package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
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

import com.googlecode.blaisemath.encode.StringDecoder;
import com.googlecode.blaisemath.encode.StringEncoder;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import java.util.ServiceLoader;

/**
 * Reads/writes graphics to SVG.
 * @author Elisha Peterson
 */
public abstract class SvgCoder implements StringEncoder<SvgGraphic>, StringDecoder<SvgGraphic> {

    /**
     * Create an SVG graphic from the given component.
     * @param comp component
     * @return svg graphic
     */
    public abstract SvgGraphic graphicFrom(JGraphicComponent comp);

    /**
     * Get a registered coder instance.
     * @return coder
     */
    public static SvgCoder defaultInstance() {
        for (SvgCoder c : ServiceLoader.load(SvgCoder.class)) {
            if (!(c instanceof SvgCoderBlank)) {
                return c;
            }
        }
        return new SvgCoderBlank();
    }

}
