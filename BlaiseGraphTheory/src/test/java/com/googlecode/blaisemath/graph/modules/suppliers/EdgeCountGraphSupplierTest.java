/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.modules.suppliers;

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

import com.googlecode.blaisemath.graph.modules.suppliers.EdgeCountGraphSupplier;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class EdgeCountGraphSupplierTest {

    @Test
    public void testEdgeCountBuilder() {
        System.out.println("EdgeCountBuilderTest: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new EdgeCountGraphSupplier(false, 10, 0).get();
        assertEquals(10, result1.nodeCount()); assertEquals(0, result1.edgeCount());
        result1 = new EdgeCountGraphSupplier(false, 10, 30).get();
        assertEquals(10, result1.nodeCount()); assertEquals(30, result1.edgeCount());
        System.out.println("  UNDIRECTED: " + GraphUtils.printGraph(result1));
        result1 = new EdgeCountGraphSupplier(true, 10, 30).get();
        assertEquals(10, result1.nodeCount()); assertEquals(30, result1.edgeCount());
        System.out.println("  DIRECTED: " + GraphUtils.printGraph(result1));
    }

}
