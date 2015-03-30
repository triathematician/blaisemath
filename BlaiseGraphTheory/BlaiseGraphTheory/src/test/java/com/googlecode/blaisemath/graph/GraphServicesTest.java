/*
 * Copyright 2015 elisha.
 *
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
 */
package com.googlecode.blaisemath.graph;

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
import java.util.List;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class GraphServicesTest {

    /**
     * Test of graphSuppliers method, of class GraphServices.
     */
    @Test
    public void testGraphSuppliers() {
        System.out.println("graphSuppliers");
        List<GraphGenerator> supp = GraphServices.generators();
        System.out.println(supp.size()+" supppliers");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    /**
     * Test of globalMetrics method, of class GraphServices.
     */
    @Test
    public void testGlobalMetrics() {
        System.out.println("globalMetrics");
        List<GraphMetric> supp = GraphServices.globalMetrics();
        System.out.println(supp.size()+" global metrics");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    /**
     * Test of nodeMetrics method, of class GraphServices.
     */
    @Test
    public void testNodeMetrics() {
        System.out.println("nodeMetrics");
        List<GraphNodeMetric> supp = GraphServices.nodeMetrics();
        System.out.println(supp.size()+" node metrics");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    /**
     * Test of subsetMetrics method, of class GraphServices.
     */
    @Test
    public void testSubsetMetrics() {
        System.out.println("subsetMetrics");
        List<GraphSubsetMetric> supp = GraphServices.subsetMetrics();
        System.out.println(supp.size()+" subset metrics");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    /**
     * Test of staticLayouts method, of class GraphServices.
     */
    @Test
    public void testStaticLayouts() {
        System.out.println("staticLayouts");
        List<StaticGraphLayout> supp = GraphServices.staticLayouts();
        System.out.println(supp.size()+" static layouts");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    /**
     * Test of iterativeLayouts method, of class GraphServices.
     */
    @Test
    public void testIterativeLayouts() {
        System.out.println("iterativeLayouts");
        List<IterativeGraphLayout> supp = GraphServices.iterativeLayouts();
        System.out.println(supp.size()+" iterative layouts");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }
    
}
