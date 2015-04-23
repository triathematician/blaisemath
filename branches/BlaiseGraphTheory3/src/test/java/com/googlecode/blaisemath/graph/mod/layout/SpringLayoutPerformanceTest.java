/*
 * SpringLayoutTest.java
 * Created on Jun 3, 2013
 */

package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graph.GAInstrument;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.mod.layout.StaticSpringLayout.StaticSpringLayoutParameters;
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator;
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;
import com.googlecode.blaisemath.graph.mod.generators.WattsStrogatzGenerator;
import com.googlecode.blaisemath.graph.mod.generators.WattsStrogatzGenerator.WattsStrogatzParameters;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author petereb1
 */
public class SpringLayoutPerformanceTest {

    public static void main(String[] args) {
        Random randomSeed = new Random(1290309812);
        
        StaticSpringLayout sl = new StaticSpringLayout();
        
        Graph[] graphs = new Graph[] {
            new EdgeLikelihoodGenerator(randomSeed).generate(new EdgeLikelihoodParameters(false, 100, .01f)),
            new EdgeLikelihoodGenerator(randomSeed).generate(new EdgeLikelihoodParameters(false, 100, .05f)),
            new EdgeLikelihoodGenerator(randomSeed).generate(new EdgeLikelihoodParameters(false, 100, .1f)),
            new EdgeLikelihoodGenerator(randomSeed).generate(new EdgeLikelihoodParameters(false, 300, .01f)),
            new EdgeLikelihoodGenerator(randomSeed).generate(new EdgeLikelihoodParameters(false, 1000, .002f)),
//            new EdgeLikelihoodGenerator(false, 300, .05f).get(),
//            new EdgeLikelihoodGenerator(false, 300, .1f).get(),
//            new EdgeLikelihoodGenerator(true, 300, .05f).get(),
            new WattsStrogatzGenerator(randomSeed).generate(new WattsStrogatzParameters(false, 100, 4, .05f))
//            new WattsStrogatzGenerator(false, 1000, 4, .01f).randomGenerator(randomSeed).get()
        };
        
        for (Graph g : graphs) {
            System.out.printf("\nGraph dir=%s, |V|=%s, |E|=%s, #components=%s, degrees=%s\n", 
                    g.isDirected(), g.nodeCount(), g.edgeCount(), 
                    GraphUtils.components(g).size(),
                    nicer(GraphUtils.degreeDistribution(g)));
            int id = GAInstrument.start("EdgePD", g+"");
            sl.layout(g, Collections.EMPTY_MAP, Collections.EMPTY_SET, new StaticSpringLayoutParameters());
            GAInstrument.end(id);
        }
        System.out.println("\n\n");
        GAInstrument.print(System.out);
    }

    private static String nicer(Multiset set) {
        List<String> ss = Lists.newArrayList();
        for (Object el : Sets.newTreeSet(set.elementSet())) {
            ss.add(el+":"+set.count(el));
        }
        return "["+Joiner.on(",").join(ss)+"]";
    }

}
