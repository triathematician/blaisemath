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

import com.googlecode.blaisemath.graphics.svg.SvgCoder;
import com.googlecode.blaisemath.svg.reader.*;
import com.googlecode.blaisemath.svg.BlaiseSvgCoder;
import com.googlecode.blaisemath.svg.xml.SvgLine;
import com.googlecode.blaisemath.svg.xml.SvgPath;
import com.googlecode.blaisemath.svg.xml.SvgRect;

module com.googlecode.blaisemath.svg {
    requires java.desktop;
    requires java.logging;

    requires com.google.common;
    requires org.checkerframework.checker.qual;

    requires java.xml.bind;

    requires com.googlecode.blaisemath.common;
    requires com.googlecode.blaisemath.graphics;
    requires com.googlecode.blaisemath.json;

    exports com.googlecode.blaisemath.svg;
    exports com.googlecode.blaisemath.svg.reader;
    exports com.googlecode.blaisemath.svg.render;
    exports com.googlecode.blaisemath.svg.swing;
    exports com.googlecode.blaisemath.svg.xml;

    opens com.googlecode.blaisemath.svg.xml to java.xml.bind;

    // services
    uses SvgCoder;
    uses SvgReader;

    provides SvgCoder with
            BlaiseSvgCoder;
    provides SvgReader with
            SvgGroupReader,
            SvgCircleReader,
            SvgEllipseReader,
            SvgImageReader,
            SvgLineReader,
            SvgPathReader,
            SvgPolygonReader,
            SvgPolylineReader,
            SvgRectReader,
            SvgTextReader;
}
