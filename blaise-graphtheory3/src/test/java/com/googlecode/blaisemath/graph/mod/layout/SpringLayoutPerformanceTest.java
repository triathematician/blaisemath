package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.mod.generators.EdgeCountGenerator;
import com.googlecode.blaisemath.graph.mod.layout.StaticSpringLayout.StaticSpringLayoutParameters;
import com.googlecode.blaisemath.graph.mod.generators.ExtendedGeneratorParameters;
import java.util.List;
import java.util.Random;

/**
 *
 * @author petereb1
 */
public class SpringLayoutPerformanceTest {

    public static void main(String[] args) throws InterruptedException {
        List<int[]> vals1 = Lists.newArrayList(new int[]{5, 3}, new int[]{4, 6});
        System.out.println(vals1.contains(new int[]{5, 3}));
        
        Thread.sleep(5000L);
        Random randomSeed = new Random(1290309812);
        
        StaticSpringLayout sl = new StaticSpringLayout();
        
        ExtendedGeneratorParameters[] graphs = new ExtendedGeneratorParameters[] {
            new ExtendedGeneratorParameters(false, 100, 100),
            new ExtendedGeneratorParameters(false, 100, 100),
            new ExtendedGeneratorParameters(false, 100, 500),
            new ExtendedGeneratorParameters(false, 100, 1000),
            new ExtendedGeneratorParameters(false, 1000, 1000),
            new ExtendedGeneratorParameters(false, 1000, 5000),
//            new ExtendedGeneratorParameters(false, 1000, 10000),
            new ExtendedGeneratorParameters(false, 10000, 10000),
//            new ExtendedGeneratorParameters(false, 10000, 50000),
//            new ExtendedGeneratorParameters(false, 10000, 100000),
//            new ExtendedGeneratorParameters(false, 10000, 1000000),
//            new ExtendedGeneratorParameters(false, 100000, 100000),
//            new ExtendedGeneratorParameters(false, 100000, 500000),
//            new ExtendedGeneratorParameters(false, 100000, 1000000),
//            new ExtendedGeneratorParameters(false, 1000000, 1000000),
//            new ExtendedGeneratorParameters(false, 1000000, 5000000),
//            new ExtendedGeneratorParameters(false, 1000000, 10000000)
//            new ExtendedGeneratorParameters(false, 10000000, 10000000),
//            new WattsStrogatzGenerator(randomSeed).apply(new WattsStrogatzParameters(false, 100, 4, .05f))
        };
        
        for (ExtendedGeneratorParameters gg : graphs) {
            long t0 = System.currentTimeMillis();
            Graph g = new EdgeCountGenerator(randomSeed).apply(gg);
            long t1 = System.currentTimeMillis();
            System.out.printf("Graph dir=%s, |V|=%s, |E|=%s, #components=%s, degrees=%s\n", 
                    g.isDirected(), g.nodeCount(), g.edgeCount(), 
                    GraphUtils.components(g).size(),
                    nicer(GraphUtils.degreeDistribution(g)));
            System.out.printf("    T_Gen = %s", t1-t0);
            sl.layout(g, null, new StaticSpringLayoutParameters());
            long t2 = System.currentTimeMillis();
            System.out.printf(", T_Lay = %s\n", t2-t1);
        }
    }

    private static String nicer(Multiset set) {
        List<String> ss = Lists.newArrayList();
        for (Object el : Sets.newTreeSet(set.elementSet())) {
            ss.add(el+":"+set.count(el));
        }
        return "["+Joiner.on(",").join(ss)+"]";
    }

}
