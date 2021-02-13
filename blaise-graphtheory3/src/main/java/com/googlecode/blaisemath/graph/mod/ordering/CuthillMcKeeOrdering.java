package com.googlecode.blaisemath.graph.mod.ordering;

/*
 * #%L
 * blaise-graphtheory3
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


import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.math.util.MathUtils;

/**
 * Implementation of Cuthill-McKee ordering of nodes in the graph.
 * @author petereb1
 */
public class CuthillMcKeeOrdering<C> implements NodeOrdering<C> {
    
    private boolean runTwice = true;

    @Override
    public List<C> order(Graph<C> graph) {
        List<C> nodes = Lists.newArrayList(graph.nodes());
        boolean[][] adjacencyLists = GraphUtils.adjacencyMatrix(graph, nodes);
        
        // find a vertex with the smallest branching factor and start there
        int minBranchingFactor = Integer.MAX_VALUE;
        int minBranchingFactorVertex = -1;
        for (int v = 0; v < adjacencyLists.length; v++) {
            int branchingFactor = adjacencyLists[v].length;
            if (branchingFactor < minBranchingFactor) {
                minBranchingFactor = branchingFactor;
                minBranchingFactorVertex = v;
            }
        }

        // first run of RCM
        int[] permutation = cuthillMcKee(adjacencyLists, minBranchingFactorVertex);

        if (!runTwice) {
            return list(nodes, permutation);
        }

        // second run of RCM, starting at optimal first vertex
        return list(nodes, cuthillMcKee(adjacencyLists, permutation[0]));
    }
    
    private static <C> List<C> list(List<C> nodes, int[] permutation) {
        List<C> res = Lists.newArrayList();
        for (int i : permutation) {
            res.add(nodes.get(i));
        }
        return res;
    }

    private int[] cuthillMcKee(boolean[][] adjacencyLists, int startingVertex) {
        // error check args
        if (startingVertex < 0 || startingVertex >= adjacencyLists.length) {
            throw new IllegalArgumentException("startingVertex must be in [0," + adjacencyLists.length + ").");
        }

        int numVertices = adjacencyLists.length;
        HashSet<Integer> unvisitedVertices = new HashSet<Integer>();

        // determine the branching factors of each vertex
        int[] branchingFactors = new int[numVertices];
        for (int v = 0; v < numVertices; v++) {
            branchingFactors[v] = adjacencyLists[v].length;
            unvisitedVertices.add(v);
        }

        ArrayList<Integer> ordering = new ArrayList<Integer>();

        // start at the given vertex
        ordering.add(startingVertex);
        unvisitedVertices.remove(startingVertex);

        int i = 0;
        // keep looping until all vertices have been explored
        while (ordering.size() < numVertices) {

            // if we've reached the end of one component of the graph, start another
            if (i == ordering.size()) {
                // determine the lowest branching factor of the unvisitedVertices
                int minBranchingFactor = Integer.MAX_VALUE;
                int minBranchingFactorVertex = -1;
                for (int v : unvisitedVertices) {
                    if (branchingFactors[v] < minBranchingFactor) {
                        minBranchingFactor = branchingFactors[v];
                        minBranchingFactorVertex = v;
                    }
                }
                // start again with that vertex
                ordering.add(minBranchingFactorVertex);
                unvisitedVertices.remove(minBranchingFactorVertex);
            }

            int currVertex = ordering.get(i++);

            // sort the neighbors in increasing order
            int[] neighborDegrees = new int[adjacencyLists[currVertex].length];
            for (int nIdx = 0; nIdx < adjacencyLists[currVertex].length; nIdx++) {
                int neighbor = adjacencyLists[currVertex][nIdx];
                neighborDegrees[nIdx] = branchingFactors[neighbor];
            }
            int[] neighborOrder = MathUtils.sortOrder(neighborDegrees);

            // append unvisited neighbors to the ordering
            for (int nIdx : neighborOrder) {
                int neighbor = adjacencyLists[currVertex][nIdx];
                if (unvisitedVertices.contains(neighbor)) {
                    ordering.add(neighbor);
                    unvisitedVertices.remove(neighbor);
                }
            }
        }

        // return the reverse ordering
        int[] permutation = new int[ordering.size()];
        i = ordering.size() - 1;
        for (int v : ordering) {
            permutation[i--] = v;
        }
        return permutation;
    }

}
