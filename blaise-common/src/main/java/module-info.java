/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2024 Elisha Peterson
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

import com.googlecode.blaisemath.primitive.Marker;
import com.googlecode.blaisemath.primitive.Markers;

module com.googlecode.blaisemath.common {
    requires java.desktop;
    requires java.logging;

    requires com.google.common;
    requires org.checkerframework.checker.qual;
    requires csscolor4j;

    exports com.googlecode.blaisemath.annotation;
    exports com.googlecode.blaisemath.coordinate;
    exports com.googlecode.blaisemath.encode;
    exports com.googlecode.blaisemath.geom;
    exports com.googlecode.blaisemath.palette;
    exports com.googlecode.blaisemath.primitive;
    exports com.googlecode.blaisemath.style;
    exports com.googlecode.blaisemath.util;
    exports com.googlecode.blaisemath.util.swing;

    // services (service loader API)
    uses Marker;

    provides Marker with
            Markers.CircleMarker,
            Markers.SquareMarker,
            Markers.DiamondMarker,
            Markers.TriangleMarker,
            Markers.StarMarker5,
            Markers.StarMarker7,
            Markers.StarMarker11,
            Markers.PlusMarker,
            Markers.CrossMarker,
            Markers.TargetMarker,
            Markers.ArrowMarker,
            Markers.GapArrowMarker,
            Markers.ThickArrowMarker,
            Markers.ChevronMarker,
            Markers.TriangleMarkerForward,
            Markers.ArrowheadMarker,
            Markers.TeardropMarker,
            Markers.HappyFaceMarker,
            Markers.HouseMarker,
            Markers.BlankMarker;

}
