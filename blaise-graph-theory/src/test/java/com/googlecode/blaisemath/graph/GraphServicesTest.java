package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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

public class GraphServicesTest {

    @Test
    public void testGraphSuppliers() {
        List<GraphGenerator> supp = GraphServices.generators();
        System.out.println(supp.size()+" suppliers");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    @Test
    public void testGlobalMetrics() {
        List<GraphMetric> supp = GraphServices.globalMetrics();
        System.out.println(supp.size()+" global metrics");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    @Test
    public void testNodeMetrics() {
        List<GraphNodeMetric> supp = GraphServices.nodeMetrics();
        System.out.println(supp.size()+" node metrics");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    @Test
    public void testSubsetMetrics() {
        List<GraphSubsetMetric> supp = GraphServices.subsetMetrics();
        System.out.println(supp.size()+" subset metrics");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    @Test
    public void testStaticLayouts() {
        List<StaticGraphLayout> supp = GraphServices.staticLayouts();
        System.out.println(supp.size()+" static layouts");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }

    @Test
    public void testIterativeLayouts() {
        List<IterativeGraphLayout> supp = GraphServices.iterativeLayouts();
        System.out.println(supp.size()+" iterative layouts");
        System.out.println("- "+Joiner.on("\n- ").join(supp));
    }
    
}
