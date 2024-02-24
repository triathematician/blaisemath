/*-
 * #%L
 * blaise-graph-theory
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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
module com.googlecode.blaisemath.graphtheory {
    requires java.desktop;
    requires java.logging;

    requires com.google.common;
    requires org.checkerframework.checker.qual;

    requires com.googlecode.blaisemath.common;

    exports com.googlecode.blaisemath.graph;
    exports com.googlecode.blaisemath.graph.generate;
    exports com.googlecode.blaisemath.graph.layout;
    exports com.googlecode.blaisemath.graph.metrics;
}
