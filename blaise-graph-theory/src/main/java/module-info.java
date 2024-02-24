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

import com.googlecode.blaisemath.graph.GraphGenerator;
import com.googlecode.blaisemath.graph.GraphMetric;
import com.googlecode.blaisemath.graph.GraphNodeMetric;
import com.googlecode.blaisemath.graph.GraphSubsetMetric;
import com.googlecode.blaisemath.graph.IterativeGraphLayout;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.generate.*;
import com.googlecode.blaisemath.graph.layout.*;
import com.googlecode.blaisemath.graph.metrics.*;

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

    // services
    uses GraphGenerator;
    uses GraphMetric;
    uses GraphNodeMetric;
    uses GraphSubsetMetric;
    uses IterativeGraphLayout;
    uses StaticGraphLayout;

    provides GraphGenerator with
            EmptyGraphGenerator,
            CycleGraphGenerator,
            StarGraphGenerator,
            WheelGraphGenerator,
            CompleteGraphGenerator,
            EdgeCountGenerator,
            EdgeLikelihoodGenerator,
            DegreeDistributionGenerator,
            ProximityGraphGenerator,
            WattsStrogatzGenerator,
            PreferentialAttachmentGenerator;
    provides GraphMetric with
            AverageDegree,
            ClusteringCoefficient,
            ClusteringCoefficientByPath,
            ComponentCount,
            EdgeDensity,
            GraphDiameter,
            EdgeCount,
            NodeCount,
            GraphRadius;
    provides GraphNodeMetric with
            BetweenCentrality,
            CliqueCount,
            CliqueCountTwo,
            ClosenessCentrality,
            DecayCentrality,
            Degree,
            DegreeTwo,
            EigenCentrality,
            GraphCentrality,
            InDegree,
            OutDegree;
    provides IterativeGraphLayout with
            SpringLayout;
    provides StaticGraphLayout with
            CircleLayout,
            RandomBoxLayout,
            PositionalAddingLayout,
            StaticSpringLayout;

}
