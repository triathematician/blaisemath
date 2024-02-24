package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator;
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator;
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator.WattsStrogatzParameters;
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout.StaticSpringLayoutParameters;
import com.googlecode.blaisemath.util.Instrument;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class SpringLayoutPerformanceTest {

    public static void main(String[] args) {
        Random randomSeed = new Random(1290309812);
        
        StaticSpringLayout sl = new StaticSpringLayout();
        
        List<Graph<Integer>> graphs = Arrays.asList(
            new EdgeLikelihoodGenerator(randomSeed).apply(new EdgeLikelihoodParameters(false, 100, .01f)),
            new EdgeLikelihoodGenerator(randomSeed).apply(new EdgeLikelihoodParameters(false, 100, .05f)),
            new EdgeLikelihoodGenerator(randomSeed).apply(new EdgeLikelihoodParameters(false, 100, .1f)),
            new EdgeLikelihoodGenerator(randomSeed).apply(new EdgeLikelihoodParameters(false, 300, .01f)),
            new EdgeLikelihoodGenerator(randomSeed).apply(new EdgeLikelihoodParameters(false, 1000, .002f)),
//            new EdgeLikelihoodGenerator(false, 300, .05f).get(),
//            new EdgeLikelihoodGenerator(false, 300, .1f).get(),
//            new EdgeLikelihoodGenerator(true, 300, .05f).get(),
            new WattsStrogatzGenerator(randomSeed).apply(new WattsStrogatzParameters(false, 100, 4, .05f))
//            new WattsStrogatzGenerator(false, 1000, 4, .01f).randomGenerator(randomSeed).get()
        );
        
        for (Graph<Integer> g : graphs) {
            System.out.printf("\nGraph dir=%s, |V|=%s, |E|=%s, #components=%s, degrees=%s\n", 
                    g.isDirected(), g.nodes().size(), g.edges().size(),
                    GraphUtils.components(g).size(),
                    nicer(GraphUtils.degreeDistribution(g)));
            int id = Instrument.start("EdgePD", g+"");
            sl.layout(g, null, new StaticSpringLayoutParameters());
            Instrument.end(id);
        }
        System.out.println("\n\n");
        Instrument.print(System.out);
    }

    private static <X extends Comparable> String nicer(Multiset<X> set) {
        List<String> ss = Lists.newArrayList();
        for (X el : Sets.newTreeSet(set.elementSet())) {
            ss.add(el+":"+set.count(el));
        }
        return "["+Joiner.on(",").join(ss)+"]";
    }

}
