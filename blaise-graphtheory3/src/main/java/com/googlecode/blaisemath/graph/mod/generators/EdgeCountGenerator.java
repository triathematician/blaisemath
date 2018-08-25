/**
 * EdgeCountGenerator.java
 * Created Aug 18, 2012
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphGenerator;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Generate random graph with specified edge count.
 * @author Elisha Peterson
 */
public final class EdgeCountGenerator implements GraphGenerator<ExtendedGeneratorParameters,Integer> {
    
    //<editor-fold defaultstate="collapsed" desc="COMPARATOR INSTANCES">
    
    /**
     * Used to sort pairs of integers when order of the two matters.
     */
    static final Comparator<Integer[]> PAIR_COMPARE = new Comparator<Integer[]>() {
        @Override
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2) {
                throw new IllegalStateException("This object only compares integer pairs.");
            }
            return o1[0].equals(o2[0]) ? o1[1] - o2[1] : o1[0] - o2[0];
        }
    };

    /**
     * Used to sort pairs of integers when order of the two does not matter.
     */
    static final Comparator<Integer[]> PAIR_COMPARE_UNDIRECTED = new Comparator<Integer[]>() {
        @Override
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2) {
                throw new IllegalStateException("This object only compares integer pairs.");
            }
            int min1 = Math.min(o1[0], o1[1]);
            int min2 = Math.min(o2[0], o2[1]);
            return min1 == min2 ? Math.max(o1[0], o1[1]) - Math.max(o2[0], o2[1]) : min1 - min2;
        }
    };
    
    //</editor-fold>

    private static final EdgeCountGenerator INST = new EdgeCountGenerator();
    
    public static EdgeCountGenerator getInstance() {
        return INST;
    }

    @Override
    public String toString() {
        return "Random Graph (fixed Edge Count)";
    }

    @Override
    public ExtendedGeneratorParameters createParameters() {
        return new ExtendedGeneratorParameters();
    }

    @Override
    public Graph<Integer> apply(ExtendedGeneratorParameters parm) {
        boolean directed = parm.isDirected();
        int nodes = parm.getNodeCount();
        Set<Integer[]> edgeSet = new TreeSet<Integer[]>(directed ? PAIR_COMPARE : PAIR_COMPARE_UNDIRECTED);
        Integer[] potential;
        for (int i = 0; i < parm.getCheckedEdgeCount(); i++) {
            do {
                potential = new Integer[]{(int) (nodes * Math.random()), (int) (nodes * Math.random())};
            } while ((!directed && potential[0].equals(potential[1])) || edgeSet.contains(potential));
            edgeSet.add(potential);
        }
        return DefaultGeneratorParameters.createGraphWithEdges(parm, edgeSet);
    }
}
