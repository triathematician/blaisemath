/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.modules;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import com.googlecode.blaisemath.graph.modules.EdgeProbabilityGraphSupplier;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class EdgeProbabilityGraphSupplierTest {

    @Test
    public void testEdgeProbailityBuilder() {
        System.out.println("EdgeProbabilityBuilder: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new EdgeProbabilityGraphSupplier(false, 10, 0f).get();
        assertEquals(10, result1.nodeCount()); assertEquals(0, result1.edgeCount());
        result1 = new EdgeProbabilityGraphSupplier(true, 10, 1f).get();
        assertEquals(10, result1.nodeCount()); assertEquals(100, result1.edgeCount());
        result1 = new EdgeProbabilityGraphSupplier(false, 10, 1f).get();
        assertEquals(10, result1.nodeCount()); assertEquals(45, result1.edgeCount());
        result1 = new EdgeProbabilityGraphSupplier(false, 10, .25f).get();
        System.out.println("  UNDIRECTED (.25 probability): " + result1.edgeCount() + " edges, " + GraphUtils.printGraph(result1));
        result1 = new EdgeProbabilityGraphSupplier(true, 10, .25f).get();
        System.out.println("  DIRECTED (.25 probability): " + result1.edgeCount() + " edges, " + GraphUtils.printGraph(result1));
    }

}